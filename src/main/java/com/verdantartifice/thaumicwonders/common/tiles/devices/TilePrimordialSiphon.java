package com.verdantartifice.thaumicwonders.common.tiles.devices;

import com.verdantartifice.thaumicwonders.common.config.ConfigHandlerTW;
import com.verdantartifice.thaumicwonders.common.items.ItemsTW;
import com.verdantartifice.thaumicwonders.common.tiles.base.TileTWInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.Vec3d;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.lib.utils.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class TilePrimordialSiphon extends TileTWInventory implements ITickable {
    private static final int[] slots = new int[]{};
    public int progress = 0;
    int counter = 0;

    public TilePrimordialSiphon() {
        super(1);
    }

    @Override
    public void update() {
        ++this.counter;
        if (!this.getWorld().isRemote && !this.isPowered() && this.counter % 20 == 0 && this.progress < 2000 && (this.getStackInSlot(0).isEmpty() || this.getStackInSlot(0).getItem() == ItemsTW.PRIMORDIAL_GRAIN && this.getStackInSlot(0).getCount() < this.getStackInSlot(0).getMaxStackSize())) {
            List<EntityFluxRift> rifts = this.getValidRifts();
            boolean did = false;

            for (EntityFluxRift rift : rifts) {
                double riftSize = Math.sqrt(rift.getRiftSize());
                this.progress = (int) ((double) this.progress + riftSize);
                rift.setRiftStability((float) ((double) rift.getRiftStability() - riftSize / (double) 15.0F));
                if (this.world.rand.nextInt(33) == 0) {
                    rift.setRiftSize(rift.getRiftSize() - 1);
                }

                did = riftSize >= (double) 1.0F;
            }

            if (did && this.counter % 40 == 0) {
                this.world.addBlockEvent(this.pos, this.getBlockType(), 5, this.counter);
            }

            for (did = false; this.progress >= ConfigHandlerTW.primordial_siphon.requiredRiftDrain && (this.getStackInSlot(0).isEmpty() || this.getStackInSlot(0).getItem() == ItemsTW.PRIMORDIAL_GRAIN && this.getStackInSlot(0).getCount() < this.getStackInSlot(0).getMaxStackSize()); did = true) {
                this.progress -= ConfigHandlerTW.primordial_siphon.requiredRiftDrain;
                if (this.getStackInSlot(0).isEmpty()) {
                    this.setInventorySlotContents(0, new ItemStack(ItemsTW.PRIMORDIAL_GRAIN));
                } else {
                    this.getStackInSlot(0).setCount(this.getStackInSlot(0).getCount() + 1);
                }
            }

            if (did) {
                this.syncTile(false);
                this.markDirty();
            }
        }
    }

    private boolean isPowered() {
        return this.world.getRedstonePowerFromNeighbors(this.pos) > 0;
    }

    private List<EntityFluxRift> getValidRifts() {
        ArrayList<EntityFluxRift> ret = new ArrayList<>();

        for (EntityFluxRift fr : EntityUtils.getEntitiesInRange(this.getWorld(), this.getPos(), null, EntityFluxRift.class, 8.0F)) {
            if (!fr.isDead && fr.getRiftSize() >= 2) {
                double xx = (double) this.getPos().getX() + (double) 0.5F;
                double yy = (this.getPos().getY() + 1);
                double zz = (double) this.getPos().getZ() + (double) 0.5F;
                Vec3d v1 = new Vec3d(xx, yy, zz);
                Vec3d v2 = new Vec3d(fr.posX, fr.posY, fr.posZ);
                v1 = v1.add(v2.subtract(v1).normalize());
                if (EntityUtils.canEntityBeSeen(fr, v1.x, v1.y, v1.z)) {
                    ret.add(fr);
                }
            }
        }

        return ret;
    }

    public boolean isItemValidForSlot(int par1, ItemStack stack) {
        return stack.getItem() == ItemsTW.PRIMORDIAL_GRAIN;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.progress = nbt.getShort("progress");
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setShort("progress", (short) this.progress);
        return nbt;
    }

    public int[] getSlotsForFace(EnumFacing side) {
        return slots;
    }

    public boolean canInsertItem(int par1, ItemStack stack, EnumFacing par3) {
        return false;
    }

    public boolean canExtractItem(int par1, ItemStack stack2, EnumFacing par3) {
        return true;
    }

    public boolean receiveClientEvent(int i, int j) {
        if (i != 5) {
            return super.receiveClientEvent(i, j);
        } else {
            if (this.world.isRemote) {
                for (EntityFluxRift fr : this.getValidRifts()) {
                    FXDispatcher.INSTANCE.voidStreak(fr.posX, fr.posY, fr.posZ, (double) this.getPos().getX() + (double) 0.5F, (double) ((float) this.getPos().getY() + 0.5625F), (double) this.getPos().getZ() + (double) 0.5F, j, 0.04F);
                }
            }

            return true;
        }
    }
}
