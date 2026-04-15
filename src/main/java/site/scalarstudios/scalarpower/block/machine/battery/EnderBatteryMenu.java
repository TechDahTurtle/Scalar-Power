package site.scalarstudios.scalarpower.block.machine.battery;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import site.scalarstudios.scalarpower.gui.ScalarPowerMenus;

public class EnderBatteryMenu extends BatteryMenu {
    public EnderBatteryMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
        this(id, inventory,
                (EnderBatteryBlockEntity) inventory.player.level().getBlockEntity(buf.readBlockPos()),
                new SimpleContainerData(3));
    }

    public EnderBatteryMenu(int id, Inventory inventory, EnderBatteryBlockEntity blockEntity, ContainerData data) {
        super(ScalarPowerMenus.ENDER_BATTERY_MENU.get(), id, inventory, blockEntity, data, 3);
    }
}


