package com.scalarpower.scalarpower.client;

import com.scalarpower.scalarpower.ScalarPower;
import com.scalarpower.scalarpower.content.generator.CoalGeneratorScreen;
import com.scalarpower.scalarpower.content.grinder.GrinderScreen;
import com.scalarpower.scalarpower.content.poweredfurnace.PoweredFurnaceScreen;
import com.scalarpower.scalarpower.registry.ScalarPowerMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = ScalarPower.MODID, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    static void onScreenRegistry(RegisterMenuScreensEvent event) {
        event.register(ScalarPowerMenus.COAL_GENERATOR_MENU.get(), CoalGeneratorScreen::new);
        event.register(ScalarPowerMenus.GRINDER_MENU.get(), GrinderScreen::new);
        event.register(ScalarPowerMenus.POWERED_FURNACE_MENU.get(), PoweredFurnaceScreen::new);
    }
}


