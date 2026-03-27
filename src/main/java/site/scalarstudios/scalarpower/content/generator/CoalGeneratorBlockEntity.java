package site.scalarstudios.scalarpower.content.generator;

import site.scalarstudios.scalarpower.power.NeoEnergyTransferUtil;
import site.scalarstudios.scalarpower.block.ScalarPowerBlockEntities;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;

public class CoalGeneratorBlockEntity extends BlockEntity implements Container, MenuProvider {
    private static final int ENERGY_CAPACITY = 20000;
    private static final int ENERGY_PER_TICK = 40;
    private static final int PUSH_PER_SIDE = 120;

    private ItemStack fuelStack = ItemStack.EMPTY;
    private int burnTime;
    private int burnTimeTotal;
    private final net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler energyHandler = new net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler(ENERGY_CAPACITY, ENERGY_CAPACITY, ENERGY_CAPACITY, 0) {
        @Override
        protected void onEnergyChanged(int previousAmount) {
            setChanged();
        }
    };

    public CoalGeneratorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ScalarPowerBlockEntities.COAL_GENERATOR.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CoalGeneratorBlockEntity blockEntity) {
        if (level == null || level.isClientSide()) {
            return;
        }

        boolean changed = false;

        if (blockEntity.burnTime > 0) {
            blockEntity.burnTime--;
            int generated = Math.min(ENERGY_PER_TICK, (int)(blockEntity.energyHandler.getCapacityAsLong() - blockEntity.energyHandler.getAmountAsLong()));
            if (generated > 0) {
                blockEntity.energyHandler.set((int)(blockEntity.energyHandler.getAmountAsLong() + generated));
                changed = true;
            }
        }

        if (blockEntity.burnTime <= 0) {
            int newBurnTime = getFuelTicks(blockEntity.fuelStack.getItem());
            if (!blockEntity.fuelStack.isEmpty() && newBurnTime > 0) {
                blockEntity.fuelStack.shrink(1);
                blockEntity.burnTime = newBurnTime;
                blockEntity.burnTimeTotal = newBurnTime;
                changed = true;
            }
        }

        if (blockEntity.energyHandler.getAmountAsLong() > 0) {
            int moved = NeoEnergyTransferUtil.pushEnergy(level, pos, blockEntity.energyHandler, PUSH_PER_SIDE);
            changed |= moved > 0;
        }

        if (changed) {
            blockEntity.setChanged();
        }

        boolean isWorking = blockEntity.burnTime > 0;
        if (state.hasProperty(CoalGeneratorBlock.LIT) && state.getValue(CoalGeneratorBlock.LIT) != isWorking) {
            level.setBlock(pos, state.setValue(CoalGeneratorBlock.LIT, isWorking), 3);
        }
    }

    private static int getFuelTicks(net.minecraft.world.item.Item item) {
        return (item == Items.COAL || item == Items.CHARCOAL) ? 1600 : 0;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        energyHandler.serialize(output);
        output.putInt("BurnTime", burnTime);
        output.putInt("BurnTimeTotal", burnTimeTotal);
        output.store("Fuel", ItemStack.OPTIONAL_CODEC, fuelStack);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energyHandler.deserialize(input);
        burnTime = input.getIntOr("BurnTime", 0);
        burnTimeTotal = input.getIntOr("BurnTimeTotal", 0);
        fuelStack = input.read("Fuel", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Coal Generator");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new CoalGeneratorMenu(id, inv, this, new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> burnTime;
                    case 1 -> burnTimeTotal;
                    case 2 -> (int)energyHandler.getAmountAsLong();
                    case 3 -> (int)energyHandler.getCapacityAsLong();
                    default -> 0;
                };
            }
            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> burnTime = value;
                    case 1 -> burnTimeTotal = value;
                    case 2 -> energyHandler.set(value);
                    default -> {}
                }
            }
            @Override
            public int getCount() { return 4; }
        });
    }

    @Override
    public int getContainerSize() { return 1; }
    @Override
    public boolean isEmpty() { return fuelStack.isEmpty(); }
    @Override
    public ItemStack getItem(int slot) { return slot == 0 ? fuelStack : ItemStack.EMPTY; }
    @Override
    public ItemStack removeItem(int slot, int count) {
        if (slot == 0) {
            ItemStack split = fuelStack.split(count);
            if (!split.isEmpty()) setChanged();
            return split;
        }
        return ItemStack.EMPTY;
    }
    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot == 0) {
            ItemStack stack = fuelStack;
            fuelStack = ItemStack.EMPTY;
            return stack;
        }
        return ItemStack.EMPTY;
    }
    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == 0) {
            fuelStack = stack;
            if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize());
            setChanged();
        }
    }
    @Override
    public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override
    public void clearContent() { fuelStack = ItemStack.EMPTY; }

    public EnergyHandler getEnergyHandler(Direction side) { return energyHandler; }

    public int getBurnTime() { return burnTime; }
    public int getBurnTimeTotal() { return burnTimeTotal; }
    public int getEnergy() { return (int)energyHandler.getAmountAsLong(); }
    public boolean isFuel(ItemStack stack) { return getFuelTicks(stack.getItem()) > 0; }
}



