package com.verdantartifice.thaumicwonders.common.init;

import com.verdantartifice.thaumicwonders.common.tiles.devices.TileVoidBeacon;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;

public class InitVoidBeacon {
    public static void init() {
        TileVoidBeacon.initRegistry();
        registerEntries();
    }
    
    private static void registerEntries() {
        int meta = 0;
        
        TileVoidBeacon.registerOreDict("oreLapis");
        TileVoidBeacon.registerOreDict("oreDiamond");
        TileVoidBeacon.registerOreDict("oreRedstone");
        TileVoidBeacon.registerOreDict("oreEmerald");
        TileVoidBeacon.registerOreDict("oreQuartz");
        TileVoidBeacon.registerOreDict("oreIron");
        TileVoidBeacon.registerOreDict("oreGold");
        TileVoidBeacon.registerItemStack(new ItemStack(Blocks.COAL_ORE));
        TileVoidBeacon.registerOreDict("glowstone");
        
        TileVoidBeacon.registerOreDict("oreCopper");
        TileVoidBeacon.registerOreDict("oreTin");
        TileVoidBeacon.registerOreDict("oreSilver");
        TileVoidBeacon.registerOreDict("oreLead");
        
        TileVoidBeacon.registerOreDict("oreUranium");
        TileVoidBeacon.registerOreDict("oreRuby");
        TileVoidBeacon.registerOreDict("oreGreenSapphire");
        TileVoidBeacon.registerOreDict("oreSapphire");
        
        TileVoidBeacon.registerOreDict("stone");
        TileVoidBeacon.registerOreDict("stoneGranite");
        TileVoidBeacon.registerOreDict("stoneDiorite");
        TileVoidBeacon.registerOreDict("stoneAndesite");
        TileVoidBeacon.registerOreDict("cobblestone");
        TileVoidBeacon.registerOreDict("dirt");
        TileVoidBeacon.registerItemStack(new ItemStack(Blocks.DIRT, 1, 2));
        TileVoidBeacon.registerItemStack(new ItemStack(Blocks.SAND, 1, 0));
        TileVoidBeacon.registerItemStack(new ItemStack(Blocks.SAND, 1, 1));
        TileVoidBeacon.registerItemStack(new ItemStack(Blocks.SANDSTONE));
        TileVoidBeacon.registerItemStack(new ItemStack(Blocks.RED_SANDSTONE));
        TileVoidBeacon.registerOreDict("grass");
        TileVoidBeacon.registerOreDict("endstone");
        TileVoidBeacon.registerOreDict("gravel");
        TileVoidBeacon.registerItemStack(new ItemStack(Blocks.MYCELIUM));
        TileVoidBeacon.registerItemStack(new ItemStack(Blocks.CLAY));
        TileVoidBeacon.registerOreDict("netherrack");
        TileVoidBeacon.registerItemStack(new ItemStack(Blocks.SOUL_SAND));
        
        TileVoidBeacon.registerItemStack(new ItemStack(Blocks.MOSSY_COBBLESTONE));
        TileVoidBeacon.registerOreDict("obsidian");
        
        for (meta = 0; meta < 6; meta++) {
            TileVoidBeacon.registerItemStack(new ItemStack(Blocks.LOG, 1, meta));
            TileVoidBeacon.registerItemStack(new ItemStack(Blocks.LEAVES, 1, meta));
            TileVoidBeacon.registerItemStack(new ItemStack(Blocks.SAPLING, 1, meta));
        }
        
        TileVoidBeacon.registerItemStack(new ItemStack(Blocks.TALLGRASS));
        for (meta = 0; meta < 6; meta++) {
            TileVoidBeacon.registerItemStack(new ItemStack(Blocks.DOUBLE_PLANT, 1, meta));
        }
        TileVoidBeacon.registerItemStack(new ItemStack(Blocks.WATERLILY));
        TileVoidBeacon.registerItemStack(new ItemStack(Blocks.DEADBUSH));
        TileVoidBeacon.registerOreDict("vine");
        TileVoidBeacon.registerItemStack(new ItemStack(Items.WHEAT_SEEDS));
        TileVoidBeacon.registerItemStack(new ItemStack(Items.MELON_SEEDS));
        TileVoidBeacon.registerItemStack(new ItemStack(Items.PUMPKIN_SEEDS));
        TileVoidBeacon.registerItemStack(new ItemStack(Items.BEETROOT_SEEDS));
        TileVoidBeacon.registerOreDict("cropNetherWart");

        TileVoidBeacon.registerItemStack(new ItemStack(Blocks.RED_FLOWER));
        TileVoidBeacon.registerItemStack(new ItemStack(Blocks.YELLOW_FLOWER));
        TileVoidBeacon.registerOreDict("blockCactus");
        TileVoidBeacon.registerItemStack(new ItemStack(Blocks.BROWN_MUSHROOM));
        TileVoidBeacon.registerItemStack(new ItemStack(Blocks.RED_MUSHROOM));
        TileVoidBeacon.registerOreDict("sugarCane");
        TileVoidBeacon.registerOreDict("cropWheat");
        TileVoidBeacon.registerItemStack(new ItemStack(Items.APPLE));
        TileVoidBeacon.registerOreDict("cropCarrot");
        TileVoidBeacon.registerOreDict("cropPotato");
        TileVoidBeacon.registerItemStack(new ItemStack(Items.BEETROOT));
        TileVoidBeacon.registerItemStack(new ItemStack(Items.POISONOUS_POTATO));
        TileVoidBeacon.registerItemStack(new ItemStack(Blocks.PUMPKIN));
        TileVoidBeacon.registerItemStack(new ItemStack(Blocks.MELON_BLOCK));
        TileVoidBeacon.registerItemStack(new ItemStack(Items.MELON));
        TileVoidBeacon.registerItemStack(new ItemStack(Blocks.SPONGE, 1, 0));
        TileVoidBeacon.registerItemStack(new ItemStack(Blocks.SPONGE, 1, 1));

        TileVoidBeacon.registerItemStack(new ItemStack(Blocks.WOOL));
        TileVoidBeacon.registerItemStack(new ItemStack(Blocks.MAGMA));
        TileVoidBeacon.registerItemStack(new ItemStack(Blocks.CHORUS_FLOWER));
        TileVoidBeacon.registerItemStack(new ItemStack(Blocks.CHORUS_PLANT));
        TileVoidBeacon.registerItemStack(new ItemStack(Items.CHORUS_FRUIT));
        
        TileVoidBeacon.registerItemStack(new ItemStack(Blocks.ICE));
        TileVoidBeacon.registerItemStack(new ItemStack(Blocks.PACKED_ICE));
        TileVoidBeacon.registerItemStack(new ItemStack(Items.SNOWBALL));
        TileVoidBeacon.registerItemStack(new ItemStack(Blocks.WEB));
        TileVoidBeacon.registerItemStack(new ItemStack(Items.FLINT));
        TileVoidBeacon.registerOreDict("string");
        TileVoidBeacon.registerOreDict("slimeball");
        TileVoidBeacon.registerOreDict("leather");
        TileVoidBeacon.registerItemStack(new ItemStack(Items.ROTTEN_FLESH));
        TileVoidBeacon.registerOreDict("feather");
        TileVoidBeacon.registerOreDict("bone");
        TileVoidBeacon.registerOreDict("egg");
        TileVoidBeacon.registerItemStack(new ItemStack(Items.SPIDER_EYE));
        TileVoidBeacon.registerOreDict("gunpowder");
        
        for (meta = 0; meta < 4; meta++) {
            TileVoidBeacon.registerItemStack(new ItemStack(Items.FISH, 1, meta));
        }
        TileVoidBeacon.registerItemStack(new ItemStack(Items.CHICKEN));
        TileVoidBeacon.registerItemStack(new ItemStack(Items.PORKCHOP));
        TileVoidBeacon.registerItemStack(new ItemStack(Items.BEEF));
        TileVoidBeacon.registerItemStack(new ItemStack(Items.MUTTON));
        TileVoidBeacon.registerItemStack(new ItemStack(Items.RABBIT));
        TileVoidBeacon.registerItemStack(new ItemStack(Items.RABBIT_FOOT));
        TileVoidBeacon.registerItemStack(new ItemStack(Items.RABBIT_HIDE));

        TileVoidBeacon.registerItemStack(new ItemStack(Items.BLAZE_ROD));
        TileVoidBeacon.registerOreDict("enderpearl");
        TileVoidBeacon.registerItemStack(new ItemStack(Items.GHAST_TEAR));
        for (meta = 0; meta < 5; meta++) {
            TileVoidBeacon.registerItemStack(new ItemStack(Items.SKULL, 1, meta));
        }
        TileVoidBeacon.registerItemStack(new ItemStack(Items.DRAGON_BREATH));
        TileVoidBeacon.registerItemStack(new ItemStack(Items.MAGMA_CREAM));
        TileVoidBeacon.registerItemStack(new ItemStack(Items.SHULKER_SHELL));

        TileVoidBeacon.registerItemStack(new ItemStack(Items.PRISMARINE_SHARD));
        TileVoidBeacon.registerItemStack(new ItemStack(Items.PRISMARINE_CRYSTALS));

        TileVoidBeacon.registerItemStack(new ItemStack(Items.DYE, 1, 0));
        TileVoidBeacon.registerItemStack(new ItemStack(Items.DYE, 1, 3));
        
        TileVoidBeacon.registerOreDict("oreCinnabar");
        TileVoidBeacon.registerOreDict("oreAmber");
        TileVoidBeacon.registerItemStack(new ItemStack(ItemsTC.nuggets, 1, 10));
        
        TileVoidBeacon.registerItemStack(new ItemStack(BlocksTC.crystalAir));
        TileVoidBeacon.registerItemStack(new ItemStack(BlocksTC.crystalFire));
        TileVoidBeacon.registerItemStack(new ItemStack(BlocksTC.crystalWater));
        TileVoidBeacon.registerItemStack(new ItemStack(BlocksTC.crystalEarth));
        TileVoidBeacon.registerItemStack(new ItemStack(BlocksTC.crystalOrder));
        TileVoidBeacon.registerItemStack(new ItemStack(BlocksTC.crystalEntropy));
        TileVoidBeacon.registerItemStack(new ItemStack(BlocksTC.crystalTaint));

        TileVoidBeacon.registerItemStack(new ItemStack(BlocksTC.taintFibre));
        TileVoidBeacon.registerItemStack(new ItemStack(BlocksTC.taintCrust));
        TileVoidBeacon.registerItemStack(new ItemStack(BlocksTC.taintSoil));
        TileVoidBeacon.registerItemStack(new ItemStack(BlocksTC.taintGeyser));
        TileVoidBeacon.registerItemStack(new ItemStack(BlocksTC.taintRock));
        TileVoidBeacon.registerItemStack(new ItemStack(BlocksTC.taintFeature));
        TileVoidBeacon.registerItemStack(new ItemStack(BlocksTC.taintLog));
        
        TileVoidBeacon.registerItemStack(new ItemStack(BlocksTC.logGreatwood));
        TileVoidBeacon.registerItemStack(new ItemStack(BlocksTC.leafGreatwood));
        TileVoidBeacon.registerItemStack(new ItemStack(BlocksTC.saplingGreatwood));
        TileVoidBeacon.registerItemStack(new ItemStack(BlocksTC.logSilverwood));
        TileVoidBeacon.registerItemStack(new ItemStack(BlocksTC.leafSilverwood));
        TileVoidBeacon.registerItemStack(new ItemStack(BlocksTC.saplingSilverwood));
        TileVoidBeacon.registerItemStack(new ItemStack(BlocksTC.shimmerleaf));
        TileVoidBeacon.registerItemStack(new ItemStack(BlocksTC.cinderpearl));
        TileVoidBeacon.registerItemStack(new ItemStack(BlocksTC.vishroom));

        TileVoidBeacon.registerItemStack(new ItemStack(ItemsTC.brain));
        TileVoidBeacon.registerItemStack(new ItemStack(ItemsTC.curio, 1, 1));
    }
}
