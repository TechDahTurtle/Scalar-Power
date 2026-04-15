package site.scalarstudios.scalarpower.block.machine.macerator;

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
import site.scalarstudios.scalarpower.recipe.MaceratingRecipe;
import site.scalarstudios.scalarpower.recipe.ScalarPowerRecipes;

import java.util.Optional;

public class MaceratorBlockEntity extends BlockEntity implements Container, MenuProvider {
    private static final int ENERGY_CAPACITY = MachineUtils.BASIC_MACHINE_CAPACITY;
    private static final int ENERGY_PER_TICK = MachineUtils.BASIC_MACHINE_SPU_PER_TICK;
    private static final int RECIPE_TIME = MachineUtils.BASIC_MACHINE_TIME_PER_CRAFT;
    private static final int PULL_PER_SIDE = MachineUtils.BASIC_MACHINE_SPU_PER_SIDE;

    private ItemStack inputStack = ItemStack.EMPTY;
    private ItemStack primaryOutputStack = ItemStack.EMPTY;
    private ItemStack secondaryOutputStack = ItemStack.EMPTY;
    private int progress;

    private final SimpleEnergyHandler energyHandler = new SimpleEnergyHandler(ENERGY_CAPACITY, ENERGY_CAPACITY, ENERGY_CAPACITY, 0) {
        @Override
        protected void onEnergyChanged(int previousAmount) {
            setChanged();
        }
    };

