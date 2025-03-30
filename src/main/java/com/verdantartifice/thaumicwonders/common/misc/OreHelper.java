package com.verdantartifice.thaumicwonders.common.misc;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class OreHelper {
    public static boolean isOreNamed(@Nonnull ItemStack stack, @Nonnull String name) {
        List<String> oreNames = OreHelper.getOreNames(stack);
        return oreNames.contains(name);
    }
    
    @Nonnull
    public static List<String> getOreNames(@Nonnull ItemStack stack) {
        List<String> names = new ArrayList<>();
        if (stack.isEmpty()) {
            return names;
        }
        int[] oreIds = OreDictionary.getOreIDs(stack);
        for (int id : oreIds) {
            names.add(OreDictionary.getOreName(id));
        }
        return names;
    }
    
    public static boolean isOreBlock(ItemStack stack) {
        for (String name : OreHelper.getOreNames(stack)) {
            if (name != null && name.toUpperCase().startsWith("ORE")) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isOreMatch(@Nonnull ItemStack stack1, @Nonnull ItemStack stack2) {
        List<String> names1 = OreHelper.getOreNames(stack1);
        List<String> names2 = OreHelper.getOreNames(stack2);
        names1.retainAll(names2);
        return !names1.isEmpty();
    }
}
