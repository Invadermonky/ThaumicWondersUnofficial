package com.verdantartifice.thaumicwonders.common.crafting.recipes;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.items.ItemsTW;
import com.verdantartifice.thaumicwonders.common.items.entities.ItemFlyingCarpet;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.oredict.DyeUtils;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;

public class RecipeFlyingCarpetDyes extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    public RecipeFlyingCarpetDyes() {
        super();
        this.setRegistryName(new ResourceLocation(ThaumicWonders.MODID, "flying_carpet_dyes"));
    }

    @Override
    public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World worldIn) {
        boolean foundCarpet = false;
        boolean foundDye = false;
        
        for (int index = 0; index < inv.getSizeInventory(); index++) {
            ItemStack stack = inv.getStackInSlot(index);
            if (!stack.isEmpty()) {
                if (stack.getItem() == ItemsTW.FLYING_CARPET && !foundCarpet) {
                    foundCarpet = true;
                } else if (DyeUtils.isDye(stack) && !foundDye) {
                    foundDye = true;
                } else {
                    // Invalid item, abort
                    return false;
                }
            }
        }
        
        return foundCarpet && foundDye;
    }

    @Override
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
        ItemStack carpetStack = ItemStack.EMPTY;
        EnumDyeColor color = null;
        
        for (int index = 0; index < inv.getSizeInventory(); index++) {
            ItemStack stack = inv.getStackInSlot(index);
            if (!stack.isEmpty()) {
                if (stack.getItem() == ItemsTW.FLYING_CARPET) {
                    carpetStack = stack.copy();
                } else if (DyeUtils.isDye(stack)) {
                    color = DyeUtils.colorFromStack(stack).orElse(null);
                }
            }
        }
        if (!carpetStack.isEmpty() && carpetStack.getItem() instanceof ItemFlyingCarpet && color != null) {
            ((ItemFlyingCarpet)carpetStack.getItem()).setDyeColor(carpetStack, color);
        }
        
        return carpetStack;
    }

    @Override
    public boolean canFit(int width, int height) {
        return (width >= 2) || (height >= 2);
    }

    @Override
    public ItemStack getRecipeOutput() {
        // Recipe is dynamic, so return empty stack
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}
