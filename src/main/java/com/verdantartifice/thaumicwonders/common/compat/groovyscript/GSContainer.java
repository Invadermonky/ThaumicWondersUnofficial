package com.verdantartifice.thaumicwonders.common.compat.groovyscript;

import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import com.verdantartifice.thaumicwonders.common.compat.groovyscript.handlers.*;

public class GSContainer extends GroovyPropertyContainer {
    public final CatalyzationChamber CatalyzationChamber = new CatalyzationChamber();
    public final MeatyOrb MeatyOrb = new MeatyOrb();
    public final PrimordialAccelerator PrimordialAccelerator = new PrimordialAccelerator();
    public final PrimordialAccretionChamber PrimordialAccretionChamber = new PrimordialAccretionChamber();
    public final VoidBeacon VoidBeacon = new VoidBeacon();

    public GSContainer() {
        this.addProperty(CatalyzationChamber);
        this.addProperty(MeatyOrb);
        this.addProperty(PrimordialAccelerator);
        this.addProperty(PrimordialAccretionChamber);
        this.addProperty(VoidBeacon);
    }
}
