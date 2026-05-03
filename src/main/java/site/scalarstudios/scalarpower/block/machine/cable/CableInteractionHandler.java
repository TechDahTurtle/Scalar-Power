package site.scalarstudios.scalarpower.block.machine.cable;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
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
        Direction clickedSide = event.getFace();
        if (clickedSide == null) {
            return;
        }

        String directionName = formatDirectionName(clickedSide);

        // Check if shift is held
        if (player.isShiftKeyDown()) {
            // Shift-click: Cycle to next behavior
            CableBehavior newBehavior = cableEntity.cycleBehavior(clickedSide);
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






