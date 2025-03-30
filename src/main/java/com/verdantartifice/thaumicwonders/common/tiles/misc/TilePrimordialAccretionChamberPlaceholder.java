package com.verdantartifice.thaumicwonders.common.tiles.misc;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.tiles.base.TileTW;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TilePrimordialAccretionChamber;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.aura.AuraHelper;

import javax.annotation.Nullable;

public class TilePrimordialAccretionChamberPlaceholder extends TileTW implements IAspectContainer, IEssentiaTransport, ITickable {
    protected int tickCounter = 0;
    
    @Override
    public void update() {
        if (!this.world.isRemote && (++this.tickCounter % 5 == 0)) {
            TilePrimordialAccretionChamber tile = this.findCentralChamber();
            if (tile != null && !tile.isEssentiaFull()) {
                this.fill();
            }
        }
    }
    
    protected void fill() {
        TilePrimordialAccretionChamber centralTile = this.findCentralChamber();
        if (centralTile == null) {
            return;
        }
        for (EnumFacing face : EnumFacing.VALUES) {
            if (!this.canInputFrom(face)) {
                continue;
            }
            TileEntity te = ThaumcraftApiHelper.getConnectableTile(this.world, this.pos, face);
            if (te instanceof IEssentiaTransport) {
                IEssentiaTransport otherTile = (IEssentiaTransport)te;
                if (!otherTile.canOutputTo(face.getOpposite())) {
                    continue;
                }
                Aspect currentSuction = centralTile.getCurrentSuction();
                if ( otherTile.getEssentiaType(face.getOpposite()) == currentSuction &&
                     otherTile.getEssentiaAmount(face.getOpposite()) > 0 &&
                     this.getSuctionAmount(face) > otherTile.getSuctionAmount(face.getOpposite()) &&
                     this.getSuctionAmount(face) >= otherTile.getMinimumSuction() ) {
                    int taken = otherTile.takeEssentia(currentSuction, 1, face.getOpposite());
                    int leftover = this.addToContainer(currentSuction, taken);
                    if (leftover > 0) {
                        ThaumicWonders.LOGGER.info("Primordial accretion chamber spilling {} essentia on fill", leftover);
                        AuraHelper.polluteAura(this.world, this.pos, leftover, true);
                    }
                    centralTile.syncTile(false);
                    centralTile.markDirty();
                    if (centralTile.isEssentiaFull()) {
                        break;
                    }
                }
            }
        }
    }
    
    @Nullable
    protected TilePrimordialAccretionChamber findCentralChamber() {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    BlockPos searchPos = this.pos.add(i, j, k);
                    TileEntity tile = this.world.getTileEntity(searchPos);
                    if (tile instanceof TilePrimordialAccretionChamber) {
                        return (TilePrimordialAccretionChamber)tile;
                    }
                }
            }
        }
        return null;
    }
    
    protected boolean isEssentiaPort(EnumFacing face) {
        TilePrimordialAccretionChamber tile = this.findCentralChamber();
        if (tile == null) {
            return false;
        } else {
            EnumFacing facing = tile.getFacing();
            for (int offset = -1; offset <= 1; offset++) {
                EnumFacing leftFace = facing.rotateYCCW();
                BlockPos leftPos = tile.getPos().offset(facing, offset).offset(leftFace);
                if (leftPos.equals(this.pos)) {
                    return (face == leftFace);
                }
                
                EnumFacing rightFace = facing.rotateY();
                BlockPos rightPos = tile.getPos().offset(facing, offset).offset(rightFace);
                if (rightPos.equals(this.pos)) {
                    return (face == rightFace);
                }
            }
        }
        return false;
    }

    @Override
    public int addToContainer(Aspect aspect, int toAdd) {
        TilePrimordialAccretionChamber tile = this.findCentralChamber();
        return tile == null ? 0 : tile.addToContainer(aspect, toAdd);
    }

    @Override
    public int containerContains(Aspect aspect) {
        TilePrimordialAccretionChamber tile = this.findCentralChamber();
        return tile == null ? 0 : tile.containerContains(aspect);
    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        TilePrimordialAccretionChamber tile = this.findCentralChamber();
        return tile != null && tile.doesContainerAccept(aspect);
    }

    @Override
    public boolean doesContainerContain(AspectList aspectList) {
        TilePrimordialAccretionChamber tile = this.findCentralChamber();
        return tile != null && tile.doesContainerContain(aspectList);
    }

    @Override
    public boolean doesContainerContainAmount(Aspect aspect, int amt) {
        TilePrimordialAccretionChamber tile = this.findCentralChamber();
        return tile != null && tile.doesContainerContainAmount(aspect, amt);
    }

    @Override
    public AspectList getAspects() {
        TilePrimordialAccretionChamber tile = this.findCentralChamber();
        return tile == null ? new AspectList() : tile.getAspects();
    }

    @Override
    public void setAspects(AspectList aspects) {
        TilePrimordialAccretionChamber tile = this.findCentralChamber();
        if (tile != null) {
            tile.setAspects(aspects);
        }
    }

    @Override
    public boolean takeFromContainer(AspectList aspectList) {
        TilePrimordialAccretionChamber tile = this.findCentralChamber();
        return tile != null && tile.takeFromContainer(aspectList);
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int amt) {
        TilePrimordialAccretionChamber tile = this.findCentralChamber();
        return tile != null && tile.takeFromContainer(aspect, amt);
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
    public boolean canInputFrom(EnumFacing face) {
        return this.isConnectable(face);
    }

    @Override
    public boolean canOutputTo(EnumFacing face) {
        return false;
    }

    @Override
    public int getEssentiaAmount(EnumFacing face) {
        Aspect type = this.getEssentiaType(face);
        if (type == null) {
            return 0;
        } else {
            return this.getAspects().getAmount(type);
        }
    }

    @Override
    public Aspect getEssentiaType(EnumFacing face) {
        AspectList aspects = this.getAspects();
        if (aspects == null || aspects.size() == 0) {
            return null;
        } else {
            return aspects.getAspectsSortedByAmount()[0];
        }
    }

    @Override
    public int getMinimumSuction() {
        // Can't output, so no need for minimum suction
        return 0;
    }

    @Override
    public int getSuctionAmount(EnumFacing face) {
        if (this.isEssentiaPort(face)) {
            Aspect type = this.getSuctionType(face);
            return type == null ? 0 : 128;
        } else {
            return 0;
        }
    }

    @Override
    public Aspect getSuctionType(EnumFacing face) {
        if (this.isEssentiaPort(face)) {
            TilePrimordialAccretionChamber tile = this.findCentralChamber();
            return tile == null ? null : tile.getCurrentSuction();
        } else {
            return null;
        }
    }

    @Override
    public boolean isConnectable(EnumFacing face) {
        return this.isEssentiaPort(face);
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

}
