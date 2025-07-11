package com.verdantartifice.thaumicwonders.common.blocks.devices;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBlockFluxDistiller extends ItemBlock {
    public ItemBlockFluxDistiller(Block block) {
        super(block);
        this.addPropertyOverride(new ResourceLocation(ThaumicWonders.MODID, "flux"), new IItemPropertyGetter() {
            @Override
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
                return (float) ItemBlockFluxDistiller.this.getCharge(stack);
            }
        });
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        int charge = this.getCharge(stack);
        tooltip.add(TextFormatting.YELLOW + I18n.format("tooltip.thaumicwonders:flux_distiller.flux", charge));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        boolean success = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
        if (success && !world.isRemote) {
            int charge = this.getCharge(stack);
            world.setBlockState(pos, newState.withProperty(BlockFluxDistiller.CHARGE, charge));
        }
        return success;
    }

    protected int getCharge(ItemStack stack) {
        return stack.hasTagCompound() ? stack.getTagCompound().getInteger("charge") : 0;
    }
}
