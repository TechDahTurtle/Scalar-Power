package com.scalarpower.scalarpower;

import com.scalarpower.scalarpower.registry.ScalarPowerEntities;
import com.scalarpower.scalarpower.registry.ScalarPowerBlocks;
import com.scalarpower.scalarpower.registry.ScalarPowerItems;
import com.scalarpower.scalarpower.registry.ScalarPowerMenus;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(ScalarPower.MODID)
public class ScalarPower {
    public static final String MODID = "scalarpower";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ScalarPower(IEventBus modEventBus, ModContainer modContainer) {
        ScalarPowerBlocks.register(modEventBus);
        ScalarPowerItems.register(modEventBus);
        ScalarPowerEntities.register(modEventBus);
        ScalarPowerMenus.register(modEventBus);

        LOGGER.info("Scalar Power content registered.");
    }
}
