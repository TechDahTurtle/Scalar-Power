package com.scalarpower.scalarpower.power;

import com.scalarpower.scalarpower.registry.ModItems;
import net.minecraft.world.item.ItemStack;

public final class UpgradeTiers {
    private UpgradeTiers() {
    }

    public static float getMachineAndWireMultiplier(ItemStack upgrade) {
        if (upgrade.is(ModItems.GOLD_UPGRADE.get())) {
            return 2.0F;
        }
        if (upgrade.is(ModItems.REDIUM_UPGRADE.get())) {
            return 4.0F;
        }
        if (upgrade.is(ModItems.DIAMOND_UPGRADE.get())) {
            return 6.0F;
        }
        if (upgrade.is(ModItems.CLASTUS_UPGRADE.get())) {
            return 8.0F;
        }
        if (upgrade.is(ModItems.CRYSTUS_UPGRADE.get())) {
            return 10.0F;
        }
        return 1.0F;
    }
}


