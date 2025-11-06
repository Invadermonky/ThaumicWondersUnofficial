package com.verdantartifice.thaumicwonders.common.containers.slots;

import com.verdantartifice.thaumicwonders.common.items.linkers.ItemPortalLinker;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TilePortalAnchor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class SlotPortalLinker extends SlotItemHandler {
    private TilePortalAnchor tile;

    public SlotPortalLinker(TilePortalAnchor tilePortalAnchor, int index, int xPosition, int yPosition) {
        super(tilePortalAnchor.stackHandler, index, xPosition, yPosition);
        this.tile = tilePortalAnchor;
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
        this.tile.removePortals(false);
        this.tile.setPrimaryAnchor(false);
        super.onSlotChanged();
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
