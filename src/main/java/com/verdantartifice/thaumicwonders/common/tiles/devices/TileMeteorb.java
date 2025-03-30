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
import thaumcraft.common.blocks.IBlockFacingHorizontal;

public class TileMeteorb extends TileTW implements IAspectContainer, IEssentiaTransport, ITickable {
    private static final int CAPACITY = 50;
    public static final int MIN_FUEL = 50;

    protected int airEssentia = 0;
    protected int waterEssentia = 0;
    protected int energyEssentia = 0;
    protected int tickCounter = 0;

    @Override
    protected void readFromTileNBT(NBTTagCompound compound) {
        this.airEssentia = compound.getShort("airEssentia");
        this.waterEssentia = compound.getShort("waterEssentia");
        this.energyEssentia = compound.getShort("energyEssentia");
    }

    @Override
    protected NBTTagCompound writeToTileNBT(NBTTagCompound compound) {
        compound.setShort("airEssentia", (short)this.airEssentia);
        compound.setShort("waterEssentia", (short)this.waterEssentia);
        compound.setShort("energyEssentia", (short)this.energyEssentia);
        return compound;
    }

    @Override
    public void update() {
        if (!this.world.isRemote && (++this.tickCounter % 5 == 0)) {
            this.fill();
        }
    }

    protected void fill() {
        for (EnumFacing face : EnumFacing.HORIZONTALS) {
            if (!this.canInputFrom(face) || this.getEssentiaAmount(face) >= CAPACITY) {
                continue;
            }
            TileEntity te = ThaumcraftApiHelper.getConnectableTile(this.world, this.pos, face);
            if (te instanceof IEssentiaTransport) {
                IEssentiaTransport otherTile = (IEssentiaTransport)te;
                if (!otherTile.canOutputTo(face.getOpposite())) {
                    continue;
                }
                if ( otherTile.getEssentiaType(face.getOpposite()) == this.getEssentiaType(face) &&
                     otherTile.getEssentiaAmount(face.getOpposite()) > 0 &&
                     this.getSuctionAmount(face) > otherTile.getSuctionAmount(face.getOpposite()) &&
                     this.getSuctionAmount(face) >= otherTile.getMinimumSuction() ) {
                    int taken = otherTile.takeEssentia(this.getEssentiaType(face), 1, face.getOpposite());
                    int leftover = this.addToContainer(this.getEssentiaType(face), taken);
                    if (leftover > 0) {
                        ThaumicWonders.LOGGER.info("Meteorb spilling {} essentia on fill", leftover);
                        AuraHelper.polluteAura(this.world, this.pos, leftover, true);
                    }
                    this.syncTile(false);
                    this.markDirty();
                }
            }
        }
    }
    
    /**
     * Get the relative facing for the given absolute facing.  The Meteorb's relative north is its control
     * panel, west is air input, east is water input, south is energy input.
     * @param absFace facing relative to world axes
     * @return facing relative to orb faces
     */
    public EnumFacing getRelativeFacing(EnumFacing absFace) {
        if (this.getBlockType() instanceof IBlockFacingHorizontal && absFace != EnumFacing.UP && absFace != EnumFacing.DOWN) {
            EnumFacing blockFacing = this.world.getBlockState(this.pos).getValue(IBlockFacingHorizontal.FACING);
            int rotations = 0;
            EnumFacing relativeFacing = absFace;
            while (blockFacing != EnumFacing.NORTH) {
                blockFacing = blockFacing.rotateY();
                rotations++;
            }
            for (int index = 0; index < rotations; index++) {
                relativeFacing = relativeFacing.rotateY();
            }
            return relativeFacing;
        } else {
            return absFace;
        }
    }
    
    @Override
    public int addEssentia(Aspect aspect, int amt, EnumFacing face) {
        if (this.canInputFrom(face) && aspect == this.getEssentiaType(face)) {
            return (amt - this.addToContainer(aspect, amt));
        } else {
            return 0;
        }
    }

    @Override
    public boolean canInputFrom(EnumFacing face) {
        EnumFacing relFace = this.getRelativeFacing(face);
        return (relFace == EnumFacing.WEST || relFace == EnumFacing.EAST || relFace == EnumFacing.SOUTH);
    }

    @Override
    public boolean canOutputTo(EnumFacing face) {
        return false;
    }

    @Override
    public int getEssentiaAmount(EnumFacing face) {
        EnumFacing relFace = this.getRelativeFacing(face);
        if (relFace == EnumFacing.WEST) {
            return this.airEssentia;
        } else if (relFace == EnumFacing.EAST) {
            return this.waterEssentia;
        } else if (relFace == EnumFacing.SOUTH) {
            return this.energyEssentia;
        } else {
            return 0;
        }
    }

