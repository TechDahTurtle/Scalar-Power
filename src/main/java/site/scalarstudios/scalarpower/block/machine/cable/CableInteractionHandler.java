package site.scalarstudios.scalarpower.block.machine.cable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import site.scalarstudios.scalarpower.block.transport.BaseTransportBlockEntity;

@EventBusSubscriber(modid = "scalarpower")
public class CableInteractionHandler {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        // Only handle on server
        if (event.getLevel().isClientSide()) {
            return;
        }

        // Check if player is holding wrench
        if (!event.getItemStack().is(site.scalarstudios.scalarpower.item.ScalarPowerItems.WRENCH.get())) {
            return;
        }

        // Get the block entity
        BlockEntity blockEntity = event.getLevel().getBlockEntity(event.getPos());
        if (!(blockEntity instanceof BaseTransportBlockEntity cableEntity)) {
            return;
        }

        Component transportName = blockEntity.getBlockState().getBlock().getName();

        Player player = event.getEntity();

        // Get the direction that was clicked
        Direction clickedSide = resolveConfiguredSide(event.getPos(), event.getHitVec(), event.getFace());
        if (clickedSide == null) {
            return;
        }

        String directionName = formatDirectionName(clickedSide);

        // Check if shift is held
        if (player.isShiftKeyDown()) {
            // Shift-click: Cycle to next behavior
            CableBehavior newBehavior = cableEntity.cycleBehavior(clickedSide);
            refreshConnectionState(event.getLevel(), event.getPos(), clickedSide, cableEntity);

            BlockPos neighborPos = event.getPos().relative(clickedSide);
            BlockEntity neighbor = event.getLevel().getBlockEntity(neighborPos);
            if (neighbor instanceof BaseTransportBlockEntity neighborTransport) {
                refreshConnectionState(event.getLevel(), neighborPos, clickedSide.getOpposite(), neighborTransport);
            }

            player.sendSystemMessage(
                Component.translatable("chat.scalarpower.behavior_set", transportName, directionName, newBehavior.getDisplayName())
            );
        } else {
            // Normal click: Show current behavior
            CableBehavior current = cableEntity.getBehavior(clickedSide);
            player.sendSystemMessage(
                Component.translatable("chat.scalarpower.current_behavior_display", transportName, directionName, current.getDisplayName())
            );
        }

        // Consume the interaction
        event.setCanceled(true);
    }

    /**
     * Updates the blockstate connection property for one side based on the new behavior.
     * DISABLED hides the arm; any other behavior re-checks physical connectivity.
     */
    private static void refreshConnectionState(Level level, BlockPos pos, Direction side, BaseTransportBlockEntity entity) {
        BlockState state = level.getBlockState(pos);
        BooleanProperty prop = directionProperty(side);
        if (!state.hasProperty(prop)) {
            return;
        }

        boolean connected = entity.exposesSide(side)
                && entity.canPhysicallyConnect(level, pos.relative(side), side.getOpposite());

        if (state.getValue(prop) != connected) {
            level.setBlock(pos, state.setValue(prop, connected), 3);
        }
    }

    private static Direction resolveConfiguredSide(BlockPos pos, BlockHitResult hitResult, Direction fallbackFace) {
        if (hitResult == null) {
            return fallbackFace;
        }

        Vec3 offset = hitResult.getLocation().subtract(
                pos.getX() + 0.5D,
                pos.getY() + 0.5D,
                pos.getZ() + 0.5D);

        double absX = Math.abs(offset.x());
        double absY = Math.abs(offset.y());
        double absZ = Math.abs(offset.z());
        double armThreshold = 2.0D / 16.0D;

        if (Math.max(absX, Math.max(absY, absZ)) <= armThreshold) {
            return fallbackFace;
        }

        if (absX >= absY && absX >= absZ) {
            return offset.x() >= 0.0D ? Direction.EAST : Direction.WEST;
        }
        if (absY >= absX && absY >= absZ) {
            return offset.y() >= 0.0D ? Direction.UP : Direction.DOWN;
        }
        return offset.z() >= 0.0D ? Direction.SOUTH : Direction.NORTH;
    }

    private static BooleanProperty directionProperty(Direction direction) {
        return switch (direction) {
            case NORTH -> BlockStateProperties.NORTH;
            case EAST  -> BlockStateProperties.EAST;
            case SOUTH -> BlockStateProperties.SOUTH;
            case WEST  -> BlockStateProperties.WEST;
            case UP    -> BlockStateProperties.UP;
            case DOWN  -> BlockStateProperties.DOWN;
        };
    }

    /**
     * Capitalizes the first letter in a given string name (used for directions)
     *
     * @param direction The direction whose first letter to capitalize
     * @return The direction with its first letter capitalized
     */
    private static String formatDirectionName(Direction direction) {
        String name = direction.getName();
        if (name.isEmpty()) {
            return name;
        }

        return Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase();
    }
}








