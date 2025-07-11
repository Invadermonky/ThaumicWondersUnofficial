package com.verdantartifice.thaumicwonders.common.registry;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.UUID;

public class AttributesTW {
    public static final UUID ATTACK_DAMAGE_VOID_ID = UUID.fromString("122736f9-b4de-401f-bbd1-36a5654d9442");
    public static final UUID HEALING_REDUCTION_ID = UUID.fromString("539a76ec-b0b5-414a-af8a-43179f54b658");

    public static final IAttribute ATTACK_DAMAGE_VOID = new RangedAttribute(null, ThaumicWonders.MODID + ":void_damage", 0.0, 0.0, Double.MAX_VALUE).setShouldWatch(true);
    public static final IAttribute HEALING_REDUCTION = new RangedAttribute(null, ThaumicWonders.MODID + ":healing_reduction", 0.0, 0.0, 1.0).setShouldWatch(true);

    public static void handleAttackDamageVoid(LivingHurtEvent event) {
        if (event.getSource().getTrueSource() instanceof EntityLivingBase && event.getEntityLiving().isEntityAlive()) {
            EntityLivingBase source = (EntityLivingBase) event.getSource().getTrueSource();
            EntityLivingBase target = event.getEntityLiving();
            IAttributeInstance voidDamage = source.getEntityAttribute(ATTACK_DAMAGE_VOID);
            if (voidDamage != null) {
                double amount = voidDamage.getAttributeValue();
                if (amount > 0) {
                    target.hurtResistantTime = 0;
                    target.attackEntityFrom(DamageSourceTW.VOIDFLAME, (float) amount);
                }
            }
        }
    }

    public static void handleHealingReduction(LivingHealEvent event) {
        EntityLivingBase entityLiving = event.getEntityLiving();
        IAttributeInstance healingReduction = entityLiving.getEntityAttribute(HEALING_REDUCTION);
        if (healingReduction != null && event.getAmount() > 0) {
            double reductionFactor = MathHelper.clamp(healingReduction.getAttributeValue(), 0.0, 1.0);
            event.setAmount((float) (event.getAmount() * (1 - reductionFactor)));
        }
    }
}
