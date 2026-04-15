package site.scalarstudios.scalarpower.block.machine.wire.reinforcedfiberglass;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import site.scalarstudios.scalarpower.block.ScalarPowerBlockEntities;

public class ReinforcedFiberGlassWireBlock extends BaseEntityBlock {
    public static final MapCodec<ReinforcedFiberGlassWireBlock> CODEC = simpleCodec(ReinforcedFiberGlassWireBlock::new);
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;

    private static final VoxelShape CORE = box(6, 6, 6, 10, 10, 10);
    private static final VoxelShape NORTH_ARM = box(7, 7, 0, 9, 9, 6);
    private static final VoxelShape EAST_ARM = box(10, 7, 7, 16, 9, 9);
    private static final VoxelShape SOUTH_ARM = box(7, 7, 10, 9, 9, 16);
    private static final VoxelShape WEST_ARM = box(0, 7, 7, 6, 9, 9);
    private static final VoxelShape UP_ARM = box(7, 10, 7, 9, 16, 9);
    private static final VoxelShape DOWN_ARM = box(7, 0, 7, 9, 6, 9);

    public ReinforcedFiberGlassWireBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        return false;
    }


    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return updateConnections(defaultBlockState(), context.getLevel(), context.getClickedPos());
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess,
            BlockPos currentPos, Direction direction, BlockPos neighborPos, BlockState neighborState,
            RandomSource random) {
        return state.setValue(propertyFor(direction), canConnectTo(level, neighborPos, direction.getOpposite()));
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = CORE;
        if (state.getValue(NORTH)) shape = Shapes.or(shape, NORTH_ARM);
        if (state.getValue(EAST)) shape = Shapes.or(shape, EAST_ARM);
        if (state.getValue(SOUTH)) shape = Shapes.or(shape, SOUTH_ARM);
        if (state.getValue(WEST)) shape = Shapes.or(shape, WEST_ARM);
        if (state.getValue(UP)) shape = Shapes.or(shape, UP_ARM);
        if (state.getValue(DOWN)) shape = Shapes.or(shape, DOWN_ARM);
        return shape;
    }

    private static BlockState updateConnections(BlockState state, LevelReader level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            state = state.setValue(propertyFor(direction),
                    canConnectTo(level, pos.relative(direction), direction.getOpposite()));
        }
        return state;
    }

    private static boolean canConnectTo(LevelReader level, BlockPos pos, Direction incomingSide) {
        if (!(level instanceof Level actualLevel)) {
            return false;
        }
        return actualLevel.getCapability(Capabilities.Energy.BLOCK, pos, incomingSide) != null;
    }

    private static BooleanProperty propertyFor(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH;
            case EAST -> EAST;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case UP -> UP;
            case DOWN -> DOWN;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ReinforcedFiberGlassWireBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, ScalarPowerBlockEntities.REINFORCED_FIBER_GLASS_WIRE.get(), ReinforcedFiberGlassWireBlockEntity::tick);
    }
}

