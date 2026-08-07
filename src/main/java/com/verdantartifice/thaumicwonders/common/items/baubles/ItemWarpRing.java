package com.verdantartifice.thaumicwonders.common.items.baubles;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import com.invadermonky.thaumicapi.api.warpevent.IWarpEvent;
import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.config.ConfigTags;
import com.verdantartifice.thaumicwonders.common.items.base.ItemTW;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.api.items.IWarpingGear;
import thaumcraft.common.lib.SoundsTC;

import java.util.ArrayList;
import java.util.List;

public class ItemWarpRing extends ItemTW implements IWarpingGear, IVisDiscountGear, IBauble {
    public static int[] rankThresholds = {4, 8, 16, 24, 32};

    public ItemWarpRing() {
        super("warp_ring");
        this.setMaxStackSize(1);
        this.addPropertyOverride(new ResourceLocation(ThaumicWonders.MODID, "warp"), (stack, worldIn, entityIn) -> this.getWarp(stack));
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab == this.getCreativeTab()) {
            ItemStack base = new ItemStack(this);
            items.add(base);
            ItemStack warped = base.copy();
            this.setWarp(warped, 5);
            items.add(warped);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        int warp = this.getWarp(stack);
        String flavorText = "item.thaumicwonders:warp_ring.tooltip." + warp;
        if (I18n.hasKey(flavorText)) {
            tooltip.add(I18n.format(flavorText));
        }
    }

    @Override
    public IRarity getForgeRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }

    @Override
    public int getWarp(ItemStack itemStack, EntityPlayer entityPlayer) {
        return this.getWarp(itemStack);
    }

    @Override
    public int getVisDiscount(ItemStack itemStack, EntityPlayer entityPlayer) {
        return Math.max(1, this.getWarp(itemStack) * 3);
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.RING;
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase entityLiving) {
        if (entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entityLiving;
            int slot = BaublesApi.isBaubleEquipped(player, this);
            if(slot > -1) {
                List<PotionEffect> effects = new ArrayList<>(player.getActivePotionEffects());
                for (PotionEffect effect : effects) {
                    if (this.canRemovePotionEffect(stack, effect)) {
                        entityLiving.removePotionEffect(effect.getPotion());
                        this.incrementAndUpdateRing(player, stack, slot);
                    }
                }
            }
        }
    }

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase entityLiving) {
        if (!entityLiving.world.isRemote && entityLiving instanceof EntityPlayer && !((EntityPlayer) entityLiving).isCreative()) {
            int warp = this.getWarp(stack);
            if (warp > 0) {
                entityLiving.world.playSound(null, entityLiving.getPosition(), SoundsTC.whispers, SoundCategory.HOSTILE, 0.6f, 1.0f);
                entityLiving.sendMessage(new TextComponentTranslation("chat.thaumicwonders:warp_ring.warp"));
                ThaumcraftCapabilities.getWarp((EntityPlayer) entityLiving).add(IPlayerWarp.EnumWarpType.TEMPORARY, warp);
                this.setWarp(stack, 0);
                this.setBuffer(stack, 0);
            }
        }
    }

    @Override
    public boolean willAutoSync(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    public boolean canRemovePotionEffect(ItemStack ringStack, PotionEffect effect) {
        return ConfigTags.WARP_RING_POTIONS.containsKey(effect.getPotion()) && ConfigTags.WARP_RING_POTIONS.get(effect.getPotion()) <= this.getWarp(ringStack);
    }

    public boolean canPreventWarpEvent(ItemStack ringStack, IWarpEvent warpEvent) {
        return ConfigTags.WARP_RING_EVENTS.containsKey(warpEvent.getEventName()) && ConfigTags.WARP_RING_EVENTS.get(warpEvent.getEventName()) <= this.getWarp(ringStack);
    }

    public void incrementAndUpdateRing(EntityPlayer player, ItemStack ringStack, int ringSlot) {
        ringStack = ringStack.copy();
        this.incrementBuffer(ringStack);
        BaublesApi.getBaublesHandler(player).setStackInSlot(ringSlot, ringStack);
    }

    protected int getBuffer(ItemStack stack) {
        return getTag(stack).getInteger("buffer");
    }

    protected void setBuffer(ItemStack stack, int bufferValue) {
        this.getTag(stack).setInteger("buffer", bufferValue);
    }

    protected void incrementBuffer(ItemStack stack) {
        int warp = this.getWarp(stack);
        if (warp < 5) {
            int buffer = this.getBuffer(stack) + 1;
            if (buffer >= rankThresholds[warp]) {
                this.setWarp(stack, warp + 1);
                buffer = 0;
            }
            this.setBuffer(stack, buffer);
        }
    }

    protected int getWarp(ItemStack stack) {
        return this.getTag(stack).getShort("warp");
    }

    protected void setWarp(ItemStack stack, int warp) {
        this.getTag(stack).setShort("warp", (short) Math.min(warp, 5));
    }

    protected NBTTagCompound getTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }
}
