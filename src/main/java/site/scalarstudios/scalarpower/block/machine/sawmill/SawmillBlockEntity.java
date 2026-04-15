package site.scalarstudios.scalarpower.block.machine.sawmill;

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
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import site.scalarstudios.scalarpower.block.ScalarPowerBlockEntities;
import site.scalarstudios.scalarpower.block.machine.MachineUtils;
import site.scalarstudios.scalarpower.power.NeoEnergyTransferUtil;
import site.scalarstudios.scalarpower.recipe.SawmillRecipe;
import site.scalarstudios.scalarpower.recipe.ScalarPowerRecipes;

import java.util.Optional;

public class SawmillBlockEntity extends BlockEntity implements Container, MenuProvider {
    private static final int ENERGY_CAPACITY = MachineUtils.BASIC_MACHINE_CAPACITY;
    private static final int ENERGY_PER_TICK = MachineUtils.BASIC_MACHINE_SPU_PER_TICK;
    private static final int RECIPE_TIME = MachineUtils.BASIC_MACHINE_TIME_PER_CRAFT;
    private static final int PULL_PER_SIDE = MachineUtils.BASIC_MACHINE_SPU_PER_SIDE;

    private ItemStack inputStack = ItemStack.EMPTY;
    private ItemStack planksStack = ItemStack.EMPTY;
    private ItemStack sawdustStack = ItemStack.EMPTY;
    private int progress;

    private final SimpleEnergyHandler energyHandler = new SimpleEnergyHandler(ENERGY_CAPACITY, ENERGY_CAPACITY, ENERGY_CAPACITY, 0) {
        @Override
        protected void onEnergyChanged(int previousAmount) {
            setChanged();
        }
    };

