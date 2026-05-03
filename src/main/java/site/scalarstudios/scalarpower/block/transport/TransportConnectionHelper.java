package site.scalarstudios.scalarpower.block.transport;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class TransportConnectionHelper {

    public static boolean isSideEnabled(LevelReader level, BlockPos pos, Direction side) {
        if (!(level instanceof Level actualLevel)) {
            return true;
        }

        BlockEntity blockEntity = actualLevel.getBlockEntity(pos);
        return !(blockEntity instanceof BaseTransportBlockEntity transport) || transport.exposesSide(side);
    }
}

