package com.verdantartifice.thaumicwonders.common.items.base;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;

public interface IVariantItem {
    Item getItem();

    default String[] getVariantNames() {
        return new String[]{"normal"};
    }

    default int[] getVariantMeta() {
        return new int[]{0};
    }

    default ModelResourceLocation getCustomModelResourceLocation(String variant) {
        return new ModelResourceLocation(ThaumicWonders.MODID + ":" + variant);
    }
}
