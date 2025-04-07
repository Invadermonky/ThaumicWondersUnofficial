package com.verdantartifice.thaumicwonders.common.config;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = ThaumicWonders.MODID)
public class ConfigHandlerTW {
    @Config.Name("Catalyst Stones")
    public static CatalystStoneCategory catalyst_stones = new CatalystStoneCategory();
    @Config.Name("Cleansing Charm")
    public static CleansingCharmCategory cleansing_charm = new CleansingCharmCategory();
    @Config.Name("Client")
    public static ClientCategory client = new ClientCategory();
    @Config.Name("Flying Carpet")
    public static FlyingCarpetCategory flying_carpet = new FlyingCarpetCategory();
    @Config.Name("Meaty Orb")
    public static MeatyOrbCategory meaty_orb = new MeatyOrbCategory();
    @Config.Name("Night Vision Goggles")
    public static NightVisionGogglesCategory night_vision_goggles = new NightVisionGogglesCategory();
    @Config.Name("Sharing Tome")
    public static SharingTomeCategory sharing_tome = new SharingTomeCategory();
    @Config.Name("Void Beacon")
    public static VoidBeaconCategory void_beacon = new VoidBeaconCategory();

    public static class CatalystStoneCategory {
        @Config.Name("Alchemist Stone")
        public StoneCategory alchemist_stone = new StoneCategory(50);
        @Config.Name("Alienist Stone")
        public StoneCategory alienist_stone = new StoneCategory(10);
        @Config.Name("Transmuter Stone")
        public StoneCategory transmuter_stone = new StoneCategory(33);

        public static class StoneCategory {
            @Config.RequiresMcRestart
            @Config.Name("Catalyst Stone Uses")
            @Config.Comment("The number of uses before the catalyst stone is consumed. [default: 64]")
            public int durability = 64;

            @Config.Name("Enable Stone Enchants")
            @Config.Comment("Allows the Catalyst Stone to be enchanted with Unbreaking and Mending. [default: true]")
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

            public StoneCategory(int defaultFluxChance) {
                this.defaultFluxChance = defaultFluxChance;
            }
        }
    }

    public static class CleansingCharmCategory {
        @Config.RangeInt(min = 1, max = 99999)
        @Config.Name("Capacity")
        @Config.Comment("The maximum Vis capacity of the Cleansing Charm. [default: 200]")
        @Config.RequiresMcRestart
        public int capacity = 200;

        @Config.RangeInt(min = 1, max = 72000)
        @Config.Name("Removal Time")
        @Config.Comment("The time, in ticks, it takes the Cleansing Charm to complete a full operation. [default: 24000]")
        public int timeToRemoveFlux = 24000;        

        @Config.RangeDouble(min = 0.1, max = 99999)
        @Config.Name("Flux Amount")
        @Config.Comment("The amount of polluting Flux that generates from the activated Cleansing Charm. [default: 0.1]")
        @Config.RequiresMcRestart
        public double fluxAmount = 0.1D;

        @Config.RangeInt(min = 1, max = 100)
        @Config.Name("Flux Removed")
        @Config.Comment("The amount of 'Sticky' warp removed from the player per full operation. [default: 1]")
        public int fluxRemoved = 1;

        @Config.RangeInt(min = 1, max = 99999)
        @Config.Name("Flux Time")
        @Config.Comment("The amount of time, in ticks, it takes for polluting Flux to dissipate into the aura while the activated Cleansing Charm is worn. [default: 600]")
        @Config.RequiresMcRestart
        public int fluxTime = 600;
    }

    public static class ClientCategory {
        @Config.Name("HUDs")
        @Config.Comment("Whether or not to enable the HUDs displayed from the Magic Carpet and the Primal Destroyer. [default: true]")
        @Config.RequiresMcRestart
        public boolean huds = true;
    }

    public static class FlyingCarpetCategory {
        @Config.RangeDouble(min = 0, max = 20.0)
        @Config.Name("Max Speed")
        @Config.Comment("Magic Carpet maximum speed. [default: 5.0]")
        public double maxSpeed = 5.0;

        @Config.RangeInt(min = 1, max = 10000)
        @Config.Name("Energy Per Vis")
        @Config.Comment("The energy gained per point of Vis consumed. Each point of energy translates to 1 second of flight. [default: 30]")
        public int energyPerVis = 30;

        @Config.RangeInt(min = 1, max = 10000)
        @Config.Name("Vis Capacity")
        @Config.Comment("The maximum Vis that can be stored in the Flying Carpet. [default: 240]")
        public int visCapacity = 240;
    }

    public static class MeatyOrbCategory {
        @Config.RangeInt(min = 5, max = 6000)
        @Config.Name("Activation Duration")
        @Config.Comment("The duration, in ticks, the Meaty Orb will remain on per activation. The orb spawns 1 item every 5 ticks. [default: 300]")
        public int meatyOrbDuration = 300;

        @Config.RangeInt(min = 1, max = 10000)
        @Config.Name("Activation Essentia")
        @Config.Comment("The amount of Aqua, Victus, and Alienis essentia that is needed to activate the Meaty Orb. [default: 250]")
        public int essentiaRequirement = 250;
    }

    public static class NightVisionGogglesCategory {
        @Config.Name("Adaptive Night Vision")
        @Config.Comment("Goggles of Night Vision only apply their effect when the player is in or looking at darkness. [default: true]")
        public boolean adaptiveNightVision = true;

        @Config.RangeInt(min = 1, max = 10000)
        @Config.Name("Energy Per Vis")
        @Config.Comment("The energy gained per point of Vis consumed. Each point of energy translates to 1 second of Night Vision. [default: 40]")
        public int energyPerVis = 40;

        @Config.RangeInt(min = 1, max = 10000)
        @Config.Name("Vis Capacity")
        @Config.Comment("The maximum Vis that can be stored in the Goggles of Night Vision. [default: 100]")
        public int visCapacity = 100;
    }

    public static class SharingTomeCategory {
        @Config.RequiresMcRestart
        @Config.Name("Enable Sharing Tome")
        @Config.Comment("One of the only Thaumcraft Sharing Tomes that comes with a on/off switch. [default: true]")
        public boolean enableTome = true;

        @Config.Name("Consume Tome")
        @Config.Comment("Destroys the sharing tome after a player uses it to gain knowledge. [default: false]")
        public boolean consumeTome = false;

        @Config.RangeInt(min = 0, max = 100)
        @Config.Name("Experience Required")
        @Config.Comment("The amount of experience, in levels, required for a player to gain knowledge from the tome. [default: 10]")
        public int requiredExp = 10;

        @Config.Name("Share Observations")
        @Config.Comment("Observations will also be shared when using the Sharing Tome. [default: true]")
        public boolean shareObservations = true;
    }

    public static class VoidBeaconCategory {
        @Config.RangeInt(min = 20, max = 10000)
        @Config.Name("Essentia Cost")
        @Config.Comment("The amount of essentia required to generate a drop from the Void Beacon. Each beacon tier will reduce this amount by half. [default: 20]")
        public int baseEssentiaCost = 20;
    }

    @Mod.EventBusSubscriber(modid = ThaumicWonders.MODID)
    public static class ConfigChangeListener {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(ThaumicWonders.MODID)) {
                ConfigManager.sync(ThaumicWonders.MODID, Config.Type.INSTANCE);
            }
        }
    }
}
