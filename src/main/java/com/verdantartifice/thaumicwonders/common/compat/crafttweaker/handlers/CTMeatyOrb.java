package com.verdantartifice.thaumicwonders.common.compat.crafttweaker.handlers;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.crafting.meatyorb.MeatyOrbEntryRegistry;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods." + ThaumicWonders.MODID + ".MeatyOrb")
public class CTMeatyOrb {
    @ZenMethod
    public static void add(IItemStack stack, int weight) {
        ItemStack meatStack = CraftTweakerMC.getItemStack(stack);
        if (meatStack.isEmpty()) {
            CraftTweakerAPI.logError("Error adding Meaty Orb entry. IItemStack cannot be empty.");
        } else {
            MeatyOrbEntryRegistry.addEntry(meatStack, Math.max(weight, 1));
            CraftTweakerAPI.logInfo("Added Meaty Orb entry: " + stack.getName());
        }
    }

    @ZenMethod
    public static void remove(IItemStack stack) {
        MeatyOrbEntryRegistry.removeEntry(CraftTweakerMC.getItemStack(stack));
    }

    @ZenMethod
    public static void removeAll() {
        MeatyOrbEntryRegistry.removeAll();
    }
}
