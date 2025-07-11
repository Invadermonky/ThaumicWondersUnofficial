package com.verdantartifice.thaumicwonders.common.blocks.devices;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBlockCapacitor extends ItemBlock {
    public ItemBlockCapacitor(Block block) {
        super(block);
        this.addPropertyOverride(new ResourceLocation(ThaumicWonders.MODID, "charge"), ((stack, worldIn, entityIn) -> {
            int charge = stack.hasTagCompound() ? stack.getTagCompound().getShort("charge") : 0;
            if (charge <= 0) {
                return 0.0F;
            } else if (charge <= 3) {
                return 1.0F;
            } else if (charge <= 6) {
                return 2.0F;
            } else if (charge <= 9) {
                return 3.0F;
            } else {
                return 4.0F;
            }
        }));
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        boolean success = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
        if (success && !world.isRemote) {
            int charge = stack.hasTagCompound() ? stack.getTagCompound().getShort("charge") : 0;
            world.setBlockState(pos, newState.withProperty(BlockFluxCapacitor.CHARGE, charge));
        }
        return success;
    }
}
