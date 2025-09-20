package com.verdantartifice.thaumicwonders.common.tiles.devices;

import com.verdantartifice.thaumicwonders.common.registry.SoundsTW;
import net.minecraft.util.SoundCategory;
import thaumcraft.api.aspects.Aspect;

public class TileMadnessEngine extends AbstractTileResearchEngine {
    private static final int COST = 10;
    private static final int CAPACITY = 50;

    @Override
    public int getCost() {
        return COST;
    }

    @Override
    public int getCapacity() {
        return CAPACITY;
    }

    @Override
    public Aspect getAspect() {
        return Aspect.ELDRITCH;
    }

    @Override
    public void playAmbientSound() {
        if(this.tickCounter % 260 == 0) {
            this.world.playSound(null, this.pos, SoundsTW.MADNESS_ENGINE, SoundCategory.BLOCKS, 0.7f, 1.0f);
        }
    }
}
