package com.verdantartifice.thaumicwonders.common.compat.groovyscript;

import com.cleanroommc.groovyscript.documentation.linkgenerator.BasicLinkGenerator;
import com.verdantartifice.thaumicwonders.ThaumicWonders;

public class GSLinkGenerator extends BasicLinkGenerator {
    @Override
    public String id() {
        return ThaumicWonders.MODID;
    }

    @Override
    protected String version() {
        return ThaumicWonders.VERSION;
    }

    @Override
    protected String domain() {
        return "https://github.com/Invadermonky/ThaumicWondersUnofficial/";
    }
}
