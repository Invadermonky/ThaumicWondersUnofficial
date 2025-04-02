package com.verdantartifice.thaumicwonders.common.compat.crafttweaker.handlers;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.crafting.WeightedEntry;
import com.verdantartifice.thaumicwonders.common.crafting.accretionchamber.PrimordialAccretionChamberRegistry;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.WeightedItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ZenRegister
@ZenClass("mods." + ThaumicWonders.MODID + ".PrimordialAccretionChamber")
public class CTPrimordialAccretionChamber {
    //TODO

    @ZenMethod
    public static void addRecipe(IIngredient input, int aer, int aqua, int ignis, int terra, int ordo, int perditio, WeightedItemStack... weightedOutputs) {
        List<WeightedEntry> outputs = new ArrayList<>();
        Arrays.stream(weightedOutputs).forEach(output -> outputs.add(
                new WeightedEntry(CraftTweakerMC.getItemStack(output.getStack()), Math.max(1, (int) output.getPercent()))
        ));
        PrimordialAccretionChamberRegistry.addRecipe(
                CraftTweakerMC.getIngredient(input),
                aer, aqua, ignis, terra, ordo, perditio,
                outputs.toArray(new WeightedEntry[0])
        );
    }

    @ZenMethod
    public static void addRecipe(IIngredient input, int aspectCost, WeightedItemStack... weightedOutputs) {
        addRecipe(input, aspectCost, aspectCost, aspectCost, aspectCost, aspectCost, aspectCost, weightedOutputs);
    }

    @ZenMethod
    public static void removeByInput(IIngredient input) {
        PrimordialAccretionChamberRegistry.removeByInput(CraftTweakerMC.getIngredient(input));
    }

    @ZenMethod
    public static void removeByOutput(IItemStack output) {
        PrimordialAccretionChamberRegistry.removeByOutput(CraftTweakerMC.getItemStack(output));
    }

    @ZenMethod
    public static void removeAll() {
        PrimordialAccretionChamberRegistry.removeAll();
    }
}
