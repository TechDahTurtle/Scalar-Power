package site.scalarstudios.scalarpower.content.wire;

import site.scalarstudios.scalarpower.power.NeoEnergyTransferUtil;
import site.scalarstudios.scalarpower.block.ScalarPowerBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;

public class CopperWireBlockEntity extends BlockEntity {
    private static final int ENERGY_CAPACITY = 2000;
    private static final int PUSH_PER_SIDE = 90;

    private final net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler energyHandler = new net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler(ENERGY_CAPACITY, ENERGY_CAPACITY, ENERGY_CAPACITY, 0) {
        @Override
        protected void onEnergyChanged(int previousAmount) {
            setChanged();
        }
    };

    public CopperWireBlockEntity(BlockPos pos, BlockState blockState) {
        super(ScalarPowerBlockEntities.COPPER_WIRE.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CopperWireBlockEntity blockEntity) {
        if (level == null || level.isClientSide() || blockEntity.energyHandler.getAmountAsLong() <= 0) {
            return;
        }

        int moved = NeoEnergyTransferUtil.pushEnergy(level, pos, blockEntity.energyHandler, PUSH_PER_SIDE);
        if (moved > 0) {
            blockEntity.setChanged();
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        energyHandler.serialize(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energyHandler.deserialize(input);
    }

    public EnergyHandler getEnergyHandler(Direction side) { return energyHandler; }
}
