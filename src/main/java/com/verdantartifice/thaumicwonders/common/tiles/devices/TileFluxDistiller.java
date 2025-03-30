package com.verdantartifice.thaumicwonders.common.tiles.devices;

import com.verdantartifice.thaumicwonders.common.blocks.BlocksTW;
import com.verdantartifice.thaumicwonders.common.blocks.devices.BlockFluxCapacitor;
import com.verdantartifice.thaumicwonders.common.blocks.devices.BlockFluxDistiller;
import com.verdantartifice.thaumicwonders.common.tiles.base.TileTW;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;

public class TileFluxDistiller extends TileTW implements IAspectContainer, IEssentiaTransport, ITickable {
    protected static final int PROCESS_TIME = 20;
    protected static final int MAX_CHARGE = 10;
    protected static final int MAX_ESSENTIA = 1;
    
    protected int amount = 0;
    protected int tickCounter = 0;
    
    public int getAmount() {
        return this.amount;
    }
    
    @Override
    protected void readFromTileNBT(NBTTagCompound compound) {
        this.amount = compound.getShort("essentia");
    }
    
    @Override
    protected NBTTagCompound writeToTileNBT(NBTTagCompound compound) {
        compound.setShort("essentia", (short)this.amount);
        return compound;
    }

    @Override
    public void update() {
        if (!this.world.isRemote && (++this.tickCounter % PROCESS_TIME == 0)) {
            IBlockState capacitorState = this.world.getBlockState(this.pos.down());
            if (capacitorState != null && capacitorState.getBlock() == BlocksTW.FLUX_CAPACITOR) {
                BlockFluxCapacitor capacitorBlock = (BlockFluxCapacitor)capacitorState.getBlock();
                int capacitorCharge = capacitorBlock.getCharge(this.world, this.pos.down());
                int distillerCharge = this.getCharge();
                if (capacitorCharge > 0 && distillerCharge < MAX_CHARGE) {
                    capacitorBlock.decrementCharge(this.world, this.pos.down(), 1);
                    this.setCharge(distillerCharge + 1);
                } else if (distillerCharge >= MAX_CHARGE && this.amount < MAX_ESSENTIA) {
                    this.amount++;
                    this.setCharge(0);
                    this.syncTile(false);
                    this.markDirty();
                }
            }
        }
    }
    
    protected int getCharge() {
        IBlockState state = this.world.getBlockState(this.pos);
        if (state.getBlock() == BlocksTW.FLUX_DISTILLER) {
            return state.getBlock().getMetaFromState(state);
        } else {
            return 0;
        }
    }
    
    protected void setCharge(int charge) {
        IBlockState state = this.world.getBlockState(this.pos);
        if (state.getBlock() == BlocksTW.FLUX_DISTILLER) {
            this.world.setBlockState(this.pos, state.withProperty(BlockFluxDistiller.CHARGE, Integer.valueOf(charge)));
        }
    }

    @Override
    public int addEssentia(Aspect aspect, int amt, EnumFacing face) {
        // Can't input, so always return zero
        return 0;
    }

    @Override
    public boolean canInputFrom(EnumFacing face) {
        return false;
    }

    @Override
    public boolean canOutputTo(EnumFacing face) {
        return face == EnumFacing.UP;
    }

    @Override
    public int getEssentiaAmount(EnumFacing face) {
        return this.amount;
    }

    @Override
    public Aspect getEssentiaType(EnumFacing face) {
        return Aspect.FLUX;
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Override
    public int getSuctionAmount(EnumFacing face) {
        return 0;
    }

    @Override
    public Aspect getSuctionType(EnumFacing face) {
        return null;
    }

    @Override
    public boolean isConnectable(EnumFacing face) {
        return face == EnumFacing.UP;
    }

    @Override
    public void setSuction(Aspect aspect, int amt) {
        // Do nothing
    }

    @Override
    public int takeEssentia(Aspect aspect, int amt, EnumFacing face) {
        return (this.canOutputTo(face) && this.takeFromContainer(aspect, amt)) ? amt : 0;
    }

    @Override
    public int addToContainer(Aspect aspect, int toAdd) {
        if (toAdd == 0) {
            return 0;
        } else if (this.amount >= MAX_ESSENTIA || aspect != Aspect.FLUX) {
            // Incompatible addition; return all of it
            this.syncTile(false);
            this.markDirty();
            return toAdd;
        } else {
            // Add as much as possible and return the remainder
            int added = Math.min(toAdd, MAX_ESSENTIA - this.amount);
            this.amount += added;
            this.syncTile(false);
            this.markDirty();
            return (toAdd - added);
        }
    }

    @Override
    public int containerContains(Aspect aspect) {
        return (aspect == Aspect.FLUX) ? this.amount : 0;
    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        return (aspect == Aspect.FLUX);
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
        return (aspect == Aspect.FLUX && this.amount >= amt);
    }

    @Override
    public AspectList getAspects() {
        AspectList list = new AspectList();
        if (this.amount > 0) {
            list.add(Aspect.FLUX, this.amount);
        }
        return list;
    }

    @Override
    public void setAspects(AspectList aspectList) {
        if (aspectList != null && aspectList.size() > 0) {
            this.amount = aspectList.getAmount(Aspect.FLUX);
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
        if (aspect == Aspect.FLUX && this.amount >= amt) {
            this.amount -= amt;
            this.syncTile(false);
            this.markDirty();
            return true;
        } else {
            return false;
        }
    }

}
