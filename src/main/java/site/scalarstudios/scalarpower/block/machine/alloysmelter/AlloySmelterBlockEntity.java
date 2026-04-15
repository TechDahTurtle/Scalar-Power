package site.scalarstudios.scalarpower.block.machine.alloysmelter;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import site.scalarstudios.scalarpower.block.ScalarPowerBlockEntities;
import site.scalarstudios.scalarpower.block.machine.MachineUtils;
import site.scalarstudios.scalarpower.recipe.AlloySmeltingRecipe;
import site.scalarstudios.scalarpower.recipe.ExternalRecipeCompat;
import site.scalarstudios.scalarpower.power.NeoEnergyTransferUtil;
import site.scalarstudios.scalarpower.recipe.ScalarPowerRecipes;

import java.util.Optional;

public class AlloySmelterBlockEntity extends BlockEntity implements Container, MenuProvider {
    private static final int ENERGY_CAPACITY = MachineUtils.BASIC_MACHINE_CAPACITY;
    private static final int ENERGY_PER_TICK = MachineUtils.BASIC_MACHINE_SPU_PER_TICK;
    private static final int RECIPE_TIME = MachineUtils.ALLOY_SMELTER_TIME_PER_CRAFT;
    private static final int PULL_PER_SIDE = MachineUtils.BASIC_MACHINE_SPU_PER_SIDE;

    private ItemStack inputStack0 = ItemStack.EMPTY;
    private ItemStack inputStack1 = ItemStack.EMPTY;
    private ItemStack inputStack2 = ItemStack.EMPTY;
    private ItemStack outputStack = ItemStack.EMPTY;
    private int progress;

    private final SimpleEnergyHandler energyHandler = new SimpleEnergyHandler(ENERGY_CAPACITY, ENERGY_CAPACITY, ENERGY_CAPACITY, 0) {
        @Override
        protected void onEnergyChanged(int previousAmount) {
            setChanged();
        }
    };

