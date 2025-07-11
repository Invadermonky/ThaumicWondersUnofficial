package com.verdantartifice.thaumicwonders.common.compat.crafttweaker;

import com.verdantartifice.thaumicwonders.common.compat.IModPlugin;
import com.verdantartifice.thaumicwonders.common.crafting.catalyzationchamber.CatalyzationChamberRecipeRegistry;
import com.verdantartifice.thaumicwonders.common.crafting.meatyorb.MeatyOrbEntryRegistry;
import com.verdantartifice.thaumicwonders.common.crafting.voidbeacon.VoidBeaconEntryRegistry;
import com.verdantartifice.thaumicwonders.common.init.InitRecipes;
import com.verdantartifice.thaumicwonders.common.init.InitVoidBeacon;
import crafttweaker.mc1120.events.ScriptRunEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CTPlugin implements IModPlugin {
    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onScriptReloading(ScriptRunEvent.Pre event) {
        CatalyzationChamberRecipeRegistry.removeAll();
        MeatyOrbEntryRegistry.removeAll();
        VoidBeaconEntryRegistry.removeAll();
    }

    @SubscribeEvent
    public void onScriptReloadingPost(ScriptRunEvent.Post event) {
        InitRecipes.initCatalyzationChamberRecipes();
        InitRecipes.initMeatyOrb();
        InitVoidBeacon.registerDefaultEntries();
    }

}
