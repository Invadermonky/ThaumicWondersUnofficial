package com.verdantartifice.thaumicwonders.common.registry;

import com.google.common.collect.ImmutableSet;
import com.verdantartifice.thaumicwonders.common.effects.PotionsTW;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import thaumcraft.common.lib.enchantment.EnumInfusionEnchantment;

import java.util.Set;

public class InfusionEnchantmentsTW {
    public static EnumInfusionEnchantment VOIDFLAME = EnumHelper.addEnum(
            EnumInfusionEnchantment.class, "VOIDFLAME",
            new Class<?>[]{Set.class, int.class, String.class},
            ImmutableSet.of("weapon"), 3, "TWOND_VOIDFLAMEINFUSION"
    );

    public static void handleVoidflameAttack(LivingHurtEvent event) {
        if (event.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase source = (EntityLivingBase) event.getSource().getTrueSource();
            EntityLivingBase target = event.getEntityLiving();
            ItemStack heldStack = source.getHeldItem(source.getActiveHand());
            int rank = EnumInfusionEnchantment.getInfusionEnchantmentLevel(heldStack, VOIDFLAME);
            if (rank > 0) {
                target.addPotionEffect(new PotionEffect(PotionsTW.VOIDFLAME, 60 + 60 * rank, 1, true, false));
            }
        }

    }
}
