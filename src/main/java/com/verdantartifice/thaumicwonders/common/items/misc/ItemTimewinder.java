package com.verdantartifice.thaumicwonders.common.items.misc;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.misc.GuiIds;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemClock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;

public class ItemTimewinder extends ItemClock implements IRechargable {
    public static final int COST = 25;
    public static final int CAPACITY = 100;

    public ItemTimewinder() {
        super();
        this.setRegistryName(ThaumicWonders.MODID, "timewinder");
        this.setTranslationKey(this.getRegistryName().toString());
        this.setCreativeTab(ThaumicWonders.CREATIVE_TAB);
        setMaxStackSize(1);
        setNoRepair();
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if (worldIn.provider.isSurfaceWorld()) {
            if (RechargeHelper.getCharge(playerIn.getHeldItem(handIn)) >= COST) {
                playerIn.openGui(ThaumicWonders.INSTANCE, GuiIds.TIMEWINDER, worldIn, 0, 0, 0);
                return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
            } else {
                return new ActionResult<>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
            }
        } else {
            if (worldIn.isRemote) {
                playerIn.sendStatusMessage(new TextComponentString(TextFormatting.DARK_PURPLE + I18n.format("event.timewinder.offworld")), true);
            }
            return new ActionResult<>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
        }
    }

    @Override
    public IRarity getForgeRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }

    @Override
    public int getMaxCharge(ItemStack stack, EntityLivingBase player) {
        return CAPACITY;
    }

    @Override
    public EnumChargeDisplay showInHud(ItemStack stack, EntityLivingBase player) {
        return IRechargable.EnumChargeDisplay.NORMAL;
    }
}
