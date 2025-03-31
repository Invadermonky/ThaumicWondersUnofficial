package com.verdantartifice.thaumicwonders.common.events;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.api.aspects.AspectRegistryEvent;

@Mod.EventBusSubscriber(modid = ThaumicWonders.MODID)
public class AspectEvents {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void postAspects(AspectRegistryEvent event) {
        //InitVoidBeacon.init();
    }
}
