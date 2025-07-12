package com.verdantartifice.thaumicwonders.common.compat.crafttweaker.handlers;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.crafting.voidbeacon.VoidBeaconEntryRegistry;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods." + ThaumicWonders.MODID + ".VoidBeacon")
public class CTVoidBeacon {
    @ZenMethod
    public static void add(IItemStack stack, int weight) {
        ItemStack itemStack = CraftTweakerMC.getItemStack(stack);
        if (itemStack.isEmpty()) {
            CraftTweakerAPI.logError("Failure adding void beacon entry, IItemStack cannot be empty");
        } else {
            VoidBeaconEntryRegistry.addEntry(itemStack, Math.max(weight, 1));
        }
    }

    @ZenMethod
    public static void remove(IItemStack stack) {
        VoidBeaconEntryRegistry.removeEntry(CraftTweakerMC.getItemStack(stack));
    }

    @ZenMethod
    public static void remove(IOreDictEntry oreDictEntry) {
        VoidBeaconEntryRegistry.removeEntry(oreDictEntry.getName());
    }

    @ZenMethod
    public static void removeAll() {
        VoidBeaconEntryRegistry.removeAll();
    }
}
