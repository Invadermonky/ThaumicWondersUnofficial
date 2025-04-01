package com.verdantartifice.thaumicwonders.common.crafting.catalyzationchamber;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.config.ConfigHandlerTW;
import com.verdantartifice.thaumicwonders.common.crafting.IngredientHelper;
import com.verdantartifice.thaumicwonders.common.items.ItemsTW;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;

import java.util.HashSet;
import java.util.Set;

public class CatalyzationChamberRegistry {
    private static final Set<CatalyzationChamberRecipe> CATALYZATION_RECIPES = new HashSet<>();

    public static void addRecipe(CatalyzationChamberRecipe recipe) {
        CATALYZATION_RECIPES.add(recipe);
    }

    public static void addRecipe(Ingredient input, Ingredient catalyst, ItemStack output, int fluxChance, Aspect color) {
        if(input == null || input == Ingredient.EMPTY) {
            ThaumicWonders.LOGGER.error("Input cannot be empty");
        } else if(catalyst == null || catalyst == Ingredient.EMPTY) {
            ThaumicWonders.LOGGER.error("Catalyst cannot be empty");
        } else if(output.isEmpty()) {
            ThaumicWonders.LOGGER.error("Output cannot be empty");
        } else if(fluxChance <= 0) {
            ThaumicWonders.LOGGER.error("Flux chance cannot be less than 0");
        } else {
            try {
                addRecipe(new CatalyzationChamberRecipe(input, catalyst, output, fluxChance, color));
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public static void addRecipe(Ingredient input, Ingredient catalyst, ItemStack output, int fluxChance) {
        addRecipe(input, catalyst, output, fluxChance, Aspect.ORDER);
    }

    public static void addAlchemistRecipe(Ingredient input, ItemStack output) {
        addRecipe(input, Ingredient.fromItem(ItemsTW.ALCHEMIST_STONE), output, ConfigHandlerTW.alchemist_stone.defaultFluxChance, Aspect.ORDER);
    }

    public static void addAlchemistRecipe(String inputOreDict, ItemStack output) {
        if(OreDictionary.doesOreNameExist(inputOreDict)) {
            addAlchemistRecipe(CraftingHelper.getIngredient(inputOreDict), output);
        }
    }

    public static void addAlienistRecipe(Ingredient input, ItemStack output) {
        addRecipe(input, Ingredient.fromItem(ItemsTW.ALIENIST_STONE), output, ConfigHandlerTW.alienist_stone.defaultFluxChance, Aspect.FLUX);
    }

    public static void addAlienistRecipe(String inputOreDict, ItemStack output) {
        if(OreDictionary.doesOreNameExist(inputOreDict)) {
            addAlienistRecipe(CraftingHelper.getIngredient(inputOreDict), output);
        }
    }

    public static void addTransmuterRecipe(Ingredient input, ItemStack output) {
        addRecipe(input, Ingredient.fromItem(ItemsTW.TRANSMUTER_STONE), output, ConfigHandlerTW.transmuter_stone.defaultFluxChance, Aspect.EXCHANGE);
    }

    public static void addTransmuterRecipe(String inputOreDict, ItemStack output) {
        if(OreDictionary.doesOreNameExist(inputOreDict)) {
            addTransmuterRecipe(CraftingHelper.getIngredient(inputOreDict), output);
        }
    }

    public static void addTransmuterRecipe(String inputOreDict, String outputOreDict) {
        if(OreDictionary.doesOreNameExist(outputOreDict)) {
            OreDictionary.getOres(outputOreDict, false).stream().findFirst().ifPresent(output -> addTransmuterRecipe(inputOreDict, output));
        }
    }

    public static void removeByInput(Ingredient input) {
        CATALYZATION_RECIPES.removeIf(recipe ->
                IngredientHelper.areIngredientsEqual(recipe.getInput(), input));
    }

    public static void removeByInput(Ingredient input, Ingredient catalyst) {
        CATALYZATION_RECIPES.removeIf(recipe ->
                IngredientHelper.areIngredientsEqual(recipe.getInput(), input) && IngredientHelper.areIngredientsEqual(recipe.getCatalyst(), catalyst));
    }

    public static void removeByOutput(ItemStack output, Ingredient catalyst) {
        CATALYZATION_RECIPES.removeIf(recipe ->
                ItemStack.areItemStacksEqual(recipe.getOutput(), output) && IngredientHelper.areIngredientsEqual(recipe.getCatalyst(), catalyst));
    }

    public static void removeByOutput(ItemStack output) {
        CATALYZATION_RECIPES.removeIf(recipe ->
                ItemStack.areItemStacksEqual(output, recipe.getOutput()));
    }

    public static void removeAll() {
        CATALYZATION_RECIPES.clear();
    }

    public static boolean isValidCatalyst(ItemStack stack) {
        return CATALYZATION_RECIPES.stream().anyMatch(recipe -> recipe.getCatalyst().test(stack));
    }

    @Nullable
    public static CatalyzationChamberRecipe getRecipe(ItemStack input, ItemStack catalyst) {
        if(input != null && !input.isEmpty() && catalyst != null && !catalyst.isEmpty()) {
            for (CatalyzationChamberRecipe recipe : CATALYZATION_RECIPES) {
                if (recipe.matches(input, catalyst)) {
                    return recipe;
                }
            }
        }
        return null;
    }
}
