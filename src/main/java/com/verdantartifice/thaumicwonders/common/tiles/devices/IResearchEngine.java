package com.verdantartifice.thaumicwonders.common.tiles.devices;

public interface IResearchEngine {
    /**
     * Determines whether the research engine has sufficient fuel for use
     *
     * @return whether the engine is fueled
     */
    boolean isFueled();

    /**
     * Attempts to deduct the essentia cost from the engine's store
     *
     * @return whether the deduction was successful
     */
    boolean deductCost();
}
