package com.verdantartifice.thaumicwonders.common.init;

import com.verdantartifice.thaumicwonders.common.registry.SoundsTW;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class InitSounds {
    public static void initSounds(IForgeRegistry<SoundEvent> registry) {
        registry.register(SoundsTW.AVATAR_AMBIENT);
        registry.register(SoundsTW.AVATAR_DEATH);
        registry.register(SoundsTW.ENCHANT_END);
        registry.register(SoundsTW.ENCHANT_LOOP);
        registry.register(SoundsTW.ENCHANT_START);
        registry.register(SoundsTW.ENCHANT_ZAP);
        registry.register(SoundsTW.ESSENTIA_LOOP);
        registry.register(SoundsTW.INSPIRATION_ENGINE);
        registry.register(SoundsTW.MADNESS_ENGINE);
        registry.register(SoundsTW.PRIMAL_DESTROYER_ANGERED);
        registry.register(SoundsTW.VOID_BEACON_ACTIVATE);
        registry.register(SoundsTW.VOID_BEACON_AMBIENT);
        registry.register(SoundsTW.VOID_BEACON_DEACTIVATE);
    }
}
