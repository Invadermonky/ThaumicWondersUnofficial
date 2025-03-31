package com.verdantartifice.thaumicwonders.common.crafting.voidbeacon;

import com.verdantartifice.thaumicwonders.common.crafting.WeightedEntry;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;

public class VoidBeaconEntry extends WeightedEntry {
    public VoidBeaconEntry(ItemStack stack, int weight) {
        super(stack, weight);
    }

    public VoidBeaconEntry(ItemStack stack) {
        this(stack, 10);
    }

    public boolean matches(Aspect aspect) {
        return AspectHelper.getObjectAspects(this.getStack()).aspects.containsKey(aspect);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof VoidBeaconEntry)) return false;
        VoidBeaconEntry that = (VoidBeaconEntry) o;
        return ItemStack.areItemStacksEqual(this.getStack(), that.getStack());
    }
}
