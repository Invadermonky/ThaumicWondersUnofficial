package com.verdantartifice.thaumicwonders.common.tiles.devices;

import com.verdantartifice.thaumicwonders.common.tiles.base.TileTW;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import thaumcraft.api.aura.AuraHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEverburningUrn extends TileTW implements ITickable, IFluidHandler {
    protected static final int CAPACITY = 1000;
    protected static final int MAX_PER_FILL = 40;

    protected int counter = 0;
    protected FluidTank tank = new FluidTank(new FluidStack(FluidRegistry.LAVA, 0), CAPACITY);

    public FluidTank getTank() {
        return this.tank;
    }

    @Override
    public void update() {
        this.counter++;
        if (!this.world.isRemote && this.counter % 5 == 0 && this.tank.getFluidAmount() < CAPACITY) {
            float visToDrain = (CAPACITY - this.tank.getFluidAmount()) / (float) MAX_PER_FILL;     // A full tank costs 25 vis
            if (visToDrain > 0.1F) {
                // Cap drain per op at 0.1 vis
                visToDrain = 0.1F;
            }
            float actualVisDrain = AuraHelper.drainVis(getWorld(), getPos(), visToDrain, false);
            int mbToAdd = (int) ((float) MAX_PER_FILL * actualVisDrain);
            if (mbToAdd > 0) {
                this.tank.fill(new FluidStack(FluidRegistry.LAVA, mbToAdd), true);
                this.markDirty();
                if (this.tank.getFluidAmount() >= this.tank.getCapacity()) {
                    this.syncTile(false);
                }
            }
        }
    }

    @Override
    protected void readFromTileNBT(NBTTagCompound compound) {
        this.tank.readFromNBT(compound);
    }

    @Override
    protected NBTTagCompound writeToTileNBT(NBTTagCompound compound) {
        this.tank.writeToNBT(compound);
        return compound;
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return this.tank.getTankProperties();
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        // Can't be filled, so do nothing
        return 0;
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        boolean wasFull = (this.tank.getFluidAmount() >= this.tank.getCapacity());
        FluidStack fluidStack = this.tank.drain(resource, doDrain);
        this.markDirty();
        if (wasFull && (this.tank.getFluidAmount() < this.tank.getCapacity())) {
            this.syncTile(false);
        }
        return fluidStack;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        boolean wasFull = (this.tank.getFluidAmount() >= this.tank.getCapacity());
        FluidStack fluidStack = this.tank.drain(maxDrain, doDrain);
        this.markDirty();
        if (wasFull && (this.tank.getFluidAmount() < this.tank.getCapacity())) {
            this.syncTile(false);
        }
        return fluidStack;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return ((facing == EnumFacing.UP || facing == EnumFacing.DOWN) && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if ((facing == EnumFacing.UP || facing == EnumFacing.DOWN) && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return (T) this.tank;
        } else {
            return super.getCapability(capability, facing);
        }
    }
}
