package com.verdantartifice.thaumicwonders.common.items.misc;

import com.verdantartifice.thaumicwonders.common.items.base.ItemTW;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemDisjunctionCloth extends ItemTW {
    public ItemDisjunctionCloth() {
        super("disjunction_cloth");
        setMaxDamage(50);
        setMaxStackSize(1);
        setNoRepair();
    }
    
    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    @Nonnull
    @Override
    public ItemStack getContainerItem(@Nonnull ItemStack itemStack) {
        ItemStack newStack = itemStack.copy();
        newStack.setItemDamage(itemStack.getItemDamage() + 1);
        return newStack;
    }
}
