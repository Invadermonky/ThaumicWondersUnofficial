package com.verdantartifice.thaumicwonders.common.compat.jei.wrapper;

import com.verdantartifice.thaumicwonders.common.compat.jei.util.BaseRecipeWrapper;
import com.verdantartifice.thaumicwonders.common.crafting.voidbeacon.VoidBeaconEntry;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;

import java.util.Collections;
import java.util.List;

public class VoidBeaconWrapper extends BaseRecipeWrapper<VoidBeaconEntry> {
    public VoidBeaconWrapper(VoidBeaconEntry entry) {
        this.recipe = entry;
    }

    @Override
    public void getIngredients(IIngredients iIngredients) {

    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return Collections.emptyList();
    }

    @Override
    public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        return false;
    }
}