    public AlloySmelterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ScalarPowerBlockEntities.ALLOY_SMELTER.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AlloySmelterBlockEntity blockEntity) {
        if (level == null || level.isClientSide()) {
            return;
        }

        boolean changed = false;
        boolean isWorking = false;

        if (blockEntity.energyHandler.getAmountAsLong() < ENERGY_CAPACITY) {
            int pulled = NeoEnergyTransferUtil.pullEnergy(level, pos, blockEntity.energyHandler, PULL_PER_SIDE);
            changed |= pulled > 0;
        }

        AlloySmeltingInput input = blockEntity.getRecipeInput();
        Optional<RecipeMatch> recipeMatch = blockEntity.findRecipeMatch(input);
        ItemStack result = recipeMatch
                .map(RecipeMatch::result)
                .orElse(ItemStack.EMPTY);

        if (!result.isEmpty()
                && blockEntity.canOutput(blockEntity.outputStack, result)
                && blockEntity.energyHandler.getAmountAsLong() >= ENERGY_PER_TICK) {
            blockEntity.energyHandler.set((int) (blockEntity.energyHandler.getAmountAsLong() - ENERGY_PER_TICK));
            blockEntity.progress++;
            changed = true;
            isWorking = true;

            if (blockEntity.progress >= RECIPE_TIME) {
                for (int slot : recipeMatch.orElseThrow().matchedSlots()) {
                    blockEntity.getItem(slot).shrink(1);
                }

                if (blockEntity.outputStack.isEmpty()) {
                    blockEntity.outputStack = result.copy();
                } else {
                    blockEntity.outputStack.grow(result.getCount());
                }
                blockEntity.progress = 0;
            }
        } else if (blockEntity.progress > 0) {
            blockEntity.progress = Math.max(0, blockEntity.progress - 2);
            changed = true;
        }

        if (changed) {
            blockEntity.setChanged();
        }

        if (state.hasProperty(AlloySmelterBlock.LIT) && state.getValue(AlloySmelterBlock.LIT) != isWorking) {
            level.setBlock(pos, state.setValue(AlloySmelterBlock.LIT, isWorking), 3);
        }
    }

    private AlloySmeltingInput getRecipeInput() {
        return new AlloySmeltingInput(inputStack0, inputStack1, inputStack2);
    }

    private Optional<RecipeMatch> findRecipeMatch(AlloySmeltingInput input) {
        return findRecipe(input)
                .map(holder -> {
                    AlloySmeltingRecipe recipe = holder.value();
                    return new RecipeMatch(recipe.assemble(input), recipe.findMatchingSlots(input));
                })
                .filter(match -> !match.result().isEmpty() && match.matchedSlots().length > 0);
    }

    public boolean canAlloy(ItemStack stack) {
        return !stack.isEmpty();
    }

    private boolean isOutputSlot(int slot) {
        return slot == 3;
    }

    private Optional<RecipeHolder<AlloySmeltingRecipe>> findRecipe(AlloySmeltingInput input) {
        if (input.nonEmptyCount() < 2 || !(level instanceof ServerLevel serverLevel)) {
            return Optional.empty();
        }

        Optional<RecipeHolder<AlloySmeltingRecipe>> byType = serverLevel.recipeAccess().getRecipeFor(
                ScalarPowerRecipes.ALLOY_SMELTING_RECIPE_TYPE,
                input,
                serverLevel);
        if (byType.isPresent()) {
            return byType;
        }

        // Fallback scan covers edge-cases where typed lookups miss custom recipe entries.
        RecipeManager recipeManager = serverLevel.recipeAccess();
        Optional<RecipeHolder<AlloySmeltingRecipe>> fallback = recipeManager.getRecipes().stream()
                .filter(holder -> holder.value().getType() == ScalarPowerRecipes.ALLOY_SMELTING_RECIPE_TYPE)
                .map(holder -> (RecipeHolder<AlloySmeltingRecipe>) holder)
                .filter(holder -> holder.value().matches(input, serverLevel))
                .findFirst();

        if (fallback.isPresent()) {
            return fallback;
        }

        Optional<RecipeHolder<AlloySmeltingRecipe>> external = ExternalRecipeCompat.findExternalAlloyRecipe(serverLevel, input);
        if (external.isPresent()) {
            return external;
        }


        return Optional.empty();
    }

    private boolean canOutput(ItemStack current, ItemStack recipe) {
        if (current.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameComponents(current, recipe)) {
            return false;
        }
        return current.getCount() + recipe.getCount() <= current.getMaxStackSize();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        energyHandler.serialize(output);
        output.putInt("Progress", progress);
        output.store("Input0", ItemStack.OPTIONAL_CODEC, inputStack0);
        output.store("Input1", ItemStack.OPTIONAL_CODEC, inputStack1);
        output.store("Input2", ItemStack.OPTIONAL_CODEC, inputStack2);
        output.store("Output", ItemStack.OPTIONAL_CODEC, outputStack);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energyHandler.deserialize(input);
        progress = input.getIntOr("Progress", 0);
        inputStack0 = input.read("Input0", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        inputStack1 = input.read("Input1", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        inputStack2 = input.read("Input2", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        outputStack = input.read("Output", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.scalarpower.alloy_smelter");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new AlloySmelterMenu(id, inv, this, new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> progress;
                    case 1 -> RECIPE_TIME;
                    case 2 -> (int) energyHandler.getAmountAsLong();
                    case 3 -> (int) energyHandler.getCapacityAsLong();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> progress = value;
                    case 2 -> energyHandler.set(value);
                    default -> {
                    }
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        });
    }

    @Override
    public int getContainerSize() {
        return 4;
    }

    @Override
    public boolean isEmpty() {
        return inputStack0.isEmpty() && inputStack1.isEmpty() && inputStack2.isEmpty() && outputStack.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return switch (slot) {
            case 0 -> inputStack0;
            case 1 -> inputStack1;
            case 2 -> inputStack2;
            case 3 -> outputStack;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemStack stack = getItem(slot);
        if (!stack.isEmpty()) {
            ItemStack split = stack.split(count);
            setSlotStack(slot, stack, true);
            return split;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = getItem(slot);
        setSlotStack(slot, ItemStack.EMPTY, false);
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        setSlotStack(slot, stack, false);

        if (slot >= 0 && slot <= 2) {
            progress = 0;
        }
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
    }

    private void setSlotStack(int slot, ItemStack stack, boolean markChanged) {
        switch (slot) {
            case 0 -> inputStack0 = stack;
            case 1 -> inputStack1 = stack;
            case 2 -> inputStack2 = stack;
            case 3 -> outputStack = stack;
            default -> {
                return;
            }
        }

        if (markChanged) {
            setChanged();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        inputStack0 = ItemStack.EMPTY;
        inputStack1 = ItemStack.EMPTY;
        inputStack2 = ItemStack.EMPTY;
        outputStack = ItemStack.EMPTY;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return !isOutputSlot(slot) && canAlloy(stack);
    }

    @Override
    public boolean canTakeItem(Container target, int slot, ItemStack stack) {
        return isOutputSlot(slot);
    }

    public EnergyHandler getEnergyHandler(Direction side) {
        return energyHandler;
    }

    private record RecipeMatch(ItemStack result, int[] matchedSlots) {
    }
}

