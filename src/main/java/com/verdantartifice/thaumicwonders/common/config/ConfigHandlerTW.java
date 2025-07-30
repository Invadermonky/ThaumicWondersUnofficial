package com.verdantartifice.thaumicwonders.common.config;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = ThaumicWonders.MODID)
public class ConfigHandlerTW {
    @Config.Name("Avatar of Corruption")
    public static CorruptionAvatarCategory avatar_of_corruption = new CorruptionAvatarCategory();
    @Config.Name("Catalyst Stones")
    public static CatalystStoneCategory catalyst_stones = new CatalystStoneCategory();
    @Config.Name("Cleansing Charm")
    public static CleansingCharmCategory cleansing_charm = new CleansingCharmCategory();
    @Config.Name("Dimensional Ripper")
    public static DimensionalRipperCategory dimensional_ripper = new DimensionalRipperCategory();
    @Config.Name("Everburning Urn")
    public static EverburningUrnCategory everburning_urn = new EverburningUrnCategory();
    @Config.Name("Crucible Crushing")
    public static ErythurgyCategory erythurgy = new ErythurgyCategory();
    @Config.Name("Flying Carpet")
    public static FlyingCarpetCategory flying_carpet = new FlyingCarpetCategory();
    @Config.Name("Meaty Orb")
    public static MeatyOrbCategory meaty_orb = new MeatyOrbCategory();
    @Config.Name("Night Vision Goggles")
    public static NightVisionGogglesCategory night_vision_goggles = new NightVisionGogglesCategory();
    @Config.Name("Ore Diviner")
    public static OreDivinerCategory ore_diviner = new OreDivinerCategory();
    @Config.Name("Portal Network")
    public static PortalCategory portal_network = new PortalCategory();
    @Config.Name("Primal Destroyer")
    public static PrimalDestroyerCategory primal_destroyer = new PrimalDestroyerCategory();
    @Config.Name("Primordial Siphon")
    public static PrimordialSiphonCategory primordial_siphon = new PrimordialSiphonCategory();
    @Config.Name("Sharing Tome")
    public static SharingTomeCategory sharing_tome = new SharingTomeCategory();
    @Config.Name("Vis Capacitor")
    public static VisCapacitorCategory vis_capacitor = new VisCapacitorCategory();
    @Config.Name("Void Beacon")
    public static VoidBeaconCategory void_beacon = new VoidBeaconCategory();
    @Config.Name("Void Walker Boots")
    public static VoidWalkerBootsCategory void_walker_boots = new VoidWalkerBootsCategory();
    @Config.Name("Initiate's Ring of Cleansing")
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
            @Config.Name("Enable Stone Repairing")
            @Config.Comment("Allows the Catalyst Stones to be repaired")
            public boolean repairable = false;

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
        public int fluxRemoved = 3;
    }

    public static class CorruptionAvatarCategory {
        @Config.RangeInt(min = 50, max = 2000)
        @Config.Name("Avatar Health")
        @Config.Comment("The amount of health the Avatar of Corruption spawns with.")
        public int entityHealth = 300;

        @Config.RangeInt(min = 0, max = 600)
        @Config.Name("Pollution Timer")
        @Config.Comment
                ({
                        "The time, in ticks, between each aura pollution that occurs while the Avatar of Corruption is",
                        "alive. Each pollution action increases the local flux by 1. Setting this value to 0 will disable",
                        "aura pollution."
                })
        public int pollutionTimer = 5;

        @Config.RangeInt(min = 1, max = 30)
        @Config.Name("Max Regeneration Amplifier")
        @Config.Comment
                ({
                        "The maximum regeneration amplifier that can be gained by the Avatar of Corruption. This amplifier",
                        "is calculated using the local flux every 2 seconds."
                })
        public int maxRegenAmplifier = 10;

        @Config.RangeInt(min = 0, max = 3000)
        @Config.Name("Taint Seed Timer")
        @Config.Comment
                ({
                        "The amount of time, in ticks, between each Taint Seed spawn while the Avatar of Corruption is",
                        "alive. Setting this value to 0 will disable this taint seed spawning."
                })
        public int taintSeedTimer = 200;
    }

    public static class DimensionalRipperCategory {
        @Config.RangeInt(min = 1, max = 250)
        @Config.Name("Fuel Required")
        @Config.Comment("The amount of Vitium Essentia required to grow the targeted rift.")
        public int fuelRequired = 50;
    }

    public static class ErythurgyCategory {
        @Config.RequiresMcRestart
        @Config.RangeInt(min = 1, max = 100)
        @Config.Name("Ordered Deconstruction Cost")
        @Config.Comment("The Ordo cost of each ordered deconstruction crucible recipe. (Example: Wool -> String x4)")
        public int ordoCost = 20;

        @Config.RequiresMcRestart
        @Config.RangeInt(min = 1, max = 100)
        @Config.Name("Entropic Deconstruction Cost")
        @Config.Comment("The Perditio cost of each entropic deconstruction crucible recipe. (Example: Blaze Rod -> Blaze Powder x6)")
        public int perditioCost = 20;

        @Config.RequiresMcRestart
        @Config.RangeInt(min = 1, max = 100)
        @Config.Name("Avaric Deconstruction Cost")
        @Config.Comment("The Desiderium cost of each avaric deconstruction crucible recipe. This amount is in addition to the perditio cost set in Entropic Deconstruction Cost.")
        public int desireCost = 10;
    }

    public static class EverburningUrnCategory {
        @Config.RangeInt(min = 1, max = 1000)
        @Config.Name("Fill Per Operation")
        @Config.Comment("The amount of lava generated per fill operation. Fill operations occur twice per second.")
        public int fillPerOperation = 20;

        @Config.RangeDouble(min = 0, max = 100)
        @Config.Name("Vis Per Operation")
        @Config.Comment("The amount of Vis drained from the aura each time a lava fill operation occurs. Fill operations occur twice per second.")
        public double visDrainPerOperation = 0.1;
    }

    public static class FlyingCarpetCategory {
        @Config.RangeDouble(min = 0, max = 20.0)
        @Config.Name("Max Speed")
        @Config.Comment("Magic Carpet maximum speed")
        public double maxSpeed = 5.0;

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

    public static class OreDivinerCategory {
        @Config.RangeInt(min = 1, max = 100)
        @Config.Name("Ore Diviner Range")
        @Config.Comment("The horizontal range the Ore Diviner can detect ores.")
        public int searchRange = 20;

        @Config.Name("Ore Associations")
        @Config.Comment
                ({
                        "Associated ore dictionary types that can be used to ping ores of the same type. An item with an",
                        "ore dictionary entry of 'ingotIron' matches to ores of 'oreIron'."
                })
        public String[] oreAssociations = new String[]{
                "dust",
                "gem",
                "ingot"
        };

        @Config.Name("Additional Ore Types")
        @Config.Comment("Different ore types that are related to this material. Searching for 'Iron' would be related to 'oreIron', 'oreNetherIron', or 'oreEndIron'.")
        public String[] oreTypes = new String[]{
                "ore",
                "oreNether",
                "oreEnd"
        };

        @Config.RangeInt(min = 1, max = 10000)
        @Config.Name("Search Duration")
        @Config.Comment("The duration, in seconds, the Ore Diviner will ping nearby ores per search operation.")
        public int searchDuration = 180;

        @Config.RangeInt(min = 1, max = 20)
        @Config.Name("Pinged Ores")
        @Config.Comment("The number of ores pinged by the Ore Diviner at once.")
        public int pingedOres = 6;

        @Config.RangeInt(min = 0, max = 1000)
        @Config.Name("Vis Drain")
        @Config.Comment("The amount of Vis drained from the aura each time a new search is started. Setting this value to 0 will disable this cost.")
        public int visDrain = 5;
    }

    public static class PortalCategory {
        @Config.RangeDouble(min = 0, max = 1000)
        @Config.Name("Portal Activation Cost")
        @Config.Comment("The amount of Vis that is drained from the aura when generating a linked portal.")
        public double activationVisCost = 20;
    }

    public static class PrimalDestroyerCategory {
        @Config.RangeDouble(min = 1.0, max = 100.0)
        @Config.Name("Hungering Damage")
        @Config.Comment("The amount of damage done to players once the hunger meter fills.")
        public double hungeringDamage = 12.0;

        @Config.RequiresMcRestart
        @Config.RangeDouble(min = 0, max = 100.0)
        @Config.Name("Void Damage")
        @Config.Comment
                ({
                        "The amount of void damage dealt by the sword on attack. Void damage bypasses all defenses",
                        "including armor, enchantments and potions."
                })
        public double voidDamage = 6.0;
    }

    public static class PrimordialSiphonCategory {
        @Config.RangeInt(min = 1, max = 10000)
        @Config.Name("Required Rift Power")
        @Config.Comment("The amount of drained rift power required to form a Primordial Grain.")
        public int requiredRiftDrain = 2000;
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

    public static class VisCapacitorCategory {
        @Config.RangeDouble(min = 0, max = 1.0)
        @Config.Name("Discharge Threshold")
        @Config.Comment
                ({
                        "The amount of Vis, as a percentage of base chunk aura, required for the Vis Capacitor to begin",
                        "discharging its contents in an attempt to replenish the local environment."
                })
        public double dischargeThreshold = 0.75;

        @Config.RangeDouble(min = 0, max = 1.0)
        @Config.Name("Recharge Threshold")
        @Config.Comment
                ({
                        "The amount of Vis, as a percentage of base chunk aura, required to allow the Vis Capacitor to charge.",
                        "If the chunk aura is above this threshold, it will begin storing vis within the capacitor."
                })
        public double rechargeThreshold = 0.9;

        @Config.Name("Vis Storage Capacity")
        @Config.Comment("The maximum amount of Vis the Vis Capacitor can store.")
        public int visCapacity = 200;
    }

    public static class VoidBeaconCategory {
        @Config.Name("Disable Ambient Sound")
        @Config.Comment("Disables the ambient sound that plays while the Void Beacon is active.")
        public boolean disableAmbient = false;

        @Config.RangeInt(min = 0, max = 10000)
        @Config.Name("Essentia Cost")
        @Config.Comment("The amount of Aurum essentia required to generate Vis injected into the aura.")
        public int essentiaCost = 20;

        @Config.RangeInt(min = 1, max = 1000)
        @Config.Name("Required Rift Power")
        @Config.Comment("The amount of rift power required to generated Vis injected into the aura.")
        public int riftPowerRequired = 50;

        @Config.RangeInt(min = 0, max = 10)
        @Config.Name("Range - Tier 1")
        @Config.Comment
                ({
                        "The radius, in chunks, the Tier 1 Void Beacon will regenerate vis. A value of 0 will only generate",
                        "in the Void Beacon chunk. A value of 1 will generate in a 3x3 chunk area centered on the Void Beacon."
                })
        public int tierOneRange = 1;

        @Config.RangeDouble(min = 0, max = 100)
        @Config.Name("Vis Generated - Tier 1")
        @Config.Comment("The amount of vis generated by a Tier 1 Void Beacon every 10 seconds.")
        public double tierOneVisGenerated = 1.0;

        @Config.RangeInt(min = 0, max = 10)
        @Config.Name("Range - Tier 2")
        @Config.Comment
                ({
                        "The radius, in chunks, the Tier 2 Void Beacon will regenerate vis. A value of 0 will only generate",
                        "in the Void Beacon chunk. A value of 1 will generate in a 3x3 chunk area centered on the Void Beacon."
                })
        public int tierTwoRange = 1;

        @Config.RangeDouble(min = 0, max = 100)
        @Config.Name("Vis Generated - Tier 2")
        @Config.Comment("The amount of vis generated by a Tier 2 Void Beacon every 10 seconds. This amount will only generated in the Void Beacon chunk.")
        public double tierTwoVisGenerated = 2.0;

        @Config.RangeInt(min = 0, max = 10)
        @Config.Name("Range - Tier 3")
        @Config.Comment
                ({
                        "The radius, in chunks, the Tier 3 Void Beacon will regenerate vis. A value of 0 will only generate",
                        "in the Void Beacon chunk. A value of 1 will generate in a 3x3 chunk area centered on the Void Beacon."
                })
        public int tierThreeRange = 1;

        @Config.RangeDouble(min = 0, max = 100)
        @Config.Name("Vis Generated - Tier 3")
        @Config.Comment
                ({
                        "The amount of vis generated by a Tier 3 Void Beacon every 10 seconds. This amount will generate",
                        "in all chunks in a 3x3 chunk area centered on the Void Beacon."
                })
        public double tierThreeVisGenerated = 4.0;

        @Config.RangeInt(min = 0, max = 10)
        @Config.Name("Range - Tier 4")
        @Config.Comment
                ({
                        "The radius, in chunks, the Tier 4 Void Beacon will regenerate vis. A value of 0 will only generate",
                        "in the Void Beacon chunk. A value of 2 will generate in a 5x5 chunk area centered on the Void Beacon."
                })
        public int tierFourRange = 1;

        @Config.RangeDouble(min = 0, max = 100)
        @Config.Name("Vis Generated - Tier 4")
        @Config.Comment
                ({
                        "The amount of vis generated by a Tier 4 Void Beacon every 10 seconds. This amount will generated",
                        "in all chunks in a 5x5 chunk area centered on the Void Beacon."
                })
        public double tierFourVisGenerated = 8.0;

    }

    public static class VoidWalkerBootsCategory {
        @Config.RequiresMcRestart
        @Config.Name("Enable Void Walker Boots")
        @Config.Comment("")
        public boolean enableBoots = true;

        @Config.RangeInt(min = 1, max = 10000)
        @Config.Name("Energy Per Vis")
        @Config.Comment("The amount of energy gained per point of vis drained")
        public int energyPerVis = 60;

        @Config.RangeDouble(min = 0, max = 10.0)
        @Config.Name("Land Speed Boost")
        @Config.Comment("Movement speed increase while on dry land")
        public double landSpeedBoost = 0.06;

        @Config.RangeDouble(min = 0, max = 10.0)
        @Config.Name("Water Speed Boost")
        @Config.Comment("Movement speed increase while in water or swimming")
        public double waterSpeedBoost = 0.03;

        @Config.RangeDouble(min = 0, max = 10.0)
        @Config.Name("Jump Height Boost")
        @Config.Comment("Jump height increase while wearing empowered boots")
        public double jumpBoost = 0.27;

        @Config.RangeDouble(min = 0, max = 10.0)
        @Config.Name("Jump Factor")
        @Config.Comment("Momentum horizontal momentum increase when jumping")
        public double jumpFactor = 0.03;

        @Config.Name("Step Height Increase")
        @Config.Comment("The step height increase when wearing empowered boots")
        public double stepHeight = 0.67;

        @Config.RangeInt(min = 1, max = 10000)
        @Config.Name("Vis Capacity")
        @Config.Comment("The maximum vis capacity of the boots")
        public int visCapacity = 240;
    }

    public static class WarpRingCategory {
        @Config.RequiresMcRestart
        @Config.Name("Enable Initiate's Ring of Cleansing")
        @Config.Comment("Enables the Initiate's Ring of Cleansing")
        public boolean enableRing = true;

        @Config.Name("Removal Values")
        @Config.Comment
                ({
                        "Potions the Initiate's Ring of Cleansing can remove. Potions can be restricted to only be removed",
                        "once the ring reaches a certain warp level.",
                        " Format: potion=warplevel",
                        "  potion - the potion registry name",
                        "  warplevel - the required warp level to remove this effect (must be between 0 and 5)",
                        "Note: It is advised to not have the ring remove mining fatigue as this can easily be exploited to rapidly",
                        "gain ring levels."
                })
        public String[] removalRanks = new String[]{
                "minecraft:hunger=0",
                "minecraft:poison=0",
                "minecraft:wither=0",

                "minecraft:blindness=1",
                "minecraft:levitation=1",
                "thaumcraft:unnaturalhunger=1",

                "thaumcraft:visexhaust=2",
                "thaumcraft:infectiousvisexhaust=2",
                "thaumcraft:thaumarhia=2",

                "thaumcraft:blurredvision=3",
                "thaumcraft:sunscorned=3",

                "thaumcraft:deathgaze=4",
                "thaumcraft:fluxtaint=4"
        };
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
