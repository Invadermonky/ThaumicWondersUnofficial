package com.verdantartifice.thaumicwonders.common.crafting.accretionchamber;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.crafting.IngredientHelper;
import com.verdantartifice.thaumicwonders.common.crafting.WeightedEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AccretionChamberRecipeRegistry {
    private static final Set<AccretionChamberRecipe> ACCRETION_CHAMBER_RECIPES = new HashSet<>();

    public static Set<AccretionChamberRecipe> getRecipes() {
        return ACCRETION_CHAMBER_RECIPES;
    }

    public static void addRecipe(AccretionChamberRecipe recipe) {
        ACCRETION_CHAMBER_RECIPES.add(recipe);
    }

    public static void addRecipe(Ingredient input, int aer, int aqua, int ignis, int terra, int ordo, int perditio, WeightedEntry... weightedOutput) {
        try {
            addRecipe(new AccretionChamberRecipe(input, aer, aqua, ignis, terra, ordo, perditio, weightedOutput));
        } catch (IllegalArgumentException e) {
            ThaumicWonders.LOGGER.error("Failed to add Primordial Accretion Chamber recipe");
            ThaumicWonders.LOGGER.error(e);
        }
    }

    public static void addRecipe(Ingredient input, int aspectCost, WeightedEntry... weightedOutput) {
        addRecipe(input, aspectCost, aspectCost, aspectCost, aspectCost, aspectCost, aspectCost, weightedOutput);
    }

    public static void addRecipe(Ingredient input, int aspectCost, ItemStack... outputs) {
        addRecipe(input, aspectCost, Arrays.stream(outputs).map(output -> new WeightedEntry(output, 1)).toArray(WeightedEntry[]::new));
    }

    public static void removeByInput(Ingredient input) {
        ACCRETION_CHAMBER_RECIPES.removeIf(recipe -> IngredientHelper.areIngredientsEqual(recipe.getInput(), input));
    }

    public static void removeByOutput(ItemStack output) {
        ACCRETION_CHAMBER_RECIPES.removeIf(recipe -> recipe.matchesOutput(output));
    }

    public static void removeAll() {
        ACCRETION_CHAMBER_RECIPES.clear();
    }

    @Nullable
    public static AccretionChamberRecipe getRecipe(ItemStack input) {
        if(!input.isEmpty()) {
            return ACCRETION_CHAMBER_RECIPES.stream().filter(recipe -> recipe.matchesInput(input)).findFirst().orElse(null);
        }
        return null;
    }
}
