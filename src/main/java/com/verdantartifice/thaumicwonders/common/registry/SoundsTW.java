package com.verdantartifice.thaumicwonders.common.registry;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class SoundsTW {
    public static final SoundEvent AVATAR_DEATH;
    public static final SoundEvent AVATAR_AMBIENT;
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
        VOID_BEACON_ACTIVATE = makeSoundEvent("vb_activate");
        VOID_BEACON_AMBIENT = makeSoundEvent("vb_ambient");
        VOID_BEACON_DEACTIVATE = makeSoundEvent("vb_deactivate");
    }
}
