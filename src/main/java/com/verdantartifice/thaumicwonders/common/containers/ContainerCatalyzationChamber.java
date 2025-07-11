package com.verdantartifice.thaumicwonders.common.containers;

import com.verdantartifice.thaumicwonders.common.containers.slots.SlotCatalyzationStone;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TileCatalyzationChamber;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerCatalyzationChamber extends Container {
    private InventoryPlayer inventoryPlayer;
    private TileCatalyzationChamber tileEntity;

    public ContainerCatalyzationChamber(InventoryPlayer inventoryPlayer, TileCatalyzationChamber chamberTile) {
        this.inventoryPlayer = inventoryPlayer;
        this.tileEntity = chamberTile;

        this.addSlotToContainer(new SlotCatalyzationStone(this.tileEntity, 0, 80, 62));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new Slot(this.inventoryPlayer, j + (i * 9) + 9, 8 + (j * 18), 117 + (i * 18)));
            }
        }
        for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new Slot(this.inventoryPlayer, i, 8 + (i * 18), 175));
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slotObject = this.inventorySlots.get(index);
        if (slotObject != null && slotObject.getHasStack()) {
            ItemStack stackInSlot = slotObject.getStack();
            stack = stackInSlot.copy();
            if (index == 0) {
                if (!this.mergeItemStack(stackInSlot, 1, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(stackInSlot, 0, 1, false)) {
                return ItemStack.EMPTY;
            }

            if (stackInSlot.getCount() == 0) {
                slotObject.putStack(ItemStack.EMPTY);
            } else {
                slotObject.onSlotChanged();
            }
        }
        return stack;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }
}
