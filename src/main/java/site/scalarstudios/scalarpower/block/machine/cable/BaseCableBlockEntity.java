package site.scalarstudios.scalarpower.block.machine.cable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import site.scalarstudios.scalarpower.block.transport.BaseTransportBlockEntity;

public abstract class BaseCableBlockEntity extends BaseTransportBlockEntity {
    protected BaseCableBlockEntity(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public boolean canPhysicallyConnect(Level level, BlockPos neighborPos, Direction incomingSide) {
        return level.getCapability(Capabilities.Energy.BLOCK, neighborPos, incomingSide) != null;
    }

    protected final EnergyHandler exposedEnergyHandler(Direction side, EnergyHandler energyHandler) {
        return exposesSide(side) ? energyHandler : null;
    }
}
