package com.verdantartifice.thaumicwonders.proxy;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.registries.IForgeRegistry;

public class ProxyBlock {
    private static ModelResourceLocation fluidQuicksilverLocation = new ModelResourceLocation(new ResourceLocation(ThaumicWonders.MODID, "fluid_quicksilver"), "fluid");
    
    public static void setupBlocksClient(IForgeRegistry<Block> forgeRegistry) {
        Block fluidQuicksilverBlock = forgeRegistry.getValue(new ResourceLocation(ThaumicWonders.MODID, "fluid_quicksilver"));
        Item fluidQuicksilverItem = Item.getItemFromBlock(fluidQuicksilverBlock);
        ModelBakery.registerItemVariants(fluidQuicksilverItem);
        ModelLoader.setCustomMeshDefinition(fluidQuicksilverItem, stack -> ProxyBlock.fluidQuicksilverLocation);
        ModelLoader.setCustomStateMapper(fluidQuicksilverBlock, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return ProxyBlock.fluidQuicksilverLocation;
            }
        });
    }
}
