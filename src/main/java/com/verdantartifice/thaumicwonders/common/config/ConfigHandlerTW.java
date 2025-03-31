package com.verdantartifice.thaumicwonders.common.config;

import net.minecraftforge.common.config.Config;

public class ConfigHandlerTW {

    public static MeatyOrbCategory meaty_orb = new MeatyOrbCategory();

    public static class MeatyOrbCategory {
        @Config.RangeInt(min = 5, max = 6000)
        @Config.Name("Activation Duration")
        @Config.Comment("The duration, in ticks, the Meaty Orb will remain on per activation. The orb spawns 1 item every 5 ticks.")
        public int meatyOrbDuration = 300;

        @Config.RangeInt(min = 1, max = 10000)
        @Config.Name("Activation Essentia")
        @Config.Comment("The amount of Aqua, Victus, and Alienis essentia that is needed to activate the Meaty Orb")
        public int essentiaRequirement = 250;
    }
}
