package com.scalarpower.scalarpower.registry;

import com.scalarpower.scalarpower.ScalarPower;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ScalarPower.MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB,
            ScalarPower.MODID);

    public static final DeferredItem<BlockItem> COAL_GENERATOR_ITEM = ITEMS.registerSimpleBlockItem("coal_generator",
            ModBlocks.COAL_GENERATOR);
    public static final DeferredItem<BlockItem> GRINDER_ITEM = ITEMS.registerSimpleBlockItem("grinder",
            ModBlocks.GRINDER);
    public static final DeferredItem<BlockItem> POWERED_FURNACE_ITEM = ITEMS.registerSimpleBlockItem("powered_furnace",
            ModBlocks.POWERED_FURNACE);
    public static final DeferredItem<BlockItem> COPPER_WIRE_ITEM = ITEMS.registerSimpleBlockItem("copper_wire",
            ModBlocks.COPPER_WIRE);

    public static final DeferredItem<Item> IRON_DUST = ITEMS.registerSimpleItem("iron_dust");
    public static final DeferredItem<Item> GOLD_DUST = ITEMS.registerSimpleItem("gold_dust");
    public static final DeferredItem<Item> COPPER_DUST = ITEMS.registerSimpleItem("copper_dust");

    public static final DeferredItem<Item> GOLD_UPGRADE = ITEMS.registerSimpleItem("gold_upgrade");
    public static final DeferredItem<Item> REDIUM = ITEMS.registerSimpleItem("redium");
    public static final DeferredItem<Item> REDIUM_UPGRADE = ITEMS.registerSimpleItem("redium_upgrade");
    public static final DeferredItem<Item> DIAMOND_UPGRADE = ITEMS.registerSimpleItem("diamond_upgrade");
    public static final DeferredItem<Item> CLASTUS_UPGRADE = ITEMS.registerSimpleItem("clastus_upgrade");
    public static final DeferredItem<Item> CRYSTUS_UPGRADE = ITEMS.registerSimpleItem("crystus_upgrade");

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SCALAR_POWER_TAB = TABS.register("scalar_power",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.scalarpower.scalar_power"))
                    .withTabsBefore(CreativeModeTabs.REDSTONE_BLOCKS)
                    .icon(() -> COAL_GENERATOR_ITEM.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(COAL_GENERATOR_ITEM.get());
                        output.accept(GRINDER_ITEM.get());
                        output.accept(POWERED_FURNACE_ITEM.get());
                        output.accept(COPPER_WIRE_ITEM.get());
                        output.accept(IRON_DUST.get());
                        output.accept(GOLD_DUST.get());
                        output.accept(COPPER_DUST.get());
                        output.accept(GOLD_UPGRADE.get());
                        output.accept(REDIUM.get());
                        output.accept(REDIUM_UPGRADE.get());
                        output.accept(DIAMOND_UPGRADE.get());
                        output.accept(CLASTUS_UPGRADE.get());
                        output.accept(CRYSTUS_UPGRADE.get());
                    })
                    .build());

    private ModItems() {
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        TABS.register(eventBus);
    }
}

