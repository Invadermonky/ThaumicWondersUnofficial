package com.verdantartifice.thaumicwonders.common.items.base;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemTW extends Item implements IVariantItem {
    protected final String baseName;
    protected String[] variants;
    protected int[] variantsMeta;
    
    public ItemTW(String name) {
        this(name, new String[0]);
    }
    
    public ItemTW(String name, String... variants) {
        super();
        this.setRegistryName(ThaumicWonders.MODID, name);
        this.setTranslationKey(this.getRegistryName().toString());
        this.setCreativeTab(ThaumicWonders.CREATIVE_TAB);
        
        this.baseName = name;
        this.setHasSubtypes(variants.length > 1);
        this.variants = (variants.length == 0) ? new String[] { name } : variants;
        this.variantsMeta = new int[this.variants.length];
        for (int index = 0; index < this.variants.length; index++) {
            this.variantsMeta[index] = index;
        }
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        if (this.getHasSubtypes() && stack.getMetadata() < this.variants.length && !this.variants[stack.getMetadata()].equals(this.baseName)) {
            return String.format(super.getTranslationKey() + ".%s", this.variants[stack.getMetadata()]);
        } else {
            return super.getTranslationKey(stack);
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab == ThaumicWonders.CREATIVE_TAB || tab == CreativeTabs.SEARCH) {
            if (!this.getHasSubtypes()) {
                super.getSubItems(tab, items);
            } else {
                for (int meta : this.variantsMeta) {
                    items.add(new ItemStack(this, 1, meta));
                }
            }
        }
    }

    @Override
    public Item getItem() {
        return this;
    }
    
    @Override
    public String[] getVariantNames() {
        return this.variants;
    }
    
    @Override
    public int[] getVariantMeta() {
        return this.variantsMeta;
    }
    
    @Override
    public ModelResourceLocation getCustomModelResourceLocation(String variant) {
        if (this.baseName.equals(variant)) {
            return new ModelResourceLocation(ThaumicWonders.MODID + ":" + this.baseName);
        } else {
            return new ModelResourceLocation(ThaumicWonders.MODID + ":" + this.baseName, variant);
        }
    }
}
