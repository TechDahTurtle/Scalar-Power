package com.scalarpower.scalarpower.content.grinder;

import com.scalarpower.scalarpower.power.PowerNode;
import com.scalarpower.scalarpower.power.PowerUtil;
import com.scalarpower.scalarpower.registry.ScalarPowerEntities;
import com.scalarpower.scalarpower.registry.ScalarPowerItems;
import com.scalarpower.scalarpower.registry.ScalarPowerTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class GrinderBlockEntity extends BlockEntity implements Container, PowerNode, MenuProvider {
    private static final int ENERGY_CAPACITY = 12000;
    private static final int ENERGY_PER_TICK = 20;
    private static final int RECIPE_TIME = 100;
    private static final int PULL_PER_SIDE = 60;

    private ItemStack inputStack = ItemStack.EMPTY;
    private ItemStack outputStack = ItemStack.EMPTY;
    private int energy;
    private int progress;

    public GrinderBlockEntity(BlockPos pos, BlockState blockState) {
        super(ScalarPowerEntities.GRINDER.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, GrinderBlockEntity blockEntity) {
        if (level == null || level.isClientSide()) {
            return;
        }

        boolean changed = false;
        boolean isWorking = false;

        if (blockEntity.energy < ENERGY_CAPACITY) {
            int pulled = PowerUtil.pullEnergy(level, pos, blockEntity, PULL_PER_SIDE);
            changed |= pulled > 0;
        }

        ItemStack result = blockEntity.getGrindingOutput(blockEntity.inputStack);
        if (!result.isEmpty() && blockEntity.canOutput(blockEntity.outputStack, result) && blockEntity.energy >= ENERGY_PER_TICK) {
            blockEntity.energy -= ENERGY_PER_TICK;
            blockEntity.progress++;
            changed = true;
            isWorking = true;

            if (blockEntity.progress >= RECIPE_TIME) {
                blockEntity.inputStack.shrink(1);
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

        if (state.hasProperty(GrinderBlock.LIT) && state.getValue(GrinderBlock.LIT) != isWorking) {
            level.setBlock(pos, state.setValue(GrinderBlock.LIT, isWorking), 3);
        }
    }

    private boolean canOutput(ItemStack current, ItemStack recipe) {
        if (current.isEmpty()) return true;
        if (!ItemStack.isSameItemSameComponents(current, recipe)) return false;
        return current.getCount() + recipe.getCount() <= current.getMaxStackSize();
    }

    public ItemStack getGrindingOutput(ItemStack stack) {
        if (stack.is(ScalarPowerTags.Items.RAW_IRON_GRINDABLE)) return new ItemStack(ScalarPowerItems.IRON_DUST.get(), 2);
        if (stack.is(ScalarPowerTags.Items.RAW_GOLD_GRINDABLE)) return new ItemStack(ScalarPowerItems.GOLD_DUST.get(), 2);
        if (stack.is(ScalarPowerTags.Items.RAW_COPPER_GRINDABLE)) return new ItemStack(ScalarPowerItems.COPPER_DUST.get(), 2);
        return ItemStack.EMPTY;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("Energy", energy);
        output.putInt("Progress", progress);
        output.store("Input", ItemStack.OPTIONAL_CODEC, inputStack);
        output.store("Output", ItemStack.OPTIONAL_CODEC, outputStack);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energy = input.getIntOr("Energy", 0);
        progress = input.getIntOr("Progress", 0);
        inputStack = input.read("Input", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        outputStack = input.read("Output", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    public Component getDisplayName() { return Component.literal("Grinder"); }
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new GrinderMenu(id, inv, this, new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> progress;
                    case 1 -> RECIPE_TIME;
                    case 2 -> energy;
                    case 3 -> ENERGY_CAPACITY;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> progress = value;
                    case 2 -> energy = value;
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
    public int getContainerSize() { return 2; }
    @Override
    public boolean isEmpty() { return inputStack.isEmpty() && outputStack.isEmpty(); }
    @Override
    public ItemStack getItem(int slot) { return slot == 0 ? inputStack : (slot == 1 ? outputStack : ItemStack.EMPTY); }
    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemStack stack = getItem(slot);
        if (!stack.isEmpty()) {
            ItemStack split = stack.split(count);
            if (slot == 0) inputStack = stack;
            else if (slot == 1) outputStack = stack;
            setChanged();
            return split;
        }
        return ItemStack.EMPTY;
    }
    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = getItem(slot);
        if (slot == 0) inputStack = ItemStack.EMPTY;
        else if (slot == 1) outputStack = ItemStack.EMPTY;
        return stack;
    }
    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == 0) {
            inputStack = stack;
            progress = 0;
        } else if (slot == 1) {
            outputStack = stack;
        }
        if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize());
        setChanged();
    }
    @Override
    public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override
    public void clearContent() { inputStack = ItemStack.EMPTY; outputStack = ItemStack.EMPTY; }

    @Override
    public int getEnergyStored() { return energy; }
    @Override
    public int getEnergyCapacity() { return ENERGY_CAPACITY; }
    @Override
    public int receiveEnergy(int amount, boolean simulate) {
        int accepted = Math.min(amount, ENERGY_CAPACITY - energy);
        if (!simulate) {
            energy += accepted;
            setChanged();
        }
        return accepted;
    }
    @Override
    public int extractEnergy(int amount, boolean simulate) { return 0; }
    @Override
    public boolean canConnectPower(Direction side) { return true; }

    public int getProgress() { return progress; }
    public int getMaxProgress() { return RECIPE_TIME; }
    public int getEnergy() { return energy; }
}



