package com.verdantartifice.thaumicwonders.common.items.armor;

import java.util.List;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.client.renderers.models.gear.ModelVoidFortressArmor;
import com.verdantartifice.thaumicwonders.client.renderers.models.gear.ModelVoidcallerArmor;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IWarpingGear;
import thaumcraft.api.items.ItemsTC;

public class ItemVoidcallerArmor extends ItemArmor implements ISpecialArmor, IWarpingGear, IGoggles {
    public static ItemArmor.ArmorMaterial MATERIAL = EnumHelper.addArmorMaterial("VOIDCALLER", "VOIDCALLER", 50, new int[]{2, 3, 4, 2}, 15, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 3.0F);
    ModelBiped model1 = null;
    ModelBiped model2 = null;

    public ItemVoidcallerArmor(String name, ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
        super(materialIn, renderIndexIn, equipmentSlotIn);
        this.setRegistryName(ThaumicWonders.MODID, name);
        this.setTranslationKey(ThaumicWonders.MODID + "." + this.getRegistryName().getPath());
        this.setCreativeTab(ThaumicWonders.CREATIVE_TAB);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
        if (this.model1 == null) {
            this.model1 = new ModelVoidcallerArmor(0.5f);
        }
        if (this.model2 == null) {
            this.model2 = new ModelVoidcallerArmor(1.0F);
        }
        EntityEquipmentSlot type = ((ItemArmor) itemStack.getItem()).armorType;
        ModelBiped model = (type == EntityEquipmentSlot.LEGS) ? this.model1 : this.model2;
        return CustomArmorHelper.getCustomArmorModel(entityLiving, itemStack, armorSlot, model);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return ThaumicWonders.MODID + ":textures/entities/armor/voidcaller_armor.png";
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return repair.isItemEqual(new ItemStack(ItemsTC.ingots, 1, 1)) ? true : super.getIsRepairable(toRepair, repair);
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        super.onArmorTick(world, player, itemStack);
        if (!world.isRemote && itemStack.getItemDamage() > 0 && player.ticksExisted % 20 == 0) {
            itemStack.damageItem(-1, player);
        }
    }

    @Override
    public int getWarp(ItemStack itemstack, EntityPlayer player) {
        return 3;
    }

    @Override
    public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
        int priority = 0;
        double ratio = this.damageReduceAmount / 25.0D;
        if (source.isMagicDamage()) {
            priority = 1;
            ratio = this.damageReduceAmount / 35.0D;
        } else if (source.isFireDamage() || source.isExplosion()) {
            priority = 1;
            ratio = this.damageReduceAmount / 20.0D;
        } else if (source.isUnblockable()) {
            priority = 0;
            ratio = 0.0D;
        }

        ArmorProperties ap = new ArmorProperties(priority, ratio, armor.getMaxDamage() - armor.getItemDamage() + 1);

        // Compute set bonus
        EntityEquipmentSlot[] slots = new EntityEquipmentSlot[]{EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.HEAD};
        int set = 0;
        for (EntityEquipmentSlot equipmentSlot : slots) {
            ItemStack piece = player.getItemStackFromSlot(equipmentSlot);
            if (piece != null && piece.getItem() instanceof ItemVoidcallerArmor) {
                set++;
            }
        }
        if (set >= 2) {
            ap.Armor += 1.0D;
            ap.Toughness += 1.0D;
        }
        if (set >= 3) {
            ap.Armor += 1.0D;
        }

        // Compute warpshell bonus
        if (player instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer) player;
            IPlayerWarp warp = ThaumcraftCapabilities.getWarp(ep);
            if (warp != null) {
                int pw = Math.min(100, warp.get(IPlayerWarp.EnumWarpType.PERMANENT));
                ap.Toughness += ((double) pw / 25.0D);
            }
        }

        return ap;
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
        // Compute set bonus for armor display
        EntityEquipmentSlot[] slots = new EntityEquipmentSlot[]{EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.HEAD};
        int set = 0;
        int armorRating = 0;
        for (EntityEquipmentSlot equipmentSlot : slots) {
            ItemStack piece = player.getItemStackFromSlot(equipmentSlot);
            if (piece != null && piece.getItem() instanceof ItemVoidcallerArmor) {
                set++;
            }
        }
        if (set >= 2) {
            armorRating++;
        }
        if (set >= 3) {
            armorRating++;
        }

        return armorRating;
    }

    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
        if (source != DamageSource.FALL) {
            stack.damageItem(damage, entity);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.GOLD + I18n.format("enchantment.special.warpshell"));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean showIngamePopups(ItemStack itemstack, EntityLivingBase player) {
        if (itemstack != null && itemstack.getItem() instanceof ItemArmor) {
            ItemArmor armor = (ItemArmor) itemstack.getItem();
            return armor.armorType == EntityEquipmentSlot.HEAD;
        } else {
            return false;
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }
}
