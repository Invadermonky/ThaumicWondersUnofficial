package com.verdantartifice.thaumicwonders.common.blocks.plants;

import com.verdantartifice.thaumicwonders.common.items.ItemsTW;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.EnumPlantType;
import thaumcraft.api.blocks.BlocksTC;

public class BlockCinderpearlCrop extends AbstractBlockMysticCrop {
    public BlockCinderpearlCrop() {
        super("cinderpearl_crop");
    }

    @Override
    protected IBlockState getMatureBlockState() {
        return BlocksTC.cinderpearl.getDefaultState();
    }

    @Override
    protected Item getSeed() {
        return ItemsTW.CINDERPEARL_SEED;
    }
    
    @Override
    protected boolean canSustainBush(IBlockState state) {
        return (state.getBlock() == Blocks.SAND) || (state.getBlock() == Blocks.DIRT) || 
                (state.getBlock() == Blocks.STAINED_HARDENED_CLAY) || (state.getBlock() == Blocks.HARDENED_CLAY);
    }
    
    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return EnumPlantType.Desert;
    }
    
    @Override
    public EnumOffsetType getOffsetType() {
        return EnumOffsetType.XZ;
    }
}
