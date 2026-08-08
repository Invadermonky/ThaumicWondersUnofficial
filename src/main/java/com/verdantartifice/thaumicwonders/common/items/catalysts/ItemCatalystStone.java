package com.verdantartifice.thaumicwonders.common.items.catalysts;

import com.verdantartifice.thaumicwonders.common.items.base.ItemTW;
import net.minecraft.item.ItemStack;

import java.util.Random;

public class ItemCatalystStone extends ItemTW {
    public static final Random RANDOM = new Random();
    private boolean isEnchantable;

    public ItemCatalystStone(String name, int uses, boolean isEnchantable, boolean canRepair) {
        super(name);
        this.setMaxDamage(uses - 1);
        this.setMaxStackSize(1);
        if(!canRepair) {
            this.setNoRepair();
        }
        this.isEnchantable = isEnchantable;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return this.isEnchantable;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return this.isEnchantable;
    }

    @Override
    public int getItemEnchantability() {
        return 10;
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return stack.getItemDamage() < stack.getMaxDamage();
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        if(!this.hasContainerItem(stack)) {
            return ItemStack.EMPTY;
        } else {
            if(stack.attemptDamageItem(1, RANDOM, null)) {
                return ItemStack.EMPTY;
            }
            return stack;
        }
    }
}
