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
    @Config.Name("Void Walker Boots")
    public static VoidWalkerBootsCategory void_walker_boots = new VoidWalkerBootsCategory();
    //TODO: Rename and redo this
    @Config.Ignore
    @Config.Name("Ring of Warped Cleansing")
    public static WarpRingCategory warp_ring = new WarpRingCategory();

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

            public StoneCategory(int defaultFluxChance) {
                this.defaultFluxChance = defaultFluxChance;
            }
        }
    }

    public static class CleansingCharmCategory {
        @Config.RangeInt(min = 1, max = 72000)
        @Config.Name("Removal Time")
        @Config.Comment("The time, in ticks, it takes the Cleansing Charm to complete a full operation.")
        public int timeToRemoveFlux = 24000;

        @Config.RangeInt(min = 1, max = 100)
        @Config.Name("Flux Removed")
        @Config.Comment("The amount of 'Sticky' warp removed from the player per full operation.")
        public int fluxRemoved = 1;
    }

    public static class FlyingCarpetCategory {
        @Config.RangeDouble(min = 0, max = 20.0)
        @Config.Name("Max Speed")
        @Config.Comment("Magic Carpet maximum speed")
        public double maxSpeed = 6.0;

        @Config.RangeInt(min = 1, max = 10000)
        @Config.Name("Energy Per Vis")
        @Config.Comment("The energy gained per point of Vis consumed. Each point of energy translates to 1 second of flight.")
        public int energyPerVis = 30;

        @Config.RangeInt(min = 1, max = 10000)
        @Config.Name("Vis Capacity")
        @Config.Comment("The maximum Vis that can be stored in the Flying Carpet")
        public int visCapacity = 240;
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

    public static class NightVisionGogglesCategory {
        @Config.Name("Adaptive Night Vision")
        @Config.Comment("Night Vision Goggles only apply their effect when the player is in or looking at darkness.")
        public boolean adaptiveNightVision = true;

        @Config.RangeInt(min = 1, max = 10000)
        @Config.Name("Energy Per Vis")
        @Config.Comment("The energy gained per point of Vis consumed. Each point of energy translates to 1 second of Night Vision.")
        public int energyPerVis = 40;

        @Config.RangeInt(min = 1, max = 10000)
        @Config.Name("Vis Capacity")
        @Config.Comment("The maximum Vis that can be stored in the Night Vision Goggles")
        public int visCapacity = 100;
    }

    public static class SharingTomeCategory {
        @Config.RequiresMcRestart
        @Config.Name("Enable Sharing Tome")
        @Config.Comment("One of the only Thaumcraft Sharing Tomes that comes with a on/off switch")
        public boolean enableTome = true;

        @Config.Name("Consume Tome")
        @Config.Comment("Destroys the sharing tome after a player uses it to gain knowledge")
        public boolean consumeTome = false;

        @Config.RangeInt(min = 0, max = 100)
        @Config.Name("Experience Required")
        @Config.Comment("The amount of experience, in levels, required for a player to gain knowledge from the tome")
        public int requiredExp = 10;

        @Config.Name("Share Observations")
        @Config.Comment("Observations will also be shared when using the Sharing Tome")
        public boolean shareObservations = true;
    }

    public static class VoidBeaconCategory {
        @Config.RangeInt(min = 20, max = 10000)
        @Config.Name("Essentia Cost")
        @Config.Comment("The amount of essentia required to generate a drop from the Void Beacon. Each beacon tier will reduce this amount by half.")
        public int baseEssentiaCost = 20;
    }

    public static class VoidWalkerBootsCategory {
        @Config.RequiresMcRestart
        @Config.Name("Enable Void Walker Boots")
        @Config.Comment("Enables the Void Walker Boots, a higher armor version of the Traveler's Boots")
        public boolean enableBoots = true;

        @Config.RangeInt(min = 1, max = 10000)
        @Config.Name("Energy Per Vis")
        @Config.Comment("The energy gained per point of Vis consumed. Each point of energy translates to 1 second of increased movement.")
        public int visCapacity = 240;

        @Config.RangeInt(min = 1, max = 10000)
        @Config.Name("Vis Capacity")
        @Config.Comment("The maximum Vis that can be stored in the Void Walker Boots")
        public int energyPerVis = 60;

        @Config.RangeDouble(min = 0, max = 10.0)
        @Config.Name("Jump Boost")
        @Config.Comment("The amount added to the base jump height of the player")
        public double jumpBoost = 0.27;

        @Config.RangeDouble(min = 0, max = 10.0)
        @Config.Name("Jump Factor")
        @Config.Comment("The amount of forward momentum gained when the player jumps into the air when moving")
        public double jumpFactor = 0.03;

        @Config.RangeDouble(min = 0, max = 10.0)
        @Config.Name("Land Speed Boost")
        @Config.Comment("The speed boost applied to the player when they are on the ground")
        public double landSpeedBoost = 0.05;

        @Config.RangeDouble(min = 1.0, max = 10.0)
        @Config.Name("Sneak Slowdown")
        @Config.Comment("Any boosts will be divided by this value when sneaking")
        public double sneakReduction = 4.0;

        @Config.RangeDouble(min = 0, max = 10.0)
        @Config.Name("Step Height")
        @Config.Comment("Boost applied to the player's step height while not sneaking")
        public double stepHeight = 0.67;

        @Config.RangeDouble(min = 0, max = 10.0)
        @Config.Name("Swim Speed Boost")
        @Config.Comment("The boost applied to the player while they are swimming or wading through water")
        public double waterSpeedBoost = 0.03;
    }

    //TODO: Rename and redo this
    public static class WarpRingCategory {
        @Config.RequiresMcRestart
        @Config.Name("Enable Initiate's Band of Cleansing")
        @Config.Comment("Enables the Initiate's Band of Cleansing")
        public boolean enableRing = true;

        @Config.Name("Effect Removals")
        @Config.Comment
                ({
                        "Negative effects that can be removed by the Initiate's Band of Cleansing. The ring removes different",
                        "effects based on the current warp level of the ring.",
                        " Format: potion;warplevel;effectvalue",
                        "  potion - the potion identifier",
                        "  warplevel - the required ring warp level to allow removing this effect",
                        "  effectvalue - the buffer value of this potion when removed",
                        "It is advised to not add Mining Fatigue as a potion removal, as being near an ocean monument will",
                        "level the ring to max warp in a matter of seconds."
                })
        public String[] removalRanks = new String[]{
                "minecraft:hunger;0;30",
                "minecraft:poison;0;20",
                "minecraft:wither;0;50",

                "minecraft:blindness;1;50",
                "minecraft:levitation;1;30",
                "thaumcraft:unnaturalhunger;1;50",

                "thaumcraft:visexhaust;2;80",
                "thaumcraft:infectiousvisexhaust;2;80",
                "thaumcraft:thaumarhia;2;100",

                "thaumcraft:blurredvision;3;50",
                "thaumcraft:sunscorned;3;50",

                "thaumcraft:deathgaze;4;100",
                "thaumcraft:fluxtaint;4;200"
        };

        @Config.RangeInt(min = 1, max = 10000)
        @Config.Name("Buffer Size")
        @Config.Comment("The amount of removed potion value required for the ring to increase Warp level")
        public int bufferSize = 250;
    }

    @Mod.EventBusSubscriber(modid = ThaumicWonders.MODID)
    public static class ConfigChangeListener {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(ThaumicWonders.MODID)) {
                ConfigManager.sync(ThaumicWonders.MODID, Config.Type.INSTANCE);
                ConfigTags.syncConfig();
            }
        }
    }
}
