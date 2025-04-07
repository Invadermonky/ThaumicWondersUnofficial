package com.verdantartifice.thaumicwonders.common.items.tools;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.network.PacketHandler;
import com.verdantartifice.thaumicwonders.common.network.packets.PacketLocalizedMessage;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.items.IWarpingGear;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.lib.enchantment.EnumInfusionEnchantment;

import java.util.List;

public class ItemPrimalDestroyer extends ItemSword implements IWarpingGear {
    public static final int MAX_HUNGER = 600;

    public static Item.ToolMaterial toolMatVoidflame = EnumHelper.addToolMaterial("VOIDFLAME", 4, 200, 8.0F, 8.0F, 20).setRepairItem(new ItemStack(ItemsTC.ingots, 1, 1));

    public ItemPrimalDestroyer() {
        super(toolMatVoidflame);
        this.setCreativeTab(ThaumicWonders.CREATIVE_TAB);
        this.setRegistryName(ThaumicWonders.MODID, "primal_destroyer");
        this.setTranslationKey(this.getRegistryName().toString());
    }

    @Override
    public int getWarp(ItemStack itemStack, EntityPlayer player) {
        return 3;
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
        if (!worldIn.isRemote && entityIn != null && (entityIn.ticksExisted % 20 == 0) && entityIn instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer) entityIn;
            if (stack.isItemDamaged()) {
                stack.damageItem(-1, entityPlayer);
            }

            boolean inOffHand = itemSlot == 0 && ItemStack.areItemStacksEqual(stack, entityPlayer.getHeldItemOffhand());
            boolean held = isSelected || inOffHand;
            boolean onHotbar = (itemSlot >= 0 && itemSlot <= 8);
            boolean equipped = held || onHotbar;
            if (equipped) {
                int hunger = 0;
                if (stack.hasTagCompound()) {
                    hunger = stack.getTagCompound().getInteger("hunger");
                }
                if (hunger >= MAX_HUNGER) {
                    // Damage player and reset hunger
                    if (entityIn instanceof EntityPlayerMP) {
                        PacketHandler.INSTANCE.sendTo(new PacketLocalizedMessage("event.primal_destroyer.hunger_full"), (EntityPlayerMP) entityIn);
                    }
                    entityPlayer.attackEntityFrom(new DamageSource("primalDestroyerHunger"), 12.0F);
                    entityPlayer.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 60));
                    entityPlayer.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 120));
                    hunger = 0;

                    // Give addenda research
                    IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(entityPlayer);
                    if (!knowledge.isResearchKnown("f_thevoidhungers")) {
                        knowledge.addResearch("f_thevoidhungers");
                        knowledge.sync((EntityPlayerMP) entityPlayer);
                    }
                } else {
                    hunger++;
                }
                stack.setTagInfo("hunger", new NBTTagInt(hunger));
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.GOLD + I18n.format("enchantment.special.sapgreat"));
        tooltip.add(TextFormatting.GOLD + I18n.format("enchantment.special.voidflame"));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        if (oldStack.getItem() == newStack.getItem() && !slotChanged) {
            // Suppress the re-equip animation if only the NBT data has changed
            return false;
        } else {
            return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
        }
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (!target.world.isRemote && (!(target instanceof EntityPlayer) || !(attacker instanceof EntityPlayer) || FMLCommonHandler.instance().getMinecraftServerInstance().isPVPEnabled())) {
            try {
                // Attempt to apply the greater sapping and voidflame effects
                target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 60));
                target.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 120));
                target.setFire(3);
            } catch (Exception ignored) {
            }
        }
        if (!target.world.isRemote) {
            if (target.getHealth() <= 0.0F) {
                this.decreaseHunger(stack, MAX_HUNGER);
            } else {
                this.decreaseHunger(stack, MAX_HUNGER / 5);
            }
        }
        return super.hitEntity(stack, target, attacker);
    }

    private void decreaseHunger(ItemStack stack, int delta) {
        int hunger = 0;
        if (stack.hasTagCompound()) {
            hunger = stack.getTagCompound().getInteger("hunger");
        }
        hunger = Math.max(0, hunger - delta);
        stack.setTagInfo("hunger", new NBTTagInt(hunger));
    }
    
    // Correctly applies the infusion enchant when taken from Creative mode or JEI
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            ItemStack stack = new ItemStack(this);
            EnumInfusionEnchantment.addInfusionEnchantment(stack, EnumInfusionEnchantment.ESSENCE, 3);
            items.add(stack);
        }
    }
}
