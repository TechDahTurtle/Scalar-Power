package site.scalarstudios.scalarpower.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import site.scalarstudios.scalarpower.ScalarPower;

public class ScalarPowerItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ScalarPower.MODID);

    // Dusts
    public static final DeferredItem<Item> COAL_DUST = ITEMS.registerSimpleItem("coal_dust");
    public static final DeferredItem<Item> COPPER_DUST = ITEMS.registerSimpleItem("copper_dust");
    public static final DeferredItem<Item> COBALT_DUST = ITEMS.registerSimpleItem("cobalt_dust");
    public static final DeferredItem<Item> DIAMOND_DUST = ITEMS.registerSimpleItem("diamond_dust");
    public static final DeferredItem<Item> ENDER_ALLOY_DUST = ITEMS.registerSimpleItem("ender_alloy_dust");
    public static final DeferredItem<Item> EMERALD_DUST = ITEMS.registerSimpleItem("emerald_dust");
    public static final DeferredItem<Item> GOLD_DUST = ITEMS.registerSimpleItem("gold_dust");
    public static final DeferredItem<Item> IRON_DUST = ITEMS.registerSimpleItem("iron_dust");
    public static final DeferredItem<Item> OBSIDIAN_DUST = ITEMS.registerSimpleItem("obsidian_dust");
    public static final DeferredItem<Item> REDIUM_DUST = ITEMS.registerSimpleItem("redium_dust");
    public static final DeferredItem<Item> STEEL_DUST = ITEMS.registerSimpleItem("steel_dust");

    public static final DeferredItem<Item> SAWDUST = ITEMS.registerSimpleItem("sawdust");

    // Chunks
    public static final DeferredItem<Item> COBALT_CHUNK = ITEMS.registerSimpleItem("cobalt_chunk");
    public static final DeferredItem<Item> COPPER_CHUNK = ITEMS.registerSimpleItem("copper_chunk");
    public static final DeferredItem<Item> IRON_CHUNK = ITEMS.registerSimpleItem("iron_chunk");
    public static final DeferredItem<Item> GOLD_CHUNK = ITEMS.registerSimpleItem("gold_chunk");

    // Ingots
    public static final DeferredItem<Item> COBALT_INGOT = ITEMS.registerSimpleItem("cobalt_ingot");
    public static final DeferredItem<Item> ENDER_ALLOY_INGOT = ITEMS.registerSimpleItem("ender_alloy_ingot");
    public static final DeferredItem<Item> REDIUM_INGOT = ITEMS.registerSimpleItem("redium_ingot");
    public static final DeferredItem<Item> STEEL_INGOT = ITEMS.registerSimpleItem("steel_ingot");

    // Rubber
    public static final DeferredItem<Item> RUBBER = ITEMS.registerSimpleItem("rubber");
    public static final DeferredItem<Item> SYNTHETIC_RESIN = ITEMS.registerSimpleItem("synthetic_resin");

    // Machine Frames
    public static final DeferredItem<Item> DEVICE_FRAME = ITEMS.registerSimpleItem("device_frame");
    public static final DeferredItem<Item> MACHINE_FRAME = ITEMS.registerSimpleItem("machine_frame");
    public static final DeferredItem<Item> STEEL_MACHINE_FRAME = ITEMS.registerSimpleItem("steel_machine_frame");

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
