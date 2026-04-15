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

public class DoubleMaceratorBlockEntity extends BlockEntity implements Container, MenuProvider {
    private static final int ENERGY_CAPACITY = MachineUtils.STEEL_MACHINE_CAPACITY;
    private static final int ENERGY_PER_TICK_PER_LANE = MachineUtils.STEEL_MACHINE_SPU_PER_TICK_PER_LANE;
    private static final int PULL_PER_SIDE = MachineUtils.STEEL_MACHINE_SPU_PER_SIDE;
    private static final int RECIPE_TIME = MachineUtils.STEEL_MACHINE_TIME_PER_CRAFT;

    private ItemStack inputStackA = ItemStack.EMPTY;
    private ItemStack inputStackB = ItemStack.EMPTY;
    private ItemStack primaryOutputStackA = ItemStack.EMPTY;
    private ItemStack primaryOutputStackB = ItemStack.EMPTY;
    private ItemStack secondaryOutputStackA = ItemStack.EMPTY;
    private ItemStack secondaryOutputStackB = ItemStack.EMPTY;
    private int progressA;
    private int progressB;

    private final SimpleEnergyHandler energyHandler = new SimpleEnergyHandler(ENERGY_CAPACITY, ENERGY_CAPACITY, ENERGY_CAPACITY, 0) {
        @Override
        protected void onEnergyChanged(int previousAmount) {
            setChanged();
        }
    };

