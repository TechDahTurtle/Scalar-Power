package site.scalarstudios.scalarpower.block.machine.cable;

import site.scalarstudios.scalarpower.block.transport.BaseTransportBlockEntity;

public abstract class BaseCableBlockEntity extends BaseTransportBlockEntity {
    protected BaseCableBlockEntity(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState blockState) {
        super(type, pos, blockState);
    }
}



