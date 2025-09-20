package com.verdantartifice.thaumicwonders.common.registry;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class SoundsTW {
    public static final SoundEvent AVATAR_DEATH;
    public static final SoundEvent AVATAR_AMBIENT;
    public static final SoundEvent INSPIRATION_ENGINE;
    public static final SoundEvent MADNESS_ENGINE;
    public static final SoundEvent PRIMAL_DESTROYER_ANGERED;
    public static final SoundEvent VOID_BEACON_ACTIVATE;
    public static final SoundEvent VOID_BEACON_AMBIENT;
    public static final SoundEvent VOID_BEACON_DEACTIVATE;

    private static SoundEvent makeSoundEvent(String unloc) {
        ResourceLocation loc = new ResourceLocation(ThaumicWonders.MODID, unloc);
        return new SoundEvent(loc).setRegistryName(loc);
    }

    static {
        AVATAR_AMBIENT = makeSoundEvent("avatar_ambient");
        AVATAR_DEATH = makeSoundEvent("avatar_death");
        INSPIRATION_ENGINE = makeSoundEvent("inspiration_engine");
        MADNESS_ENGINE = makeSoundEvent("madness_engine");
        PRIMAL_DESTROYER_ANGERED = makeSoundEvent("primal_destroyer_angered");
        VOID_BEACON_ACTIVATE = makeSoundEvent("vb_activate");
        VOID_BEACON_AMBIENT = makeSoundEvent("vb_ambient");
        VOID_BEACON_DEACTIVATE = makeSoundEvent("vb_deactivate");
    }
}
