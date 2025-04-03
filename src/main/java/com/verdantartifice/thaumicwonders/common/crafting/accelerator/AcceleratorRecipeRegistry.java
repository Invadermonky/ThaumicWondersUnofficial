package com.verdantartifice.thaumicwonders.common.crafting.accelerator;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.crafting.IngredientHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class AcceleratorRecipeRegistry {
    private static final Set<AcceleratorRecipe> ACCELERATOR_RECIPES = new HashSet<>();

    public static Set<AcceleratorRecipe> getRecipes() {
        return ACCELERATOR_RECIPES;
    }

    public static void addRecipe(AcceleratorRecipe recipe) {
        ACCELERATOR_RECIPES.add(recipe);
    }

    public static void addRecipe(Ingredient input, ItemStack output) {
        try{
            addRecipe(new AcceleratorRecipe(input, output));
        } catch (IllegalArgumentException e) {
            ThaumicWonders.LOGGER.error("Failed to add Primordial Accelerator recipe");
            ThaumicWonders.LOGGER.error(e);
        }
    }

    public static void removeByInput(Ingredient ingredient) {
        ACCELERATOR_RECIPES.removeIf(recipe -> IngredientHelper.areIngredientsEqual(ingredient, recipe.getInput()));
    }

    public static void removeByOutput(ItemStack output) {
        ACCELERATOR_RECIPES.removeIf(recipe -> ItemStack.areItemStacksEqual(output, recipe.getOutput()));
    }

    public static void removeAll() {
        ACCELERATOR_RECIPES.clear();
    }

    @Nullable
    public static AcceleratorRecipe getRecipe(ItemStack input) {
        if(!input.isEmpty()) {
            return ACCELERATOR_RECIPES.stream().filter(recipe -> recipe.matches(input)).findFirst().orElse(null);
        }
        return null;
    }
}
