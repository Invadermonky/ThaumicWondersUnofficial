package com.verdantartifice.thaumicwonders.common.init;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.blocks.base.BlockTW;
import com.verdantartifice.thaumicwonders.common.blocks.devices.*;
import com.verdantartifice.thaumicwonders.common.blocks.essentia.BlockCreativeEssentiaJar;
import com.verdantartifice.thaumicwonders.common.blocks.essentia.ItemBlockCreativeEssentiaJar;
import com.verdantartifice.thaumicwonders.common.blocks.fluids.BlockFluidQuicksilver;
import com.verdantartifice.thaumicwonders.common.blocks.misc.*;
import com.verdantartifice.thaumicwonders.common.blocks.plants.BlockCinderpearlCrop;
import com.verdantartifice.thaumicwonders.common.blocks.plants.BlockShimmerleafCrop;
import com.verdantartifice.thaumicwonders.common.blocks.plants.BlockVishroomCrop;
import com.verdantartifice.thaumicwonders.common.fluids.FluidQuicksilver;
import com.verdantartifice.thaumicwonders.common.tiles.devices.*;
import com.verdantartifice.thaumicwonders.common.tiles.essentia.TileCreativeEssentiaJar;
import com.verdantartifice.thaumicwonders.common.tiles.misc.TilePrimordialAccretionChamberPlaceholder;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("ConstantConditions")
public class InitBlocks {
    public static final Set<ItemBlock> ITEM_BLOCKS = new HashSet<>();

    public static void initBlocks(IForgeRegistry<Block> forgeRegistry) {
        registerBlock(forgeRegistry, new BlockEverburningUrn());
        registerBlock(forgeRegistry, new BlockDimensionalRipper());
        registerBlock(forgeRegistry, new BlockCreativeEssentiaJar(), ItemBlockCreativeEssentiaJar.class);
        registerBlock(forgeRegistry, new BlockInspirationEngine());
        registerBlock(forgeRegistry, new BlockMadnessEngine());
        registerBlock(forgeRegistry, new BlockPortalAnchor());
        registerBlock(forgeRegistry, new BlockPortalGenerator(), ItemBlockPortalGenerator.class);
        registerBlock(forgeRegistry, new BlockCatalyzationChamber());
        registerBlock(forgeRegistry, new BlockHexamite());
        registerBlock(forgeRegistry, new BlockFluxCapacitor(), ItemBlockFluxCapacitor.class);
        registerBlock(forgeRegistry, new BlockMeteorb());
        registerBlock(forgeRegistry, new BlockOreDiviner());
        registerBlock(forgeRegistry, new BlockMeatyOrb());
        registerBlock(forgeRegistry, new BlockVoidBeacon());
        registerBlock(forgeRegistry, new BlockFluxDistiller(), ItemBlockFluxDistiller.class);
        registerBlock(forgeRegistry, new BlockPrimordialAccelerator());
        registerBlock(forgeRegistry, new BlockPrimordialAcceleratorTunnel());
        registerBlock(forgeRegistry, new BlockPrimordialAcceleratorTerminus());
        registerBlock(forgeRegistry, new BlockPrimordialAccretionChamber());
        registerBlock(forgeRegistry, new BlockShimmerleafCrop(), false);
        registerBlock(forgeRegistry, new BlockCinderpearlCrop(), false);
        registerBlock(forgeRegistry, new BlockVishroomCrop(), false);
        registerBlock(forgeRegistry, new BlockAlkahestVat());
        registerBlock(forgeRegistry, new BlockCoalescenceMatrix());
        registerBlock(forgeRegistry, new BlockTW(Material.IRON, "coalescence_matrix_precursor"));

        registerBlock(forgeRegistry, new BlockTWPlaceholder("placeholder_arcane_stone"));
        registerBlock(forgeRegistry, new BlockTWPlaceholder("placeholder_obsidian"));
        registerBlock(forgeRegistry, new BlockPrimordialAccretionChamberPlaceholder("placeholder_thaumium_block"));
        registerBlock(forgeRegistry, new BlockPrimordialAccretionChamberPlaceholder("placeholder_void_metal_block"));
        registerBlock(forgeRegistry, new BlockPrimordialAccretionChamberPlaceholder("placeholder_adv_alch_construct"));

        FluidRegistry.registerFluid(FluidQuicksilver.INSTANCE);
        FluidRegistry.addBucketForFluid(FluidQuicksilver.INSTANCE);
        forgeRegistry.register(new BlockFluidQuicksilver());
    }

