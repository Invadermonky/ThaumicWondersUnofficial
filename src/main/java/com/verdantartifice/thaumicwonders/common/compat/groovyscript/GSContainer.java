package com.verdantartifice.thaumicwonders.common.compat.groovyscript;

import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import com.verdantartifice.thaumicwonders.common.compat.groovyscript.handlers.MeatyOrb;

public class GSContainer extends GroovyPropertyContainer {
    public final MeatyOrb MeatyOrb = new MeatyOrb();

    public GSContainer() {
        this.addProperty(MeatyOrb);
    }
}
