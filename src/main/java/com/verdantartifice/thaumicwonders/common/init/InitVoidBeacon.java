package com.verdantartifice.thaumicwonders.common.init;

import com.verdantartifice.thaumicwonders.common.crafting.voidbeacon.VoidBeaconEntryRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;

public class InitVoidBeacon {
    public static void init() {
        registerDefaultEntries();
    }

    public static void registerDefaultEntries() {
        int meta;

        VoidBeaconEntryRegistry.addEntry("oreLapis");
        VoidBeaconEntryRegistry.addEntry("oreDiamond");
        VoidBeaconEntryRegistry.addEntry("oreRedstone");
        VoidBeaconEntryRegistry.addEntry("oreEmerald");
        VoidBeaconEntryRegistry.addEntry("oreQuartz");
        VoidBeaconEntryRegistry.addEntry("oreIron");
        VoidBeaconEntryRegistry.addEntry("oreGold");
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Blocks.COAL_ORE));
        VoidBeaconEntryRegistry.addEntry("glowstone");

        VoidBeaconEntryRegistry.addEntry("oreCopper");
        VoidBeaconEntryRegistry.addEntry("oreTin");
        VoidBeaconEntryRegistry.addEntry("oreSilver");
        VoidBeaconEntryRegistry.addEntry("oreLead");

        VoidBeaconEntryRegistry.addEntry("oreUranium");
        VoidBeaconEntryRegistry.addEntry("oreRuby");
        VoidBeaconEntryRegistry.addEntry("oreGreenSapphire");
        VoidBeaconEntryRegistry.addEntry("oreSapphire");

        VoidBeaconEntryRegistry.addEntry("stone");
        VoidBeaconEntryRegistry.addEntry("stoneGranite");
        VoidBeaconEntryRegistry.addEntry("stoneDiorite");
        VoidBeaconEntryRegistry.addEntry("stoneAndesite");
        VoidBeaconEntryRegistry.addEntry("cobblestone");
        VoidBeaconEntryRegistry.addEntry("dirt");
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Blocks.DIRT, 1, 2));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Blocks.SAND, 1, 0));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Blocks.SAND, 1, 1));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Blocks.SANDSTONE));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Blocks.RED_SANDSTONE));
        VoidBeaconEntryRegistry.addEntry("grass");
        VoidBeaconEntryRegistry.addEntry("endstone");
        VoidBeaconEntryRegistry.addEntry("gravel");
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Blocks.MYCELIUM));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Blocks.CLAY));
        VoidBeaconEntryRegistry.addEntry("netherrack");
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Blocks.SOUL_SAND));

        VoidBeaconEntryRegistry.addEntry(new ItemStack(Blocks.MOSSY_COBBLESTONE));
        VoidBeaconEntryRegistry.addEntry("obsidian");

        for (meta = 0; meta < 6; meta++) {
            VoidBeaconEntryRegistry.addEntry(new ItemStack(Blocks.LOG, 1, meta));
            VoidBeaconEntryRegistry.addEntry(new ItemStack(Blocks.LEAVES, 1, meta));
            VoidBeaconEntryRegistry.addEntry(new ItemStack(Blocks.SAPLING, 1, meta));
        }

        VoidBeaconEntryRegistry.addEntry(new ItemStack(Blocks.TALLGRASS));
        for (meta = 0; meta < 6; meta++) {
            VoidBeaconEntryRegistry.addEntry(new ItemStack(Blocks.DOUBLE_PLANT, 1, meta));
        }
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Blocks.WATERLILY));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Blocks.DEADBUSH));
        VoidBeaconEntryRegistry.addEntry("vine");
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Items.WHEAT_SEEDS));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Items.MELON_SEEDS));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Items.PUMPKIN_SEEDS));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Items.BEETROOT_SEEDS));
        VoidBeaconEntryRegistry.addEntry("cropNetherWart");

        VoidBeaconEntryRegistry.addEntry(new ItemStack(Blocks.RED_FLOWER));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Blocks.YELLOW_FLOWER));
        VoidBeaconEntryRegistry.addEntry("blockCactus");
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Blocks.BROWN_MUSHROOM));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Blocks.RED_MUSHROOM));
        VoidBeaconEntryRegistry.addEntry("sugarCane");
        VoidBeaconEntryRegistry.addEntry("cropWheat");
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Items.APPLE));
        VoidBeaconEntryRegistry.addEntry("cropCarrot");
        VoidBeaconEntryRegistry.addEntry("cropPotato");
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Items.BEETROOT));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Items.POISONOUS_POTATO));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Blocks.PUMPKIN));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Blocks.MELON_BLOCK));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Items.MELON));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Blocks.SPONGE, 1, 0));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Blocks.SPONGE, 1, 1));

        VoidBeaconEntryRegistry.addEntry(new ItemStack(Blocks.WOOL));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Blocks.MAGMA));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Blocks.CHORUS_FLOWER));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Blocks.CHORUS_PLANT));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Items.CHORUS_FRUIT));

        VoidBeaconEntryRegistry.addEntry(new ItemStack(Blocks.ICE));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Blocks.PACKED_ICE));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Items.SNOWBALL));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Blocks.WEB));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Items.FLINT));
        VoidBeaconEntryRegistry.addEntry("string");
        VoidBeaconEntryRegistry.addEntry("slimeball");
        VoidBeaconEntryRegistry.addEntry("leather");
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Items.ROTTEN_FLESH));
        VoidBeaconEntryRegistry.addEntry("feather");
        VoidBeaconEntryRegistry.addEntry("bone");
        VoidBeaconEntryRegistry.addEntry("egg");
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Items.SPIDER_EYE));
        VoidBeaconEntryRegistry.addEntry("gunpowder");

        for (meta = 0; meta < 4; meta++) {
            VoidBeaconEntryRegistry.addEntry(new ItemStack(Items.FISH, 1, meta));
        }
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Items.CHICKEN));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Items.PORKCHOP));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Items.BEEF));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Items.MUTTON));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Items.RABBIT));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Items.RABBIT_FOOT));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Items.RABBIT_HIDE));

        VoidBeaconEntryRegistry.addEntry(new ItemStack(Items.BLAZE_ROD));
        VoidBeaconEntryRegistry.addEntry("enderpearl");
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Items.GHAST_TEAR));
        for (meta = 0; meta < 5; meta++) {
            VoidBeaconEntryRegistry.addEntry(new ItemStack(Items.SKULL, 1, meta));
        }
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Items.DRAGON_BREATH));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Items.MAGMA_CREAM));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Items.SHULKER_SHELL));

        VoidBeaconEntryRegistry.addEntry(new ItemStack(Items.PRISMARINE_SHARD));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Items.PRISMARINE_CRYSTALS));

        VoidBeaconEntryRegistry.addEntry(new ItemStack(Items.DYE, 1, 0));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(Items.DYE, 1, 3));

        VoidBeaconEntryRegistry.addEntry("oreCinnabar");
        VoidBeaconEntryRegistry.addEntry("oreAmber");
        VoidBeaconEntryRegistry.addEntry(new ItemStack(ItemsTC.nuggets, 1, 10));

        VoidBeaconEntryRegistry.addEntry(new ItemStack(BlocksTC.crystalAir));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(BlocksTC.crystalFire));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(BlocksTC.crystalWater));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(BlocksTC.crystalEarth));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(BlocksTC.crystalOrder));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(BlocksTC.crystalEntropy));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(BlocksTC.crystalTaint));

        VoidBeaconEntryRegistry.addEntry(new ItemStack(BlocksTC.taintFibre));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(BlocksTC.taintCrust));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(BlocksTC.taintSoil));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(BlocksTC.taintGeyser));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(BlocksTC.taintRock));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(BlocksTC.taintFeature));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(BlocksTC.taintLog));

        VoidBeaconEntryRegistry.addEntry(new ItemStack(BlocksTC.logGreatwood));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(BlocksTC.leafGreatwood));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(BlocksTC.saplingGreatwood));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(BlocksTC.logSilverwood));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(BlocksTC.leafSilverwood));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(BlocksTC.saplingSilverwood));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(BlocksTC.shimmerleaf));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(BlocksTC.cinderpearl));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(BlocksTC.vishroom));

        VoidBeaconEntryRegistry.addEntry(new ItemStack(ItemsTC.brain));
        VoidBeaconEntryRegistry.addEntry(new ItemStack(ItemsTC.curio, 1, 1));
    }
}