    private static void registerBlock(IForgeRegistry<Block> forgeRegistry, Block block) {
        registerBlock(forgeRegistry, block, true);
    }

    private static void registerBlock(IForgeRegistry<Block> forgeRegistry, Block block, boolean hasItem) {
        forgeRegistry.register(block);
        if (hasItem)
            InitBlocks.ITEM_BLOCKS.add(new ItemBlock(block));
    }

    private static <T extends ItemBlock> void registerBlock(IForgeRegistry<Block> forgeRegistry, Block block, Class<T> clazz) {
        forgeRegistry.register(block);
        try {
            ItemBlock itemBlock = (ItemBlock) clazz.getConstructors()[0].newInstance(new Object[]{block});
            InitBlocks.ITEM_BLOCKS.add(itemBlock);
        } catch (Exception e) {
            ThaumicWonders.LOGGER.catching(e);
        }
    }

    public static void initItemBlocks(IForgeRegistry<Item> forgeRegistry) {
        for (ItemBlock itemBlock : InitBlocks.ITEM_BLOCKS) {
            Block block = itemBlock.getBlock();
            itemBlock.setRegistryName(block.getRegistryName());
            forgeRegistry.register(itemBlock);
        }
    }

    public static void initTileEntities() {
        GameRegistry.registerTileEntity(TileEverburningUrn.class, new ResourceLocation(ThaumicWonders.MODID, "TileEverburningUrn"));
        GameRegistry.registerTileEntity(TileDimensionalRipper.class, new ResourceLocation(ThaumicWonders.MODID, "TileDimensionalRipper"));
        GameRegistry.registerTileEntity(TileCreativeEssentiaJar.class, new ResourceLocation(ThaumicWonders.MODID, "TileCreativeEssentiaJar"));
        GameRegistry.registerTileEntity(TileInspirationEngine.class, new ResourceLocation(ThaumicWonders.MODID, "TileInspirationEngine"));
        GameRegistry.registerTileEntity(TileMadnessEngine.class, new ResourceLocation(ThaumicWonders.MODID, "TileMadnessEngine"));
        GameRegistry.registerTileEntity(TilePortalAnchor.class, new ResourceLocation(ThaumicWonders.MODID, "TilePortalAnchor"));
        GameRegistry.registerTileEntity(TilePortalGenerator.class, new ResourceLocation(ThaumicWonders.MODID, "TilePortalGenerator"));
        GameRegistry.registerTileEntity(TileCatalyzationChamber.class, new ResourceLocation(ThaumicWonders.MODID, "TileCatalyzationChamber"));
        GameRegistry.registerTileEntity(TileMeteorb.class, new ResourceLocation(ThaumicWonders.MODID, "TileMeteorb"));
        GameRegistry.registerTileEntity(TileOreDiviner.class, new ResourceLocation(ThaumicWonders.MODID, "TileOreDiviner"));
        GameRegistry.registerTileEntity(TileMeatyOrb.class, new ResourceLocation(ThaumicWonders.MODID, "TileMeatyOrb"));
        GameRegistry.registerTileEntity(TileVoidBeacon.class, new ResourceLocation(ThaumicWonders.MODID, "TileVoidBeacon"));
        GameRegistry.registerTileEntity(TileFluxDistiller.class, new ResourceLocation(ThaumicWonders.MODID, "TileFluxDistiller"));
        GameRegistry.registerTileEntity(TilePrimordialAccelerator.class, new ResourceLocation(ThaumicWonders.MODID, "TilePrimordialAccelerator"));
        GameRegistry.registerTileEntity(TilePrimordialAccretionChamber.class, new ResourceLocation(ThaumicWonders.MODID, "TilePrimordialAccretionChamber"));
        GameRegistry.registerTileEntity(TilePrimordialAccretionChamberPlaceholder.class, new ResourceLocation(ThaumicWonders.MODID, "TilePrimordialAccretionChamberPlaceholder"));
        GameRegistry.registerTileEntity(TileAlkahestVat.class, new ResourceLocation(ThaumicWonders.MODID, "TileAlkahestVat"));
        GameRegistry.registerTileEntity(TileCoalescenceMatrix.class, new ResourceLocation(ThaumicWonders.MODID, "TileCoalescenceMatrix"));
    }
}
