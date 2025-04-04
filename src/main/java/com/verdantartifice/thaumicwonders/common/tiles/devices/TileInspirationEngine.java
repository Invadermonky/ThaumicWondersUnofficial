package com.verdantartifice.thaumicwonders.common.tiles.devices;

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
}
