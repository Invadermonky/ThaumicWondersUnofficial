package com.verdantartifice.thaumicwonders.common.blocks.devices;

import com.verdantartifice.thaumicwonders.common.blocks.base.BlockDeviceTW;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TileVoidBeacon;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.lib.SoundsTC;

public class BlockVoidBeacon extends BlockDeviceTW<TileVoidBeacon> implements IBlockEnabled {
    public BlockVoidBeacon() {
        super(Material.GLASS, TileVoidBeacon.class, "void_beacon");
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileVoidBeacon) {
            TileVoidBeacon tileEntity = (TileVoidBeacon) te;
            if (playerIn.isSneaking() && playerIn.getHeldItem(hand).isEmpty()) {
                // Dump the beacon's contents
                if (!worldIn.isRemote) {
                    worldIn.playSound(null, pos, SoundsTC.jar, SoundCategory.BLOCKS, 0.4F, 1.0F);
                    worldIn.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 0.5F, 1.0F + (2F * worldIn.rand.nextFloat() - 1.0F) * 0.3F);
                    AuraHelper.polluteAura(worldIn, pos, tileEntity.getEssentiaAmount(), true);
                }
                tileEntity.clearEssentia();
            }
        }
        return true;
    }

    @Override
    protected void updateState(World worldIn, BlockPos pos, IBlockState state) {
        boolean flag = !worldIn.isBlockPowered(pos);
        if (flag != state.getValue(IBlockEnabled.ENABLED)) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileVoidBeacon) {
                if (flag) {
                    ((TileVoidBeacon) tile).playActivateSound();
                } else {
                    ((TileVoidBeacon) tile).playDeactivateSound();
                }
            }
            worldIn.setBlockState(pos, state.withProperty(IBlockEnabled.ENABLED, flag), 0x3);
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
    }
}
