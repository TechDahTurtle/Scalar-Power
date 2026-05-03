package site.scalarstudios.scalarpower.block.machine.cable.fiberglass;

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

public class FiberGlassCableBlockEntity extends BaseCableBlockEntity {
    private static final int ENERGY_CAPACITY = MachineUtils.CABLE_BASE_CAPACITY * MachineUtils.CABLE_T3_MULTIPLIER;
    private static final int PUSH_PER_SIDE = MachineUtils.CABLE_BASE_THROUGHPUT * MachineUtils.CABLE_T3_MULTIPLIER;

    private final net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler energyHandler =
            new net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler(ENERGY_CAPACITY, ENERGY_CAPACITY, ENERGY_CAPACITY, 0) {
                @Override
                protected void onEnergyChanged(int previousAmount) {
                    setChanged();
                }
            };

    public FiberGlassCableBlockEntity(BlockPos pos, BlockState blockState) {
        super(ScalarPowerBlockEntities.FIBER_GLASS_CABLE.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FiberGlassCableBlockEntity blockEntity) {
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

    public EnergyHandler getEnergyHandler(Direction side) { return energyHandler; }
}

