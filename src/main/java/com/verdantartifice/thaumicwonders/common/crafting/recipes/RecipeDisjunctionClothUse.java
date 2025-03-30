package com.verdantartifice.thaumicwonders.common.crafting.recipes;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.items.ItemsTW;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;

public class RecipeDisjunctionClothUse extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    public RecipeDisjunctionClothUse() {
        super();
        this.setRegistryName(ThaumicWonders.MODID, "disenchant");
    }
    
    @Override
    public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World worldIn) {
        return RecipeDisjunctionClothUse.matches(inv);
    }
    
    public static boolean matches(@Nonnull IInventory inv) {
        boolean foundCloth = false;
        boolean foundTarget = false;
        
        for (int index = 0; index < inv.getSizeInventory(); index++) {
            ItemStack stack = inv.getStackInSlot(index);
            if (!stack.isEmpty()) {
                if (stack.getItem() == ItemsTW.DISJUNCTION_CLOTH && !foundCloth) {
                    foundCloth = true;
                } else if (stack.getItem() != ItemsTW.DISJUNCTION_CLOTH && !foundTarget && stack.isItemEnchanted()) {
                    foundTarget = true;
                } else {
                    // Invalid item, abort
                    return false;
                }
            }
        }
        
        return foundCloth && foundTarget;
    }

    @Override
    @Nonnull
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
        ItemStack stackToDisenchant = ItemStack.EMPTY;
        
        for (int index = 0; index < inv.getSizeInventory(); index++) {
            ItemStack stack = inv.getStackInSlot(index);
            if (!stack.isEmpty() && stack.getItem() != ItemsTW.DISJUNCTION_CLOTH && stack.isItemEnchanted()) {
                stackToDisenchant = stack.copy();
                break;
            }
        }
        if (!stackToDisenchant.isEmpty() && stackToDisenchant.hasTagCompound()) {
            stackToDisenchant.getTagCompound().removeTag("ench");
        }
        
        return stackToDisenchant;
    }

    @Override
    public boolean canFit(int width, int height) {
        return (width >= 2) || (height >= 2);
    }

    @Override
    @Nonnull
    public ItemStack getRecipeOutput() {
        // Recipe is dynamic, so return empty stack
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}
