package com.verdantartifice.thaumicwonders.common.tiles.devices;

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
}
