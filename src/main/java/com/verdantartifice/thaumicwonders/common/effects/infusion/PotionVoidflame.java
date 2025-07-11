package com.verdantartifice.thaumicwonders.common.effects.infusion;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.effects.PotionsTW;
import com.verdantartifice.thaumicwonders.common.registry.AttributesTW;
import com.verdantartifice.thaumicwonders.common.registry.DamageSourceTW;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.PotionEvent;

public class PotionVoidflame extends Potion {
    //TODO: Potion Icon

    public PotionVoidflame() {
        super(true, 0x0);
        this.setRegistryName(new ResourceLocation(ThaumicWonders.MODID, "voidflame"));
        this.setPotionName(this.getRegistryName().toString());
        this.registerPotionAttributeModifier(AttributesTW.HEALING_REDUCTION, AttributesTW.HEALING_REDUCTION_ID.toString(), 0.5, 0);
    }

    public static void onPotionRemoval(PotionEvent.PotionRemoveEvent event) {
        if (event.getPotion() == PotionsTW.VOIDFLAME) {
            event.setCanceled(true);
        }
    }

    @Override
    public void performEffect(EntityLivingBase entityLiving, int amplifier) {
        entityLiving.attackEntityFrom(DamageSourceTW.VOIDFLAME, 1.0f);
        entityLiving.hurtResistantTime = 0;
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
