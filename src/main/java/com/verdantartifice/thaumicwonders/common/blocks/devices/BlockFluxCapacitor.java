package com.verdantartifice.thaumicwonders.common.blocks.devices;

import com.verdantartifice.thaumicwonders.common.blocks.BlocksTW;
import com.verdantartifice.thaumicwonders.common.blocks.base.BlockTW;
import com.verdantartifice.thaumicwonders.common.utils.StringHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aura.AuraHelper;

import java.util.List;
import java.util.Random;

public class BlockFluxCapacitor extends BlockTW {
    public static final PropertyInteger CHARGE = PropertyInteger.create("charge", 0, 10);

    public BlockFluxCapacitor() {
        super(Material.ROCK, "flux_capacitor");
        this.setTickRandomly(true);
        this.setDefaultState(this.blockState.getBaseState().withProperty(CHARGE, 0));
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
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, CHARGE);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        int charge = stack.hasTagCompound() ? stack.getTagCompound().getInteger("charge") : 0;
        tooltip.add(TextFormatting.YELLOW + I18n.format(StringHelper.getTranslationKey("flux_capacitor", "tooltip", "flux"), charge));
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.getBlock().getMetaFromState(state);
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
