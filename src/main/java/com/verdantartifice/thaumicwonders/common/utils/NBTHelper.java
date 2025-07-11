package com.verdantartifice.thaumicwonders.common.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class NBTHelper {

    public static void initNBT(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
    }

    public static NBTTagCompound serializeBlockPos(BlockPos pos) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("posX", pos.getX());
        tag.setInteger("posY", pos.getY());
        tag.setInteger("posZ", pos.getZ());
        return tag;
    }

    public static BlockPos deserializeBlockPos(NBTTagCompound tag) {
        int x = tag.getInteger("posX");
        int y = tag.getInteger("posY");
        int z = tag.getInteger("posZ");
        return new BlockPos(x, y, z);
    }
}
