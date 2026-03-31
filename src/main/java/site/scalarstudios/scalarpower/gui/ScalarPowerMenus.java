package site.scalarstudios.scalarpower.gui;

import site.scalarstudios.scalarpower.content.alloysmelter.AlloySmelterMenu;
import site.scalarstudios.scalarpower.content.battery.BatteryMenu;
import site.scalarstudios.scalarpower.content.generator.coal.CoalGeneratorMenu;
import site.scalarstudios.scalarpower.content.generator.culinary.CulinaryGeneratorMenu;
import site.scalarstudios.scalarpower.content.generator.entropy.EntropyGeneratorMenu;
import site.scalarstudios.scalarpower.content.grinder.DoubleGrinderMenu;
import site.scalarstudios.scalarpower.content.grinder.GrinderMenu;
import site.scalarstudios.scalarpower.content.extractor.ExtractorMenu;
import site.scalarstudios.scalarpower.content.poweredfurnace.DoublePoweredFurnaceMenu;
import site.scalarstudios.scalarpower.content.poweredfurnace.PoweredFurnaceMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import site.scalarstudios.scalarpower.ScalarPower;

public final class ScalarPowerMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ScalarPower.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<CoalGeneratorMenu>> COAL_GENERATOR_MENU = MENUS
            .register("coal_generator", () -> IMenuTypeExtension.create(CoalGeneratorMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<CulinaryGeneratorMenu>> CULINARY_GENERATOR_MENU = MENUS
            .register("culinary_generator", () -> IMenuTypeExtension.create(CulinaryGeneratorMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<EntropyGeneratorMenu>> ENTROPY_GENERATOR_MENU = MENUS
            .register("entropy_generator", () -> IMenuTypeExtension.create(EntropyGeneratorMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<GrinderMenu>> GRINDER_MENU = MENUS
            .register("grinder", () -> IMenuTypeExtension.create(GrinderMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<DoubleGrinderMenu>> DOUBLE_GRINDER_MENU = MENUS
            .register("double_grinder", () -> IMenuTypeExtension.create(DoubleGrinderMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<ExtractorMenu>> EXTRACTOR_MENU = MENUS
            .register("extractor", () -> IMenuTypeExtension.create(ExtractorMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<PoweredFurnaceMenu>> POWERED_FURNACE_MENU = MENUS
            .register("powered_furnace", () -> IMenuTypeExtension.create(PoweredFurnaceMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<DoublePoweredFurnaceMenu>> DOUBLE_POWERED_FURNACE_MENU = MENUS
            .register("double_powered_furnace", () -> IMenuTypeExtension.create(DoublePoweredFurnaceMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<AlloySmelterMenu>> ALLOY_SMELTER_MENU = MENUS
            .register("alloy_smelter", () -> IMenuTypeExtension.create(AlloySmelterMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<BatteryMenu>> BATTERY_MENU = MENUS
            .register("battery", () -> IMenuTypeExtension.create(BatteryMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
