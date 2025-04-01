package com.verdantartifice.thaumicwonders.common.blocks.devices;

import com.verdantartifice.thaumicwonders.common.blocks.BlocksTW;
import com.verdantartifice.thaumicwonders.common.blocks.base.BlockTW;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aura.AuraHelper;

import java.util.Random;

public class BlockFluxCapacitor extends BlockTW {
    public static final PropertyInteger CHARGE = PropertyInteger.create("charge", 0, 10);
    
    public BlockFluxCapacitor() {
        super(Material.ROCK, "flux_capacitor");
        this.setTickRandomly(true);
        this.setDefaultState(this.blockState.getBaseState().withProperty(CHARGE, 0));
    }
    
    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!worldIn.isRemote) {
            int charge = this.getMetaFromState(state);
            if (worldIn.isBlockPowered(pos)) {
                if (charge > 0) {
                    AuraHelper.polluteAura(worldIn, pos, 1.0F, true);
                    worldIn.setBlockState(pos, state.withProperty(CHARGE, charge - 1));
                    worldIn.scheduleUpdate(pos, state.getBlock(), 5);
                }
            } else {
                float flux = AuraHelper.getFlux(worldIn, pos);
                if (charge < 10 && flux >= 1.0F) {
                    AuraHelper.drainFlux(worldIn, pos, 1.0F, false);
                    worldIn.setBlockState(pos, state.withProperty(CHARGE, charge + 1));
                    worldIn.scheduleUpdate(pos, state.getBlock(), 100 + rand.nextInt(100));
                }
            }
        }
    }
    
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (worldIn.isBlockPowered(pos)) {
            worldIn.scheduleUpdate(pos, this, 1);
        }
    }
    
    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }
    
    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        return this.getMetaFromState(blockState);
    }
    
    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.getBlock().getMetaFromState(state);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public int getPackedLightmapCoords(IBlockState state, IBlockAccess source, BlockPos pos) {
        int i = source.getCombinedLight(pos, state.getLightValue(source, pos));
        int j = 180;
        int k = i & 0xFF;
        int l = j & 0xFF;
        int i1 = i >> 16 & 0xFF;
        int j1 = j >> 16 & 0xFF;
        return (Math.max(k, l)) | (Math.max(i1, j1)) << 16;
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, CHARGE);
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
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ItemStack drop = new ItemStack(this);
        if (!drop.hasTagCompound()) {
            drop.setTagCompound(new NBTTagCompound());
        }
        drop.getTagCompound().setInteger("charge", this.getMetaFromState(state));
        drops.add(drop);
    }

    public int getCharge(World worldIn, BlockPos pos) {
        IBlockState state = worldIn.getBlockState(pos);
        if (state.getBlock() == BlocksTW.FLUX_CAPACITOR) {
            return state.getBlock().getMetaFromState(state);
        } else {
            return 0;
        }
    }
    
    public void decrementCharge(World worldIn, BlockPos pos, int amount) {
        IBlockState state = worldIn.getBlockState(pos);
        if (state.getBlock() == BlocksTW.FLUX_CAPACITOR) {
            int charge = this.getMetaFromState(state);
            int newCharge = Math.max(0, charge - amount);
            worldIn.setBlockState(pos, state.withProperty(CHARGE, newCharge));
        }
    }
}