    public SawmillBlockEntity(BlockPos pos, BlockState blockState) {
        super(ScalarPowerBlockEntities.SAWMILL.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SawmillBlockEntity blockEntity) {
        if (level == null || level.isClientSide()) return;

        boolean changed = false;
        boolean isWorking = false;

        if (blockEntity.energyHandler.getAmountAsLong() < ENERGY_CAPACITY) {
            int pulled = NeoEnergyTransferUtil.pullEnergy(level, pos, blockEntity.energyHandler, PULL_PER_SIDE);
            changed |= pulled > 0;
        }

        Optional<RecipeHolder<SawmillRecipe>> recipeHolder = blockEntity.findRecipe(blockEntity.inputStack);
        ItemStack result = recipeHolder
                .map(holder -> holder.value().assemble(new SingleRecipeInput(blockEntity.inputStack)))
                .orElse(ItemStack.EMPTY);

        if (!result.isEmpty() && blockEntity.canOutput(blockEntity.planksStack, result)
                && blockEntity.energyHandler.getAmountAsLong() >= ENERGY_PER_TICK) {
            blockEntity.energyHandler.set((int) (blockEntity.energyHandler.getAmountAsLong() - ENERGY_PER_TICK));
            blockEntity.progress++;
            changed = true;
            isWorking = true;

            if (blockEntity.progress >= RECIPE_TIME) {
                blockEntity.inputStack.shrink(1);

                int produced = Math.min(result.getCount(), blockEntity.getMaxAddable(blockEntity.planksStack, result));
                if (blockEntity.planksStack.isEmpty()) {
                    blockEntity.planksStack = result.copyWithCount(produced);
                } else {
                    blockEntity.planksStack.grow(produced);
                }

                // Roll for byproduct (sawdust)
                SawmillRecipe recipe = recipeHolder.get().value();
                if (recipe.byproductChance() > 0 && level.getRandom().nextFloat() < recipe.byproductChance()) {
                    ItemStack byproductStack = recipe.assembleByproduct();
                    if (!byproductStack.isEmpty()) {
                        int maxAddable = blockEntity.getMaxAddable(blockEntity.sawdustStack, byproductStack);
                        if (maxAddable > 0) {
                            int toAdd = Math.min(byproductStack.getCount(), maxAddable);
                            if (blockEntity.sawdustStack.isEmpty()) {
                                blockEntity.sawdustStack = byproductStack.copyWithCount(toAdd);
                            } else {
                                blockEntity.sawdustStack.grow(toAdd);
                            }
                        }
                    }
                }

                blockEntity.progress = 0;
            }
        } else if (blockEntity.progress > 0) {
            blockEntity.progress = Math.max(0, blockEntity.progress - 2);
            changed = true;
        }

        if (changed) blockEntity.setChanged();

        if (state.hasProperty(SawmillBlock.LIT) && state.getValue(SawmillBlock.LIT) != isWorking) {
            level.setBlock(pos, state.setValue(SawmillBlock.LIT, isWorking), 3);
        }
    }

    private boolean canOutput(ItemStack current, ItemStack recipe) {
        if (current.isEmpty()) return true;
        if (!ItemStack.isSameItemSameComponents(current, recipe)) return false;
        return current.getCount() + recipe.getCount() <= current.getMaxStackSize();
    }

    private int getMaxAddable(ItemStack current, ItemStack recipe) {
        if (current.isEmpty()) return recipe.getMaxStackSize();
        if (!ItemStack.isSameItemSameComponents(current, recipe)) return 0;
        return current.getMaxStackSize() - current.getCount();
    }

    public boolean canSaw(ItemStack stack) {
        return !stack.isEmpty();
    }

    private boolean isOutputSlot(int slot) {
        return slot == 1 || slot == 2;
    }

    private Optional<RecipeHolder<SawmillRecipe>> findRecipe(ItemStack stack) {
        if (stack.isEmpty() || !(level instanceof ServerLevel serverLevel)) {
            return Optional.empty();
        }

        SingleRecipeInput input = new SingleRecipeInput(stack);
        Optional<RecipeHolder<SawmillRecipe>> byType = serverLevel.recipeAccess().getRecipeFor(
                ScalarPowerRecipes.SAWMILLING_RECIPE_TYPE, input, serverLevel);
        if (byType.isPresent()) return byType;

        // Fallback scan
        RecipeManager recipeManager = serverLevel.recipeAccess();
        return recipeManager.getRecipes().stream()
                .filter(holder -> holder.value().getType() == ScalarPowerRecipes.SAWMILLING_RECIPE_TYPE)
                .map(holder -> (RecipeHolder<SawmillRecipe>) holder)
                .filter(holder -> holder.value().matches(input, serverLevel))
                .findFirst();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        energyHandler.serialize(output);
        output.putInt("Progress", progress);
        output.store("Input", ItemStack.OPTIONAL_CODEC, inputStack);
        output.store("Planks", ItemStack.OPTIONAL_CODEC, planksStack);
        output.store("Sawdust", ItemStack.OPTIONAL_CODEC, sawdustStack);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energyHandler.deserialize(input);
        progress = input.getIntOr("Progress", 0);
        inputStack = input.read("Input", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        planksStack = input.read("Planks", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        sawdustStack = input.read("Sawdust", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    public Component getDisplayName() { return Component.translatable("container.scalarpower.sawmill"); }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new SawmillMenu(id, inv, this, new ContainerData() {
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
                    default -> {}
                }
            }

            @Override
            public int getCount() { return 4; }
        });
    }

    @Override
    public int getContainerSize() { return 3; }

    @Override
    public boolean isEmpty() {
        return inputStack.isEmpty() && planksStack.isEmpty() && sawdustStack.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return switch (slot) {
            case 0 -> inputStack;
            case 1 -> planksStack;
            case 2 -> sawdustStack;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemStack stack = getItem(slot);
        if (!stack.isEmpty()) {
            ItemStack split = stack.split(count);
            if (slot == 0) inputStack = stack;
            else if (slot == 1) planksStack = stack;
            else if (slot == 2) sawdustStack = stack;
            setChanged();
            return split;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = getItem(slot);
        if (slot == 0) inputStack = ItemStack.EMPTY;
        else if (slot == 1) planksStack = ItemStack.EMPTY;
        else if (slot == 2) sawdustStack = ItemStack.EMPTY;
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == 0) {
            inputStack = stack;
            progress = 0;
        } else if (slot == 1) {
            planksStack = stack;
        } else if (slot == 2) {
            sawdustStack = stack;
        }
        if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize());
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }

    @Override
    public void clearContent() {
        inputStack = ItemStack.EMPTY;
        planksStack = ItemStack.EMPTY;
        sawdustStack = ItemStack.EMPTY;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return !isOutputSlot(slot) && canSaw(stack);
    }

    @Override
    public boolean canTakeItem(Container target, int slot, ItemStack stack) {
        return isOutputSlot(slot);
    }

    public EnergyHandler getEnergyHandler(Direction side) { return energyHandler; }

    public int getProgress() { return progress; }
    public int getMaxProgress() { return RECIPE_TIME; }
    public int getEnergy() { return (int) energyHandler.getAmountAsLong(); }
}

