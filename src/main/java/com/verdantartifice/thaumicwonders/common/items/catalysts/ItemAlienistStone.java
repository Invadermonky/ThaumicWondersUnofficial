package com.verdantartifice.thaumicwonders.common.items.catalysts;

import com.verdantartifice.thaumicwonders.common.config.ConfigHandlerTW;
import com.verdantartifice.thaumicwonders.common.items.base.ItemTW;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IRarity;

public class ItemAlienistStone extends ItemTW {
    public ItemAlienistStone() {
        super("alienist_stone");
        this.setMaxDamage(ConfigHandlerTW.catalyst_stones.alienist_stone.durability - 1);  // Gets one last use at durability 0
        this.setMaxStackSize(1);
        this.setNoRepair();
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return ConfigHandlerTW.catalyst_stones.alienist_stone.enchantable;
    }

    @Override
    public int getItemEnchantability() {
        return 10;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    public IRarity getForgeRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }
}
