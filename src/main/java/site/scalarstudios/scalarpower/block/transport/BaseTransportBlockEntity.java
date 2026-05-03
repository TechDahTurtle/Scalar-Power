package site.scalarstudios.scalarpower.block.transport;

import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import site.scalarstudios.scalarpower.block.machine.cable.CableBehavior;

public abstract class BaseTransportBlockEntity extends BlockEntity {
    private final CableBehavior[] sideBehaviors = new CableBehavior[6];

    protected BaseTransportBlockEntity(net.minecraft.world.level.block.entity.BlockEntityType<?> type,
            net.minecraft.core.BlockPos pos,
            net.minecraft.world.level.block.state.BlockState blockState) {
        super(type, pos, blockState);
        for (int i = 0; i < 6; i++) {
            sideBehaviors[i] = CableBehavior.INPUT_OUTPUT;
        }
    }

    public CableBehavior getBehavior(Direction side) {
        return sideBehaviors[side.ordinal()];
    }

    public void setBehavior(Direction side, CableBehavior behavior) {
        int index = side.ordinal();
        if (sideBehaviors[index] != behavior) {
            sideBehaviors[index] = behavior;
            setChanged();
        }
    }

    public CableBehavior cycleBehavior(Direction side) {
        int index = side.ordinal();
        CableBehavior nextBehavior = sideBehaviors[index].next();
        sideBehaviors[index] = nextBehavior;
        setChanged();
        return nextBehavior;
    }

    public boolean exposesSide(Direction side) {
        return side == null || getBehavior(side) != CableBehavior.DISABLED;
    }

    /**
     * Returns whether this transport block can physically connect to the neighbor at the given position.
     * Cables check for energy capability; pipes check for fluid capability.
     */
    public abstract boolean canPhysicallyConnect(Level level, net.minecraft.core.BlockPos neighborPos, Direction incomingSide);

    @Override
    protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput output) {
        super.saveAdditional(output);
        for (Direction direction : Direction.values()) {
            output.putInt("behavior_" + direction.name(), getBehavior(direction).ordinal());
        }
    }

    @Override
    protected void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
        super.loadAdditional(input);
        for (Direction direction : Direction.values()) {
            String key = "behavior_" + direction.name();
            int ordinal = input.getIntOr(key, CableBehavior.INPUT_OUTPUT.ordinal());
            sideBehaviors[direction.ordinal()] = CableBehavior.fromOrdinal(ordinal);
        }
    }
}

