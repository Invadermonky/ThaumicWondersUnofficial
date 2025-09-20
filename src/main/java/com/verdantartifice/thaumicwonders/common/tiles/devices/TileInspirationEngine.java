package com.verdantartifice.thaumicwonders.common.tiles.devices;

import com.verdantartifice.thaumicwonders.common.registry.SoundsTW;
import net.minecraft.util.SoundCategory;
import thaumcraft.api.aspects.Aspect;

public class TileInspirationEngine extends AbstractTileResearchEngine {
    private static final int COST = 5;
    private static final int CAPACITY = 25;

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
        return Aspect.MIND;
    }

    @Override
    public void playAmbientSound() {
        if(this.tickCounter % 160 == 0) {
            this.world.playSound(null, this.pos, SoundsTW.INSPIRATION_ENGINE, SoundCategory.BLOCKS, 0.7f, 1.0f);
        }
    }
}
