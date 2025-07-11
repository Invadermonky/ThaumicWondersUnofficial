package com.verdantartifice.thaumicwonders.common.containers.slots;

import com.verdantartifice.thaumicwonders.common.items.ItemsTW;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotPrimordialPearl extends Slot {
    public SlotPrimordialPearl(IInventory tileEntity, int index, int xPosition, int yPosition) {
        super(tileEntity, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == ItemsTW.PRIMORDIAL_GRAIN;
    }
}
