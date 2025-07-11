package com.verdantartifice.thaumicwonders.common.compat;

import com.verdantartifice.thaumicwonders.common.compat.crafttweaker.CTPlugin;
import com.verdantartifice.thaumicwonders.common.compat.expandedarcanum.ExpandedArcanumPlugin;
import com.verdantartifice.thaumicwonders.common.compat.jer.JERPlugin;
import com.verdantartifice.thaumicwonders.common.compat.theoneprobe.TOPPlugin;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.HashSet;
import java.util.Set;

public class ModPlugins {
    private static final Set<IModPlugin> plugins = new HashSet<>();

    private static void buildPlugins() {
        if (ModIds.crafttweaker.isLoaded)
            plugins.add(new CTPlugin());
        if (ModIds.expanded_arcanum.isLoaded)
            plugins.add(new ExpandedArcanumPlugin());
        if (ModIds.jeresources.isLoaded)
            plugins.add(new JERPlugin());
        if (ModIds.the_one_probe.isLoaded)
            plugins.add(new TOPPlugin());
    }

    public static void preInit(FMLPreInitializationEvent event) {
        buildPlugins();
        plugins.forEach(IModPlugin::preInit);
    }

    public static void init(FMLInitializationEvent event) {
        plugins.forEach(IModPlugin::init);
    }

    public static void postInit(FMLPostInitializationEvent event) {
        plugins.forEach(IModPlugin::postInit);
    }

    @SideOnly(Side.CLIENT)
    public static void preInitClient(FMLPreInitializationEvent event) {
        plugins.forEach(IModPlugin::preInit);
    }

    @SideOnly(Side.CLIENT)
    public static void initClient(FMLInitializationEvent event) {
        plugins.forEach(IModPlugin::init);
    }

    @SideOnly(Side.CLIENT)
    public static void postInitClient(FMLPostInitializationEvent event) {
        plugins.forEach(IModPlugin::postInit);
    }


    public static void registerRecipes(IForgeRegistry<IRecipe> forgeRegistry) {
        plugins.forEach(plugin -> plugin.registerRecipes(forgeRegistry));
    }
}
