package com.verdantartifice.thaumicwonders.common.items.entities;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.config.ConfigHandlerTW;
import com.verdantartifice.thaumicwonders.common.entities.EntityFlyingCarpet;
import com.verdantartifice.thaumicwonders.common.items.base.ItemTW;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;

public class ItemFlyingCarpet extends ItemTW implements IRechargable {
    public ItemFlyingCarpet() {
        super("flying_carpet");

        this.addPropertyOverride(new ResourceLocation(ThaumicWonders.MODID, "color"), (stack, worldIn, entityIn) -> {
            EnumDyeColor color = null;
            if (stack.getItem() instanceof ItemFlyingCarpet) {
                color = ((ItemFlyingCarpet) stack.getItem()).getDyeColor(stack);
            }
            if (color == null) {
                // Default to red if no dye color is applied
                color = EnumDyeColor.RED;
            }
            return ((float) color.getMetadata() / 16.0F);
        });
    }

    @Override
    public int getMaxCharge(ItemStack stack, EntityLivingBase player) {
        return ConfigHandlerTW.flying_carpet.visCapacity;
    }

    @Override
    public IRechargable.EnumChargeDisplay showInHud(ItemStack stack, EntityLivingBase player) {
        return IRechargable.EnumChargeDisplay.NORMAL;
    }

    public EnumDyeColor getDyeColor(ItemStack stack) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound != null) {
            NBTTagCompound innerCompound = compound.getCompoundTag("display");
            if (innerCompound != null && innerCompound.hasKey("color")) {
                return EnumDyeColor.byMetadata(innerCompound.getInteger("color"));
            }
        }
        return null;
    }

    public void setDyeColor(ItemStack stack, EnumDyeColor color) {
        if (color == null) {
            return;
        }
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey("display")) {
            compound.setTag("display", new NBTTagCompound());
        }
        compound.getCompoundTag("display").setInteger("color", color.getMetadata());
    }

    public void removeDyeColor(ItemStack stack) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound != null) {
            NBTTagCompound innerCompound = compound.getCompoundTag("display");
            if (innerCompound != null && innerCompound.hasKey("color")) {
                innerCompound.removeTag("color");
            }
        }
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        IBlockState state = world.getBlockState(pos);
        if (!world.isRemote && state.getBlock() == Blocks.CAULDRON) {
            int level = state.getValue(BlockCauldron.LEVEL);
            if (level > 0) {
                this.removeDyeColor(player.getHeldItem(hand));
                Blocks.CAULDRON.setWaterLevel(world, pos, state, level - 1);
                return EnumActionResult.SUCCESS;
            } else {
                return EnumActionResult.PASS;
            }
        } else if (!world.isRemote && state.getBlock() != BlocksTC.rechargePedestal) {
            if (side != EnumFacing.UP) {
                return EnumActionResult.PASS;
            }
            double posX = (double) pos.getX() + (double) hitX;
            double posY = (double) pos.getY() + (double) hitY;
            double posZ = (double) pos.getZ() + (double) hitZ;
            EntityFlyingCarpet entityCarpet = new EntityFlyingCarpet(world, posX, posY, posZ);
            if (player.getHeldItem(hand).hasTagCompound()) {
                entityCarpet.setVisCharge(RechargeHelper.getCharge(player.getHeldItem(hand)));
                entityCarpet.setEnergy(player.getHeldItem(hand).getTagCompound().getInteger("energy"));
                entityCarpet.setDyeColor(this.getDyeColor(player.getHeldItem(hand)));
            }
            entityCarpet.rotationYaw = player.rotationYaw;
            world.spawnEntity(entityCarpet);
            world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_CLOTH_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            player.getHeldItem(hand).shrink(1);
            return EnumActionResult.SUCCESS;
        } else {
            return EnumActionResult.PASS;
        }
    }

    @Override
    public IRarity getForgeRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }
}
