package site.scalarstudios.scalarpower.gui;

import site.scalarstudios.scalarpower.block.machine.alloysmelter.AlloySmelterMenu;
import site.scalarstudios.scalarpower.block.machine.battery.BatteryMenu;
import site.scalarstudios.scalarpower.block.machine.battery.EnderBatteryMenu;
import site.scalarstudios.scalarpower.block.machine.generator.coal.CoalGeneratorMenu;
import site.scalarstudios.scalarpower.block.machine.generator.barometric.BarometricGeneratorMenu;
import site.scalarstudios.scalarpower.block.machine.generator.culinary.CulinaryGeneratorMenu;
import site.scalarstudios.scalarpower.block.machine.generator.entropy.EntropyGeneratorMenu;
import site.scalarstudios.scalarpower.block.machine.generator.geothermal.GeothermalGeneratorMenu;
import site.scalarstudios.scalarpower.block.machine.generator.watermill.WaterMillGeneratorMenu;
import site.scalarstudios.scalarpower.block.machine.grinder.DoubleGrinderMenu;
import site.scalarstudios.scalarpower.block.machine.grinder.GrinderMenu;
import site.scalarstudios.scalarpower.block.machine.freezer.FreezerMenu;
import site.scalarstudios.scalarpower.block.machine.macerator.DoubleMaceratorMenu;
import site.scalarstudios.scalarpower.block.machine.macerator.MaceratorMenu;
import site.scalarstudios.scalarpower.block.machine.liquifier.LiquifierMenu;
import site.scalarstudios.scalarpower.block.machine.extractor.ExtractorMenu;
import site.scalarstudios.scalarpower.block.machine.sawmill.SawmillMenu;
import site.scalarstudios.scalarpower.block.machine.poweredfurnace.DoublePoweredFurnaceMenu;
import site.scalarstudios.scalarpower.block.machine.poweredfurnace.PoweredFurnaceMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import site.scalarstudios.scalarpower.ScalarPower;

public final class ScalarPowerMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ScalarPower.MODID);

    /* Generators */
    public static final DeferredHolder<MenuType<?>, MenuType<BarometricGeneratorMenu>> BAROMETRIC_GENERATOR_MENU = MENUS
            .register("barometric_generator", () -> IMenuTypeExtension.create(BarometricGeneratorMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<CoalGeneratorMenu>> COAL_GENERATOR_MENU = MENUS
            .register("coal_generator", () -> IMenuTypeExtension.create(CoalGeneratorMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<CulinaryGeneratorMenu>> CULINARY_GENERATOR_MENU = MENUS
            .register("culinary_generator", () -> IMenuTypeExtension.create(CulinaryGeneratorMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<EntropyGeneratorMenu>> ENTROPY_GENERATOR_MENU = MENUS
            .register("entropy_generator", () -> IMenuTypeExtension.create(EntropyGeneratorMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<GeothermalGeneratorMenu>> GEOTHERMAL_GENERATOR_MENU = MENUS
            .register("geothermal_generator", () -> IMenuTypeExtension.create(GeothermalGeneratorMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<WaterMillGeneratorMenu>> WATER_MILL_GENERATOR_MENU = MENUS
            .register("water_mill_generator", () -> IMenuTypeExtension.create(WaterMillGeneratorMenu::new));

    /* Machines */
    public static final DeferredHolder<MenuType<?>, MenuType<AlloySmelterMenu>> ALLOY_SMELTER_MENU = MENUS
            .register("alloy_smelter", () -> IMenuTypeExtension.create(AlloySmelterMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<ExtractorMenu>> EXTRACTOR_MENU = MENUS
            .register("extractor", () -> IMenuTypeExtension.create(ExtractorMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<FreezerMenu>> FREEZER_MENU = MENUS
            .register("freezer", () -> IMenuTypeExtension.create(FreezerMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<GrinderMenu>> GRINDER_MENU = MENUS
            .register("grinder", () -> IMenuTypeExtension.create(GrinderMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<DoubleGrinderMenu>> DOUBLE_GRINDER_MENU = MENUS
            .register("double_grinder", () -> IMenuTypeExtension.create(DoubleGrinderMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<LiquifierMenu>> LIQUIFIER_MENU = MENUS
            .register("liquifier", () -> IMenuTypeExtension.create(LiquifierMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<MaceratorMenu>> MACERATOR_MENU = MENUS
            .register("macerator", () -> IMenuTypeExtension.create(MaceratorMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<DoubleMaceratorMenu>> DOUBLE_MACERATOR_MENU = MENUS
            .register("double_macerator", () -> IMenuTypeExtension.create(DoubleMaceratorMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<PoweredFurnaceMenu>> POWERED_FURNACE_MENU = MENUS
            .register("powered_furnace", () -> IMenuTypeExtension.create(PoweredFurnaceMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<DoublePoweredFurnaceMenu>> DOUBLE_POWERED_FURNACE_MENU = MENUS
            .register("double_powered_furnace", () -> IMenuTypeExtension.create(DoublePoweredFurnaceMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<SawmillMenu>> SAWMILL_MENU = MENUS
            .register("sawmill", () -> IMenuTypeExtension.create(SawmillMenu::new));

    /* Energy Storage & Transfer */
    public static final DeferredHolder<MenuType<?>, MenuType<BatteryMenu>> BATTERY_MENU = MENUS
            .register("battery", () -> IMenuTypeExtension.create(BatteryMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<EnderBatteryMenu>> ENDER_BATTERY_MENU = MENUS
            .register("ender_battery", () -> IMenuTypeExtension.create(EnderBatteryMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
