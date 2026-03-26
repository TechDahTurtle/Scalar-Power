package com.scalarpower.scalarpower.client;

import com.scalarpower.scalarpower.ScalarPower;
import com.scalarpower.scalarpower.content.generator.CoalGeneratorScreen;
import com.scalarpower.scalarpower.content.grinder.GrinderScreen;
import com.scalarpower.scalarpower.content.poweredfurnace.PoweredFurnaceScreen;
import com.scalarpower.scalarpower.registry.ModMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = ScalarPower.MODID, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    static void onScreenRegistry(RegisterMenuScreensEvent event) {
        event.register(ModMenus.COAL_GENERATOR_MENU.get(), CoalGeneratorScreen::new);
        event.register(ModMenus.GRINDER_MENU.get(), GrinderScreen::new);
        event.register(ModMenus.POWERED_FURNACE_MENU.get(), PoweredFurnaceScreen::new);
    }
}


