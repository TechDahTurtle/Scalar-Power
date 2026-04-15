package site.scalarstudios.scalarpower.block.machine.battery;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import site.scalarstudios.scalarpower.block.ScalarPowerBlockEntities;
import site.scalarstudios.scalarpower.block.machine.MachineUtils;

public class SteelBatteryBlockEntity extends BatteryBlockEntity {
    private static final int ENERGY_CAPACITY = MachineUtils.STEEL_BATTERY_CAPACITY;
    private static final int ENERGY_TRANSFER_PER_SIDE = MachineUtils.STEEL_BATTERY_SPU_PER_SIDE;
    private static final String CONTAINER_TRANSLATION_KEY = "container.scalarpower.steel_battery";

    public SteelBatteryBlockEntity(BlockPos pos, BlockState blockState) {
        super(
                ScalarPowerBlockEntities.STEEL_BATTERY.get(),
                pos,
                blockState,
                ENERGY_CAPACITY,
                ENERGY_TRANSFER_PER_SIDE,
                CONTAINER_TRANSLATION_KEY);
    }
}

