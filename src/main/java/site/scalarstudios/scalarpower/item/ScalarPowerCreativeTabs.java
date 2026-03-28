package site.scalarstudios.scalarpower.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import site.scalarstudios.scalarpower.ScalarPower;
import site.scalarstudios.scalarpower.block.ScalarPowerBlocks;

public class ScalarPowerCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ScalarPower.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SCALAR_POWER_TAB = CREATIVE_MODE_TABS.register("scalar_power",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.scalarpower.scalar_power"))
                    .icon(() -> new ItemStack(ScalarPowerBlocks.COAL_GENERATOR.get()))
                    .build());

    public static void registerTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == SCALAR_POWER_TAB.get()) {
            event.accept(ScalarPowerBlocks.COAL_GENERATOR.get());
            event.accept(ScalarPowerBlocks.CULINARY_GENERATOR.get());
            event.accept(ScalarPowerBlocks.GRINDER.get());
            event.accept(ScalarPowerBlocks.POWERED_FURNACE.get());
            event.accept(ScalarPowerBlocks.ALLOY_SMELTER.get());
            event.accept(ScalarPowerBlocks.BATTERY.get());
            event.accept(ScalarPowerBlocks.COPPER_WIRE.get());
            event.accept(ScalarPowerItems.IRON_DUST.get());
            event.accept(ScalarPowerItems.GOLD_DUST.get());
            event.accept(ScalarPowerItems.COPPER_DUST.get());
            event.accept(ScalarPowerItems.GOLD_UPGRADE.get());
            event.accept(ScalarPowerItems.REDIUM_INGOT.get());
            event.accept(ScalarPowerItems.REDIUM_UPGRADE.get());
            event.accept(ScalarPowerItems.DIAMOND_UPGRADE.get());
            event.accept(ScalarPowerItems.CLASTUS_UPGRADE.get());
            event.accept(ScalarPowerItems.CRYSTUS_UPGRADE.get());
        }
    }

    public static void register(IEventBus eventBus) {
            CREATIVE_MODE_TABS.register(eventBus);
    }
}
