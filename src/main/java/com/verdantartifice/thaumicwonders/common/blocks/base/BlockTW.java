package com.verdantartifice.thaumicwonders.common.blocks.base;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockTW extends Block {
    public BlockTW(Material material, String name) {
        super(material);
        this.setRegistryName(ThaumicWonders.MODID, name);
        this.setTranslationKey(this.getRegistryName().toString());
        this.setCreativeTab(ThaumicWonders.CREATIVE_TAB);
        this.setResistance(2.0F);
        this.setHardness(1.5F);
    }
}
