package com.verdantartifice.thaumicwonders.common.compat.crafttweaker;

import com.verdantartifice.thaumicwonders.common.crafting.meatyorb.MeatyOrbRegistry;
import com.verdantartifice.thaumicwonders.common.crafting.voidbeacon.VoidBeaconRegistry;
import com.verdantartifice.thaumicwonders.common.init.InitRecipes;
import com.verdantartifice.thaumicwonders.common.init.InitVoidBeacon;
import crafttweaker.mc1120.events.ScriptRunEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CTIntegration {
    //TODO: Register event handler if CrT is loaded

    @SubscribeEvent
    public void onScriptReloading(ScriptRunEvent.Pre event) {
        MeatyOrbRegistry.removeAll();
        VoidBeaconRegistry.removeAll();
    }

    @SubscribeEvent
    public void onScriptReloadingPost(ScriptRunEvent.Post event) {
        InitRecipes.initMeatyOrb();
        InitVoidBeacon.registerDefaultEntries();
    }
}
