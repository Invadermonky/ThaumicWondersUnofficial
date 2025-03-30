package com.verdantartifice.thaumicwonders.common.blocks.plants;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public abstract class AbstractBlockMysticCrop extends BlockCrops {
    protected static final AxisAlignedBB CROP_AABB = new AxisAlignedBB(0.3D, 0.0D, 0.3D, 0.7D, 0.6D, 0.7D);

    public AbstractBlockMysticCrop(String name) {
        super();
        this.setRegistryName(ThaumicWonders.MODID, name);
        this.setTranslationKey(this.getRegistryName().toString());
    }
    
    protected abstract IBlockState getMatureBlockState();
    
    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(worldIn, pos, state, rand);
        if (this.isMaxAge(worldIn.getBlockState(pos))) {
            worldIn.setBlockState(pos, this.getMatureBlockState(), 3);
        }
    }
    
    @Override
    public void grow(World worldIn, BlockPos pos, IBlockState state) {
        super.grow(worldIn, pos, state);
        if (this.isMaxAge(worldIn.getBlockState(pos))) {
            worldIn.setBlockState(pos, this.getMatureBlockState(), 3);
        }
    }
    
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return CROP_AABB;
    }
}
