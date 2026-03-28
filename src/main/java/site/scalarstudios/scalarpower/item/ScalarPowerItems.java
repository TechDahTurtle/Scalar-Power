package site.scalarstudios.scalarpower.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import site.scalarstudios.scalarpower.ScalarPower;

public class ScalarPowerItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ScalarPower.MODID);

    public static final DeferredItem<Item> COAL_DUST = ITEMS.registerSimpleItem("coal_dust");
    public static final DeferredItem<Item> COPPER_DUST = ITEMS.registerSimpleItem("copper_dust");
    public static final DeferredItem<Item> DIAMOND_DUST = ITEMS.registerSimpleItem("diamond_dust");
    public static final DeferredItem<Item> EMERALD_DUST = ITEMS.registerSimpleItem("emerald_dust");
    public static final DeferredItem<Item> GOLD_DUST = ITEMS.registerSimpleItem("gold_dust");
    public static final DeferredItem<Item> IRON_DUST = ITEMS.registerSimpleItem("iron_dust");
    public static final DeferredItem<Item> OBSIDIAN_DUST = ITEMS.registerSimpleItem("obsidian_dust");
    public static final DeferredItem<Item> STEEL_DUST = ITEMS.registerSimpleItem("steel_dust");

    public static final DeferredItem<Item> REDIUM_INGOT = ITEMS.registerSimpleItem("redium_ingot");
    public static final DeferredItem<Item> STEEL_INGOT = ITEMS.registerSimpleItem("steel_ingot");

    public static final DeferredItem<Item> GOLD_UPGRADE = ITEMS.registerSimpleItem("gold_upgrade");
    public static final DeferredItem<Item> REDIUM_UPGRADE = ITEMS.registerSimpleItem("redium_upgrade");
    public static final DeferredItem<Item> DIAMOND_UPGRADE = ITEMS.registerSimpleItem("diamond_upgrade");
    public static final DeferredItem<Item> CLASTUS_UPGRADE = ITEMS.registerSimpleItem("clastus_upgrade");
    public static final DeferredItem<Item> CRYSTUS_UPGRADE = ITEMS.registerSimpleItem("crystus_upgrade");

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
