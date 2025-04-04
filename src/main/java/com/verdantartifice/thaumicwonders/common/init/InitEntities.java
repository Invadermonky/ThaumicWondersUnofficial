package com.verdantartifice.thaumicwonders.common.init;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.entities.*;
import com.verdantartifice.thaumicwonders.common.entities.monsters.EntityCorruptionAvatar;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.registries.IForgeRegistry;

public class InitEntities {
    public static void initEntities(IForgeRegistry<EntityEntry> iForgeRegistry) {
        int id = 0;

        EntityEntry flyingCarpetEntry = EntityEntryBuilder.create()
                .entity(EntityFlyingCarpet.class)
                .id(new ResourceLocation(ThaumicWonders.MODID, "flying_carpet"), id++)
                .name("flying_carpet")
                .tracker(64, 1, true)
                .build();
        iForgeRegistry.register(flyingCarpetEntry);

        EntityEntry voidPortalEntry = EntityEntryBuilder.create()
                .entity(EntityVoidPortal.class)
                .id(new ResourceLocation(ThaumicWonders.MODID, "void_portal"), id++)
                .name("void_portal")
                .tracker(64, 20, false)
                .build();
        iForgeRegistry.register(voidPortalEntry);

        EntityEntry hexamitePrimedEntry = EntityEntryBuilder.create()
                .entity(EntityHexamitePrimed.class)
                .id(new ResourceLocation(ThaumicWonders.MODID, "hexamite_primed"), id++)
                .name("hexamite_primed")
                .tracker(64, 1, true)
                .build();
        iForgeRegistry.register(hexamitePrimedEntry);

        EntityEntry primalArrowEntry = EntityEntryBuilder.create()
                .entity(EntityPrimalArrow.class)
                .id(new ResourceLocation(ThaumicWonders.MODID, "primal_arrow"), id++)
                .name("primal_arrow")
                .tracker(64, 1, true)
                .build();
        iForgeRegistry.register(primalArrowEntry);

        EntityEntry corruptionAvatarEntry = EntityEntryBuilder.create()
                .entity(EntityCorruptionAvatar.class)
                .id(new ResourceLocation(ThaumicWonders.MODID, "corruption_avatar"), id++)
                .name("corruption_avatar")
                .egg(0x800080, 0x6A0005)
                .tracker(64, 1, true)
                .build();
        iForgeRegistry.register(corruptionAvatarEntry);
        LootTableList.register(LootTablesTW.CORRUPTION_AVATAR);

        EntityEntry fluxFireballEntry = EntityEntryBuilder.create()
                .entity(EntityFluxFireball.class)
                .id(new ResourceLocation(ThaumicWonders.MODID, "flux_fireball"), id++)
                .name("flux_fireball")
                .tracker(64, 1, true)
                .build();
        iForgeRegistry.register(fluxFireballEntry);
    }
}
