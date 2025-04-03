package com.verdantartifice.thaumicwonders.common.compat.crafttweaker;

import com.verdantartifice.thaumicwonders.common.crafting.accelerator.AcceleratorRecipeRegistry;
import com.verdantartifice.thaumicwonders.common.crafting.accretionchamber.AccretionChamberRecipeRegistry;
import com.verdantartifice.thaumicwonders.common.crafting.catalyzationchamber.CatalyzationChamberRecipeRegistry;
import com.verdantartifice.thaumicwonders.common.crafting.meatyorb.MeatyOrbEntryRegistry;
import com.verdantartifice.thaumicwonders.common.crafting.voidbeacon.VoidBeaconEntryRegistry;
import com.verdantartifice.thaumicwonders.common.init.InitRecipes;
import com.verdantartifice.thaumicwonders.common.init.InitVoidBeacon;
import crafttweaker.mc1120.events.ScriptRunEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CTIntegration {
    @SubscribeEvent
    public void onScriptReloading(ScriptRunEvent.Pre event) {
        AcceleratorRecipeRegistry.removeAll();
        AccretionChamberRecipeRegistry.removeAll();
        CatalyzationChamberRecipeRegistry.removeAll();
        MeatyOrbEntryRegistry.removeAll();
        VoidBeaconEntryRegistry.removeAll();
    }

    @SubscribeEvent
    public void onScriptReloadingPost(ScriptRunEvent.Post event) {
        InitRecipes.initPrimordialAcceleratorRecipes();
        InitRecipes.initPrimordialAccretionChamberRecipes();
        InitRecipes.initCatalyzationChamberRecipes();
        InitRecipes.initMeatyOrb();
        InitVoidBeacon.registerDefaultEntries();
    }
}
