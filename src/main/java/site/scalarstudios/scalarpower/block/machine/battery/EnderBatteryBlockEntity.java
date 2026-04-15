package site.scalarstudios.scalarpower.block.machine.battery;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import site.scalarstudios.scalarpower.block.ScalarPowerBlockEntities;
import site.scalarstudios.scalarpower.block.machine.MachineUtils;
import site.scalarstudios.scalarpower.power.NeoEnergyTransferUtil;

public class EnderBatteryBlockEntity extends BatteryBlockEntity {
    private static final int ENERGY_CAPACITY = MachineUtils.ENDER_BATTERY_CAPACITY;
    private static final int ENERGY_TRANSFER_PER_SIDE = MachineUtils.ENDER_BATTERY_SPU_PER_SIDE;
    private static final String CONTAINER_TRANSLATION_KEY = "container.scalarpower.ender_battery";

    public EnderBatteryBlockEntity(BlockPos pos, BlockState blockState) {
        super(
                ScalarPowerBlockEntities.ENDER_BATTERY.get(),
                pos,
                blockState,
                ENERGY_CAPACITY,
                ENERGY_TRANSFER_PER_SIDE,
                CONTAINER_TRANSLATION_KEY);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, EnderBatteryBlockEntity blockEntity) {
        if (level == null || level.isClientSide()) {
            return;
        }

        blockEntity.syncFromPoolStorage();
        if (blockEntity.energyHandler.getAmountAsLong() <= 0) {
            return;
        }

        int moved = NeoEnergyTransferUtil.pushEnergy(level, pos, blockEntity.energyHandler, blockEntity.energyTransferPerSide);
        if (moved > 0) {
            blockEntity.setChanged();
        }
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new EnderBatteryMenu(id, inv, this, new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> getMenuEnergy();
                    case 1 -> getMenuEnergyCapacity();
                    case 2 -> hasInfiniteEnergy() ? 1 : 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                if (index == 0) {
                    setMenuEnergy(value);
                }
            }

            @Override
            public int getCount() {
                return DEFAULT_MENU_DATA_COUNT;
            }
        });
    }

    @Override
    public net.neoforged.neoforge.transfer.energy.EnergyHandler getEnergyHandler(Direction side) {
        syncFromPoolStorage();
        return super.getEnergyHandler(side);
    }

    @Override
    protected void onEnergyChanged(int previousAmount) {
        super.onEnergyChanged(previousAmount);
        syncToPoolStorage();
    }

    @Override
    protected void saveEnergy(ValueOutput output) {
    }

    @Override
    protected void loadEnergy(ValueInput input) {
    }

    @Override
    protected int getMenuEnergy() {
        return getSharedEnergy();
    }

    private int getSharedEnergy() {
        if (level instanceof ServerLevel serverLevel) {
            return EnderBatteryPoolSavedData.get(serverLevel).getEnergy();
        }
        return clampToInt(energyHandler.getAmountAsLong());
    }

    private void syncFromPoolStorage() {
        int sharedEnergy = getSharedEnergy();
        if (clampToInt(energyHandler.getAmountAsLong()) != sharedEnergy) {
            energyHandler.set(sharedEnergy);
        }
    }

    private void syncToPoolStorage() {
        if (level instanceof ServerLevel serverLevel) {
            EnderBatteryPoolSavedData.get(serverLevel).setEnergy(clampToInt(energyHandler.getAmountAsLong()));
        }
    }
}




