package com.verdantartifice.thaumicwonders.common.containers.slots;

import com.verdantartifice.thaumicwonders.common.items.misc.ItemPortalLinker;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TilePortalAnchor;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotPortalLinker extends Slot {

    public SlotPortalLinker(TilePortalAnchor tilePortalAnchor, int index, int xPosition, int yPosition) {
        super(tilePortalAnchor, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack.getItem() instanceof ItemPortalLinker && ((ItemPortalLinker) stack.getItem()).isLinked(stack);
    }

    @Override
    public ItemStack getStack() {
        return super.getStack();
    }

    @Override
    public void putStack(ItemStack stack) {
        super.putStack(stack);
    }

    @Override
    public void onSlotChanged() {
//        super.onSlotChanged();
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
}
