package com.verdantartifice.thaumicwonders.common.blocks.plants;

import com.verdantartifice.thaumicwonders.common.items.ItemsTW;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.EnumPlantType;
import thaumcraft.api.blocks.BlocksTC;

public class BlockVishroomCrop extends AbstractBlockMysticCrop {
    public BlockVishroomCrop() {
        super("vishroom_crop");
    }

    @Override
    protected IBlockState getMatureBlockState() {
        return BlocksTC.vishroom.getDefaultState();
    }

    @Override
    protected Item getSeed() {
        return ItemsTW.VISHROOM_SPORE;
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return EnumPlantType.Cave;
    }
}
