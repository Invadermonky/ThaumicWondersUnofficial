package com.verdantartifice.thaumicwonders.common.compat.jei.util;

import mezz.jei.api.recipe.IRecipeWrapper;

public abstract class BaseRecipeWrapper<T> implements IRecipeWrapper {
    protected T recipe;
}
