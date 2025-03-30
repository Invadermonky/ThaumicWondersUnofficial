package com.verdantartifice.thaumicwonders.common.items.armor;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.render.IRenderBauble;
import com.verdantartifice.thaumicwonders.ThaumicWonders;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftMaterials;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.client.lib.UtilsFX;

public class ItemNightVisionGoggles extends ItemArmor implements IBauble, IRenderBauble, IRechargable {
    protected static final ResourceLocation BAUBLE_TEXTURE = new ResourceLocation(ThaumicWonders.MODID, "textures/items/night_vision_goggles_bauble.png");
    protected static final int VIS_CAPACITY = 100;
    protected static final int ENERGY_PER_VIS = (20 * 60 * 30) / VIS_CAPACITY;
    
    public ItemNightVisionGoggles() {
        super(ThaumcraftMaterials.ARMORMAT_SPECIAL, 4, EntityEquipmentSlot.HEAD);
        this.setRegistryName(ThaumicWonders.MODID, "night_vision_goggles");
        this.setTranslationKey(this.getRegistryName().toString());
        this.setCreativeTab(ThaumicWonders.CREATIVE_TAB);
        this.setMaxDamage(350);
    }
    
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return "thaumicwonders:textures/entities/armor/night_vision_goggles.png";
    }
    
    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return repair.isItemEqual(new ItemStack(ItemsTC.ingots, 1, 2)) || super.getIsRepairable(toRepair, repair);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void onPlayerBaubleRender(ItemStack stack, EntityPlayer player, RenderType type, float partialTicks) {
        if (type == RenderType.HEAD) {
            boolean wearingHelm = (player.getItemStackFromSlot(EntityEquipmentSlot.HEAD) != null);
            Minecraft.getMinecraft().renderEngine.bindTexture(BAUBLE_TEXTURE);
            IRenderBauble.Helper.translateToHeadLevel(player);
            IRenderBauble.Helper.translateToFace();
            IRenderBauble.Helper.defaultTransforms();
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(-0.5D, -0.5D, wearingHelm ? 0.12D : 0.0D);
            UtilsFX.renderTextureIn3D(0.0F, 0.0F, 1.0F, 1.0F, 16, 26, 0.1F);
        }
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.HEAD;
    }
    
    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase player) {
        this.doTick(stack, player);
    }
    
    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        this.doTick(itemStack, player);
    }
    
    protected void doTick(ItemStack stack, EntityLivingBase player) {
        this.consumeEnergy(stack, player);
        if (this.hasEnergy(stack)) {
            player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 300, 0, true, false));
        }
    }
    
    protected void consumeEnergy(ItemStack stack, EntityLivingBase player) {
        int energy = this.getEnergy(stack);
        if (energy > 0) {
            energy--;
        } else if (RechargeHelper.consumeCharge(stack, player, 1)) {
            energy = ENERGY_PER_VIS;
        }
        this.setEnergy(stack, energy);
    }
    
    protected int getEnergy(ItemStack stack) {
        if (stack.hasTagCompound()) {
            return stack.getTagCompound().getInteger("energy");
        } else {
            return 0;
        }
    }
    
    protected void setEnergy(ItemStack stack, int energy) {
        stack.setTagInfo("energy", new NBTTagInt(energy));
    }
    
    protected boolean hasEnergy(ItemStack stack) {
        return (this.getEnergy(stack) > 0 || RechargeHelper.getCharge(stack) > 0);
    }
    
    @Override
    public int getMaxCharge(ItemStack stack, EntityLivingBase player) {
        return VIS_CAPACITY;
    }

    @Override
    public EnumChargeDisplay showInHud(ItemStack stack, EntityLivingBase player) {
        return IRechargable.EnumChargeDisplay.NORMAL;
    }
}
