package com.verdantartifice.thaumicwonders.common.research.theorycraft;

import com.verdantartifice.thaumicwonders.common.blocks.BlocksTW;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

public class AidInspirationEngine implements ITheorycraftAid {
    @Override
    public Object getAidObject() {
        return BlocksTW.INSPIRATION_ENGINE;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<TheorycraftCard>[] getCards() {
        return new Class[] { CardInducedInspiration.class, CardInducedInspiration.class, CardInducedInspiration.class };
    }
}
