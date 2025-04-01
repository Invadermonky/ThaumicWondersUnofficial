package com.verdantartifice.thaumicwonders.common.config;

import net.minecraftforge.common.config.Config;

public class ConfigHandlerTW {
    public static CleansingCharmCategory cleansing_charm = new CleansingCharmCategory();
    public static MeatyOrbCategory meaty_orb = new MeatyOrbCategory();
    public static VoidBeaconCategory void_beacon = new VoidBeaconCategory();

    public static class CleansingCharmCategory {
        @Config.RangeInt(min = 1, max = 72000)
        @Config.Name("Removal Time")
        @Config.Comment("The time, in ticks, it takes the Cleansing Charm to complete a full operation.")
        public int timeToRemoveFlux = 72000;

        @Config.RangeInt(min = 1, max = 100)
        @Config.Name("Flux Removed")
        @Config.Comment("The amount of 'Sticky' warp removed from the player per full operation.")
        public int fluxRemoved = 1;
    }

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

    public static class VoidBeaconCategory {
        @Config.RangeInt(min = 20, max = 10000)
        @Config.Name("Essentia Cost")
        @Config.Comment("The amount of essentia required to generate a drop from the Void Beacon. Each beacon tier will reduce this amount by half.")
        public int baseEssentiaCost = 20;
    }
}
