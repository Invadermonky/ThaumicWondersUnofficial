package com.verdantartifice.thaumicwonders.common.registry;

import com.google.common.collect.ImmutableSet;
import com.verdantartifice.thaumicwonders.common.effects.PotionsTW;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import thaumcraft.common.lib.enchantment.EnumInfusionEnchantment;

import java.util.Set;

public class InfusionEnchantmentsTW {
    public static EnumInfusionEnchantment VOIDFLAME = EnumHelper.addEnum(
            EnumInfusionEnchantment.class, "VOIDFLAME",
            new Class<?>[]{Set.class, int.class, String.class},
            ImmutableSet.of("weapon"), 3, "TWOND_PRIMAL_DESTROYER"
    );

    public static void handleVoidflameAttack(LivingHurtEvent event) {
        if (event.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase source = (EntityLivingBase) event.getSource().getTrueSource();
            EntityLivingBase target = event.getEntityLiving();
            // 某些情况下 getActiveHand 可能为 null，直接查询主/副手可避免 NPE/IAE
            int rankMain = EnumInfusionEnchantment.getInfusionEnchantmentLevel(source.getHeldItem(EnumHand.MAIN_HAND), VOIDFLAME);
            int rankOff = EnumInfusionEnchantment.getInfusionEnchantmentLevel(source.getHeldItem(EnumHand.OFF_HAND), VOIDFLAME);
            int rank = Math.max(rankMain, rankOff);
            if (rank > 0) {
                target.addPotionEffect(new PotionEffect(PotionsTW.VOIDFLAME, 60 + 60 * rank, 1, true, false));
            }
        }

    }
}
