package com.verdantartifice.thaumicwonders.common.compat;

import net.minecraftforge.fml.common.Loader;

public enum ModIds {
    crafttweaker("crafttweaker"),
    expanded_arcanum("ea"),
    jeresources("jeresources"),
    the_one_probe("theoneprobe");

    public final String modId;
    public final boolean isLoaded;

    ModIds(String modId) {
        this.modId = modId;
        this.isLoaded = Loader.isModLoaded(modId);
    }
}
