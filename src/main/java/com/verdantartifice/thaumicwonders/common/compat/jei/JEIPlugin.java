package com.verdantartifice.thaumicwonders.common.compat.jei;

import com.verdantartifice.thaumicwonders.common.blocks.BlocksTW;
import com.verdantartifice.thaumicwonders.common.compat.jei.category.CatalyzationChamberCategory;
import com.verdantartifice.thaumicwonders.common.compat.jei.category.MeatyOrbCategory;
import com.verdantartifice.thaumicwonders.common.compat.jei.wrapper.CatalyzationChamberRecipeWrapper;
import com.verdantartifice.thaumicwonders.common.compat.jei.wrapper.MeatyOrbWrapper;
import com.verdantartifice.thaumicwonders.common.crafting.WeightedEntry;
import com.verdantartifice.thaumicwonders.common.crafting.catalyzationchamber.CatalyzationChamberRecipe;
import com.verdantartifice.thaumicwonders.common.crafting.catalyzationchamber.CatalyzationChamberRecipeRegistry;
import com.verdantartifice.thaumicwonders.common.crafting.meatyorb.MeatyOrbEntryRegistry;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.Item;
import thaumcraft.api.aspects.AspectList;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("ConstantConditions")
@mezz.jei.api.JEIPlugin
public class JEIPlugin implements IModPlugin {
    public static final String ASPECT_PATH;
    public static final IIngredientType<AspectList> ASPECT_LIST;

    public static CatalyzationChamberCategory catalyzationChamber;
    public static MeatyOrbCategory meatyOrb;

    public static IJeiRuntime runtime;
    public static HashMap<IRecipeWrapper, String> recipes;
    public static Thread aspectCacheThread;

    public static IJeiHelpers HELPER;

    @Override
    public void registerIngredients(IModIngredientRegistration registry) {
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        registry.addRecipeCategories(
                catalyzationChamber = new CatalyzationChamberCategory(guiHelper),
                meatyOrb = new MeatyOrbCategory(guiHelper)
        );
    }

    @Override
    public void register(IModRegistry registry) {
        HELPER = registry.getJeiHelpers();

        initCatalyzationChamber(registry);
        initMeatyOrb(registry);

        registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(Item.getItemFromBlock(BlocksTW.COALESCENCE_MATRIX).getDefaultInstance());
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {

    }

    private void initCatalyzationChamber(IModRegistry registry) {
        registry.handleRecipes(CatalyzationChamberRecipe.class, CatalyzationChamberRecipeWrapper::new, catalyzationChamber.getUid());
        registry.addRecipeCatalyst(Item.getItemFromBlock(BlocksTW.CATALYZATION_CHAMBER).getDefaultInstance(), catalyzationChamber.getUid());
        registry.addRecipes(CatalyzationChamberRecipeRegistry.getRecipes(), catalyzationChamber.getUid());
        registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(Item.getItemFromBlock(BlocksTW.PLACEHOLDER_ARCANE_STONE).getDefaultInstance());
        registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(Item.getItemFromBlock(BlocksTW.PLACEHOLDER_OBSIDIAN).getDefaultInstance());
    }

    private void initMeatyOrb(IModRegistry registry) {
        registry.handleRecipes(WeightedEntry.class, MeatyOrbWrapper::new, MeatyOrbCategory.UID);
        registry.addRecipeCatalyst(Item.getItemFromBlock(BlocksTW.MEATY_ORB).getDefaultInstance(), meatyOrb.getUid());
        registry.addRecipes(MeatyOrbEntryRegistry.getEntries(), MeatyOrbCategory.UID);
    }

    private class AspectCache {
        private String aspect;
        private List<String> items;

        public AspectCache() {
            this.items = new ArrayList<>();
        }

        public AspectCache(String aspect) {
            this();
            this.aspect = aspect;
        }
    }

    static {
        ASPECT_PATH = "." + File.separator + "config" + File.separator + "thaumicjei_itemstack_aspects.json";
        ASPECT_LIST = () -> AspectList.class;
        recipes = new HashMap<>();
    }
}
