package com.verdantartifice.thaumicwonders.common.containers.slots;

import com.verdantartifice.thaumicwonders.common.items.ItemsTW;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotPrimordialPearl extends SlotItemHandler {
    public SlotPrimordialPearl(IItemHandler stackHandler, int index, int xPosition, int yPosition) {
        super(stackHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == ItemsTW.PRIMORDIAL_GRAIN;
    }
}
