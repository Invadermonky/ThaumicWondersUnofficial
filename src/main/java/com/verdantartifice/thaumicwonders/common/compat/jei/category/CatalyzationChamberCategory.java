package com.verdantartifice.thaumicwonders.common.compat.jei.category;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.blocks.BlocksTW;
import com.verdantartifice.thaumicwonders.common.compat.jei.wrapper.CatalyzationChamberRecipeWrapper;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.util.Translator;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class CatalyzationChamberCategory implements IRecipeCategory<CatalyzationChamberRecipeWrapper> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(ThaumicWonders.MODID, "textures/gui/jei_guis.png");
    public static final String UID = ThaumicWonders.MODID + ".catalyzationChamber";
    public static final String L18N_KEY = "jei." + ThaumicWonders.MODID + ".recipe.catalyzation";

    private final String localizedName;
    private final IDrawable background;
    private final IDrawable icon;

    public CatalyzationChamberCategory(IGuiHelper helper) {
        this.localizedName = Translator.translateToLocal(L18N_KEY);
        this.background = helper.createDrawable(TEXTURE, 0, 33, 112, 47);
        this.icon = helper.createDrawableIngredient(Item.getItemFromBlock(BlocksTW.CATALYZATION_CHAMBER).getDefaultInstance());
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
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, CatalyzationChamberRecipeWrapper wrapper, IIngredients ingredients) {
        IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
        stacks.init(0, true, 7, 14);
        stacks.init(1, true, 32, 14);
        stacks.init(2, false, 82, 14);

        if (ingredients.getInputs(VanillaTypes.ITEM).size() == 2) {
            stacks.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
            stacks.set(1, ingredients.getInputs(VanillaTypes.ITEM).get(1));
        }
        stacks.set(2, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
    }
}
