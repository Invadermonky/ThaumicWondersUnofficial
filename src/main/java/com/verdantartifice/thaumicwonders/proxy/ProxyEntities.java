package com.verdantartifice.thaumicwonders.proxy;

import com.verdantartifice.thaumicwonders.client.renderers.entity.*;
import com.verdantartifice.thaumicwonders.client.renderers.entity.monsters.RenderCorruptionAvatar;
import com.verdantartifice.thaumicwonders.common.compat.ModIds;
import com.verdantartifice.thaumicwonders.common.entities.*;
import com.verdantartifice.thaumicwonders.common.entities.monsters.EntityCorruptionAvatar;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ProxyEntities {
    public void setupEntityRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityFlyingCarpet.class, RenderFlyingCarpet::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityVoidPortal.class, RenderVoidPortal::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityHexamitePrimed.class, RenderHexamitePrimed::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityCorruptionAvatar.class, RenderCorruptionAvatar::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityFluxFireball.class, RenderFluxFireball::new);
        if (!ModIds.new_crimson_revelations.isLoaded) {
            RenderingRegistry.registerEntityRenderingHandler(EntityPrimalArrow.class, RenderPrimalArrow::new);
        }
    }
}
