package com.verdantartifice.thaumicwonders.common.tiles.devices;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.blocks.BlocksTW;
import com.verdantartifice.thaumicwonders.common.config.ConfigHandlerTW;
import com.verdantartifice.thaumicwonders.common.network.PacketHandler;
import com.verdantartifice.thaumicwonders.common.network.packets.PacketDimensionalRipperFx;
import com.verdantartifice.thaumicwonders.common.tiles.base.TileTW;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.IBlockFacing;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.EntityUtils;

import java.util.List;

public class TileDimensionalRipper extends TileTW implements IAspectContainer, IEssentiaTransport, ITickable {
    private static final int CAPACITY = 250;
    private static final int DISTANCE = 10;

    private static final int PLAY_LINK_EFFECTS = 5;

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
        compound.setShort("essentia", (short) this.amount);
        return compound;
    }

    @Override
    public boolean isConnectable(EnumFacing face) {
        if (this.getBlockType() instanceof IBlockFacing) {
            IBlockState blockState = this.world.getBlockState(this.pos);
            EnumFacing blockFacing = blockState.getValue(IBlockFacing.FACING);
            return (blockFacing.getOpposite() == face);
        } else {
            return false;
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
    public void setSuction(Aspect aspect, int amt) {
        // Do nothing
    }

    @Override
    public Aspect getSuctionType(EnumFacing face) {
        return Aspect.FLUX;
    }

    @Override
    public int getSuctionAmount(EnumFacing face) {
        return (this.amount >= CAPACITY) ? 0 : 128;
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
        return Aspect.FLUX;
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
            list.add(Aspect.FLUX, this.amount);
        }
        return list;
    }

    @Override
    public void setAspects(AspectList aspects) {
        if (aspects != null && aspects.size() > 0) {
            this.amount = aspects.getAmount(Aspect.FLUX);
        }
    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        return (aspect == Aspect.FLUX);
    }

    @Override
    public int addToContainer(Aspect aspect, int toAdd) {
        if (toAdd == 0) {
            return 0;
        } else if (this.amount >= CAPACITY || aspect != Aspect.FLUX) {
            // Incompatible addition; return all of it
            this.syncTile(false);
            this.markDirty();
            return toAdd;
        } else {
            // Add as much as possible and return the remainder
            int added = Math.min(toAdd, CAPACITY - this.amount);
            this.amount += added;
            this.syncTile(false);
            this.markDirty();
            return (toAdd - added);
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
        return (aspect == Aspect.FLUX && this.amount >= amt);
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
        return (aspect == Aspect.FLUX) ? this.amount : 0;
    }

    @Override
    public void update() {
        if (!this.world.isRemote && (++this.tickCounter % 5 == 0)) {
            if (this.amount < CAPACITY) {
                this.fill();
            }
            this.checkForActivation();
        }
    }

    protected void fill() {
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
                if (otherTile.getEssentiaType(face.getOpposite()) == Aspect.FLUX &&
                        otherTile.getEssentiaAmount(face.getOpposite()) > 0 &&
                        this.getSuctionAmount(face) > otherTile.getSuctionAmount(face.getOpposite()) &&
                        this.getSuctionAmount(face) >= otherTile.getMinimumSuction()) {
                    int taken = otherTile.takeEssentia(Aspect.FLUX, 1, face.getOpposite());
                    int leftover = this.addToContainer(Aspect.FLUX, taken);
                    if (leftover > 0) {
                        ThaumicWonders.LOGGER.info("Ripper spilling {} essentia on fill", leftover);
                        AuraHelper.polluteAura(this.world, this.pos, leftover, true);
                    }
                    this.syncTile(false);
                    this.markDirty();
                    if (this.amount >= CAPACITY) {
                        break;
                    }
                }
            }
        }
    }

    protected void checkForActivation() {
        IBlockState state = this.world.getBlockState(this.pos);
        EnumFacing blockFacing = state.getValue(IBlockFacing.FACING);
        boolean blockEnabled = state.getValue(IBlockEnabled.ENABLED);
        BlockPos otherPos = this.pos.offset(blockFacing, DISTANCE);
        TileEntity otherTe = this.world.getTileEntity(otherPos);

        if (otherTe instanceof TileDimensionalRipper) {
            TileDimensionalRipper otherTile = (TileDimensionalRipper) otherTe;
            IBlockState otherState = this.world.getBlockState(otherPos);
            EnumFacing otherBlockFacing = otherState.getValue(IBlockFacing.FACING);
            boolean otherBlockEnabled = otherState.getValue(IBlockEnabled.ENABLED);

            if (otherBlockFacing == blockFacing.getOpposite()) {
                // Emit placement confirmation particles
                this.world.addBlockEvent(this.getPos(), BlocksTW.DIMENSIONAL_RIPPER, PLAY_LINK_EFFECTS, 0);

                // If both rippers are enabled and fueled, commence the reaction
                int minFuel = ConfigHandlerTW.dimensional_ripper.fuelRequired;
                if (blockEnabled && otherBlockEnabled && this.amount >= minFuel && otherTile.getAmount() >= minFuel) {
                    BlockPos targetPos = new BlockPos(
                            (this.pos.getX() + otherPos.getX()) / 2,
                            (this.pos.getY() + otherPos.getY()) / 2,
                            (this.pos.getZ() + otherPos.getZ()) / 2
                    );

                    // Deduct reaction fuel
                    int fuel = Math.min(this.getAmount(), otherTile.getAmount());
                    this.takeFromContainer(Aspect.FLUX, fuel);
                    otherTile.takeFromContainer(Aspect.FLUX, fuel);

                    // Play special effects
                    PacketHandler.INSTANCE.sendToAllAround(
                            new PacketDimensionalRipperFx(this.pos, targetPos),
                            new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), this.pos.getX(), this.pos.getY(), this.pos.getZ(), 32.0D));
                    PacketHandler.INSTANCE.sendToAllAround(
                            new PacketDimensionalRipperFx(otherPos, targetPos),
                            new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), otherPos.getX(), otherPos.getY(), otherPos.getZ(), 32.0D));
                    this.world.playSound(null, this.pos, SoundsTC.zap, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    this.world.playSound(null, otherPos, SoundsTC.zap, SoundCategory.BLOCKS, 1.0F, 1.0F);

                    // Create the rift
                    this.createRift(targetPos, fuel);
                }
            }
        }
    }

    protected void createRift(BlockPos pos, int fuelUsed) {
        List<EntityFluxRift> localRifts = EntityUtils.getEntitiesInRange(this.world, pos, null, EntityFluxRift.class, 2.0D);
        if (!localRifts.isEmpty()) {
            // Enlarge target rift
            EntityFluxRift rift = localRifts.get(0);
            int oldSize = rift.getRiftSize();
            double oldFuel = (oldSize * oldSize) / 3.0D;
            int newSize = (int) Math.sqrt((oldFuel + (2 * fuelUsed)) * 3.0D);
            rift.setRiftSize(newSize);
            rift.setRiftStability(rift.getRiftStability() - (newSize - oldSize));
        } else if (EntityUtils.getEntitiesInRange(this.world, pos, null, EntityFluxRift.class, 32.0D).isEmpty()) {
            // Create new rift if no others are nearby
            EntityFluxRift rift = new EntityFluxRift(this.world);
            rift.setRiftSeed(this.world.rand.nextInt());
            rift.setLocationAndAngles(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, (float) this.world.rand.nextInt(360), 0.0F);
            double size = Math.sqrt((2 * fuelUsed) * 3.0D);
            if (this.world.spawnEntity(rift)) {
                rift.setRiftSize((int) size);
            }
        }
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == PLAY_LINK_EFFECTS) {
            if (this.world.isRemote) {
                EnumFacing blockFacing = this.world.getBlockState(this.pos).getValue(IBlockFacing.FACING);
                BlockPos otherPos = this.pos.offset(blockFacing, DISTANCE);
                FXDispatcher.INSTANCE.visSparkle(
                        this.pos.getX(), this.pos.getY(), this.pos.getZ(),
                        otherPos.getX(), otherPos.getY(), otherPos.getZ(),
                        Aspect.FLUX.getColor());
            }
            return true;
        } else {
            return super.receiveClientEvent(id, type);
        }
    }
}
