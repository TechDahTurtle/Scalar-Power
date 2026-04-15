package site.scalarstudios.scalarpower.block.machine.poweredfurnace;

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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import site.scalarstudios.scalarpower.block.ScalarPowerBlockEntities;
import site.scalarstudios.scalarpower.block.machine.MachineUtils;
import site.scalarstudios.scalarpower.item.ScalarPowerItems;
import site.scalarstudios.scalarpower.power.NeoEnergyTransferUtil;

import java.util.Optional;

public class DoublePoweredFurnaceBlockEntity extends BlockEntity implements Container, MenuProvider {
    private static final int ENERGY_CAPACITY = MachineUtils.STEEL_MACHINE_CAPACITY;
    private static final int ENERGY_PER_TICK_PER_LANE = MachineUtils.STEEL_MACHINE_SPU_PER_TICK_PER_LANE;
    private static final int PULL_PER_SIDE = MachineUtils.STEEL_MACHINE_SPU_PER_SIDE;
    private static final int DEFAULT_RECIPE_TIME = MachineUtils.STEEL_POWERED_FURNACE_TIME_PER_CRAFT;

    private ItemStack inputStackA = ItemStack.EMPTY;
    private ItemStack inputStackB = ItemStack.EMPTY;
    private ItemStack outputStackA = ItemStack.EMPTY;
    private ItemStack outputStackB = ItemStack.EMPTY;
    private int progressA;
    private int progressB;
    private int recipeTimeA = DEFAULT_RECIPE_TIME;
    private int recipeTimeB = DEFAULT_RECIPE_TIME;
    private final SimpleEnergyHandler energyHandler = new SimpleEnergyHandler(ENERGY_CAPACITY, ENERGY_CAPACITY, ENERGY_CAPACITY, 0) {
        @Override
        protected void onEnergyChanged(int previousAmount) {
            setChanged();
        }
    };

