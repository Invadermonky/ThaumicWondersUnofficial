package com.verdantartifice.thaumicwonders.common.blocks.misc;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.blocks.base.BlockTW;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.lib.utils.BlockStateUtils;

public class BlockPrimordialAcceleratorTunnel extends BlockTW implements IBlockFacingHorizontal {
    public BlockPrimordialAcceleratorTunnel() {
        super(Material.IRON, "primordial_accelerator_tunnel");
        this.setSoundType(SoundType.METAL);

        IBlockState blockState = this.blockState.getBaseState();
        blockState.withProperty(IBlockFacingHorizontal.FACING, EnumFacing.NORTH);
        this.setDefaultState(blockState);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = this.getDefaultState();
        try {
            EnumFacing facing = BlockStateUtils.getFacing(meta);
            if (facing == EnumFacing.DOWN || facing == EnumFacing.UP) {
                facing = EnumFacing.NORTH;
            }
            state = state.withProperty(IBlockFacingHorizontal.FACING, facing);
        } catch (Exception e) {
            ThaumicWonders.LOGGER.catching(e);
        }
        return state;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(IBlockFacingHorizontal.FACING).getIndex();
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        EnumFacing placerFacing = placer.getHorizontalFacing();
        return this.getDefaultState().withProperty(IBlockFacingHorizontal.FACING, placer.isSneaking() ? placerFacing : placerFacing.getOpposite());
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, IBlockFacingHorizontal.FACING);
    }

    @Override
    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return true;
    }
}
