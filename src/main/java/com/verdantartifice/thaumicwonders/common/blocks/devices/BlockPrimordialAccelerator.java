package com.verdantartifice.thaumicwonders.common.blocks.devices;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.blocks.base.BlockDeviceTW;
import com.verdantartifice.thaumicwonders.common.misc.GuiIds;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TilePrimordialAccelerator;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.IBlockFacingHorizontal;

public class BlockPrimordialAccelerator extends BlockDeviceTW<TilePrimordialAccelerator> implements IBlockEnabled, IBlockFacingHorizontal {
    public BlockPrimordialAccelerator() {
        super(Material.IRON, TilePrimordialAccelerator.class, "primordial_accelerator");
        this.setSoundType(SoundType.METAL);
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        EnumFacing blockFacing = state.getValue(IBlockFacingHorizontal.FACING);
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
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            playerIn.openGui(ThaumicWonders.INSTANCE, GuiIds.PRIMORDIAL_ACCELERATOR, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TilePrimordialAccelerator) {
            ((TilePrimordialAccelerator) tile).dropInventoryContents();
        }
        super.breakBlock(worldIn, pos, state);
    }
}
