package site.scalarstudios.scalarpower.block.machine.grinder;

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
import site.scalarstudios.scalarpower.recipe.ExternalRecipeCompat;
import site.scalarstudios.scalarpower.power.NeoEnergyTransferUtil;
import site.scalarstudios.scalarpower.recipe.GrindingRecipe;
import site.scalarstudios.scalarpower.recipe.ScalarPowerRecipes;

import java.util.Optional;

public class DoubleGrinderBlockEntity extends BlockEntity implements Container, MenuProvider {
    private static final int ENERGY_CAPACITY = MachineUtils.STEEL_MACHINE_CAPACITY;
    private static final int ENERGY_PER_TICK_PER_LANE = MachineUtils.STEEL_MACHINE_SPU_PER_TICK_PER_LANE;
    private static final int PULL_PER_SIDE = MachineUtils.STEEL_MACHINE_SPU_PER_SIDE;
    private static final int RECIPE_TIME = MachineUtils.STEEL_MACHINE_TIME_PER_CRAFT;

    private ItemStack inputStackA = ItemStack.EMPTY;
    private ItemStack inputStackB = ItemStack.EMPTY;
    private ItemStack outputStackA = ItemStack.EMPTY;
    private ItemStack outputStackB = ItemStack.EMPTY;
    private int progressA;
    private int progressB;
    private final SimpleEnergyHandler energyHandler = new SimpleEnergyHandler(ENERGY_CAPACITY, ENERGY_CAPACITY, ENERGY_CAPACITY, 0) {
        @Override
        protected void onEnergyChanged(int previousAmount) {
            setChanged();
        }
    };

    public DoubleGrinderBlockEntity(BlockPos pos, BlockState blockState) {
        super(ScalarPowerBlockEntities.DOUBLE_GRINDER.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DoubleGrinderBlockEntity blockEntity) {
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

        if (state.hasProperty(DoubleGrinderBlock.LIT) && state.getValue(DoubleGrinderBlock.LIT) != isWorking) {
            level.setBlock(pos, state.setValue(DoubleGrinderBlock.LIT, isWorking), 3);
        }
    }

    private LaneTickResult processLane(Level level, boolean firstLane, int availableEnergy) {
        ItemStack input = firstLane ? inputStackA : inputStackB;
        ItemStack output = firstLane ? outputStackA : outputStackB;
        int progress = firstLane ? progressA : progressB;

        boolean changed = false;
        boolean working = false;

        Optional<RecipeHolder<GrindingRecipe>> recipeHolder = findRecipe(input);
        ItemStack result = recipeHolder
                .map(holder -> holder.value().assemble(new SingleRecipeInput(input)))
                .orElse(ItemStack.EMPTY);

        if (!result.isEmpty() && canOutput(output, result) && availableEnergy >= ENERGY_PER_TICK_PER_LANE) {
            availableEnergy -= ENERGY_PER_TICK_PER_LANE;
            progress++;
            changed = true;
            working = true;

            if (progress >= RECIPE_TIME) {
                input.shrink(1);
                int produced = result.getCount();
                GrindingRecipe recipe = recipeHolder.get().value();
                if (level.getRandom().nextFloat() < recipe.bonusChance()) {
                    produced += recipe.bonusCount();
                }
                produced = Math.min(produced, getMaxAddable(output, result));

                if (output.isEmpty()) {
                    output = result.copyWithCount(produced);
                } else {
                    output.grow(produced);
                }
                progress = 0;
                changed = true;
            }
        } else if (progress > 0) {
            progress = Math.max(0, progress - 2);
            changed = true;
        }

        if (firstLane) {
            inputStackA = input;
            outputStackA = output;
            progressA = progress;
        } else {
            inputStackB = input;
            outputStackB = output;
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

    public boolean canGrind(ItemStack stack) {
        return !stack.isEmpty();
    }

    private boolean isOutputSlot(int slot) {
        return slot == 2 || slot == 3;
    }

    private Optional<RecipeHolder<GrindingRecipe>> findRecipe(ItemStack stack) {
        if (stack.isEmpty() || !(level instanceof ServerLevel serverLevel)) {
            return Optional.empty();
        }

        SingleRecipeInput input = new SingleRecipeInput(stack);
        Optional<RecipeHolder<GrindingRecipe>> byType = serverLevel.recipeAccess().getRecipeFor(
                ScalarPowerRecipes.GRINDING_RECIPE_TYPE,
                input,
                serverLevel);
        if (byType.isPresent()) {
            return byType;
        }

        RecipeManager recipeManager = serverLevel.recipeAccess();
        Optional<RecipeHolder<GrindingRecipe>> fallback = recipeManager.getRecipes().stream()
                .filter(holder -> holder.value().getType() == ScalarPowerRecipes.GRINDING_RECIPE_TYPE)
                .map(holder -> (RecipeHolder<GrindingRecipe>) holder)
                .filter(holder -> holder.value().matches(input, serverLevel))
                .findFirst();

        if (fallback.isPresent()) {
            return fallback;
        }

        return ExternalRecipeCompat.findExternalGrindingRecipe(serverLevel, stack);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        energyHandler.serialize(output);
        output.putInt("ProgressA", progressA);
        output.putInt("ProgressB", progressB);
        output.store("InputA", ItemStack.OPTIONAL_CODEC, inputStackA);
        output.store("InputB", ItemStack.OPTIONAL_CODEC, inputStackB);
        output.store("OutputA", ItemStack.OPTIONAL_CODEC, outputStackA);
        output.store("OutputB", ItemStack.OPTIONAL_CODEC, outputStackB);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energyHandler.deserialize(input);
        progressA = input.getIntOr("ProgressA", 0);
        progressB = input.getIntOr("ProgressB", 0);
        inputStackA = input.read("InputA", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        inputStackB = input.read("InputB", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        outputStackA = input.read("OutputA", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        outputStackB = input.read("OutputB", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.scalarpower.double_grinder");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new DoubleGrinderMenu(id, inv, this, new ContainerData() {
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
        return 4;
    }

    @Override
    public boolean isEmpty() {
        return inputStackA.isEmpty() && inputStackB.isEmpty() && outputStackA.isEmpty() && outputStackB.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return switch (slot) {
            case 0 -> inputStackA;
            case 1 -> inputStackB;
            case 2 -> outputStackA;
            case 3 -> outputStackB;
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
        } else if (slot == 2 || slot == 3) {
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
            case 2 -> outputStackA = stack;
            case 3 -> outputStackB = stack;
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
        outputStackA = ItemStack.EMPTY;
        outputStackB = ItemStack.EMPTY;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return !isOutputSlot(slot) && canGrind(stack);
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

