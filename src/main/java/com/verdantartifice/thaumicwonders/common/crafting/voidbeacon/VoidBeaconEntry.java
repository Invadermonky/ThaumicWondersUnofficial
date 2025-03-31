package com.verdantartifice.thaumicwonders.common.crafting.voidbeacon;

import com.google.common.base.Preconditions;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;

import java.util.*;

public class VoidBeaconEntry {
    protected ItemStack stack;

    public VoidBeaconEntry(ItemStack stack) {
        Preconditions.checkArgument(!stack.isEmpty(), "ItemStack cannot be empty.");
        this.stack = stack;
    }

    public ItemStack getStack() {
        return this.stack.copy();
    }

    public int getAspectValue(Aspect aspect) {
        return AspectHelper.getObjectAspects(this.getStack()).aspects.getOrDefault(aspect, 0);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof VoidBeaconEntry)) return false;
        VoidBeaconEntry that = (VoidBeaconEntry) o;
        return ItemStack.areItemStacksEqual(this.getStack(), that.getStack());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(stack);
    }
}
