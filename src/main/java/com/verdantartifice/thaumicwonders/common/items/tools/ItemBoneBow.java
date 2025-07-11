package com.verdantartifice.thaumicwonders.common.items.tools;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.items.ItemsTW;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;

public class ItemBoneBow extends ItemBow implements IRechargable {
    protected static final int VIS_CAPACITY = 200;
    protected static final int POWERED_CHARGE_TIME = 10;
    protected static final int UNPOWERED_CHARGE_TIME = 20;

    public ItemBoneBow() {
        this.setCreativeTab(ThaumicWonders.CREATIVE_TAB);
        this.setRegistryName(ThaumicWonders.MODID, "bone_bow");
        this.setTranslationKey(this.getRegistryName().toString());
        this.setMaxStackSize(1);
        this.setMaxDamage(512);

        this.addPropertyOverride(new ResourceLocation(ThaumicWonders.MODID, "pull"), (stack, worldIn, entityIn) -> {
            if (entityIn == null) {
                return 0.0F;
            } else {
                float maxCharge = (RechargeHelper.getCharge(stack) > 0) ? (float) POWERED_CHARGE_TIME : (float) UNPOWERED_CHARGE_TIME;
                return entityIn.getActiveItemStack().getItem() != ItemsTW.BONE_BOW ? 0.0F : (float) (stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / maxCharge;
            }
        });
    }

    public static float getArrowVelocity(ItemStack stack, int charge) {
        float maxCharge = (RechargeHelper.getCharge(stack) > 0) ? (float) POWERED_CHARGE_TIME : (float) UNPOWERED_CHARGE_TIME;
        float f = (float) charge / maxCharge;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return repair.isItemEqual(new ItemStack(Items.BONE)) || super.getIsRepairable(toRepair, repair);
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
        int ticks = this.getMaxItemUseDuration(stack) - count;
        if (ticks >= POWERED_CHARGE_TIME && RechargeHelper.getCharge(stack) > 0) {
            player.stopActiveHand();
        }
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
    public IRarity getForgeRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }

    protected ItemStack findAmmo(EntityPlayer player) {
        if (this.isArrow(player.getHeldItemOffhand())) {
            return player.getHeldItemOffhand();
        } else if (this.isArrow(player.getHeldItemMainhand())) {
            return player.getHeldItemMainhand();
        } else {
            for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                ItemStack itemstack = player.inventory.getStackInSlot(i);
                if (this.isArrow(itemstack)) {
                    return itemstack;
                }
            }
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        if (entityLiving instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer) entityLiving;
            boolean flag = entityplayer.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
            ItemStack itemstack = this.findAmmo(entityplayer);

            int charge = this.getMaxItemUseDuration(stack) - timeLeft;
            charge = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, worldIn, entityplayer, charge, !itemstack.isEmpty() || flag);
            if (charge < 0) {
                return;
            }

            if (!itemstack.isEmpty() || flag) {
                if (itemstack.isEmpty()) {
                    itemstack = new ItemStack(Items.ARROW);
                }

                float velocity = getArrowVelocity(stack, charge);
                if ((double) velocity >= 0.1D) {
                    boolean flag1 = entityplayer.capabilities.isCreativeMode || (itemstack.getItem() instanceof ItemArrow && ((ItemArrow) itemstack.getItem()).isInfinite(itemstack, stack, entityplayer));
                    if (!worldIn.isRemote) {
                        ItemArrow itemarrow = (ItemArrow) (itemstack.getItem() instanceof ItemArrow ? itemstack.getItem() : Items.ARROW);
                        EntityArrow entityarrow = itemarrow.createArrow(worldIn, itemstack, entityplayer);
                        float velocityMultiplier = (RechargeHelper.getCharge(stack) > 0) ? 4.0F : 3.0F;
                        entityarrow.shoot(entityplayer, entityplayer.rotationPitch, entityplayer.rotationYaw, 0.0F, velocity * velocityMultiplier, 1.0F);
                        RechargeHelper.consumeCharge(stack, entityplayer, 1);

                        if (velocity == 1.0F) {
                            entityarrow.setIsCritical(true);
                        }

                        int powerLevels = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
                        if (powerLevels > 0) {
                            entityarrow.setDamage(entityarrow.getDamage() + (double) powerLevels * 0.5D + 0.5D);
                        }

                        int punchLevels = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
                        if (punchLevels > 0) {
                            entityarrow.setKnockbackStrength(punchLevels);
                        }

                        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0) {
                            entityarrow.setFire(100);
                        }

                        stack.damageItem(1, entityplayer);
                        if (flag1 || entityplayer.capabilities.isCreativeMode && (itemstack.getItem() == Items.SPECTRAL_ARROW || itemstack.getItem() == Items.TIPPED_ARROW)) {
                            entityarrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
                        }

                        worldIn.spawnEntity(entityarrow);
                    }

                    worldIn.playSound(null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + velocity * 0.5F);

                    if (!flag1 && !entityplayer.capabilities.isCreativeMode) {
                        itemstack.shrink(1);
                        if (itemstack.isEmpty()) {
                            entityplayer.inventory.deleteStack(itemstack);
                        }
                    }
                }
            }
        }
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
