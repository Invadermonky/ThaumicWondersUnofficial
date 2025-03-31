package com.verdantartifice.thaumicwonders.common.compat.crafttweaker.handlers;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.crafting.voidbeacon.VoidBeaconRegistry;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods." + ThaumicWonders.MODID + ".VoidBeacon")
public class CTVoidBeacon {
    @ZenMethod
    public static void add(IItemStack stack) {
        ItemStack itemStack = CraftTweakerMC.getItemStack(stack);
        if(itemStack.isEmpty()) {
            CraftTweakerAPI.logError("Failure adding void beacon entry, IItemStack cannot be empty");
        } else {
            VoidBeaconRegistry.addEntry(itemStack);
        }
    }

    @ZenMethod
    public static void add(IOreDictEntry oreDictEntry) {
        if(OreDictionary.getOres(oreDictEntry.getName()).isEmpty()) {
            CraftTweakerAPI.logError("There are no items registered to the Ore Dictionary " + oreDictEntry.getName());
        } else {
            VoidBeaconRegistry.addEntry(oreDictEntry.getName());
        }
    }

    @ZenMethod
    public static void remove(IItemStack stack) {
        VoidBeaconRegistry.removeEntry(CraftTweakerMC.getItemStack(stack));
    }

    @ZenMethod
    public static void remove(IOreDictEntry oreDictEntry) {
        VoidBeaconRegistry.removeEntry(oreDictEntry.getName());
    }

    @ZenMethod
    public static void removeAll() {
        VoidBeaconRegistry.removeAll();
    }
}
