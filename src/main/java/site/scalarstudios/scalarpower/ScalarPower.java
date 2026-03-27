package site.scalarstudios.scalarpower;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import site.scalarstudios.scalarpower.block.ScalarPowerBlockEntities;
import site.scalarstudios.scalarpower.block.ScalarPowerBlocks;
import site.scalarstudios.scalarpower.gui.ScalarPowerMenus;
import site.scalarstudios.scalarpower.item.ScalarPowerCreativeTabs;
import site.scalarstudios.scalarpower.item.ScalarPowerItems;
import site.scalarstudios.scalarpower.power.ScalarPowerCapabilities;

@Mod(ScalarPower.MODID)
public class ScalarPower {
    public static final String MODID = "scalarpower";

    public ScalarPower(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        // Register Items and Blocks
        ScalarPowerItems.register(modEventBus);
        ScalarPowerBlocks.register(modEventBus);

        // Register Block Entities and Menus
        ScalarPowerBlockEntities.register(modEventBus);
        ScalarPowerMenus.register(modEventBus);
        modEventBus.addListener(ScalarPowerCapabilities::register);

        // Register Creative Tabs
        ScalarPowerCreativeTabs.register(modEventBus);
        modEventBus.addListener(ScalarPowerCreativeTabs::registerTabs);
    }

    private void commonSetup(FMLCommonSetupEvent event) {}

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {}
}
