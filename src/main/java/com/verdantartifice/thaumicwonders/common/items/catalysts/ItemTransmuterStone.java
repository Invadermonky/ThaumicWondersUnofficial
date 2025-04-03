package com.verdantartifice.thaumicwonders.common.items.catalysts;

import com.verdantartifice.thaumicwonders.common.config.ConfigHandlerTW;
import com.verdantartifice.thaumicwonders.common.items.base.ItemTW;
import net.minecraft.item.ItemStack;

public class ItemTransmuterStone extends ItemTW {
    public ItemTransmuterStone() {
        super("transmuter_stone");
        this.setMaxDamage(ConfigHandlerTW.catalyst_stones.alchemist_stone.durability - 1);  // Gets one last use at durability 0
        this.setMaxStackSize(1);
        this.setNoRepair();
    }
    
    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return ConfigHandlerTW.catalyst_stones.transmuter_stone.enchantable;
    }

    @Override
    public int getItemEnchantability() {
        return 10;
    }
}
