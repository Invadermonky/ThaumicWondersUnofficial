package com.verdantartifice.thaumicwonders.common.config;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ConfigHandlerTW {
    @Config.Name("Alchemist Stone")
    public static CatalystStoneCategory alchemist_stone = new CatalystStoneCategory(50);
    @Config.Name("Alienist Stone")
    public static CatalystStoneCategory alienist_stone = new CatalystStoneCategory(10);
    @Config.Name("Transmuter Stone")
    public static CatalystStoneCategory transmuter_stone = new CatalystStoneCategory(33);
    @Config.Name("Cleansing Charm")
    public static CleansingCharmCategory cleansing_charm = new CleansingCharmCategory();
    @Config.Name("Meaty Orb")
    public static MeatyOrbCategory meaty_orb = new MeatyOrbCategory();
    @Config.Name("Void Beacon")
    public static VoidBeaconCategory void_beacon = new VoidBeaconCategory();

    public static class CatalystStoneCategory {
        @Config.RequiresMcRestart
        @Config.Name("Catalyst Stone Uses")
        @Config.Comment("The number of uses before the catalyst stone is consumed")
        public int durability = 64;

        @Config.Name("Enable Stone Enchants")
        @Config.Comment("Allows the Catalyst Stone to be enchanted with Unbreaking and Mending")
        public boolean enchantable = true;

        @Config.RequiresMcRestart
        @Config.RangeInt(min = 0, max = 10000)
        @Config.Name("Default Flux Chance")
        @Config.Comment
                ({
                        "The chance any default recipe using this catalyst stone will produce flux. The actual chance is",
                        "1 / x, where x is this value. Setting this value to 0 will disable all flux generation."
                })
        public int defaultFluxChance;

        public CatalystStoneCategory(int defaultFluxChance) {
            this.defaultFluxChance = defaultFluxChance;
        }
    }

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

    @Mod.EventBusSubscriber(modid = ThaumicWonders.MODID)
    public static class ConfigChangeListener {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if(event.getModID().equals(ThaumicWonders.MODID)) {
                ConfigManager.sync(ThaumicWonders.MODID, Config.Type.INSTANCE);
            }
        }
    }
}
