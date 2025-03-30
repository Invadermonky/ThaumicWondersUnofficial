package com.verdantartifice.thaumicwonders.common.containers.slots;

import com.verdantartifice.thaumicwonders.common.items.catalysts.ICatalystStone;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TileCatalyzationChamber;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotCatalyzationStone extends Slot {
    private TileCatalyzationChamber tileEntity;

    public SlotCatalyzationStone(TileCatalyzationChamber tileEntity, int index, int xPosition, int yPosition) {
        super(null, index, xPosition, yPosition);
        this.tileEntity = tileEntity;
    }

    @Override
    public ItemStack getStack() {
        return this.tileEntity.getEquippedStone();
    }
    
    @Override
    public void putStack(ItemStack stack) {
        if (this.tileEntity.setEquippedStone(stack)) {
            if (stack != null && !stack.isEmpty() && (stack.getCount() > this.getSlotStackLimit())) {
                stack.setCount(getSlotStackLimit());
            }
            this.onSlotChanged();
        }
    }
    
    @Override
    public void onSlotChanged() {}
    
    @Override
    public int getSlotStackLimit() {
        return 1;
    }
    
    @Override
    public ItemStack decrStackSize(int amount) {
        if (!this.getStack().isEmpty()) {
            if (this.getStack().getCount() <= amount) {
                ItemStack stack = this.getStack();
                this.putStack(ItemStack.EMPTY);
                return stack;
            } else {
                ItemStack stack = this.getStack().splitStack(amount);
                if (this.getStack().getCount() == 0) {
                    this.putStack(ItemStack.EMPTY);
                }
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
    
    @Override
    public boolean isItemValid(ItemStack stack) {
        if (stack != null && !stack.isEmpty()) {
            return stack.getItem() instanceof ICatalystStone;
        } else {
            return true;
        }
    }
}
