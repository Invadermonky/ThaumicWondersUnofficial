package com.verdantartifice.thaumicwonders.common.blocks.devices;

import com.verdantartifice.thaumicwonders.common.blocks.base.BlockDeviceTW;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TileAlkahestVat;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.damagesource.DamageSourceThaumcraft;

import java.util.List;
import java.util.Random;

public class BlockAlkahestVat extends BlockDeviceTW<TileAlkahestVat> {
    protected static final AxisAlignedBB AABB_LEGS = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.3125D, 1.0D);
    protected static final AxisAlignedBB AABB_WALL_NORTH = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.125D);
    protected static final AxisAlignedBB AABB_WALL_SOUTH = new AxisAlignedBB(0.0D, 0.0D, 0.875D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB AABB_WALL_EAST = new AxisAlignedBB(0.875D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB AABB_WALL_WEST = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.125D, 1.0D, 1.0D);

    protected int delay = 0;

    public BlockAlkahestVat() {
        super(Material.IRON, TileAlkahestVat.class, "alkahest_vat");
        this.setSoundType(SoundType.METAL);
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return FULL_BLOCK_AABB;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_LEGS);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_WEST);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_NORTH);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_EAST);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_SOUTH);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (rand.nextInt(10) == 0) {
            worldIn.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS,
                    0.1F + (rand.nextFloat() * 0.1F), 1.2F + (rand.nextFloat() * 0.2F), false);
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote && facing == EnumFacing.UP) {
            ItemStack tempStack = playerIn.getHeldItem(hand).copy();
            tempStack.setCount(1);
            this.releaseVis(worldIn, pos, tempStack);
            playerIn.inventory.decrStackSize(playerIn.inventory.currentItem, 1);
            this.playHissSound(worldIn, pos);
        }
        return true;
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (!worldIn.isRemote) {
            if (entityIn instanceof EntityItem) {
                this.releaseVis(worldIn, pos, ((EntityItem) entityIn).getItem());
                entityIn.setDead();
                this.playHissSound(worldIn, pos);
            } else {
                this.delay++;
                if (this.delay >= 10) {
                    this.delay = 0;
                    if (entityIn instanceof EntityLivingBase) {
                        entityIn.attackEntityFrom(DamageSourceThaumcraft.dissolve, 1.0F);
                        this.playHissSound(worldIn, pos);
                    }
                }
            }
        }
        super.onEntityCollision(worldIn, pos, state, entityIn);
    }

    protected void releaseVis(World worldIn, BlockPos pos, ItemStack stack) {
        if (stack != null && !stack.isEmpty()) {
            ItemStack tempStack = stack.copy();
            tempStack.setCount(1);
            float toRelease = stack.getCount() * 0.01F * MathHelper.sqrt(AspectHelper.getObjectAspects(tempStack).visSize());
            if (toRelease > 0.0F) {
                AuraHelper.addVis(worldIn, pos, toRelease);
            }
        }
    }

    protected void playHissSound(World worldIn, BlockPos pos) {
        worldIn.playSound(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.4F, 2.0F + worldIn.rand.nextFloat() * 0.4F);
    }
}
