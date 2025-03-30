package com.verdantartifice.thaumicwonders.common.items.consumables;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.entities.EntityPrimalArrow;
import com.verdantartifice.thaumicwonders.common.items.base.IVariantItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class ItemPrimalArrow extends ItemArrow implements IVariantItem {
    protected final String baseName;
    protected String[] variants;
    protected int[] variantsMeta;
    
    public ItemPrimalArrow() {
        this.baseName = "primal_arrow";
        this.setRegistryName(ThaumicWonders.MODID, this.baseName);
        this.setTranslationKey(this.getRegistryName().toString());
        this.setCreativeTab(ThaumicWonders.CREATIVE_TAB);
        
        this.variants = new String[] { "air", "earth", "fire", "water", "order", "entropy" };
        this.setHasSubtypes(true);
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
    
    @Override
    public EntityArrow createArrow(World worldIn, ItemStack stack, EntityLivingBase shooter) {
        EntityPrimalArrow entity = new EntityPrimalArrow(worldIn, shooter);
        entity.setArrowType(stack.getMetadata());
        return entity;
    }
    
    @Override
    public boolean isInfinite(ItemStack stack, ItemStack bow, EntityPlayer player) {
        int enchantLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, bow);
        if (enchantLevel <= 0) {
            return false;
        } else {
            // Primal arrows have only a 1-in-3 chance of being affected by an Infinity enchant
            return (player.world.rand.nextInt(3) == 0);
        }
    }
}
