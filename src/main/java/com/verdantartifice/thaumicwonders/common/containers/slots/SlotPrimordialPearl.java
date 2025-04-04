package com.verdantartifice.thaumicwonders.common.containers.slots;

import com.verdantartifice.thaumicwonders.common.crafting.accelerator.AcceleratorRecipe;
import com.verdantartifice.thaumicwonders.common.crafting.accelerator.AcceleratorRecipeRegistry;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.Arrays;

public class SlotPrimordialPearl extends Slot {
    private AcceleratorRecipe recipe;

    public SlotPrimordialPearl(IInventory tileEntity, int index, int xPosition, int yPosition) {
        super(tileEntity, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack != null && !stack.isEmpty() && AcceleratorRecipeRegistry.getRecipe(stack) != null;
    }

    @Override
    public void putStack(ItemStack stack) {
        this.recipe = AcceleratorRecipeRegistry.getRecipe(stack);
        super.putStack(stack);
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        if (this.recipe != null) {
            return Arrays.stream(this.recipe.getInput().getMatchingStacks())
                    .filter(input -> !input.isEmpty()).findFirst()
                    .map(ItemStack::getCount).orElse(1);
        } else {
            return super.getItemStackLimit(stack);
        }
    }
}
