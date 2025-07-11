package com.verdantartifice.thaumicwonders.common.init;

import com.verdantartifice.thaumicwonders.common.effects.infusion.PotionVoidflame;
import net.minecraft.potion.Potion;
import net.minecraftforge.registries.IForgeRegistry;

public class InitPotions {
    public static void initPotions(IForgeRegistry<Potion> registry) {
        registerPotion(registry, new PotionVoidflame());
    }

    private static void registerPotion(IForgeRegistry<Potion> registry, Potion potion) {
        registry.register(potion);
    }
}
