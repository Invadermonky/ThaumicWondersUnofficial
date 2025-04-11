package com.verdantartifice.thaumicwonders.common.blocks.devices;

import com.google.common.collect.Lists;
import com.verdantartifice.thaumicwonders.common.blocks.base.BlockDeviceTW;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TileFluxDistiller;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class BlockFluxDistiller extends BlockDeviceTW<TileFluxDistiller> {
    public static final PropertyInteger CHARGE = PropertyInteger.create("charge", 0, 10);
    private static final List<AxisAlignedBB> BLOCK_AABB = Lists.newArrayList(
            new AxisAlignedBB(0.3125, 0.4375, 0.3125, 0.6875, 1.0, 0.6875),
            new AxisAlignedBB(0.0625, 0, 0.0625, 0.9375, 0.4375, 0.9375)
    );

    public BlockFluxDistiller() {
        super(Material.IRON, TileFluxDistiller.class, "flux_distiller");
        this.setSoundType(SoundType.METAL);
        this.setDefaultState(this.blockState.getBaseState().withProperty(CHARGE, 0));

    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.0625, 0, 0.0625, 0.9375, 1.0, 0.9375);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, BLOCK_AABB.get(0));
        addCollisionBoxToList(pos, entityBox, collidingBoxes, BLOCK_AABB.get(1));
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public @Nullable RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
        return BLOCK_AABB.stream().map(bb -> rayTrace(pos, start, end, bb)).anyMatch(Objects::nonNull) ? super.collisionRayTrace(blockState, worldIn, pos, start, end) : null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {
        ItemStack drop = new ItemStack(this);
        if (!drop.hasTagCompound()) {
            drop.setTagCompound(new NBTTagCompound());
        }
        drop.getTagCompound().setInteger("charge", this.getMetaFromState(state));
        spawnAsEntity(worldIn, pos, drop);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(CHARGE, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(CHARGE);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, CHARGE);
    }
}
