package com.verdantartifice.thaumicwonders.common.compat.jei.category;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.compat.jei.wrapper.VoidBeaconWrapper;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class VoidBeaconCategory implements IRecipeCategory<VoidBeaconWrapper> {
    public static final String UID = ThaumicWonders.MODID + ".voidBeacon";
    public static final String L18N_KEY = "jei." + ThaumicWonders.MODID + ".recipe.void_beacon";

    private final String localizedName;

    public VoidBeaconCategory(IGuiHelper helper) {
        this.localizedName = Translator.translateToLocal(L18N_KEY);
    }

    @Override
    public String getUid() {
        return UID;
    }

    @Override
    public String getTitle() {
        return localizedName;
    }

    @Override
    public String getModName() {
        return ThaumicWonders.NAME;
    }

    @Override
    public IDrawable getBackground() {
        return null;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return null;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {

    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, VoidBeaconWrapper voidBeaconWrapper, IIngredients iIngredients) {

    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return Collections.emptyList();
    }
}
