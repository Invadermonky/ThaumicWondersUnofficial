package com.verdantartifice.thaumicwonders.common.blocks.devices;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.blocks.base.BlockTileTW;
import com.verdantartifice.thaumicwonders.common.misc.GuiIds;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TilePrimordialSiphon;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockPrimordialSiphon extends BlockTileTW<TilePrimordialSiphon> {
    protected static final AxisAlignedBB AABB_MAIN = new AxisAlignedBB(0.1875F, 0.0F, 0.1875F, 0.8125F, 1.0F, 0.8125F);
    protected static final AxisAlignedBB AABB_BASE = new AxisAlignedBB(0.1875F, 0.0F, 0.1875F, 0.8125F, 0.125F, 0.8125F);
    protected static final AxisAlignedBB AABB_TOP = new AxisAlignedBB(0.25F, 0.125F, 0.25F, 0.75F, 0.6875F, 0.75F);
    protected static final AxisAlignedBB AABB_ORB = new AxisAlignedBB(0.3125F, 0.75F, 0.3125F, 0.625F, 1.0F, 0.625F);

    public BlockPrimordialSiphon() {
        super(Material.IRON, TilePrimordialSiphon.class, "primordial_siphon");
        this.setSoundType(SoundType.METAL);
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABB_MAIN;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_BASE);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_TOP);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_ORB);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            playerIn.openGui(ThaumicWonders.INSTANCE, GuiIds.PRIMORDIAL_SYPHON, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }
}
