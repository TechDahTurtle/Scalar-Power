package site.scalarstudios.scalarpower.client;

import site.scalarstudios.scalarpower.content.alloysmelter.AlloySmelterScreen;
import site.scalarstudios.scalarpower.content.battery.BatteryScreen;
import site.scalarstudios.scalarpower.content.generator.coal.CoalGeneratorScreen;
import site.scalarstudios.scalarpower.content.generator.culinary.CulinaryGeneratorScreen;
import site.scalarstudios.scalarpower.content.grinder.GrinderScreen;
import site.scalarstudios.scalarpower.content.poweredfurnace.PoweredFurnaceScreen;
import site.scalarstudios.scalarpower.gui.ScalarPowerMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import site.scalarstudios.scalarpower.ScalarPower;

@EventBusSubscriber(modid = ScalarPower.MODID, value = Dist.CLIENT)
public class ScalarPowerClientEvents {
    @SubscribeEvent
    static void onScreenRegistry(RegisterMenuScreensEvent event) {
        event.register(ScalarPowerMenus.COAL_GENERATOR_MENU.get(), CoalGeneratorScreen::new);
        event.register(ScalarPowerMenus.CULINARY_GENERATOR_MENU.get(), CulinaryGeneratorScreen::new);
        event.register(ScalarPowerMenus.GRINDER_MENU.get(), GrinderScreen::new);
        event.register(ScalarPowerMenus.POWERED_FURNACE_MENU.get(), PoweredFurnaceScreen::new);
        event.register(ScalarPowerMenus.ALLOY_SMELTER_MENU.get(), AlloySmelterScreen::new);
        event.register(ScalarPowerMenus.BATTERY_MENU.get(), BatteryScreen::new);
    }
}
