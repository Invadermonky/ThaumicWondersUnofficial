package com.verdantartifice.thaumicwonders.common.compat.jei.wrapper;

import com.google.common.collect.Lists;
import com.verdantartifice.thaumicwonders.common.compat.jei.util.BaseRecipeWrapper;
import com.verdantartifice.thaumicwonders.common.crafting.catalyzationchamber.CatalyzationChamberRecipe;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.List;

public class CatalyzationChamberRecipeWrapper extends BaseRecipeWrapper<CatalyzationChamberRecipe> {
    public CatalyzationChamberRecipeWrapper(CatalyzationChamberRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        List<ItemStack> inputStacks = Arrays.asList(this.recipe.getInput().getMatchingStacks());
        List<ItemStack> catalystStacks = Arrays.asList(this.recipe.getCatalyst().getMatchingStacks());
        ingredients.setInputLists(VanillaTypes.ITEM, Lists.newArrayList(inputStacks, catalystStacks));
        ingredients.setOutput(VanillaTypes.ITEM, this.recipe.getOutput());
    }
}
