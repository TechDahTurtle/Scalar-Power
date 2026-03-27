package com.scalarpower.scalarpower.registry;

import com.scalarpower.scalarpower.ScalarPower;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class ScalarPowerTags {
    private ScalarPowerTags() {
    }

    public static final class Items {
        public static final TagKey<Item> RAW_IRON_GRINDABLE = itemTag("raw_iron_grindable");
        public static final TagKey<Item> RAW_GOLD_GRINDABLE = itemTag("raw_gold_grindable");
        public static final TagKey<Item> RAW_COPPER_GRINDABLE = itemTag("raw_copper_grindable");

        private static TagKey<Item> itemTag(String path) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ScalarPower.MODID, path));
        }
    }
}


