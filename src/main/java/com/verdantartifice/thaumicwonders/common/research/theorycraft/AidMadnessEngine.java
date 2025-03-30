package com.verdantartifice.thaumicwonders.common.research.theorycraft;

import com.verdantartifice.thaumicwonders.common.blocks.BlocksTW;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

public class AidMadnessEngine implements ITheorycraftAid {
    @Override
    public Object getAidObject() {
        return BlocksTW.MADNESS_ENGINE;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<TheorycraftCard>[] getCards() {
        return new Class[] { CardInducedMadness.class, CardInducedMadness.class, CardInducedMadness.class };
    }
}
