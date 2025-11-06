package com.verdantartifice.thaumicwonders.common.tiles.essentia;

import net.minecraft.util.EnumFacing;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.tiles.essentia.TileJarFillable;

public class TileOblivionEssentiaJar extends TileJarFillable {
    @Override
    public int addToContainer(Aspect aspect, int amount) {
        return this.aspectFilter == null || this.aspectFilter == aspect ? amount : 0;
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int amount) {
        return false;
    }

    @Override
    public boolean takeFromContainer(AspectList aspectList) {
        return false;
    }

    @Override
    public int getSuctionAmount(EnumFacing loc) {
        return this.aspectFilter != null ? 48 : 32;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, EnumFacing face) {
        return 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing face) {
        return this.canInputFrom(face) ? this.addToContainer(aspect, amount) : 0;
    }

    @Override
    public Aspect getEssentiaType(EnumFacing loc) {
        return this.aspectFilter;
    }

    @Override
    public int getEssentiaAmount(EnumFacing loc) {
        return 0;
    }

    @Override
    public int getMinimumSuction() {
        return this.aspectFilter != null ? 48 : 32;
    }
}
