package com.verdantartifice.thaumicwonders.common.compat.crafttweaker.handlers;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.crafting.accelerator.AcceleratorRecipeRegistry;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods." + ThaumicWonders.MODID + ".PrimordialAccelerator")
public class CTPrimordialAccelerator {
    @ZenMethod
    public static void addRecipe(IIngredient input, IItemStack output) {
        AcceleratorRecipeRegistry.addRecipe(CraftTweakerMC.getIngredient(input), CraftTweakerMC.getItemStack(output));
    }

    @ZenMethod
    public static void removeByInput(IIngredient input) {
        AcceleratorRecipeRegistry.removeByInput(CraftTweakerMC.getIngredient(input));
    }

    @ZenMethod
    public static void removeByOutput(IItemStack output) {
        AcceleratorRecipeRegistry.removeByOutput(CraftTweakerMC.getItemStack(output));
    }

    @ZenMethod
    public static void removeAll() {
        AcceleratorRecipeRegistry.removeAll();
    }
}
