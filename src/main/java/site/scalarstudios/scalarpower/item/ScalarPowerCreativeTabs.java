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
            // Blocks
            /* Generators */
            event.accept(ScalarPowerBlocks.BAROMETRIC_GENERATOR.get());
            event.accept(ScalarPowerBlocks.COAL_GENERATOR.get());
            event.accept(ScalarPowerBlocks.CULINARY_GENERATOR.get());
            event.accept(ScalarPowerBlocks.ENTROPY_GENERATOR.get());
            event.accept(ScalarPowerBlocks.GEOTHERMAL_GENERATOR.get());
            event.accept(ScalarPowerBlocks.WATER_MILL_GENERATOR.get());

            /* Machines */
            event.accept(ScalarPowerBlocks.ALLOY_SMELTER.get());
            event.accept(ScalarPowerBlocks.CHARGER.get());
            event.accept(ScalarPowerBlocks.EXTRACTOR.get());
            event.accept(ScalarPowerBlocks.FREEZER.get());
            event.accept(ScalarPowerBlocks.LIQUIFIER.get());
            event.accept(ScalarPowerBlocks.GRINDER.get());
            event.accept(ScalarPowerBlocks.DOUBLE_GRINDER.get());
            event.accept(ScalarPowerBlocks.MACERATOR.get());
            event.accept(ScalarPowerBlocks.DOUBLE_MACERATOR.get());
            event.accept(ScalarPowerBlocks.POWERED_FURNACE.get());
            event.accept(ScalarPowerBlocks.DOUBLE_POWERED_FURNACE.get());
            event.accept(ScalarPowerBlocks.SAWMILL.get());

            /* Energy Storage & Transfer */
            event.accept(ScalarPowerBlocks.BATTERY.get());
            event.accept(ScalarPowerBlocks.STEEL_BATTERY.get());
            event.accept(ScalarPowerBlocks.CREATIVE_BATTERY.get());
            event.accept(ScalarPowerBlocks.COPPER_CABLE.get());
            event.accept(ScalarPowerBlocks.INSULATED_COPPER_CABLE.get());
            event.accept(ScalarPowerBlocks.GOLD_CABLE.get());
            event.accept(ScalarPowerBlocks.INSULATED_GOLD_CABLE.get());
            event.accept(ScalarPowerBlocks.FIBER_GLASS_CABLE.get());
            event.accept(ScalarPowerBlocks.REINFORCED_FIBER_GLASS_CABLE.get());
            event.accept(ScalarPowerBlocks.COPPER_PIPE.get());

            /* Normal Blocks */
            event.accept(ScalarPowerBlocks.COBALT_BLOCK.get());
            event.accept(ScalarPowerBlocks.ENDER_ALLOY_BLOCK.get());

            /* Devices */
            event.accept(ScalarPowerBlocks.INFINITE_WATER_SOURCE.get());
            event.accept(ScalarPowerBlocks.REDSTONE_CLOCK.get());

            // Items
            /* Dusts */
            event.accept(ScalarPowerItems.COAL_DUST.get());
            event.accept(ScalarPowerItems.COPPER_DUST.get());
            event.accept(ScalarPowerItems.COBALT_DUST.get());
            event.accept(ScalarPowerItems.DIAMOND_DUST.get());
            event.accept(ScalarPowerItems.ENDER_ALLOY_DUST.get());
            event.accept(ScalarPowerItems.EMERALD_DUST.get());
            event.accept(ScalarPowerItems.GOLD_DUST.get());
            event.accept(ScalarPowerItems.IRON_DUST.get());
            event.accept(ScalarPowerItems.OBSIDIAN_DUST.get());
            event.accept(ScalarPowerItems.REDIUM_DUST.get());
            event.accept(ScalarPowerItems.STEEL_DUST.get());
            event.accept(ScalarPowerItems.SAWDUST.get());

            /* Chunks */
            event.accept(ScalarPowerItems.COBALT_CHUNK.get());
            event.accept(ScalarPowerItems.COPPER_CHUNK.get());
            event.accept(ScalarPowerItems.IRON_CHUNK.get());
            event.accept(ScalarPowerItems.GOLD_CHUNK.get());

            /* Ingots */
            event.accept(ScalarPowerItems.COBALT_INGOT.get());
            event.accept(ScalarPowerItems.ENDER_ALLOY_INGOT.get());
            event.accept(ScalarPowerItems.REDIUM_INGOT.get());
            event.accept(ScalarPowerItems.STEEL_INGOT.get());

            /* Other Resources */
            event.accept(ScalarPowerItems.RUBBER.get());
            event.accept(ScalarPowerItems.RESIN_PELLET.get());

            /* Misc. */
            event.accept(ScalarPowerItems.STEEL_ROD.get());

            /* Tools */
            event.accept(ScalarPowerItems.COBALT_SWORD.get());
            event.accept(ScalarPowerItems.COBALT_PICKAXE.get());
            event.accept(ScalarPowerItems.COBALT_AXE.get());
            event.accept(ScalarPowerItems.COBALT_SHOVEL.get());
            event.accept(ScalarPowerItems.COBALT_HOE.get());
            event.accept(ScalarPowerItems.COBALT_SPEAR.get());
            event.accept(ScalarPowerItems.WRENCH.get());

            /* Machine Frames */
            event.accept(ScalarPowerItems.DEVICE_FRAME.get());
            event.accept(ScalarPowerItems.MACHINE_FRAME.get());
            event.accept(ScalarPowerItems.STEEL_MACHINE_FRAME.get());
        }
    }

    public static void register(IEventBus eventBus) {
            CREATIVE_MODE_TABS.register(eventBus);
    }
}
