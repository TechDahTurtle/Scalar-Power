package site.scalarstudios.scalarpower.block.device.redstoneclock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import site.scalarstudios.scalarpower.block.ScalarPowerBlockEntities;

public class RedstoneClockBlockEntity extends BlockEntity {

    /**
     * Full period in ticks (16 ticks OFF → 4 ticks ON → repeat).
     */
    private static final int PERIOD = 20;

    /**
     * Number of ticks the clock stays OFF before turning ON.
     * The clock will be ON for (PERIOD - OFF_TICKS) = 4 ticks.
     */
    private static final int OFF_TICKS = 16;

    private int tickCounter = 0;

    public RedstoneClockBlockEntity(BlockPos pos, BlockState state) {
        super(ScalarPowerBlockEntities.REDSTONE_CLOCK.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, RedstoneClockBlockEntity blockEntity) {
        if (level == null || level.isClientSide()) return;

        boolean powered = state.getValue(RedstoneClockBlock.POWERED);

        blockEntity.tickCounter++;
        if (blockEntity.tickCounter >= PERIOD) {
            blockEntity.tickCounter = 0;
        }

        // ON during the last ON_TICKS ticks of the period (ticks 16–19), OFF otherwise.
        boolean shouldBePowered = blockEntity.tickCounter >= OFF_TICKS;

        if (shouldBePowered != powered) {
            level.setBlock(pos, state.setValue(RedstoneClockBlock.POWERED, shouldBePowered), 3);
            level.updateNeighborsAt(pos, state.getBlock());
            blockEntity.setChanged();
        }
    }

    // Persistence
    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("TickCounter", tickCounter);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        tickCounter = input.getIntOr("TickCounter", 0);
    }
}

