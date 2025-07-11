package com.verdantartifice.thaumicwonders.common.tiles.devices;

import com.verdantartifice.thaumicwonders.common.blocks.devices.BlockVisCapacitor;
import com.verdantartifice.thaumicwonders.common.config.ConfigHandlerTW;
import com.verdantartifice.thaumicwonders.common.tiles.base.TileTW;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import thaumcraft.api.aura.AuraHelper;

public class TileVisCapacitor extends TileTW implements ITickable {
    public static final String TAG_STORED_VIS = "storedVis";
    private int storedVis = 0;

    @Override
    public void markDirty() {
        super.markDirty();
        IBlockState state = this.world.getBlockState(this.pos);
        ((BlockVisCapacitor) state.getBlock()).updateState(this.world, this.pos, state);
    }

    public int getStoredVis() {
        return storedVis;
    }

    public void setStoredVis(int storedVis) {
        this.storedVis = storedVis;
    }

    public int getVisCapacity() {
        return ConfigHandlerTW.vis_capacitor.visCapacity;
    }

    @Override
    public void update() {
        if (!this.world.isRemote && this.world.getTotalWorldTime() % 20L == 0) {
            if (this.world.isBlockPowered(this.pos)) {
                //Discharging 1 Vis per second while powered
                if (this.storedVis > 0) {
                    AuraHelper.addVis(this.world, this.pos, 1.0f);
                    this.storedVis--;
                    this.markDirty();
                }
            } else {
                float aura = AuraHelper.getVis(this.world, this.pos);
                int base = AuraHelper.getAuraBase(this.world, this.pos);
                if (aura > 1.0f) {
                    if (this.storedVis < this.getVisCapacity() && (double) aura > (double) base * ConfigHandlerTW.vis_capacitor.rechargeThreshold) {
                        //Drain Vis from aura and charge capacitor
                        AuraHelper.drainVis(this.world, this.pos, 1.0f, false);
                        this.storedVis++;
                        this.markDirty();
                    } else if (this.storedVis > 0 && (double) aura < (double) base * ConfigHandlerTW.vis_capacitor.dischargeThreshold) {
                        //Discharge vis from capacitor to aura
                        AuraHelper.addVis(this.world, this.pos, 1);
                        this.storedVis--;
                        this.markDirty();
                    }
                }
            }
        }
    }

    @Override
    protected void readFromTileNBT(NBTTagCompound compound) {
        this.storedVis = compound.getShort(TAG_STORED_VIS);
    }

    @Override
    protected NBTTagCompound writeToTileNBT(NBTTagCompound compound) {
        compound.setShort(TAG_STORED_VIS, (short) this.storedVis);
        return compound;
    }
}
