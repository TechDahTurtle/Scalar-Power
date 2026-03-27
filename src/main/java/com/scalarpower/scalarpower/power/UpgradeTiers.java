package com.scalarpower.scalarpower.power;

import com.scalarpower.scalarpower.registry.ScalarPowerItems;
import net.minecraft.world.item.ItemStack;

public final class UpgradeTiers {
    private UpgradeTiers() {
    }

    public static float getMachineAndWireMultiplier(ItemStack upgrade) {
        if (upgrade.is(ScalarPowerItems.GOLD_UPGRADE.get())) {
            return 2.0F;
        }
        if (upgrade.is(ScalarPowerItems.REDIUM_UPGRADE.get())) {
            return 4.0F;
        }
        if (upgrade.is(ScalarPowerItems.DIAMOND_UPGRADE.get())) {
            return 6.0F;
        }
        if (upgrade.is(ScalarPowerItems.CLASTUS_UPGRADE.get())) {
            return 8.0F;
        }
        if (upgrade.is(ScalarPowerItems.CRYSTUS_UPGRADE.get())) {
            return 10.0F;
        }
        return 1.0F;
    }
}


