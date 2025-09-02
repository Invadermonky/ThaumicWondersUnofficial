package com.verdantartifice.thaumicwonders.core;

import com.verdantartifice.thaumicwonders.common.compat.ModIds;
import zone.rong.mixinbooter.Context;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Collections;
import java.util.List;

public class ModMixins implements ILateMixinLoader {
    @Override
    public List<String> getMixinConfigs() {
        return Collections.singletonList("mixins.thaumicwonders.json");
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        return !ModIds.tc4research.isLoaded && !ModIds.thaumtweaks.isLoaded;
    }
}
