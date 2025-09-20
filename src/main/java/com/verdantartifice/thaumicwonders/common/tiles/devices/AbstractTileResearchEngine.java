package com.verdantartifice.thaumicwonders.common.tiles.devices;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.tiles.base.TileTW;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.aura.AuraHelper;

public abstract class AbstractTileResearchEngine extends TileTW implements IAspectContainer, IEssentiaTransport, ITickable, IResearchEngine {
    protected int amount = 0;
    protected int tickCounter = 0;

    public abstract int getCost();

    public abstract int getCapacity();

    public abstract Aspect getAspect();

    public abstract void playAmbientSound();

    @Override
    public boolean isFueled() {
        return this.amount >= this.getCost();
    }

    @Override
    public boolean deductCost() {
        return this.takeFromContainer(this.getAspect(), this.getCost());
    }

    @Override
    protected void readFromTileNBT(NBTTagCompound compound) {
        this.amount = compound.getShort("essentia");
    }

    @Override
    protected NBTTagCompound writeToTileNBT(NBTTagCompound compound) {
        compound.setShort("essentia", (short) this.amount);
        return compound;
    }

    @Override
    public void update() {
        if (!this.world.isRemote && (++this.tickCounter % 5 == 0)) {
            if (this.amount < this.getCapacity()) {
                this.fill();
            }
        }
        this.playAmbientSound();
    }

    private void fill() {
        for (EnumFacing face : EnumFacing.VALUES) {
            if (!this.canInputFrom(face)) {
                continue;
            }
            TileEntity te = ThaumcraftApiHelper.getConnectableTile(this.world, this.pos, face);
            if (te instanceof IEssentiaTransport) {
                IEssentiaTransport otherTile = (IEssentiaTransport) te;
                if (!otherTile.canOutputTo(face.getOpposite())) {
                    continue;
                }
                if (otherTile.getEssentiaType(face.getOpposite()) == this.getAspect() &&
                        otherTile.getEssentiaAmount(face.getOpposite()) > 0 &&
                        this.getSuctionAmount(face) > otherTile.getSuctionAmount(face.getOpposite()) &&
                        this.getSuctionAmount(face) >= otherTile.getMinimumSuction()) {
                    int taken = otherTile.takeEssentia(this.getAspect(), 1, face.getOpposite());
                    int leftover = this.addToContainer(this.getAspect(), taken);
                    if (leftover > 0) {
                        ThaumicWonders.LOGGER.info("{} spilling {} essentia on fill", this.blockType.getRegistryName().getPath(), leftover);
                        AuraHelper.polluteAura(this.world, this.pos, leftover, true);
                    }
                    this.syncTile(false);
                    this.markDirty();
                    if (this.amount >= this.getCapacity()) {
                        break;
                    }
                }
            }
        }
    }

    @Override
    public boolean isConnectable(EnumFacing face) {
        return face != EnumFacing.UP;
    }

    @Override
    public boolean canInputFrom(EnumFacing face) {
        return this.isConnectable(face);
    }

    @Override
    public boolean canOutputTo(EnumFacing face) {
        return false;
    }

    @Override
    public void setSuction(Aspect aspect, int amt) {
        // Do nothing
    }

    @Override
    public Aspect getSuctionType(EnumFacing face) {
        return this.getAspect();
    }

    @Override
    public int getSuctionAmount(EnumFacing face) {
        return (this.amount >= this.getCapacity()) ? 0 : 128;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amt, EnumFacing face) {
        // Can't output
        return 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amt, EnumFacing face) {
        if (this.canInputFrom(face)) {
            return (amt - this.addToContainer(aspect, amt));
        } else {
            return 0;
        }
    }

    @Override
    public Aspect getEssentiaType(EnumFacing face) {
        return this.getAspect();
    }

    @Override
    public int getEssentiaAmount(EnumFacing face) {
        return this.amount;
    }

    @Override
    public int getMinimumSuction() {
        // Can't output, so no need for minimum suction
        return 0;
    }

    @Override
    public AspectList getAspects() {
        AspectList list = new AspectList();
        if (this.amount > 0) {
            list.add(this.getAspect(), this.amount);
        }
        return list;
    }

    @Override
    public void setAspects(AspectList aspects) {
        if (aspects != null && aspects.size() > 0) {
            this.amount = aspects.getAmount(this.getAspect());
        }
    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        return (aspect == this.getAspect());
    }

    @Override
    public int addToContainer(Aspect aspect, int toAdd) {
        if (toAdd == 0) {
            return 0;
        } else if (this.amount >= this.getCapacity() || aspect != this.getAspect()) {
            // Incompatible addition; return all of it
            this.syncTile(false);
            this.markDirty();
            return toAdd;
        } else {
            // Add as much as possible and return the remainder
            int added = Math.min(toAdd, this.getCapacity() - this.amount);
            this.amount += added;
            this.syncTile(false);
            this.markDirty();
            return (toAdd - added);
        }
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int amt) {
        if (aspect == this.getAspect() && this.amount >= amt) {
            this.amount -= amt;
            this.syncTile(false);
            this.markDirty();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean takeFromContainer(AspectList aspectList) {
        if (!this.doesContainerContain(aspectList)) {
            return false;
        } else {
            boolean satisfied = true;
            for (Aspect aspect : aspectList.getAspects()) {
                satisfied = satisfied && this.takeFromContainer(aspect, aspectList.getAmount(aspect));
            }
            return satisfied;
        }
    }

    @Override
    public boolean doesContainerContainAmount(Aspect aspect, int amt) {
        return (aspect == this.getAspect() && this.amount >= amt);
    }

    @Override
    public boolean doesContainerContain(AspectList aspectList) {
        boolean satisfied = true;
        for (Aspect aspect : aspectList.getAspects()) {
            satisfied = satisfied && this.doesContainerContainAmount(aspect, aspectList.getAmount(aspect));
        }
        return satisfied;
    }

    @Override
    public int containerContains(Aspect aspect) {
        return (aspect == this.getAspect()) ? this.amount : 0;
    }
}
