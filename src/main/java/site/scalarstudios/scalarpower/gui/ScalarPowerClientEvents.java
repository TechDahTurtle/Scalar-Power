package site.scalarstudios.scalarpower.gui;

import site.scalarstudios.scalarpower.block.machine.alloysmelter.AlloySmelterScreen;
import site.scalarstudios.scalarpower.block.machine.battery.BatteryScreen;
import site.scalarstudios.scalarpower.block.machine.battery.EnderBatteryScreen;
import site.scalarstudios.scalarpower.block.machine.generator.coal.CoalGeneratorScreen;
import site.scalarstudios.scalarpower.block.machine.generator.barometric.BarometricGeneratorScreen;
import site.scalarstudios.scalarpower.block.machine.generator.culinary.CulinaryGeneratorScreen;
import site.scalarstudios.scalarpower.block.machine.generator.entropy.EntropyGeneratorScreen;
import site.scalarstudios.scalarpower.block.machine.generator.geothermal.GeothermalGeneratorScreen;
import site.scalarstudios.scalarpower.block.machine.generator.watermill.WaterMillGeneratorScreen;
import site.scalarstudios.scalarpower.block.machine.grinder.DoubleGrinderScreen;
import site.scalarstudios.scalarpower.block.machine.grinder.GrinderScreen;
import site.scalarstudios.scalarpower.block.machine.macerator.DoubleMaceratorScreen;
import site.scalarstudios.scalarpower.block.machine.macerator.MaceratorScreen;
import site.scalarstudios.scalarpower.block.machine.liquifier.LiquifierScreen;
import site.scalarstudios.scalarpower.block.machine.extractor.ExtractorScreen;
import site.scalarstudios.scalarpower.block.machine.sawmill.SawmillScreen;
import site.scalarstudios.scalarpower.block.machine.poweredfurnace.DoublePoweredFurnaceScreen;
import site.scalarstudios.scalarpower.block.machine.poweredfurnace.PoweredFurnaceScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import site.scalarstudios.scalarpower.ScalarPower;

@EventBusSubscriber(modid = ScalarPower.MODID, value = Dist.CLIENT)
public class ScalarPowerClientEvents {
    @SubscribeEvent
    static void onScreenRegistry(RegisterMenuScreensEvent event) {
        /* Generators */
        event.register(ScalarPowerMenus.BAROMETRIC_GENERATOR_MENU.get(), BarometricGeneratorScreen::new);
        event.register(ScalarPowerMenus.COAL_GENERATOR_MENU.get(), CoalGeneratorScreen::new);
        event.register(ScalarPowerMenus.CULINARY_GENERATOR_MENU.get(), CulinaryGeneratorScreen::new);
        event.register(ScalarPowerMenus.ENTROPY_GENERATOR_MENU.get(), EntropyGeneratorScreen::new);
        event.register(ScalarPowerMenus.GEOTHERMAL_GENERATOR_MENU.get(), GeothermalGeneratorScreen::new);
        event.register(ScalarPowerMenus.WATER_MILL_GENERATOR_MENU.get(), WaterMillGeneratorScreen::new);

        /* Machines */
        event.register(ScalarPowerMenus.ALLOY_SMELTER_MENU.get(), AlloySmelterScreen::new);
        event.register(ScalarPowerMenus.EXTRACTOR_MENU.get(), ExtractorScreen::new);
        event.register(ScalarPowerMenus.GRINDER_MENU.get(), GrinderScreen::new);
        event.register(ScalarPowerMenus.DOUBLE_GRINDER_MENU.get(), DoubleGrinderScreen::new);
        event.register(ScalarPowerMenus.LIQUIFIER_MENU.get(), LiquifierScreen::new);
        event.register(ScalarPowerMenus.MACERATOR_MENU.get(), MaceratorScreen::new);
        event.register(ScalarPowerMenus.DOUBLE_MACERATOR_MENU.get(), DoubleMaceratorScreen::new);
        event.register(ScalarPowerMenus.POWERED_FURNACE_MENU.get(), PoweredFurnaceScreen::new);
        event.register(ScalarPowerMenus.DOUBLE_POWERED_FURNACE_MENU.get(), DoublePoweredFurnaceScreen::new);
        event.register(ScalarPowerMenus.SAWMILL_MENU.get(), SawmillScreen::new);

        /* Energy Storage & Transfer */
        event.register(ScalarPowerMenus.BATTERY_MENU.get(), BatteryScreen::new);
        event.register(ScalarPowerMenus.ENDER_BATTERY_MENU.get(), EnderBatteryScreen::new);
    }
}
