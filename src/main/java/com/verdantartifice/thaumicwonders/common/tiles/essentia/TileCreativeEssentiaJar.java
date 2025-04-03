package com.verdantartifice.thaumicwonders.common.tiles.essentia;

import net.minecraft.util.EnumFacing;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.tiles.essentia.TileJarFillable;

public class TileCreativeEssentiaJar extends TileJarFillable {
    @Override
    public int getEssentiaAmount(EnumFacing loc) {
        return this.getEssentiaType(loc) == null ? 0 : TileJarFillable.CAPACITY;
    }

    //Overwriting to ensure no null pointers from null facing value
    @Override
    public Aspect getEssentiaType(EnumFacing loc) {
        return this.aspect;
    }

    @Override
    public int addToContainer(Aspect aspect, int amount) {
        if (amount > 0) {
            if (this.amount < CAPACITY && aspect == this.aspect || this.amount == 0) {
                this.aspect = aspect;
                int added = Math.min(amount, 250 - this.amount);
                this.amount = CAPACITY;
                amount -= added;
            }

            this.syncTile(false);
            this.markDirty();
        }
        return amount;
    }

    @Override
    public boolean takeFromContainer(Aspect tt, int am) {
        // Aspect must match, but don't deduct any essentia
        return (tt == this.aspect);
    }
}
