package com.verdantartifice.thaumicwonders.common.blocks.devices;

import com.verdantartifice.thaumicwonders.common.blocks.base.BlockDeviceTW;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TileVisCapacitor;
import com.verdantartifice.thaumicwonders.common.utils.StringHelper;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockVisCapacitor extends BlockDeviceTW<TileVisCapacitor> {
    public static final PropertyInteger CHARGE = BlockFluxCapacitor.CHARGE;

    public BlockVisCapacitor() {
        super(Material.ROCK, TileVisCapacitor.class, "vis_capacitor");
        this.setDefaultState(this.blockState.getBaseState().withProperty(CHARGE, 0));
    }

    public int getMaxCharge() {
        return 10;
    }

    @Override
    public void updateState(World worldIn, BlockPos pos, IBlockState state) {
        int charge = getChargeValue(worldIn.getTileEntity(pos));
        if (state.getValue(CHARGE) != charge) {
            worldIn.setBlockState(pos, state.withProperty(CHARGE, charge));
        }
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

    public int getChargeValue(@Nullable TileEntity tile) {
        int charge = 0;
        if (tile instanceof TileVisCapacitor) {
            TileVisCapacitor visCap = (TileVisCapacitor) tile;
            int stored = visCap.getStoredVis();
            int capacity = visCap.getVisCapacity();
            charge = (int) (((double) stored / (double) capacity) * (double) this.getMaxCharge());
            if (charge == 0 && stored > 0)
                charge = 1;
        }
        return charge;
    }

    @Override
    public int getLightValue(IBlockState state) {
        return this.getMetaFromState(state);
    }

    @Override
    public int getPackedLightmapCoords(IBlockState state, IBlockAccess source, BlockPos pos) {
        int i = source.getCombinedLight(pos, state.getLightValue(source, pos));
        int j = 180;
        int k = i & 255;
        int l = j & 255;
        int i1 = i >> 16 & 255;
        int j1 = j >> 16 & 255;
        return (k > l ? k : l) | (i1 > j1 ? i1 : j1) << 16;
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
        ItemStack drop = new ItemStack(this);
        NBTTagCompound tag = new NBTTagCompound();
        if (te instanceof TileVisCapacitor) {
            tag.setTag("BlockEntityTag", te.writeToNBT(new NBTTagCompound()));
        }
        tag.setShort("charge", (short) this.getMetaFromState(state));
        drop.setTagCompound(tag);
        spawnAsEntity(worldIn, pos, drop);
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
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        int storedVis = 0;
        if (stack.hasTagCompound()) {
            storedVis = stack.getTagCompound().getCompoundTag("BlockEntityTag").getInteger(TileVisCapacitor.TAG_STORED_VIS);
        }
        tooltip.add(TextFormatting.YELLOW + I18n.format(StringHelper.getTranslationKey("vis_capacitor", "tooltip", "vis"), storedVis));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean shouldCheckWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }
}
