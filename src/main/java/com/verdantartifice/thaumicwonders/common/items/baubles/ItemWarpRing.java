package com.verdantartifice.thaumicwonders.common.items.baubles;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.config.ConfigHandlerTW;
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

import java.util.List;

public class ItemWarpRing extends ItemTW implements IWarpingGear, IVisDiscountGear, IBauble {
    public ItemWarpRing() {
        super("warp_ring");
        this.setMaxStackSize(1);
        this.addPropertyOverride(new ResourceLocation(ThaumicWonders.MODID, "warp"), (stack, worldIn, entityIn) -> this.getWarp(stack));
    }

    //TODO:
    //  Name - Initiate's Band of Cleansing
    //  Research
    //  Textures


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
            for (PotionEffect effect : player.getActivePotionEffects()) {
                if (ConfigTags.WARP_RING_REMOVALS.containsKey(effect.getPotion())) {
                    if (ConfigTags.WARP_RING_REMOVALS.get(effect.getPotion()).getFirst() <= this.getWarp(stack)) {
                        int amplifier = 1 + effect.getAmplifier();
                        int bufferValue = ConfigTags.WARP_RING_REMOVALS.get(effect.getPotion()).getSecond();
                        this.incrementBuffer(stack, bufferValue * amplifier);
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
                entityLiving.sendMessage(new TextComponentTranslation("item.thaumicwonders:warp_ring.chat"));
                ThaumcraftCapabilities.getWarp((EntityPlayer) entityLiving).add(IPlayerWarp.EnumWarpType.TEMPORARY, warp);
                this.setWarp(stack, 0);
            }
            if (this.getBuffer(stack) >= ConfigHandlerTW.warp_ring.bufferSize) {
                this.incrementBuffer(stack, 0);
            }
        }
    }

    @Override
    public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
        return player instanceof EntityPlayer && BaublesApi.isBaubleEquipped((EntityPlayer) player, this) == -1;
    }

    @Override
    public boolean willAutoSync(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    public int getBuffer(ItemStack stack) {
        return getTag(stack).getInteger("buffer");
    }

    public void setBuffer(ItemStack stack, int buffer) {
        getTag(stack).setInteger("buffer", buffer);
    }

    public void incrementBuffer(ItemStack stack, int increment) {
        int warp = this.getWarp(stack);
        if (warp < 5) {
            int bufferSize = ConfigHandlerTW.warp_ring.bufferSize;
            int buffer = this.getBuffer(stack);
            buffer += increment;
            if (buffer >= bufferSize) {
                warp += buffer / bufferSize;
                this.setWarp(stack, warp);
                buffer = warp >= 5 ? 0 : buffer % bufferSize;
            }
            this.setBuffer(stack, buffer);
        }
    }

    public int getWarp(ItemStack stack) {
        return getTag(stack).getShort("warp");
    }

    public void setWarp(ItemStack stack, int warp) {
        getTag(stack).setShort("warp", (short) Math.min(warp, 5));
    }

    protected NBTTagCompound getTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }
}
