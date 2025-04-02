package com.verdantartifice.thaumicwonders.common.blocks.devices;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.blocks.BlocksTW;
import com.verdantartifice.thaumicwonders.common.blocks.base.BlockDeviceTW;
import com.verdantartifice.thaumicwonders.common.misc.GuiIds;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TileCatalyzationChamber;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.lib.utils.BlockStateUtils;

import java.util.Random;

public class BlockCatalyzationChamber extends BlockDeviceTW<TileCatalyzationChamber> implements IBlockFacingHorizontal {
    public static boolean ignoreDestroy = false;
    
    public BlockCatalyzationChamber() {
        super(Material.ROCK, TileCatalyzationChamber.class, "catalyzation_chamber");
        this.setSoundType(SoundType.STONE);
        this.setLightLevel(0.9F);
        this.setCreativeTab(null);
    }
    
    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        return false;
    }
    
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
    }
    
    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {}

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState bs = getDefaultState();
        bs = bs.withProperty(IBlockFacingHorizontal.FACING, placer.getHorizontalFacing().getOpposite());
        return bs;
    }
    
    public static void destroyChamber(World world, BlockPos pos, IBlockState state, BlockPos startPos) {
        if (ignoreDestroy || world.isRemote) {
            return;
        }
        ignoreDestroy = true;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    BlockPos blockPos = pos.add(i, j, k);
                    if (blockPos != startPos) {
                        IBlockState bs = world.getBlockState(blockPos);
                        if (bs.getBlock() == BlocksTW.PLACEHOLDER_ARCANE_STONE) {
                            world.setBlockState(blockPos, BlocksTC.stoneArcane.getDefaultState());
                        } else if (bs.getBlock() == BlocksTW.PLACEHOLDER_OBSIDIAN) {
                            world.setBlockState(blockPos, Blocks.OBSIDIAN.getDefaultState());
                        }
                    }
                }
            }
        }
        if (world.isAirBlock(pos.offset(BlockStateUtils.getFacing(state).getOpposite()))) {
            world.setBlockState(pos.offset(BlockStateUtils.getFacing(state).getOpposite()), Blocks.IRON_BARS.getDefaultState());
        }
        world.setBlockState(pos, BlocksTW.FLUID_QUICKSILVER.getDefaultState());
        ignoreDestroy = false;
    }
    
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemById(0);
    }
    
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if(tile instanceof TileCatalyzationChamber)
            ((TileCatalyzationChamber) tile).dropInventoryContents();
        destroyChamber(worldIn, pos, state, pos);
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (entityIn.posX < pos.getX() + 0.3F) {
            entityIn.motionX += 0.0001D;
        }
        if (entityIn.posX > pos.getX() + 0.7F) {
            entityIn.motionX -= 0.0001D;
        }
        if (entityIn.posZ < pos.getZ() + 0.3F) {
            entityIn.motionZ += 0.0001D;
        }
        if (entityIn.posZ > pos.getZ() + 0.7F) {
            entityIn.motionZ -= 0.0001D;
        }
        if (!worldIn.isRemote && (entityIn.ticksExisted % 10 == 0)) {
            if (entityIn instanceof EntityItem) {
                entityIn.motionY = 0.025D;
                if (entityIn.onGround && !entityIn.isDead) {
                    TileCatalyzationChamber tcc = (TileCatalyzationChamber)worldIn.getTileEntity(pos);
                    ItemStack remainder = tcc.addItemsToInventory(((EntityItem)entityIn).getItem());
                    if(remainder != null && !remainder.isEmpty()) {
                        ((EntityItem)entityIn).setItem(remainder);
                    } else {
                        entityIn.setDead();
                    }
                }
            } else if (entityIn instanceof EntityLivingBase) {
                ((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(MobEffects.POISON, 100));
            }
        }
        super.onEntityCollision(worldIn, pos, state, entityIn);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            playerIn.openGui(ThaumicWonders.INSTANCE, GuiIds.CATALYZATION_CHAMBER, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }
}
