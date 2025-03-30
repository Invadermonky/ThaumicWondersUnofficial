package com.verdantartifice.thaumicwonders.common.misc;

import com.verdantartifice.thaumicwonders.common.items.ItemsTW;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class CreativeTabTW extends CreativeTabs {

    public CreativeTabTW(String label) {
        super(label);
    }

    public CreativeTabTW(int index, String label) {
        super(index, label);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ItemsTW.FLYING_CARPET);
    }
}
