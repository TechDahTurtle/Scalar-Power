package site.scalarstudios.scalarpower.block.machine.cable.gold;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import site.scalarstudios.scalarpower.block.ScalarPowerBlockEntities;
import site.scalarstudios.scalarpower.block.machine.MachineUtils;
import site.scalarstudios.scalarpower.block.machine.cable.BaseCableBlockEntity;
import site.scalarstudios.scalarpower.power.NeoEnergyTransferUtil;

public class GoldCableBlockEntity extends BaseCableBlockEntity {
    private static final int ENERGY_CAPACITY = MachineUtils.CABLE_BASE_CAPACITY * MachineUtils.CABLE_T2_MULTIPLIER;
    private static final int PUSH_PER_SIDE = MachineUtils.CABLE_BASE_THROUGHPUT * MachineUtils.CABLE_T2_MULTIPLIER;;

    private final net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler energyHandler = new net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler(ENERGY_CAPACITY, ENERGY_CAPACITY, ENERGY_CAPACITY, 0) {
        @Override
        protected void onEnergyChanged(int previousAmount) {
            setChanged();
        }
    };

    public GoldCableBlockEntity(BlockPos pos, BlockState blockState) {
        super(ScalarPowerBlockEntities.GOLD_CABLE.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, GoldCableBlockEntity blockEntity) {
        if (level == null || level.isClientSide()) {
            return;
        }

        int pulled = NeoEnergyTransferUtil.pullEnergy(level, pos, blockEntity.energyHandler, PUSH_PER_SIDE);
        int pushed = NeoEnergyTransferUtil.pushEnergy(level, pos, blockEntity.energyHandler, PUSH_PER_SIDE);
        if (pulled > 0 || pushed > 0) {
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

    public boolean hasCharge() {
        return energyHandler.getAmountAsLong() > 0;
    }

    public EnergyHandler getEnergyHandler(Direction side) { return energyHandler; }
}

