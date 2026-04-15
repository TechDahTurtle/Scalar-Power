package site.scalarstudios.scalarpower.block.machine.poweredfurnace;

import site.scalarstudios.scalarpower.block.machine.MachineUtils;
import site.scalarstudios.scalarpower.power.NeoEnergyTransferUtil;
import site.scalarstudios.scalarpower.block.ScalarPowerBlockEntities;
import site.scalarstudios.scalarpower.item.ScalarPowerItems;
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

import java.util.Optional;

public class PoweredFurnaceBlockEntity extends BlockEntity implements Container, MenuProvider {
    private static final int ENERGY_CAPACITY = MachineUtils.BASIC_MACHINE_CAPACITY;
    private static final int ENERGY_PER_TICK = MachineUtils.BASIC_MACHINE_SPU_PER_TICK;
    private static final int DEFAULT_RECIPE_TIME = MachineUtils.POWERED_FURNACE_TIME_PER_CRAFT;
    private static final int PULL_PER_SIDE = MachineUtils.BASIC_MACHINE_SPU_PER_SIDE;

    private ItemStack inputStack = ItemStack.EMPTY;
    private ItemStack outputStack = ItemStack.EMPTY;
    private int progress;
    private int recipeTime = DEFAULT_RECIPE_TIME;
    private final net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler energyHandler = new net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler(ENERGY_CAPACITY, ENERGY_CAPACITY, ENERGY_CAPACITY, 0) {
        @Override
        protected void onEnergyChanged(int previousAmount) {
            setChanged();
        }
    };

    public PoweredFurnaceBlockEntity(BlockPos pos, BlockState blockState) {
        super(ScalarPowerBlockEntities.POWERED_FURNACE.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PoweredFurnaceBlockEntity blockEntity) {
        boolean changed = false;
        boolean isWorking = false;

        if (blockEntity.energyHandler.getAmountAsLong() < blockEntity.energyHandler.getCapacityAsLong()) {
            int pulled = NeoEnergyTransferUtil.pullEnergy(level, pos, blockEntity.energyHandler, PULL_PER_SIDE);
            changed |= pulled > 0;
        }

        ItemStack result = blockEntity.getProcessingResult(level, blockEntity.inputStack);

        if (!result.isEmpty() && blockEntity.canOutput(blockEntity.outputStack, result)) {
            int newRecipeTime = blockEntity.getProcessingTime(level, blockEntity.inputStack);
            if (newRecipeTime <= 0) {
                newRecipeTime = DEFAULT_RECIPE_TIME;
            }
            if (blockEntity.recipeTime != newRecipeTime) {
                blockEntity.recipeTime = newRecipeTime;
                if (blockEntity.progress > blockEntity.recipeTime) {
                    blockEntity.progress = blockEntity.recipeTime;
                }
                changed = true;
            }

            if (blockEntity.energyHandler.getAmountAsLong() >= ENERGY_PER_TICK) {
                blockEntity.energyHandler.set((int)(blockEntity.energyHandler.getAmountAsLong() - ENERGY_PER_TICK));
                blockEntity.progress++;
                changed = true;
                isWorking = true;
            }

            if (blockEntity.progress >= blockEntity.recipeTime) {
                blockEntity.inputStack.shrink(1);
                if (blockEntity.outputStack.isEmpty()) {
                    blockEntity.outputStack = result.copy();
                } else {
                    blockEntity.outputStack.grow(result.getCount());
                }
                blockEntity.progress = 0;
                changed = true;
            }
        } else {
            if (blockEntity.progress > 0) {
                blockEntity.progress = Math.max(0, blockEntity.progress - 2);
                changed = true;
            }
            if (blockEntity.recipeTime != DEFAULT_RECIPE_TIME) {
                blockEntity.recipeTime = DEFAULT_RECIPE_TIME;
                changed = true;
            }
        }

        if (changed) {
            blockEntity.setChanged();
        }

        if (state.hasProperty(PoweredFurnaceBlock.LIT) && state.getValue(PoweredFurnaceBlock.LIT) != isWorking) {
            level.setBlock(pos, state.setValue(PoweredFurnaceBlock.LIT, isWorking), 3);
        }
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
                .map(holder -> holder.value().cookingTime())
                .filter(time -> time > 0)
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
        // Works on both client and server so slot filtering stays responsive.
        return level.recipeAccess().propertySet(RecipePropertySet.FURNACE_INPUT).test(stack);
    }

    private boolean isOutputSlot(int slot) {
        return slot == 1;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        energyHandler.serialize(output);
        output.putInt("Progress", progress);
        output.putInt("RecipeTime", recipeTime);
        output.store("Input", ItemStack.OPTIONAL_CODEC, inputStack);
        output.store("Output", ItemStack.OPTIONAL_CODEC, outputStack);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energyHandler.deserialize(input);
        progress = input.getIntOr("Progress", 0);
        recipeTime = input.getIntOr("RecipeTime", DEFAULT_RECIPE_TIME);
        inputStack = input.read("Input", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        outputStack = input.read("Output", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.scalarpower.powered_furnace");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new PoweredFurnaceMenu(id, inv, this, new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> progress;
                    case 1 -> recipeTime;
                    case 2 -> (int)energyHandler.getAmountAsLong();
                    case 3 -> (int)energyHandler.getCapacityAsLong();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> progress = value;
                    case 1 -> recipeTime = value;
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
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return inputStack.isEmpty() && outputStack.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == 0 ? inputStack : (slot == 1 ? outputStack : ItemStack.EMPTY);
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemStack stack = getItem(slot);
        if (!stack.isEmpty()) {
            ItemStack split = stack.split(count);
            if (slot == 0) {
                inputStack = stack;
            } else if (slot == 1) {
                outputStack = stack;
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
            outputStack = ItemStack.EMPTY;
        }
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == 0) {
            inputStack = stack;
            progress = 0;
            recipeTime = DEFAULT_RECIPE_TIME;
        } else if (slot == 1) {
            outputStack = stack;
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
        outputStack = ItemStack.EMPTY;
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
}



