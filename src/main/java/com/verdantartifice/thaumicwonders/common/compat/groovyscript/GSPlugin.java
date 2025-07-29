package com.verdantartifice.thaumicwonders.common.compat.groovyscript;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyPlugin;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import com.cleanroommc.groovyscript.documentation.linkgenerator.LinkGeneratorHooks;
import com.verdantartifice.thaumicwonders.ThaumicWonders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GSPlugin implements GroovyPlugin {
    @GroovyBlacklist
    public static GSContainer instance;

    @NotNull
    @Override
    public String getModId() {
        return ThaumicWonders.MODID;
    }

    @NotNull
    @Override
    public String getContainerName() {
        return ThaumicWonders.NAME;
    }

    @Override
    public void onCompatLoaded(GroovyContainer<?> groovyContainer) {
        LinkGeneratorHooks.registerLinkGenerator(new GSLinkGenerator());
    }

    @Override
    public @Nullable GroovyPropertyContainer createGroovyPropertyContainer() {
        return instance == null ? instance = new GSContainer() : instance;
    }
}
