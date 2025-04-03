package com.verdantartifice.thaumicwonders.common.items.armor;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.render.IRenderBauble;
import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.config.ConfigHandlerTW;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
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

    @Override
    public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
        this.cancelNightVision(player, true);
    }

    protected void doTick(ItemStack stack, EntityLivingBase player) {
        if(this.shouldNightVisionActivate(player)) {
            this.consumeEnergy(stack, player);
            if (this.hasEnergy(stack)) {
                player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 305, 0, true, false));
            }
        } else {
            this.cancelNightVision(player, false);
        }
    }

    protected boolean shouldNightVisionActivate(EntityLivingBase entityLiving) {
        if(!ConfigHandlerTW.night_vision_goggles.adaptiveNightVision)
            return true;

        World world = entityLiving.world;
        BlockPos playerPos = new BlockPos(entityLiving.posX, entityLiving.getEntityBoundingBox().maxY, entityLiving.posZ);
        int playerLight = world.getLightFromNeighbors(playerPos);
        if(world.isThundering()) {
            int skyLight = world.getSkylightSubtracted();
            world.setSkylightSubtracted(10);
            playerLight = world.getLightFromNeighbors(playerPos);
            world.setSkylightSubtracted(skyLight);
        }
        if(playerLight <= 7) {
            return true;
        } else {
            RayTraceResult trace = entityLiving.rayTrace(24, 0);
            if(trace != null) {
                switch (trace.typeOfHit) {
                    case BLOCK:
                        return world.getLight(trace.getBlockPos().offset(trace.sideHit)) <= 7;
                    case MISS:
                    case ENTITY:
                        return world.getLight(trace.getBlockPos()) <= 7;
                }
            }
            return false;
        }
    }

    protected void cancelNightVision(EntityLivingBase player, boolean forceDisable) {
        PotionEffect effect = player.getActivePotionEffect(MobEffects.NIGHT_VISION);
        if(effect != null && (effect.getDuration() < 200 || (forceDisable && effect.getDuration() < 301))) {
            player.removePotionEffect(MobEffects.NIGHT_VISION);
        }
    }
    
    protected void consumeEnergy(ItemStack stack, EntityLivingBase player) {
        int energy = this.getEnergy(stack);
        if (energy > 0) {
            energy--;
        } else if (RechargeHelper.consumeCharge(stack, player, 1)) {
            energy = ConfigHandlerTW.night_vision_goggles.energyPerVis * 20;
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
        return ConfigHandlerTW.night_vision_goggles.visCapacity;
    }

    @Override
    public EnumChargeDisplay showInHud(ItemStack stack, EntityLivingBase player) {
        return IRechargable.EnumChargeDisplay.NORMAL;
    }
}
