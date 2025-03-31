package com.verdantartifice.thaumicwonders.common.crafting.meatyorb;

import com.google.common.base.Preconditions;
import net.minecraft.item.ItemStack;

import java.util.Objects;

public class MeatyOrbEntry {
    protected final ItemStack meatStack;
    protected final int weight;

    public MeatyOrbEntry(ItemStack meatStack, int weight) {
        Preconditions.checkArgument(!meatStack.isEmpty(), "Meat ItemStack cannot be empty");
        Preconditions.checkArgument(weight > 0, "Weight must be greater than 0");

        this.meatStack = meatStack;
        this.weight = weight;
    }

    public ItemStack getMeatStack() {
        return meatStack.copy();
    }

    public int getWeight() {
        return weight;
    }

    public boolean equals(ItemStack stack) {
        return ItemStack.areItemStacksEqual(getMeatStack(), stack);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MeatyOrbEntry)) return false;
        MeatyOrbEntry that = (MeatyOrbEntry) o;
        return ItemStack.areItemStacksEqual(getMeatStack(), that.getMeatStack());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getMeatStack());
    }
}
