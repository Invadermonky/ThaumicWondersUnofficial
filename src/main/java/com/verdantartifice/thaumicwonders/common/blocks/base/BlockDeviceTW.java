package com.verdantartifice.thaumicwonders.common.blocks.base;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.IBlockFacing;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.lib.utils.BlockStateUtils;

import java.util.ArrayList;

public class BlockDeviceTW<T extends TileEntity> extends BlockTileTW<T> {
    public BlockDeviceTW(Material mat, Class<T> tileClass, String name) {
        super(mat, tileClass, name);
        
        IBlockState blockState = this.blockState.getBaseState();
        if (this instanceof IBlockFacingHorizontal) {
            blockState.withProperty(IBlockFacingHorizontal.FACING, EnumFacing.NORTH);
        } else if (this instanceof IBlockFacing) {
            blockState.withProperty(IBlockFacing.FACING, EnumFacing.UP);
        }
        if (this instanceof IBlockEnabled) {
            blockState.withProperty(IBlockEnabled.ENABLED, Boolean.TRUE);
        }
        this.setDefaultState(blockState);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        this.updateState(worldIn, pos, state);
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        this.updateState(worldIn, pos, state);
    }
    
    protected void updateState(World worldIn, BlockPos pos, IBlockState state) {
        if (this instanceof IBlockEnabled) {
            boolean flag = !worldIn.isBlockPowered(pos);
            if (flag != state.getValue(IBlockEnabled.ENABLED)) {
                worldIn.setBlockState(pos, state.withProperty(IBlockEnabled.ENABLED, flag), 0x3);
            }
        }
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState state = this.getDefaultState();
        if (this instanceof IBlockFacingHorizontal) {
            EnumFacing placerFacing = placer.getHorizontalFacing();
            state = state.withProperty(IBlockFacingHorizontal.FACING, placer.isSneaking() ? placerFacing : placerFacing.getOpposite());
        } else if (this instanceof IBlockFacing) {
            EnumFacing direction = EnumFacing.getDirectionFromEntityLiving(pos, placer);
            state = state.withProperty(IBlockFacing.FACING, placer.isSneaking() ? direction.getOpposite() : direction);
        }
        if (this instanceof IBlockEnabled) {
            state = state.withProperty(IBlockEnabled.ENABLED, Boolean.TRUE);
        }
        return state;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = this.getDefaultState();
        try {
            if (this instanceof IBlockFacingHorizontal) {
                EnumFacing facing = BlockStateUtils.getFacing(meta);
                if (facing == EnumFacing.DOWN || facing == EnumFacing.UP) {
                    facing = EnumFacing.NORTH;
                }
                state = state.withProperty(IBlockFacingHorizontal.FACING, facing);
            } else if (this instanceof IBlockFacing) {
                state = state.withProperty(IBlockFacing.FACING, BlockStateUtils.getFacing(meta));
            }
            if (this instanceof IBlockEnabled) {
                state = state.withProperty(IBlockEnabled.ENABLED, BlockStateUtils.isEnabled(meta));
            }
        } catch (Exception e) {
            ThaumicWonders.LOGGER.catching(e);
        }
        return state;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = 0;
        if (this instanceof IBlockFacingHorizontal) {
            meta = state.getValue(IBlockFacingHorizontal.FACING).getIndex();
        } else if (this instanceof IBlockFacing) {
            meta = state.getValue(IBlockFacing.FACING).getIndex();
        }
        if (this instanceof IBlockEnabled && !state.getValue(IBlockEnabled.ENABLED)) {
            meta |= 0x8;
        }
        return meta;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        ArrayList<IProperty<?>> properties = new ArrayList<>();
        if (this instanceof IBlockFacingHorizontal) {
            properties.add(IBlockFacingHorizontal.FACING);
        } else if (this instanceof IBlockFacing) {
            properties.add(IBlockFacing.FACING);
        }
        if (this instanceof IBlockEnabled) {
            properties.add(IBlockEnabled.ENABLED);
        }
        return properties.isEmpty() ?
            super.createBlockState() :
            new BlockStateContainer(this, properties.toArray(new IProperty<?>[0]));
    }
}
