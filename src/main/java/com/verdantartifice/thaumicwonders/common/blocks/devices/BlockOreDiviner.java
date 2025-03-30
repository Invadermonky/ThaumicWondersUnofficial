package com.verdantartifice.thaumicwonders.common.blocks.devices;

import com.verdantartifice.thaumicwonders.common.blocks.base.BlockDeviceTW;
import com.verdantartifice.thaumicwonders.common.misc.OreHelper;
import com.verdantartifice.thaumicwonders.common.network.PacketHandler;
import com.verdantartifice.thaumicwonders.common.network.packets.PacketOreDivinerSearch;
import com.verdantartifice.thaumicwonders.common.network.packets.PacketOreDivinerStop;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TileOreDiviner;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockOreDiviner extends BlockDeviceTW<TileOreDiviner> {
    public BlockOreDiviner() {
        super(Material.GLASS, TileOreDiviner.class, "ore_diviner");
        this.setSoundType(SoundType.GLASS);
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
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5625D, 1.0D);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = playerIn.getHeldItem(hand);
        if (!worldIn.isRemote && worldIn.getTileEntity(pos) instanceof TileOreDiviner) {
            TileOreDiviner tile = (TileOreDiviner)worldIn.getTileEntity(pos);
            if (stack == null || stack.isEmpty()) {
                tile.setSearchStack(ItemStack.EMPTY);
                PacketHandler.INSTANCE.sendToAll(new PacketOreDivinerStop(pos));
            } else if (OreHelper.isOreBlock(stack) && playerIn instanceof EntityPlayerMP) {
                tile.setSearchStack(stack);
                PacketHandler.INSTANCE.sendToAll(new PacketOreDivinerStop(pos));
                PacketHandler.INSTANCE.sendTo(new PacketOreDivinerSearch(pos, stack), (EntityPlayerMP)playerIn);
            }
        }
        return true;
    }
}
