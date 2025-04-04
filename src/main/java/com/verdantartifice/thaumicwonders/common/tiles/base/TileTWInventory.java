package com.verdantartifice.thaumicwonders.common.tiles.base;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nullable;

public class TileTWInventory extends TileTW implements ISidedInventory {
    protected int[] syncedSlots = new int[0];
    protected String customName;
    protected IItemHandler handlerTop = new SidedInvWrapper(this, EnumFacing.UP);
    protected IItemHandler handlerBottom = new SidedInvWrapper(this, EnumFacing.DOWN);
    protected IItemHandler handlerWest = new SidedInvWrapper(this, EnumFacing.WEST);
    protected IItemHandler handlerEast = new SidedInvWrapper(this, EnumFacing.EAST);
    protected IItemHandler handlerNorth = new SidedInvWrapper(this, EnumFacing.NORTH);
    protected IItemHandler handlerSouth = new SidedInvWrapper(this, EnumFacing.SOUTH);
    private NonNullList<ItemStack> stacks = NonNullList.withSize(1, ItemStack.EMPTY);
    private NonNullList<ItemStack> syncedStacks = NonNullList.withSize(1, ItemStack.EMPTY);
    private int[] faceSlots;

    public TileTWInventory(int size) {
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        this.syncedStacks = NonNullList.withSize(size, ItemStack.EMPTY);
        this.faceSlots = new int[size];
        for (int a = 0; a < size; a++) {
            this.faceSlots[a] = a;
        }
    }

    @Override
    public int getSizeInventory() {
        return this.stacks.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.stacks) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return this.getItems().get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = ItemStackHelper.getAndSplit(this.getItems(), index, count);
        if (!stack.isEmpty() && this.isSyncedSlot(index)) {
            this.syncSlots(null);
        }
        this.markDirty();
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = ItemStackHelper.getAndRemove(this.getItems(), index);
        if (this.isSyncedSlot(index)) {
            this.syncSlots(null);
        }
        this.markDirty();
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, @Nullable ItemStack stack) {
        this.getItems().set(index, stack);
        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
        this.markDirty();
        if (this.isSyncedSlot(index)) {
            this.syncSlots(null);
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.world.getTileEntity(this.getPos()) == this;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
    }

    protected NonNullList<ItemStack> getItems() {
        return this.stacks;
    }

    public ItemStack getSyncedStackInSlot(int index) {
        return this.syncedStacks.get(index);
    }

    private boolean isSyncedSlot(int slot) {
        for (int num : this.syncedSlots) {
            if (num == slot) {
                return true;
            }
        }
        return false;
    }

    protected void syncSlots(@Nullable EntityPlayerMP player) {
        if (this.syncedSlots.length > 0) {
            NBTTagCompound nbt = new NBTTagCompound();
            NBTTagList nbttaglist = new NBTTagList();
            for (int i = 0; i < this.getSizeInventory(); i++) {
                if (!this.getStackInSlot(i).isEmpty() && this.isSyncedSlot(i)) {
                    NBTTagCompound compound = new NBTTagCompound();
                    compound.setByte("Slot", (byte) i);
                    this.getStackInSlot(i).writeToNBT(compound);
                    nbttaglist.appendTag(compound);
                }
            }
            nbt.setTag("ItemsSynced", nbttaglist);
            this.sendMessageToClient(nbt, player);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("CustomName")) {
            this.customName = compound.getString("CustomName");
        }
        this.stacks = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, this.stacks);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (hasCustomName()) {
            compound.setString("CustomName", this.customName);
        }
        ItemStackHelper.saveAllItems(compound, this.stacks);
        return compound;
    }

    @Override
    public void syncTile(boolean rerender) {
        super.syncTile(rerender);
        this.syncSlots(null);
    }

    @Override
    public void messageFromServer(NBTTagCompound nbt) {
        super.messageFromServer(nbt);
        if (nbt.hasKey("ItemsSynced")) {
            this.syncedStacks = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
            NBTTagList nbttaglist = nbt.getTagList("ItemsSynced", 10);
            for (int index = 0; index < nbttaglist.tagCount(); index++) {
                NBTTagCompound compound = nbttaglist.getCompoundTagAt(index);
                byte slot = compound.getByte("Slot");
                if (this.isSyncedSlot(slot)) {
                    this.syncedStacks.set(slot, new ItemStack(compound));
                }
            }
        }
    }

    @Override
    public void messageFromClient(NBTTagCompound nbt, EntityPlayerMP player) {
        super.messageFromClient(nbt, player);
        if (nbt.hasKey("requestSync")) {
            this.syncSlots(player);
        }
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.customName : "container.thaumic_wonders";
    }

    @Override
    public boolean hasCustomName() {
        return this.customName != null && !this.customName.isEmpty();
    }

    @Nullable
    public ITextComponent getDisplayName() {
        return null;
    }

    @Override
    public void onLoad() {
        if (!this.world.isRemote) {
            this.syncSlots(null);
        } else {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setBoolean("requestSync", true);
            this.sendMessageToServer(nbt);
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing));
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            switch (facing) {
                case UP:
                    return (T) this.handlerTop;
                case DOWN:
                    return (T) this.handlerBottom;
                case NORTH:
                    return (T) this.handlerNorth;
                case SOUTH:
                    return (T) this.handlerSouth;
                case WEST:
                    return (T) this.handlerWest;
                case EAST:
                    return (T) this.handlerEast;
                default:
                    ThaumicWonders.LOGGER.error("Unknown facing value {} in TileTWInventory.getCapability", facing);
            }
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return this.faceSlots;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return this.isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return true;
    }
}