    public DoublePoweredFurnaceBlockEntity(BlockPos pos, BlockState blockState) {
        super(ScalarPowerBlockEntities.DOUBLE_POWERED_FURNACE.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DoublePoweredFurnaceBlockEntity blockEntity) {
        if (level == null || level.isClientSide()) {
            return;
        }

        boolean changed = false;
        boolean isWorking = false;

        if (blockEntity.energyHandler.getAmountAsLong() < blockEntity.energyHandler.getCapacityAsLong()) {
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

        if (state.hasProperty(DoublePoweredFurnaceBlock.LIT) && state.getValue(DoublePoweredFurnaceBlock.LIT) != isWorking) {
            level.setBlock(pos, state.setValue(DoublePoweredFurnaceBlock.LIT, isWorking), 3);
        }
    }

    private LaneTickResult processLane(Level level, boolean firstLane, int availableEnergy) {
        ItemStack input = firstLane ? inputStackA : inputStackB;
        ItemStack output = firstLane ? outputStackA : outputStackB;
        int progress = firstLane ? progressA : progressB;
        int recipeTime = firstLane ? recipeTimeA : recipeTimeB;

        boolean changed = false;
        boolean working = false;

        ItemStack result = getProcessingResult(level, input);
        if (!result.isEmpty() && canOutput(output, result)) {
            int newRecipeTime = getProcessingTime(level, input);
            if (newRecipeTime <= 0) {
                newRecipeTime = DEFAULT_RECIPE_TIME;
            }
            if (recipeTime != newRecipeTime) {
                recipeTime = newRecipeTime;
                if (progress > recipeTime) {
                    progress = recipeTime;
                }
                changed = true;
            }

            if (availableEnergy >= ENERGY_PER_TICK_PER_LANE) {
                availableEnergy -= ENERGY_PER_TICK_PER_LANE;
                progress++;
                changed = true;
                working = true;
            }

            if (progress >= recipeTime) {
                input.shrink(1);
                if (output.isEmpty()) {
                    output = result.copy();
                } else {
                    output.grow(result.getCount());
                }
                progress = 0;
                changed = true;
            }
        } else {
            if (progress > 0) {
                progress = Math.max(0, progress - 2);
                changed = true;
            }
            if (recipeTime != DEFAULT_RECIPE_TIME) {
                recipeTime = DEFAULT_RECIPE_TIME;
                changed = true;
            }
        }

        if (firstLane) {
            inputStackA = input;
            outputStackA = output;
            progressA = progress;
            recipeTimeA = recipeTime;
        } else {
            inputStackB = input;
            outputStackB = output;
            progressB = progress;
            recipeTimeB = recipeTime;
        }

        return new LaneTickResult(availableEnergy, changed, working);
    }

    private Optional<RecipeHolder<SmeltingRecipe>> findRecipe(Level level, ItemStack input) {
        if (input.isEmpty() || !(level instanceof ServerLevel serverLevel)) {
            return Optional.empty();
        }
        return serverLevel.recipeAccess().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(input), serverLevel);
    }

    private ItemStack getProcessingResult(Level level, ItemStack input) {
        ItemStack manual = getManualDustResult(input);
        if (!manual.isEmpty()) {
            return manual;
        }
        return findRecipe(level, input)
                .map(holder -> holder.value().assemble(new SingleRecipeInput(input)))
                .orElse(ItemStack.EMPTY);
    }

    private int getProcessingTime(Level level, ItemStack input) {
        if (!getManualDustResult(input).isEmpty()) {
            return DEFAULT_RECIPE_TIME;
        }
        return findRecipe(level, input)
                .map(holder -> Math.max(1, (holder.value().cookingTime() + 1) / 2))
                .orElse(DEFAULT_RECIPE_TIME);
    }

    private ItemStack getManualDustResult(ItemStack input) {
        if (input.is(ScalarPowerItems.IRON_DUST.get())) {
            return new ItemStack(Items.IRON_INGOT);
        }
        if (input.is(ScalarPowerItems.GOLD_DUST.get())) {
            return new ItemStack(Items.GOLD_INGOT);
        }
        if (input.is(ScalarPowerItems.COPPER_DUST.get())) {
            return new ItemStack(Items.COPPER_INGOT);
        }
        return ItemStack.EMPTY;
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

    public boolean canSmelt(ItemStack stack) {
        if (stack.isEmpty() || level == null) {
            return false;
        }
        if (!getManualDustResult(stack).isEmpty()) {
            return true;
        }
        return level.recipeAccess().propertySet(RecipePropertySet.FURNACE_INPUT).test(stack);
    }

    private boolean isOutputSlot(int slot) {
        return slot == 2 || slot == 3;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        energyHandler.serialize(output);
        output.putInt("ProgressA", progressA);
        output.putInt("ProgressB", progressB);
        output.putInt("RecipeTimeA", recipeTimeA);
        output.putInt("RecipeTimeB", recipeTimeB);
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
        recipeTimeA = input.getIntOr("RecipeTimeA", DEFAULT_RECIPE_TIME);
        recipeTimeB = input.getIntOr("RecipeTimeB", DEFAULT_RECIPE_TIME);
        inputStackA = input.read("InputA", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        inputStackB = input.read("InputB", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        outputStackA = input.read("OutputA", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        outputStackB = input.read("OutputB", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.scalarpower.double_powered_furnace");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new DoublePoweredFurnaceMenu(id, inv, this, new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> progressA;
                    case 1 -> recipeTimeA;
                    case 2 -> progressB;
                    case 3 -> recipeTimeB;
                    case 4 -> (int) energyHandler.getAmountAsLong();
                    case 5 -> (int) energyHandler.getCapacityAsLong();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> progressA = value;
                    case 1 -> recipeTimeA = value;
                    case 2 -> progressB = value;
                    case 3 -> recipeTimeB = value;
                    case 4 -> energyHandler.set(value);
                    default -> {
                    }
                }
            }

            @Override
            public int getCount() {
                return 6;
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
                recipeTimeA = DEFAULT_RECIPE_TIME;
            } else {
                inputStackB = stack;
                progressB = 0;
                recipeTimeB = DEFAULT_RECIPE_TIME;
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
        return !isOutputSlot(slot) && canSmelt(stack);
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

