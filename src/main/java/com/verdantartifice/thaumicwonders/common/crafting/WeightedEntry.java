package com.verdantartifice.thaumicwonders.common.crafting;

import com.google.common.base.Preconditions;
import net.minecraft.item.ItemStack;

import java.util.Objects;

public class WeightedEntry {
    protected ItemStack stack;
    protected int weight;

    public WeightedEntry(ItemStack stack, int weight) throws IllegalArgumentException {
        Preconditions.checkArgument(!stack.isEmpty(), "Meat ItemStack cannot be empty");
        Preconditions.checkArgument(weight > 0, "Weight must be greater than 0");

        this.stack = stack;
        this.weight = weight;
    }

    public ItemStack getStack() {
        return stack.copy();
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean equals(ItemStack stack) {
        return ItemStack.areItemStacksEqual(getStack(), stack);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getStack());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof WeightedEntry))
            return false;
        WeightedEntry that = (WeightedEntry) o;
        return ItemStack.areItemStacksEqual(getStack(), that.getStack());
    }
}
