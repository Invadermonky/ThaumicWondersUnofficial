package com.verdantartifice.thaumicwonders.common.compat.jei.wrapper;

import com.verdantartifice.thaumicwonders.common.compat.jei.util.BaseRecipeWrapper;
import com.verdantartifice.thaumicwonders.common.compat.jei.util.Font;
import com.verdantartifice.thaumicwonders.common.crafting.WeightedEntry;
import com.verdantartifice.thaumicwonders.common.crafting.meatyorb.MeatyOrbEntryRegistry;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class MeatyOrbWrapper extends BaseRecipeWrapper<WeightedEntry> {
    public MeatyOrbWrapper(WeightedEntry entry) {
        this.recipe = entry;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setOutput(VanillaTypes.ITEM, this.recipe.getStack());
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        double chance = (double) this.recipe.getWeight() / (double) MeatyOrbEntryRegistry.getTotalWeight();
        String text = I18n.format("jei.thaumicwonders.chance", (int) (chance * 100.0));
        Font.LARGE.print(text, 37, 9);
    }
}
