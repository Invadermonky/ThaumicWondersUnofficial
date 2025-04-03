package com.verdantartifice.thaumicwonders.common.compat.crafttweaker.handlers;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.crafting.catalyzationchamber.CatalyzationChamberRecipeRegistry;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import thaumcraft.api.aspects.Aspect;

@ZenRegister
@ZenClass("mods." + ThaumicWonders.MODID + ".CatalyzationChamber")
public class CTCatalyzationChamber {
    @ZenMethod
    public static void addRecipe(IIngredient input, IIngredient catalyst, IItemStack output, int fluxChance, @Optional Aspect color) {
        CatalyzationChamberRecipeRegistry.addRecipe(
                CraftTweakerMC.getIngredient(input),
                CraftTweakerMC.getIngredient(catalyst),
                CraftTweakerMC.getItemStack(output),
                Math.max(fluxChance, 0),
                color
        );
    }

    @ZenMethod
    public static void addAlchemistRecipe(IIngredient input, IItemStack output) {
        CatalyzationChamberRecipeRegistry.addAlchemistRecipe(CraftTweakerMC.getIngredient(input), CraftTweakerMC.getItemStack(output));
    }

    @ZenMethod
    public static void addAlienistRecipe(IIngredient input, IItemStack output) {
        CatalyzationChamberRecipeRegistry.addAlienistRecipe(CraftTweakerMC.getIngredient(input), CraftTweakerMC.getItemStack(output));
    }

    @ZenMethod
    public static void addTransmuterRecipe(IIngredient input, IItemStack output) {
        CatalyzationChamberRecipeRegistry.addTransmuterRecipe(CraftTweakerMC.getIngredient(input), CraftTweakerMC.getItemStack(output));
    }

    @ZenMethod
    public static void removeByInput(IIngredient input, @Optional IIngredient catalyst) {
        if(catalyst == null) {
            CatalyzationChamberRecipeRegistry.removeByInput(CraftTweakerMC.getIngredient(input));
        } else {
            CatalyzationChamberRecipeRegistry.removeByInput(CraftTweakerMC.getIngredient(input), CraftTweakerMC.getIngredient(catalyst));
        }
    }

    @ZenMethod
    public static void removeByOutput(IItemStack output, @Optional IIngredient catalyst) {
        if(catalyst == null) {
            CatalyzationChamberRecipeRegistry.removeByOutput(CraftTweakerMC.getItemStack(output));
        } else {
            CatalyzationChamberRecipeRegistry.removeByOutput(CraftTweakerMC.getItemStack(output), CraftTweakerMC.getIngredient(catalyst));
        }
    }

    @ZenMethod
    public static void removeAll() {
        CatalyzationChamberRecipeRegistry.removeAll();
    }
}
