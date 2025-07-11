package com.verdantartifice.thaumicwonders.common.blocks.devices;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.blocks.base.BlockDeviceTW;
import com.verdantartifice.thaumicwonders.common.misc.GuiIds;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TilePortalAnchor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.casters.IInteractWithCaster;
import thaumcraft.common.items.casters.ItemCaster;

import java.util.List;

public class BlockPortalAnchor extends BlockDeviceTW<TilePortalAnchor> implements IInteractWithCaster {
    public static AxisAlignedBB AABB_PLATFORM = new AxisAlignedBB(0, 0, 0, 1.0, 0.125, 1.0);

    public BlockPortalAnchor() {
        super(Material.IRON, TilePortalAnchor.class, "portal_anchor");
        this.setSoundType(SoundType.METAL);
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABB_PLATFORM;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_PLATFORM);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            ItemStack stack = playerIn.getHeldItemMainhand();
            if (!(stack.getItem() instanceof ItemCaster)) {
                playerIn.openGui(ThaumicWonders.INSTANCE, GuiIds.PORTAL_ANCHOR, worldIn, pos.getX(), pos.getY(), pos.getZ());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onCasterRightClick(World world, ItemStack itemStack, EntityPlayer entityPlayer, BlockPos blockPos, EnumFacing enumFacing, EnumHand enumHand) {
        TileEntity tile = world.getTileEntity(blockPos);
        if (tile instanceof TilePortalAnchor) {
            return ((TilePortalAnchor) tile).onCasterRightClick(world, itemStack, entityPlayer, blockPos, enumFacing, enumHand);
        }
        return false;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TilePortalAnchor) {
            ((TilePortalAnchor) tile).onBlockBreak();
        }
        super.breakBlock(worldIn, pos, state);
    }
}
