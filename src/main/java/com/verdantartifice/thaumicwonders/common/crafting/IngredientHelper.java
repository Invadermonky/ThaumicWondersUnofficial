package com.verdantartifice.thaumicwonders.common.crafting;

import net.minecraft.item.crafting.Ingredient;

import java.util.Arrays;

public class IngredientHelper {
    public static boolean areIngredientsEqual(Ingredient ingredient1, Ingredient ingredient2) {
        if (ingredient1 == null || ingredient2 == null) {
            return false;
        } else if (ingredient1 == Ingredient.EMPTY || ingredient2 == Ingredient.EMPTY) {
            return false;
        } else if (ingredient1.getMatchingStacks().length != ingredient2.getMatchingStacks().length) {
            return false;
        } else {
            return Arrays.stream(ingredient1.getMatchingStacks()).allMatch(ingredient2);
        }
    }
}
