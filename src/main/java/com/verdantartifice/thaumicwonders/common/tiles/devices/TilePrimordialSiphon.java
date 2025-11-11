package com.verdantartifice.thaumicwonders.common.tiles.devices;

import com.verdantartifice.thaumicwonders.common.config.ConfigHandlerTW;
import com.verdantartifice.thaumicwonders.common.items.ItemsTW;
import com.verdantartifice.thaumicwonders.common.tiles.base.TileTW;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.lib.utils.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class TilePrimordialSiphon extends TileTW implements ITickable {
    public ItemStackHandler stackHandler = new ItemStackHandler() {
        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return stack;
        }
    };
    public int progress = 0;
    int counter = 0;

    @SuppressWarnings("ConstantConditions")
    @Override
    public void update() {
        ++this.counter;
        if (!this.getWorld().isRemote && !this.isPowered() && this.counter % 20 == 0 && this.progress < 2000 && this.canInsertGrain()) {
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

            if(this.progress >= ConfigHandlerTW.primordial_siphon.requiredRiftDrain) {
                this.progress -= ConfigHandlerTW.primordial_siphon.requiredRiftDrain;
                ItemStack slotStack = this.stackHandler.getStackInSlot(0);
                ItemStack grain = new ItemStack(ItemsTW.PRIMORDIAL_GRAIN);
                if(!slotStack.isEmpty()) {
                    grain.setCount(slotStack.getCount() + 1);
                }
                this.stackHandler.setStackInSlot(0, grain);
                did = true;
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

    @SuppressWarnings("ConstantConditions")
    private boolean canInsertGrain() {
        ItemStack slotStack = this.stackHandler.getStackInSlot(0);
        return slotStack.isEmpty() || (slotStack.getItem() == ItemsTW.PRIMORDIAL_GRAIN && slotStack.getCount() < slotStack.getMaxStackSize());
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

    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.stackHandler.deserializeNBT(nbt.getCompoundTag("Inventory"));
        this.progress = nbt.getShort("progress");
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setTag("Inventory", this.stackHandler.serializeNBT());
        nbt.setShort("progress", (short) this.progress);
        return nbt;
    }

    @Override
    public void syncTile(boolean rerender) {
        super.syncTile(rerender);
        this.syncSlots(null);
    }

    @Override
    public void messageFromServer(NBTTagCompound nbt) {
        this.stackHandler.deserializeNBT(nbt.getCompoundTag("Inventory"));
    }

    @Override
    public void messageFromClient(NBTTagCompound nbt, EntityPlayerMP player) {
        if(nbt.hasKey("requestSync")) {
            nbt.removeTag("requestSync");
            this.syncSlots(player);
        }
    }

    @Override
    public void onLoad() {
        if(!this.world.isRemote) {
            this.syncSlots(null);
        } else {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setBoolean("requestSync", true);
            this.sendMessageToServer(tag);
        }
    }

    protected void syncSlots(@Nullable EntityPlayerMP player) {
        this.sendMessageToClient(this.writeToNBT(new NBTTagCompound()), player);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != EnumFacing.UP;
    }

    @Override
    public @Nullable <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != EnumFacing.UP) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.stackHandler);
        }
        return super.getCapability(capability, facing);
    }

    @Override
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
