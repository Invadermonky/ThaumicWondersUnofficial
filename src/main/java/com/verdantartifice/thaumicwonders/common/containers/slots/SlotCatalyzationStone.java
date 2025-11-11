package com.verdantartifice.thaumicwonders.common.containers.slots;

import com.verdantartifice.thaumicwonders.common.crafting.catalyzationchamber.CatalyzationChamberRecipeRegistry;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TileCatalyzationChamber;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class SlotCatalyzationStone extends SlotItemHandler {
    private TileCatalyzationChamber tileEntity;

    public SlotCatalyzationStone(TileCatalyzationChamber tileEntity, int index, int xPosition, int yPosition) {
        super(tileEntity.getCatalystHandler(), 0, xPosition, yPosition);
        this.tileEntity = tileEntity;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack.isEmpty() || CatalyzationChamberRecipeRegistry.isValidCatalyst(stack);
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
