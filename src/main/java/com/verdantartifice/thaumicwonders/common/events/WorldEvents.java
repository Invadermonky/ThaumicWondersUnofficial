package com.verdantartifice.thaumicwonders.common.events;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.misc.FluxExplosion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.common.lib.potions.PotionInfectiousVisExhaust;

@Mod.EventBusSubscriber(modid = ThaumicWonders.MODID)
public class WorldEvents {
    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
        if (event.getExplosion() instanceof FluxExplosion) {
            PotionEffect effect = new PotionEffect(PotionInfectiousVisExhaust.instance, 1200, 2);
            for (Entity entity : event.getAffectedEntities()) {
                if (entity instanceof EntityLivingBase) {
                    EntityLivingBase elb = (EntityLivingBase) entity;
                    try {
                        elb.addPotionEffect(effect);
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }
}
