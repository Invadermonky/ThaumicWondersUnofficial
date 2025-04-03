package com.verdantartifice.thaumicwonders.common.tiles.essentia;

import net.minecraft.util.EnumFacing;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.tiles.essentia.TileJar;
import thaumcraft.common.tiles.essentia.TileJarFillable;

public class TileCreativeEssentiaJar extends TileJarFillable {
    @Override
    public int getEssentiaAmount(EnumFacing loc) {
        return this.getEssentiaType(loc) == null ? 0 : TileJarFillable.CAPACITY;
    }

    @Override
    public boolean takeFromContainer(Aspect tt, int am) {
        // Aspect must match, but don't deduct any essentia
        return (tt == this.aspect);
    }
}
