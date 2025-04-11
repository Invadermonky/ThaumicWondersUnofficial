package com.verdantartifice.thaumicwonders.common.compat;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public interface IModPlugin {
    default void preInit() {
    }

    default void init() {
    }

    default void postInit() {
    }

    @SideOnly(Side.CLIENT)
    default void preInitClient() {
    }

    @SideOnly(Side.CLIENT)
    default void initClient() {
    }

    @SideOnly(Side.CLIENT)
    default void postInitClient() {
    }

    default void registerRecipes(IForgeRegistry<IRecipe> forgeRegistry) {
    }
}