    @Override
    public Aspect getEssentiaType(EnumFacing face) {
        EnumFacing relFace = this.getRelativeFacing(face);
        if (relFace == EnumFacing.WEST) {
            return Aspect.AIR;
        } else if (relFace == EnumFacing.EAST) {
            return Aspect.WATER;
        } else if (relFace == EnumFacing.SOUTH) {
            return Aspect.ENERGY;
        } else {
            return null;
        }
    }

    @Override
    public int getMinimumSuction() {
        // Can't output, so no need for minimum suction
        return 0;
    }

    @Override
    public int getSuctionAmount(EnumFacing face) {
        return (this.getEssentiaAmount(face) >= CAPACITY) ? 0 : 128;
    }

    @Override
    public Aspect getSuctionType(EnumFacing face) {
        return this.getEssentiaType(face);
    }

    @Override
    public boolean isConnectable(EnumFacing face) {
        return this.canInputFrom(face);
    }

    @Override
    public void setSuction(Aspect aspect, int amt) {
        // Do nothing
    }

    @Override
    public int takeEssentia(Aspect aspect, int amt, EnumFacing face) {
        // Can't output
        return 0;
    }

    @Override
    public int addToContainer(Aspect aspect, int toAdd) {
        int retVal = 0;
        if (toAdd == 0) {
            return 0;
        } else if (this.airEssentia < CAPACITY && aspect == Aspect.AIR) {
            // Add as much air as possible and return the remainder
            int added = Math.min(toAdd, CAPACITY - this.airEssentia);
            this.airEssentia += added;
            retVal = (toAdd - added);
        } else if (this.waterEssentia < CAPACITY && aspect == Aspect.WATER) {
            // Add as much water as possible and return the remainder
            int added = Math.min(toAdd, CAPACITY - this.waterEssentia);
            this.waterEssentia += added;
            retVal = (toAdd - added);
        } else if (this.energyEssentia < CAPACITY && aspect == Aspect.ENERGY) {
            // Add as much energy as possible and return the remainder
            int added = Math.min(toAdd, CAPACITY - this.energyEssentia);
            this.energyEssentia += added;
            retVal = (toAdd - added);
        } else {
            retVal = toAdd;
        }
        
        this.syncTile(false);
        this.markDirty();
        return retVal;
    }

    @Override
    public int containerContains(Aspect aspect) {
        if (aspect == Aspect.AIR) {
            return this.airEssentia;
        } else if (aspect == Aspect.WATER) {
            return this.waterEssentia;
        } else if (aspect == Aspect.ENERGY) {
            return this.energyEssentia;
        } else {
            return 0;
        }
    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        return (aspect == Aspect.AIR || aspect == Aspect.WATER || aspect == Aspect.ENERGY);
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
    public boolean doesContainerContainAmount(Aspect aspect, int amt) {
        return (aspect == Aspect.AIR && this.airEssentia >= amt) ||
               (aspect == Aspect.WATER && this.waterEssentia >= amt) ||
               (aspect == Aspect.ENERGY && this.energyEssentia >= amt);
    }

    @Override
    public AspectList getAspects() {
        AspectList list = new AspectList();
        if (this.airEssentia > 0) {
            list.add(Aspect.AIR, this.airEssentia);
        }
        if (this.waterEssentia > 0) {
            list.add(Aspect.WATER, this.waterEssentia);
        }
        if (this.energyEssentia > 0) {
            list.add(Aspect.ENERGY, this.energyEssentia);
        }
        return list;
    }

    @Override
    public void setAspects(AspectList aspects) {
        if (aspects != null && aspects.size() > 0) {
            this.airEssentia = aspects.getAmount(Aspect.AIR);
            this.waterEssentia = aspects.getAmount(Aspect.WATER);
            this.energyEssentia = aspects.getAmount(Aspect.ENERGY);
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
    public boolean takeFromContainer(Aspect aspect, int amt) {
        if (aspect == Aspect.AIR && this.airEssentia >= amt) {
            this.airEssentia -= amt;
        } else if (aspect == Aspect.WATER && this.waterEssentia >= amt) {
            this.waterEssentia -= amt;
        } else if (aspect == Aspect.ENERGY && this.energyEssentia >= amt) {
            this.energyEssentia -= amt;
        } else {
            return false;
        }
        this.syncTile(false);
        this.markDirty();
        return true;
    }

}
