package com.verdantartifice.thaumicwonders.common.blocks.devices;

import com.verdantartifice.thaumicwonders.common.blocks.base.BlockDeviceTW;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TileDimensionalRipper;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.IBlockFacing;

public class BlockDimensionalRipper extends BlockDeviceTW<TileDimensionalRipper> implements IBlockFacing, IBlockEnabled {

    public BlockDimensionalRipper() {
        super(Material.IRON, TileDimensionalRipper.class, "dimensional_ripper");
        this.setSoundType(SoundType.METAL);
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        EnumFacing blockFacing = state.getValue(IBlockFacing.FACING);
        if (blockFacing.getOpposite() == face) {
            return BlockFaceShape.SOLID;
        } else {
            return BlockFaceShape.UNDEFINED;
        }
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if(tile instanceof TileDimensionalRipper) {
            int essentia = ((TileDimensionalRipper) tile).getAmount();
            int capacity = ((TileDimensionalRipper) tile).getCapacity();
            if(essentia > 0) {
                return Math.max(1, (int) (15f * (float) essentia / (float) capacity));
            }
        }
        return 0;
    }
}
