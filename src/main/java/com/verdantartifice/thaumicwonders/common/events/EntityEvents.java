package com.verdantartifice.thaumicwonders.common.events;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.items.armor.ItemVoidFortressArmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = ThaumicWonders.MODID)
public class EntityEvents {
    @SubscribeEvent
    public static void entityHurt(LivingHurtEvent event) {
        if (event.getSource().getTrueSource() != null && event.getSource().getTrueSource() instanceof EntityPlayer) {
            EntityPlayer leecher = (EntityPlayer)event.getSource().getTrueSource();
            ItemStack helm = leecher.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
            if (helm != null && !helm.isEmpty() && helm.getItem() instanceof ItemVoidFortressArmor) {
                if (leecher.world.rand.nextFloat() < (event.getAmount() / 12.0F)) {
                    leecher.heal(1.0F);
                }
            }
        }
    }
}
