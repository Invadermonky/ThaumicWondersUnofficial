package com.verdantartifice.thaumicwonders.common.init;

import com.verdantartifice.thaumicwonders.common.events.EntityEvents;
import com.verdantartifice.thaumicwonders.common.registry.AttributesTW;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraftforge.event.entity.EntityEvent;

public class InitAttributes {
    /** Redirected from {@link EntityEvents#onEntityConstructed(EntityEvent.EntityConstructing)} */
    public static void onEntityconstructEvent(EntityEvent.EntityConstructing event) {
        if (event.getEntity() instanceof EntityLivingBase) {
            EntityLivingBase entity = (EntityLivingBase) event.getEntity();
            registerAttribute(entity.getAttributeMap(), AttributesTW.ATTACK_DAMAGE_VOID);
            registerAttribute(entity.getAttributeMap(), AttributesTW.HEALING_REDUCTION);
        }
    }

    private static void registerAttribute(AbstractAttributeMap attributeMap, IAttribute attribute) {
        if (attributeMap.getAttributeInstance(attribute) == null) {
            attributeMap.registerAttribute(attribute);
        }
    }
}
