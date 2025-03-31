package com.verdantartifice.thaumicwonders.common.init;

import com.verdantartifice.thaumicwonders.common.crafting.voidbeacon.VoidBeaconRegistry;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TileVoidBeacon;
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
        
        VoidBeaconRegistry.addEntry("oreLapis");
        VoidBeaconRegistry.addEntry("oreDiamond");
        VoidBeaconRegistry.addEntry("oreRedstone");
        VoidBeaconRegistry.addEntry("oreEmerald");
        VoidBeaconRegistry.addEntry("oreQuartz");
        VoidBeaconRegistry.addEntry("oreIron");
        VoidBeaconRegistry.addEntry("oreGold");
        VoidBeaconRegistry.addEntry(new ItemStack(Blocks.COAL_ORE));
        VoidBeaconRegistry.addEntry("glowstone");

        VoidBeaconRegistry.addEntry("oreCopper");
        VoidBeaconRegistry.addEntry("oreTin");
        VoidBeaconRegistry.addEntry("oreSilver");
        VoidBeaconRegistry.addEntry("oreLead");

        VoidBeaconRegistry.addEntry("oreUranium");
        VoidBeaconRegistry.addEntry("oreRuby");
        VoidBeaconRegistry.addEntry("oreGreenSapphire");
        VoidBeaconRegistry.addEntry("oreSapphire");

        VoidBeaconRegistry.addEntry("stone");
        VoidBeaconRegistry.addEntry("stoneGranite");
        VoidBeaconRegistry.addEntry("stoneDiorite");
        VoidBeaconRegistry.addEntry("stoneAndesite");
        VoidBeaconRegistry.addEntry("cobblestone");
        VoidBeaconRegistry.addEntry("dirt");
        VoidBeaconRegistry.addEntry(new ItemStack(Blocks.DIRT, 1, 2));
        VoidBeaconRegistry.addEntry(new ItemStack(Blocks.SAND, 1, 0));
        VoidBeaconRegistry.addEntry(new ItemStack(Blocks.SAND, 1, 1));
        VoidBeaconRegistry.addEntry(new ItemStack(Blocks.SANDSTONE));
        VoidBeaconRegistry.addEntry(new ItemStack(Blocks.RED_SANDSTONE));
        VoidBeaconRegistry.addEntry("grass");
        VoidBeaconRegistry.addEntry("endstone");
        VoidBeaconRegistry.addEntry("gravel");
        VoidBeaconRegistry.addEntry(new ItemStack(Blocks.MYCELIUM));
        VoidBeaconRegistry.addEntry(new ItemStack(Blocks.CLAY));
        VoidBeaconRegistry.addEntry("netherrack");
        VoidBeaconRegistry.addEntry(new ItemStack(Blocks.SOUL_SAND));

        VoidBeaconRegistry.addEntry(new ItemStack(Blocks.MOSSY_COBBLESTONE));
        VoidBeaconRegistry.addEntry("obsidian");

        for (meta = 0; meta < 6; meta++) {
            VoidBeaconRegistry.addEntry(new ItemStack(Blocks.LOG, 1, meta));
            VoidBeaconRegistry.addEntry(new ItemStack(Blocks.LEAVES, 1, meta));
            VoidBeaconRegistry.addEntry(new ItemStack(Blocks.SAPLING, 1, meta));
        }

        VoidBeaconRegistry.addEntry(new ItemStack(Blocks.TALLGRASS));
        for (meta = 0; meta < 6; meta++) {
            VoidBeaconRegistry.addEntry(new ItemStack(Blocks.DOUBLE_PLANT, 1, meta));
        }
        VoidBeaconRegistry.addEntry(new ItemStack(Blocks.WATERLILY));
        VoidBeaconRegistry.addEntry(new ItemStack(Blocks.DEADBUSH));
        VoidBeaconRegistry.addEntry("vine");
        VoidBeaconRegistry.addEntry(new ItemStack(Items.WHEAT_SEEDS));
        VoidBeaconRegistry.addEntry(new ItemStack(Items.MELON_SEEDS));
        VoidBeaconRegistry.addEntry(new ItemStack(Items.PUMPKIN_SEEDS));
        VoidBeaconRegistry.addEntry(new ItemStack(Items.BEETROOT_SEEDS));
        VoidBeaconRegistry.addEntry("cropNetherWart");

        VoidBeaconRegistry.addEntry(new ItemStack(Blocks.RED_FLOWER));
        VoidBeaconRegistry.addEntry(new ItemStack(Blocks.YELLOW_FLOWER));
        VoidBeaconRegistry.addEntry("blockCactus");
        VoidBeaconRegistry.addEntry(new ItemStack(Blocks.BROWN_MUSHROOM));
        VoidBeaconRegistry.addEntry(new ItemStack(Blocks.RED_MUSHROOM));
        VoidBeaconRegistry.addEntry("sugarCane");
        VoidBeaconRegistry.addEntry("cropWheat");
        VoidBeaconRegistry.addEntry(new ItemStack(Items.APPLE));
        VoidBeaconRegistry.addEntry("cropCarrot");
        VoidBeaconRegistry.addEntry("cropPotato");
        VoidBeaconRegistry.addEntry(new ItemStack(Items.BEETROOT));
        VoidBeaconRegistry.addEntry(new ItemStack(Items.POISONOUS_POTATO));
        VoidBeaconRegistry.addEntry(new ItemStack(Blocks.PUMPKIN));
        VoidBeaconRegistry.addEntry(new ItemStack(Blocks.MELON_BLOCK));
        VoidBeaconRegistry.addEntry(new ItemStack(Items.MELON));
        VoidBeaconRegistry.addEntry(new ItemStack(Blocks.SPONGE, 1, 0));
        VoidBeaconRegistry.addEntry(new ItemStack(Blocks.SPONGE, 1, 1));

        VoidBeaconRegistry.addEntry(new ItemStack(Blocks.WOOL));
        VoidBeaconRegistry.addEntry(new ItemStack(Blocks.MAGMA));
        VoidBeaconRegistry.addEntry(new ItemStack(Blocks.CHORUS_FLOWER));
        VoidBeaconRegistry.addEntry(new ItemStack(Blocks.CHORUS_PLANT));
        VoidBeaconRegistry.addEntry(new ItemStack(Items.CHORUS_FRUIT));

        VoidBeaconRegistry.addEntry(new ItemStack(Blocks.ICE));
        VoidBeaconRegistry.addEntry(new ItemStack(Blocks.PACKED_ICE));
        VoidBeaconRegistry.addEntry(new ItemStack(Items.SNOWBALL));
        VoidBeaconRegistry.addEntry(new ItemStack(Blocks.WEB));
        VoidBeaconRegistry.addEntry(new ItemStack(Items.FLINT));
        VoidBeaconRegistry.addEntry("string");
        VoidBeaconRegistry.addEntry("slimeball");
        VoidBeaconRegistry.addEntry("leather");
        VoidBeaconRegistry.addEntry(new ItemStack(Items.ROTTEN_FLESH));
        VoidBeaconRegistry.addEntry("feather");
        VoidBeaconRegistry.addEntry("bone");
        VoidBeaconRegistry.addEntry("egg");
        VoidBeaconRegistry.addEntry(new ItemStack(Items.SPIDER_EYE));
        VoidBeaconRegistry.addEntry("gunpowder");

        for (meta = 0; meta < 4; meta++) {
            VoidBeaconRegistry.addEntry(new ItemStack(Items.FISH, 1, meta));
        }
        VoidBeaconRegistry.addEntry(new ItemStack(Items.CHICKEN));
        VoidBeaconRegistry.addEntry(new ItemStack(Items.PORKCHOP));
        VoidBeaconRegistry.addEntry(new ItemStack(Items.BEEF));
        VoidBeaconRegistry.addEntry(new ItemStack(Items.MUTTON));
        VoidBeaconRegistry.addEntry(new ItemStack(Items.RABBIT));
        VoidBeaconRegistry.addEntry(new ItemStack(Items.RABBIT_FOOT));
        VoidBeaconRegistry.addEntry(new ItemStack(Items.RABBIT_HIDE));

        VoidBeaconRegistry.addEntry(new ItemStack(Items.BLAZE_ROD));
        VoidBeaconRegistry.addEntry("enderpearl");
        VoidBeaconRegistry.addEntry(new ItemStack(Items.GHAST_TEAR));
        for (meta = 0; meta < 5; meta++) {
            VoidBeaconRegistry.addEntry(new ItemStack(Items.SKULL, 1, meta));
        }
        VoidBeaconRegistry.addEntry(new ItemStack(Items.DRAGON_BREATH));
        VoidBeaconRegistry.addEntry(new ItemStack(Items.MAGMA_CREAM));
        VoidBeaconRegistry.addEntry(new ItemStack(Items.SHULKER_SHELL));

        VoidBeaconRegistry.addEntry(new ItemStack(Items.PRISMARINE_SHARD));
        VoidBeaconRegistry.addEntry(new ItemStack(Items.PRISMARINE_CRYSTALS));

        VoidBeaconRegistry.addEntry(new ItemStack(Items.DYE, 1, 0));
        VoidBeaconRegistry.addEntry(new ItemStack(Items.DYE, 1, 3));

        VoidBeaconRegistry.addEntry("oreCinnabar");
        VoidBeaconRegistry.addEntry("oreAmber");
        VoidBeaconRegistry.addEntry(new ItemStack(ItemsTC.nuggets, 1, 10));

        VoidBeaconRegistry.addEntry(new ItemStack(BlocksTC.crystalAir));
        VoidBeaconRegistry.addEntry(new ItemStack(BlocksTC.crystalFire));
        VoidBeaconRegistry.addEntry(new ItemStack(BlocksTC.crystalWater));
        VoidBeaconRegistry.addEntry(new ItemStack(BlocksTC.crystalEarth));
        VoidBeaconRegistry.addEntry(new ItemStack(BlocksTC.crystalOrder));
        VoidBeaconRegistry.addEntry(new ItemStack(BlocksTC.crystalEntropy));
        VoidBeaconRegistry.addEntry(new ItemStack(BlocksTC.crystalTaint));

        VoidBeaconRegistry.addEntry(new ItemStack(BlocksTC.taintFibre));
        VoidBeaconRegistry.addEntry(new ItemStack(BlocksTC.taintCrust));
        VoidBeaconRegistry.addEntry(new ItemStack(BlocksTC.taintSoil));
        VoidBeaconRegistry.addEntry(new ItemStack(BlocksTC.taintGeyser));
        VoidBeaconRegistry.addEntry(new ItemStack(BlocksTC.taintRock));
        VoidBeaconRegistry.addEntry(new ItemStack(BlocksTC.taintFeature));
        VoidBeaconRegistry.addEntry(new ItemStack(BlocksTC.taintLog));

        VoidBeaconRegistry.addEntry(new ItemStack(BlocksTC.logGreatwood));
        VoidBeaconRegistry.addEntry(new ItemStack(BlocksTC.leafGreatwood));
        VoidBeaconRegistry.addEntry(new ItemStack(BlocksTC.saplingGreatwood));
        VoidBeaconRegistry.addEntry(new ItemStack(BlocksTC.logSilverwood));
        VoidBeaconRegistry.addEntry(new ItemStack(BlocksTC.leafSilverwood));
        VoidBeaconRegistry.addEntry(new ItemStack(BlocksTC.saplingSilverwood));
        VoidBeaconRegistry.addEntry(new ItemStack(BlocksTC.shimmerleaf));
        VoidBeaconRegistry.addEntry(new ItemStack(BlocksTC.cinderpearl));
        VoidBeaconRegistry.addEntry(new ItemStack(BlocksTC.vishroom));

        VoidBeaconRegistry.addEntry(new ItemStack(ItemsTC.brain));
        VoidBeaconRegistry.addEntry(new ItemStack(ItemsTC.curio, 1, 1));
    }
}
