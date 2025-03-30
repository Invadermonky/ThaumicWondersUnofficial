package com.verdantartifice.thaumicwonders.common.blocks.essentia;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.blocks.essentia.BlockJarItem;

import java.util.List;

public class ItemBlockCreativeEssentiaJar extends BlockJarItem {
    public ItemBlockCreativeEssentiaJar(Block block) {
        super(block);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_PURPLE + I18n.format("item.creative_only"));
        tooltip.add(I18n.format("tile.thaumicwonders.creative_essentia_jar.tooltip.1"));
        tooltip.add(I18n.format("tile.thaumicwonders.creative_essentia_jar.tooltip.2"));
        tooltip.add(I18n.format("tile.thaumicwonders.creative_essentia_jar.tooltip.3"));
        tooltip.add(I18n.format("tile.thaumicwonders.creative_essentia_jar.tooltip.4"));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
