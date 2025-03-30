package com.verdantartifice.thaumicwonders.common.blocks.misc;

import com.verdantartifice.thaumicwonders.common.blocks.BlocksTW;
import com.verdantartifice.thaumicwonders.common.blocks.base.BlockTileTW;
import com.verdantartifice.thaumicwonders.common.blocks.devices.BlockPrimordialAccretionChamber;
import com.verdantartifice.thaumicwonders.common.tiles.misc.TilePrimordialAccretionChamberPlaceholder;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.blocks.BlocksTC;

import java.util.Random;

public class BlockPrimordialAccretionChamberPlaceholder extends BlockTileTW<TilePrimordialAccretionChamberPlaceholder> {
    public BlockPrimordialAccretionChamberPlaceholder(String name) {
        super(Material.IRON, TilePrimordialAccretionChamberPlaceholder.class, name);
        this.setSoundType(SoundType.METAL);
        this.setCreativeTab(null);
    }
    
    @Override
    protected boolean canSilkHarvest() {
        return false;
    }
    
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }
    
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        if (state.getBlock() == BlocksTW.PLACEHOLDER_THAUMIUM_BLOCK) {
            return Item.getItemFromBlock(BlocksTC.metalBlockThaumium);
        } else if (state.getBlock() == BlocksTW.PLACEHOLDER_VOID_METAL_BLOCK) {
            return Item.getItemFromBlock(BlocksTC.metalBlockVoid);
        } else if (state.getBlock() == BlocksTW.PLACEHOLDER_ADV_ALCH_CONSTRUCT) {
            return Item.getItemFromBlock(BlocksTC.metalAlchemicalAdvanced);
        } else {
            return Item.getItemById(0);
        }
    }
    
    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }
    
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if ((state.getBlock() == BlocksTW.PLACEHOLDER_THAUMIUM_BLOCK || state.getBlock() == BlocksTW.PLACEHOLDER_VOID_METAL_BLOCK || state.getBlock() == BlocksTW.PLACEHOLDER_ADV_ALCH_CONSTRUCT) && !BlockPrimordialAccretionChamber.ignoreDestroy && !worldIn.isRemote) {
            this.destroyAccretor(worldIn, pos);
        }
        super.breakBlock(worldIn, pos, state);
    }
    
    private void destroyAccretor(World worldIn, BlockPos pos) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    BlockPos targetPos = pos.add(i, j, k);
                    IBlockState targetState = worldIn.getBlockState(targetPos);
                    if (targetState.getBlock() == BlocksTW.PRIMORDIAL_ACCRETION_CHAMBER) {
                        BlockPrimordialAccretionChamber.destroyChamber(worldIn, targetPos, targetState, pos);
                        return;
                    }
                }
            }
        }
    }
}
