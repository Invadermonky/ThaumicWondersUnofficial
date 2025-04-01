package com.verdantartifice.thaumicwonders.common.compat.crafttweaker;

import com.verdantartifice.thaumicwonders.common.crafting.catalyzationchamber.CatalyzationChamberRegistry;
import com.verdantartifice.thaumicwonders.common.crafting.meatyorb.MeatyOrbRegistry;
import com.verdantartifice.thaumicwonders.common.crafting.voidbeacon.VoidBeaconRegistry;
import com.verdantartifice.thaumicwonders.common.init.InitRecipes;
import com.verdantartifice.thaumicwonders.common.init.InitVoidBeacon;
import crafttweaker.mc1120.events.ScriptRunEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CTIntegration {
    @SubscribeEvent
    public void onScriptReloading(ScriptRunEvent.Pre event) {
        CatalyzationChamberRegistry.removeAll();
        MeatyOrbRegistry.removeAll();
        VoidBeaconRegistry.removeAll();
    }

    @SubscribeEvent
    public void onScriptReloadingPost(ScriptRunEvent.Post event) {
        InitRecipes.initCatalyzationChamberRecipes();
        InitRecipes.initMeatyOrb();
        InitVoidBeacon.registerDefaultEntries();
    }
}
