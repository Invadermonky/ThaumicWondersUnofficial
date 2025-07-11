package com.verdantartifice.thaumicwonders.common.containers.slots;

import com.verdantartifice.thaumicwonders.common.crafting.catalyzationchamber.CatalyzationChamberRecipeRegistry;
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
    public boolean isItemValid(ItemStack stack) {
        if (!stack.isEmpty()) {
            return CatalyzationChamberRecipeRegistry.isValidCatalyst(stack);
        } else {
            return false;
        }
    }

    @Override
    public ItemStack getStack() {
        return this.tileEntity.getEquippedStone();
    }

    @Override
    public void putStack(ItemStack stack) {
        if (this.tileEntity.setEquippedStone(stack)) {
            if (!stack.isEmpty() && stack.getCount() > this.getSlotStackLimit()) {
                stack.setCount(getSlotStackLimit());
            }
            this.onSlotChanged();
        }
    }

    @Override
    public void onSlotChanged() {
    }

    @Override
    public int getSlotStackLimit() {
        return 64;
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