    public DoubleMaceratorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ScalarPowerBlockEntities.DOUBLE_MACERATOR.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DoubleMaceratorBlockEntity blockEntity) {
        if (level == null || level.isClientSide()) {
            return;
        }

        boolean changed = false;
        boolean isWorking = false;

        if (blockEntity.energyHandler.getAmountAsLong() < ENERGY_CAPACITY) {
            int pulled = NeoEnergyTransferUtil.pullEnergy(level, pos, blockEntity.energyHandler, PULL_PER_SIDE);
            changed |= pulled > 0;
        }

        int availableEnergy = (int) blockEntity.energyHandler.getAmountAsLong();

        LaneTickResult laneA = blockEntity.processLane(level, true, availableEnergy);
        availableEnergy = laneA.availableEnergy();
        changed |= laneA.changed();
        isWorking |= laneA.working();

        LaneTickResult laneB = blockEntity.processLane(level, false, availableEnergy);
        availableEnergy = laneB.availableEnergy();
        changed |= laneB.changed();
        isWorking |= laneB.working();

        if (availableEnergy != blockEntity.energyHandler.getAmountAsLong()) {
            blockEntity.energyHandler.set(availableEnergy);
            changed = true;
        }

        if (changed) {
            blockEntity.setChanged();
        }

        if (state.hasProperty(DoubleMaceratorBlock.LIT) && state.getValue(DoubleMaceratorBlock.LIT) != isWorking) {
            level.setBlock(pos, state.setValue(DoubleMaceratorBlock.LIT, isWorking), 3);
        }
    }

    private LaneTickResult processLane(Level level, boolean firstLane, int availableEnergy) {
        ItemStack input = firstLane ? inputStackA : inputStackB;
        ItemStack primaryOutput = firstLane ? primaryOutputStackA : primaryOutputStackB;
        ItemStack secondaryOutput = firstLane ? secondaryOutputStackA : secondaryOutputStackB;
        int progress = firstLane ? progressA : progressB;

        boolean changed = false;
        boolean working = false;

        Optional<RecipeHolder<MaceratingRecipe>> recipeHolder = findRecipe(input);
        ItemStack result = recipeHolder
                .map(holder -> holder.value().assemble(new SingleRecipeInput(input)))
                .orElse(ItemStack.EMPTY);

        if (!result.isEmpty() && canOutput(primaryOutput, result) && availableEnergy >= ENERGY_PER_TICK_PER_LANE) {
            availableEnergy -= ENERGY_PER_TICK_PER_LANE;
            progress++;
            changed = true;
            working = true;

            if (progress >= RECIPE_TIME) {
                input.shrink(1);

                int produced = Math.min(result.getCount(), getMaxAddable(primaryOutput, result));
                if (primaryOutput.isEmpty()) {
                    primaryOutput = result.copyWithCount(produced);
                } else {
                    primaryOutput.grow(produced);
                }

                MaceratingRecipe recipe = recipeHolder.get().value();
                if (recipe.byproductChance() > 0.0F && level.getRandom().nextFloat() < recipe.byproductChance()) {
                    ItemStack byproductStack = recipe.assembleByproduct();
                    if (!byproductStack.isEmpty()) {
                        int maxAddable = getMaxAddable(secondaryOutput, byproductStack);
                        if (maxAddable > 0) {
                            int toAdd = Math.min(byproductStack.getCount(), maxAddable);
                            if (secondaryOutput.isEmpty()) {
                                secondaryOutput = byproductStack.copyWithCount(toAdd);
                            } else {
                                secondaryOutput.grow(toAdd);
                            }
                        }
                    }
                }

                progress = 0;
            }
        } else if (progress > 0) {
            progress = Math.max(0, progress - 2);
            changed = true;
        }

        if (firstLane) {
            inputStackA = input;
            primaryOutputStackA = primaryOutput;
            secondaryOutputStackA = secondaryOutput;
            progressA = progress;
        } else {
            inputStackB = input;
            primaryOutputStackB = primaryOutput;
            secondaryOutputStackB = secondaryOutput;
            progressB = progress;
        }

        return new LaneTickResult(availableEnergy, changed, working);
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
        return slot >= 2;
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
        output.putInt("ProgressA", progressA);
        output.putInt("ProgressB", progressB);
        output.store("InputA", ItemStack.OPTIONAL_CODEC, inputStackA);
        output.store("InputB", ItemStack.OPTIONAL_CODEC, inputStackB);
        output.store("PrimaryOutputA", ItemStack.OPTIONAL_CODEC, primaryOutputStackA);
        output.store("PrimaryOutputB", ItemStack.OPTIONAL_CODEC, primaryOutputStackB);
        output.store("SecondaryOutputA", ItemStack.OPTIONAL_CODEC, secondaryOutputStackA);
        output.store("SecondaryOutputB", ItemStack.OPTIONAL_CODEC, secondaryOutputStackB);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energyHandler.deserialize(input);
        progressA = input.getIntOr("ProgressA", 0);
        progressB = input.getIntOr("ProgressB", 0);
        inputStackA = input.read("InputA", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        inputStackB = input.read("InputB", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        primaryOutputStackA = input.read("PrimaryOutputA", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        primaryOutputStackB = input.read("PrimaryOutputB", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        secondaryOutputStackA = input.read("SecondaryOutputA", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        secondaryOutputStackB = input.read("SecondaryOutputB", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.scalarpower.double_macerator");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new DoubleMaceratorMenu(id, inv, this, new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> progressA;
                    case 1 -> progressB;
                    case 2 -> RECIPE_TIME;
                    case 3 -> (int) energyHandler.getAmountAsLong();
                    case 4 -> (int) energyHandler.getCapacityAsLong();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> progressA = value;
                    case 1 -> progressB = value;
                    case 3 -> energyHandler.set(value);
                    default -> {
                    }
                }
            }

            @Override
            public int getCount() {
                return 5;
            }
        });
    }

    @Override
    public int getContainerSize() {
        return 6;
    }

    @Override
    public boolean isEmpty() {
        return inputStackA.isEmpty()
                && inputStackB.isEmpty()
                && primaryOutputStackA.isEmpty()
                && primaryOutputStackB.isEmpty()
                && secondaryOutputStackA.isEmpty()
                && secondaryOutputStackB.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return switch (slot) {
            case 0 -> inputStackA;
            case 1 -> inputStackB;
            case 2 -> primaryOutputStackA;
            case 3 -> primaryOutputStackB;
            case 4 -> secondaryOutputStackA;
            case 5 -> secondaryOutputStackB;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemStack stack = getItem(slot);
        if (!stack.isEmpty()) {
            ItemStack split = stack.split(count);
            setSlot(slot, stack);
            setChanged();
            return split;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = getItem(slot);
        setSlot(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == 0 || slot == 1) {
            if (slot == 0) {
                inputStackA = stack;
                progressA = 0;
            } else {
                inputStackB = stack;
                progressB = 0;
            }
        } else if (slot >= 2 && slot <= 5) {
            setSlot(slot, stack);
        }

        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
    }

    private void setSlot(int slot, ItemStack stack) {
        switch (slot) {
            case 0 -> inputStackA = stack;
            case 1 -> inputStackB = stack;
            case 2 -> primaryOutputStackA = stack;
            case 3 -> primaryOutputStackB = stack;
            case 4 -> secondaryOutputStackA = stack;
            case 5 -> secondaryOutputStackB = stack;
            default -> {
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        inputStackA = ItemStack.EMPTY;
        inputStackB = ItemStack.EMPTY;
        primaryOutputStackA = ItemStack.EMPTY;
        primaryOutputStackB = ItemStack.EMPTY;
        secondaryOutputStackA = ItemStack.EMPTY;
        secondaryOutputStackB = ItemStack.EMPTY;
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

    private record LaneTickResult(int availableEnergy, boolean changed, boolean working) {
    }
}

