package site.scalarstudios.scalarpower.block.machine.battery;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import site.scalarstudios.scalarpower.block.ScalarPowerBlockEntities;

public class EnderBatteryBlock extends BatteryBlock {
    public static final MapCodec<EnderBatteryBlock> CODEC = simpleCodec(EnderBatteryBlock::new);

    public EnderBatteryBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnderBatteryBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> blockEntityType) {
        return createTickerHelper(
                blockEntityType,
                ScalarPowerBlockEntities.ENDER_BATTERY.get(),
                (tickLevel, tickPos, tickState, blockEntity) -> EnderBatteryBlockEntity.tick(tickLevel, tickPos, tickState, blockEntity));
    }
}