    public MaceratorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ScalarPowerBlockEntities.MACERATOR.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MaceratorBlockEntity blockEntity) {
        if (level == null || level.isClientSide()) {
            return;
        }

        boolean changed = false;
        boolean isWorking = false;

        if (blockEntity.energyHandler.getAmountAsLong() < ENERGY_CAPACITY) {
            int pulled = NeoEnergyTransferUtil.pullEnergy(level, pos, blockEntity.energyHandler, PULL_PER_SIDE);
            changed |= pulled > 0;
        }

        Optional<RecipeHolder<MaceratingRecipe>> recipeHolder = blockEntity.findRecipe(blockEntity.inputStack);
        ItemStack result = recipeHolder
                .map(holder -> holder.value().assemble(new SingleRecipeInput(blockEntity.inputStack)))
                .orElse(ItemStack.EMPTY);

        if (!result.isEmpty() && blockEntity.canOutput(blockEntity.primaryOutputStack, result)
                && blockEntity.energyHandler.getAmountAsLong() >= ENERGY_PER_TICK) {
            blockEntity.energyHandler.set((int) (blockEntity.energyHandler.getAmountAsLong() - ENERGY_PER_TICK));
            blockEntity.progress++;
            changed = true;
            isWorking = true;

            if (blockEntity.progress >= RECIPE_TIME) {
                blockEntity.inputStack.shrink(1);

                int produced = Math.min(result.getCount(), blockEntity.getMaxAddable(blockEntity.primaryOutputStack, result));
                if (blockEntity.primaryOutputStack.isEmpty()) {
                    blockEntity.primaryOutputStack = result.copyWithCount(produced);
                } else {
                    blockEntity.primaryOutputStack.grow(produced);
                }

                MaceratingRecipe recipe = recipeHolder.get().value();
                if (recipe.byproductChance() > 0.0F && level.getRandom().nextFloat() < recipe.byproductChance()) {
                    ItemStack byproductStack = recipe.assembleByproduct();
                    if (!byproductStack.isEmpty()) {
                        int maxAddable = blockEntity.getMaxAddable(blockEntity.secondaryOutputStack, byproductStack);
                        if (maxAddable > 0) {
                            int toAdd = Math.min(byproductStack.getCount(), maxAddable);
                            if (blockEntity.secondaryOutputStack.isEmpty()) {
                                blockEntity.secondaryOutputStack = byproductStack.copyWithCount(toAdd);
                            } else {
                                blockEntity.secondaryOutputStack.grow(toAdd);
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

        if (changed) {
            blockEntity.setChanged();
        }

        if (state.hasProperty(MaceratorBlock.LIT) && state.getValue(MaceratorBlock.LIT) != isWorking) {
            level.setBlock(pos, state.setValue(MaceratorBlock.LIT, isWorking), 3);
        }
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

    private int getMaxAddable(ItemStack current, ItemStack recipe) {
        if (current.isEmpty()) {
            return recipe.getMaxStackSize();
        }
        if (!ItemStack.isSameItemSameComponents(current, recipe)) {
            return 0;
        }
        return current.getMaxStackSize() - current.getCount();
    }

    public boolean canMacerate(ItemStack stack) {
        return !stack.isEmpty();
    }

    private boolean isOutputSlot(int slot) {
        return slot == 1 || slot == 2;
    }

    @SuppressWarnings("unchecked")
    private Optional<RecipeHolder<MaceratingRecipe>> findRecipe(ItemStack stack) {
        if (stack.isEmpty() || !(level instanceof ServerLevel serverLevel)) {
            return Optional.empty();
        }

        SingleRecipeInput input = new SingleRecipeInput(stack);
        Optional<RecipeHolder<MaceratingRecipe>> byType = serverLevel.recipeAccess().getRecipeFor(
                ScalarPowerRecipes.MACERATING_RECIPE_TYPE,
                input,
                serverLevel);
        if (byType.isPresent()) {
            return byType;
        }

        RecipeManager recipeManager = serverLevel.recipeAccess();
        return recipeManager.getRecipes().stream()
                .filter(holder -> holder.value().getType() == ScalarPowerRecipes.MACERATING_RECIPE_TYPE)
                .map(holder -> (RecipeHolder<MaceratingRecipe>) holder)
                .filter(holder -> holder.value().matches(input, serverLevel))
                .findFirst();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        energyHandler.serialize(output);
        output.putInt("Progress", progress);
        output.store("Input", ItemStack.OPTIONAL_CODEC, inputStack);
        output.store("PrimaryOutput", ItemStack.OPTIONAL_CODEC, primaryOutputStack);
        output.store("SecondaryOutput", ItemStack.OPTIONAL_CODEC, secondaryOutputStack);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energyHandler.deserialize(input);
        progress = input.getIntOr("Progress", 0);
        inputStack = input.read("Input", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        primaryOutputStack = input.read("PrimaryOutput", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        secondaryOutputStack = input.read("SecondaryOutput", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.scalarpower.macerator");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new MaceratorMenu(id, inv, this, new ContainerData() {
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
        return 3;
    }

    @Override
    public boolean isEmpty() {
        return inputStack.isEmpty() && primaryOutputStack.isEmpty() && secondaryOutputStack.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return switch (slot) {
            case 0 -> inputStack;
            case 1 -> primaryOutputStack;
            case 2 -> secondaryOutputStack;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemStack stack = getItem(slot);
        if (!stack.isEmpty()) {
            ItemStack split = stack.split(count);
            if (slot == 0) {
                inputStack = stack;
            } else if (slot == 1) {
                primaryOutputStack = stack;
            } else if (slot == 2) {
                secondaryOutputStack = stack;
            }
            setChanged();
            return split;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = getItem(slot);
        if (slot == 0) {
            inputStack = ItemStack.EMPTY;
        } else if (slot == 1) {
            primaryOutputStack = ItemStack.EMPTY;
        } else if (slot == 2) {
            secondaryOutputStack = ItemStack.EMPTY;
        }
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == 0) {
            inputStack = stack;
            progress = 0;
        } else if (slot == 1) {
            primaryOutputStack = stack;
        } else if (slot == 2) {
            secondaryOutputStack = stack;
        }

        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        inputStack = ItemStack.EMPTY;
        primaryOutputStack = ItemStack.EMPTY;
        secondaryOutputStack = ItemStack.EMPTY;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return !isOutputSlot(slot) && canMacerate(stack);
    }

    @Override
    public boolean canTakeItem(Container target, int slot, ItemStack stack) {
        return isOutputSlot(slot);
    }

    public EnergyHandler getEnergyHandler(Direction side) {
        return energyHandler;
    }
}

