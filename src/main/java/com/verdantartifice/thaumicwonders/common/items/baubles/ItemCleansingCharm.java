package com.verdantartifice.thaumicwonders.common.items.baubles;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.verdantartifice.thaumicwonders.common.config.ConfigHandlerTW;
import com.verdantartifice.thaumicwonders.common.items.base.ItemTW;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;

import java.util.List;

public class ItemCleansingCharm extends ItemTW implements IBauble, IRechargable {
    protected static final int VIS_CAPACITY = 200;

    public ItemCleansingCharm() {
        super("cleansing_charm");
        this.setMaxStackSize(1);
        this.setNoRepair();
    }

    @Override
    public int getMaxCharge(ItemStack stack, EntityLivingBase player) {
        return VIS_CAPACITY;
    }

    @Override
    public EnumChargeDisplay showInHud(ItemStack stack, EntityLivingBase player) {
        return IRechargable.EnumChargeDisplay.NORMAL;
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.CHARM;
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase elb) {
        if (elb instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) elb;
            IPlayerWarp warp = ThaumcraftCapabilities.getWarp(player);
            if (warp.get(IPlayerWarp.EnumWarpType.NORMAL) > 0) {
                if (this.hasEnergy(itemstack)) {
                    this.incrementProgress(itemstack);
                    int progress = this.getProgress(itemstack);
                    if (progress % 60 == 0) {
                        AuraHelper.polluteAura(player.world, player.getPosition(), 0.1F, true);
                    }
                    if (progress >= ConfigHandlerTW.cleansing_charm.timeToRemoveFlux) {
                        ThaumcraftApi.internalMethods.addWarpToPlayer(player, -ConfigHandlerTW.cleansing_charm.fluxRemoved, IPlayerWarp.EnumWarpType.NORMAL);
                        this.setProgress(itemstack, 0);
                    }
                }
                this.consumeEnergy(itemstack, player);
            }
        }
    }

    protected void incrementProgress(ItemStack stack) {
        this.setProgress(stack, this.getProgress(stack) + 1);
    }

    protected int getProgress(ItemStack stack) {
        if (stack.hasTagCompound()) {
            return stack.getTagCompound().getInteger("progress");
        } else {
            return 0;
        }
    }

    protected void setProgress(ItemStack stack, int progress) {
        stack.setTagInfo("progress", new NBTTagInt(progress));
    }

    protected void consumeEnergy(ItemStack stack, EntityLivingBase player) {
        int energy = this.getEnergy(stack);
        if (energy > 0) {
            energy--;
        } else if (RechargeHelper.consumeCharge(stack, player, 1)) {
            energy = (ConfigHandlerTW.cleansing_charm.timeToRemoveFlux / VIS_CAPACITY) - 1;
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
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        int percent = (int) (((double) this.getProgress(stack) / (double) ConfigHandlerTW.cleansing_charm.timeToRemoveFlux) * 100);
        tooltip.add(I18n.format("item.thaumicwonders.cleansing_charm.tooltip.progress", percent));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }
}
