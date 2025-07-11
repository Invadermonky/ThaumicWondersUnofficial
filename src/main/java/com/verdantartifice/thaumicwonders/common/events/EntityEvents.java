package com.verdantartifice.thaumicwonders.common.events;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.effects.infusion.PotionVoidflame;
import com.verdantartifice.thaumicwonders.common.init.InitAttributes;
import com.verdantartifice.thaumicwonders.common.items.armor.ItemBootsVoidWalker;
import com.verdantartifice.thaumicwonders.common.items.armor.ItemVoidFortressArmor;
import com.verdantartifice.thaumicwonders.common.misc.PlayerMovementAbilityManager;
import com.verdantartifice.thaumicwonders.common.registry.AttributesTW;
import com.verdantartifice.thaumicwonders.common.registry.InfusionEnchantmentsTW;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.api.damagesource.DamageSourceThaumcraft;

@Mod.EventBusSubscriber(modid = ThaumicWonders.MODID)
public class EntityEvents {
    @SubscribeEvent
    public static void entityHurt(LivingHurtEvent event) {
        AttributesTW.handleAttackDamageVoid(event);
        InfusionEnchantmentsTW.handleVoidflameAttack(event);
        ItemVoidFortressArmor.handleSippingFiendLeech(event);

        if (event.getSource() == DamageSource.FALL) {
            ItemStack boots = event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.FEET);
            if (boots.getItem() instanceof ItemBootsVoidWalker) {
                float damage = ((ItemBootsVoidWalker) boots.getItem()).getAdjustedFallDamage(boots, event.getAmount());
                event.setAmount(damage);
                if (damage == 0) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityHeal(LivingHealEvent event) {
        AttributesTW.handleHealingReduction(event);
    }

    @SubscribeEvent
    public static void onAttackEvent(LivingAttackEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        ItemStack boots = entity.getItemStackFromSlot(EntityEquipmentSlot.FEET);
        if (boots.getItem() instanceof ItemBootsVoidWalker) {
            if (event.getSource() == DamageSource.HOT_FLOOR) {
                event.setCanceled(true);
            } else if (event.getSource() == DamageSourceThaumcraft.taint && ((ItemBootsVoidWalker) boots.getItem()).handleVoidWalk(entity.world, entity, boots)) {
                event.setCanceled(true);
            } else if (event.getSource() == DamageSource.FALL && ((ItemBootsVoidWalker) boots.getItem()).getAdjustedFallDamage(boots, event.getAmount()) <= 0) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onDamageEvent(LivingDamageEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        ItemStack boots = entity.getItemStackFromSlot(EntityEquipmentSlot.FEET);
        if (boots.getItem() instanceof ItemBootsVoidWalker) {
            if (event.getSource() == DamageSource.HOT_FLOOR) {
                event.setAmount(0);
                event.setCanceled(true);
            } else if (event.getSource() == DamageSourceThaumcraft.taint && ((ItemBootsVoidWalker) boots.getItem()).handleVoidWalk(entity.world, entity, boots)) {
                event.setAmount(0);
                event.setCanceled(true);
            } else if (event.getSource() == DamageSource.FALL && ((ItemBootsVoidWalker) boots.getItem()).getAdjustedFallDamage(boots, event.getAmount()) <= 0) {
                event.setAmount(0);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (PlayerMovementAbilityManager.isValidSideForMovement(player)) {
                PlayerMovementAbilityManager.tick(player);
            }
        }
    }

    @SubscribeEvent
    public static void onJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer && PlayerMovementAbilityManager.isValidSideForMovement((EntityPlayer) event.getEntityLiving())) {
            PlayerMovementAbilityManager.onJump((EntityPlayer) event.getEntityLiving());
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityPlayer && PlayerMovementAbilityManager.isValidSideForMovement((EntityPlayer) event.getEntity())) {
            PlayerMovementAbilityManager.onPlayerRecreation((EntityPlayer) event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onEntityConstructed(EntityEvent.EntityConstructing event) {
        InitAttributes.onEntityconstructEvent(event);
    }

    @SubscribeEvent
    public static void onPotionRemoval(PotionEvent.PotionRemoveEvent event) {
        PotionVoidflame.onPotionRemoval(event);
    }
}
