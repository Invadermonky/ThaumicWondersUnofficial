package com.verdantartifice.thaumicwonders.common.blocks.plants;

import com.verdantartifice.thaumicwonders.common.items.ItemsTW;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.EnumPlantType;
import thaumcraft.api.blocks.BlocksTC;

public class BlockShimmerleafCrop extends AbstractBlockMysticCrop {
    public BlockShimmerleafCrop() {
        super("shimmerleaf_crop");
    }

    @Override
    protected IBlockState getMatureBlockState() {
        return BlocksTC.shimmerleaf.getDefaultState();
    }

    @Override
    protected boolean canSustainBush(IBlockState state) {
        return (state.getBlock() == Blocks.GRASS) || (state.getBlock() == Blocks.DIRT);
    }

    @Override
    protected Item getSeed() {
        return ItemsTW.SHIMMERLEAF_SEED;
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return EnumPlantType.Plains;
    }

    @Override
    public EnumOffsetType getOffsetType() {
        return EnumOffsetType.XZ;
    }
}
