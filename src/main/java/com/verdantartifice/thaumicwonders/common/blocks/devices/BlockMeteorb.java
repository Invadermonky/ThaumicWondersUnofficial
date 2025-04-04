package com.verdantartifice.thaumicwonders.common.blocks.devices;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.blocks.base.BlockDeviceTW;
import com.verdantartifice.thaumicwonders.common.misc.GuiIds;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TileMeteorb;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import thaumcraft.common.blocks.IBlockFacingHorizontal;

public class BlockMeteorb extends BlockDeviceTW<TileMeteorb> implements IBlockFacingHorizontal {
    public BlockMeteorb() {
        super(Material.ROCK, TileMeteorb.class, "meteorb");
        this.setSoundType(SoundType.STONE);
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
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            if (worldIn.provider.isSurfaceWorld()) {
                playerIn.openGui(ThaumicWonders.INSTANCE, GuiIds.METEORB, worldIn, pos.getX(), pos.getY(), pos.getZ());
            } else {
                playerIn.sendStatusMessage(new TextComponentString(TextFormatting.DARK_PURPLE + I18n.format("event.meteorb.offworld")), true);
            }
        }
        return true;
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState bs = getDefaultState();
        bs = bs.withProperty(IBlockFacingHorizontal.FACING, placer.getHorizontalFacing().getOpposite());
        return bs;
    }
}
