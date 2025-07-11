package com.verdantartifice.thaumicwonders.common.compat.jei.category;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.compat.jei.wrapper.MeatyOrbWrapper;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.util.Translator;
import net.minecraft.util.ResourceLocation;

public class MeatyOrbCategory implements IRecipeCategory<MeatyOrbWrapper> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(ThaumicWonders.MODID, "textures/gui/jei_guis.png");
    public static final String UID = ThaumicWonders.MODID + ".meatyOrb";
    public static final String L18N_KEY = "jei." + ThaumicWonders.MODID + ".recipe.meaty_orb";

    private final String localizedName;
    private final IDrawable background;

    public MeatyOrbCategory(IGuiHelper helper) {
        this.localizedName = Translator.translateToLocal(L18N_KEY);
        this.background = helper.createDrawable(TEXTURE, 0, 0, 108, 24);
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
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, MeatyOrbWrapper wrapper, IIngredients ingredients) {
        IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
        stacks.init(0, false, 7, 3);
        stacks.set(0, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
    }
}
