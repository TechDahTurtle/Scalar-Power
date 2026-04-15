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

public class SteelBatteryBlock extends BatteryBlock {
    public static final MapCodec<SteelBatteryBlock> CODEC = simpleCodec(SteelBatteryBlock::new);

    public SteelBatteryBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SteelBatteryBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> blockEntityType) {
        return createTickerHelper(
                blockEntityType,
                ScalarPowerBlockEntities.STEEL_BATTERY.get(),
                (tickLevel, tickPos, tickState, blockEntity) -> BatteryBlockEntity.tick(tickLevel, tickPos, tickState, blockEntity));
    }
}

