package com.verdantartifice.thaumicwonders.common.blocks.devices;

import com.verdantartifice.thaumicwonders.common.blocks.base.BlockDeviceTW;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TilePortalGenerator;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.blocks.IBlockEnabled;

public class BlockPortalGenerator extends BlockDeviceTW<TilePortalGenerator> implements IBlockEnabled {
    public BlockPortalGenerator() {
        super(Material.IRON, TilePortalGenerator.class, "portal_generator");
        this.setSoundType(SoundType.METAL);
    }
    
    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (!worldIn.isRemote && worldIn.isAirBlock(pos.up(1)) && worldIn.isAirBlock(pos.up(2)) && worldIn.isAirBlock(pos.up(3))) { 
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TilePortalGenerator && stack.hasTagCompound()) {
                TilePortalGenerator generatorTile = (TilePortalGenerator)tile;
                generatorTile.setLink(
                        stack.getTagCompound().getInteger("linkX"), 
                        stack.getTagCompound().getInteger("linkY"), 
                        stack.getTagCompound().getInteger("linkZ"), 
                        stack.getTagCompound().getInteger("linkDim")
                );
                generatorTile.spawnPortal();
            }
        }
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }
    
    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        this.destroyPortal(worldIn, pos);
        super.onBlockHarvested(worldIn, pos, state, player);
    }
    
    @Override
    public void onBlockExploded(World worldIn, BlockPos pos, Explosion explosionIn) {
        this.destroyPortal(worldIn, pos);
        super.onBlockExploded(worldIn, pos, explosionIn);
    }
    
    private void destroyPortal(World worldIn, BlockPos pos) {
        if (!worldIn.isRemote) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TilePortalGenerator) {
                TilePortalGenerator generatorTile = (TilePortalGenerator)tile;
                generatorTile.despawnPortal(true);
            }
        }
    }
}
