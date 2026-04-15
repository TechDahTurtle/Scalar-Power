package site.scalarstudios.scalarpower.block.machine.battery;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import site.scalarstudios.scalarpower.block.ScalarPowerBlockEntities;
import site.scalarstudios.scalarpower.power.NeoEnergyTransferUtil;

public class CreativeBatteryBlockEntity extends BatteryBlockEntity {
    private static final int ENERGY_CAPACITY = Integer.MAX_VALUE;
    private static final int ENERGY_TRANSFER_PER_SIDE = 100_000_000;
    private static final String CONTAINER_TRANSLATION_KEY = "container.scalarpower.creative_battery";

    public CreativeBatteryBlockEntity(BlockPos pos, BlockState blockState) {
        super(
                ScalarPowerBlockEntities.CREATIVE_BATTERY.get(),
                pos,
                blockState,
                ENERGY_CAPACITY,
                ENERGY_TRANSFER_PER_SIDE,
                CONTAINER_TRANSLATION_KEY);
        refillToFull();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CreativeBatteryBlockEntity blockEntity) {
        if (level == null || level.isClientSide()) {
            return;
        }

        blockEntity.refillToFull();
        int moved = NeoEnergyTransferUtil.pushEnergy(level, pos, blockEntity.energyHandler, blockEntity.energyTransferPerSide);
        if (moved > 0) {
            blockEntity.setChanged();
        }
        blockEntity.refillToFull();
    }

    @Override
    public net.neoforged.neoforge.transfer.energy.EnergyHandler getEnergyHandler(Direction side) {
        refillToFull();
        return super.getEnergyHandler(side);
    }

    @Override
    protected void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
        super.loadAdditional(input);
        refillToFull();
    }

    @Override
    protected boolean hasInfiniteEnergy() {
        return true;
    }

    private void refillToFull() {
        energyHandler.set((int) energyHandler.getCapacityAsLong());
    }
}

