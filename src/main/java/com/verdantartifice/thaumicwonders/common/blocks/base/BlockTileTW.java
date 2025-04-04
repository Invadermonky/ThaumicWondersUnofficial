package com.verdantartifice.thaumicwonders.common.blocks.base;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTileTW<T extends TileEntity> extends BlockTW implements ITileEntityProvider {
    protected static boolean keepInventory = false;
    protected final Class<T> tileClass;

    public BlockTileTW(Material mat, Class<T> tileClass, String name) {
        super(mat, name);
        setHardness(2.0f);
        setResistance(20.0f);
        this.tileClass = tileClass;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        if (this.tileClass == null) {
            return null;
        }
        try {
            return this.tileClass.newInstance();
        } catch (InstantiationException e) {
            ThaumicWonders.LOGGER.catching(e);
        } catch (IllegalAccessException e) {
            ThaumicWonders.LOGGER.catching(e);
        }
        return null;
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        worldIn.removeTileEntity(pos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
        super.eventReceived(state, worldIn, pos, id, param);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity != null && tileentity.receiveClientEvent(id, param);
    }

    @Override
    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return true;
    }

}
