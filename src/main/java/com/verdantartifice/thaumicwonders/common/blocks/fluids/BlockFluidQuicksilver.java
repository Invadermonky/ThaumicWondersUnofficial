package com.verdantartifice.thaumicwonders.common.blocks.fluids;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.fluids.FluidQuicksilver;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;

public class BlockFluidQuicksilver extends BlockFluidClassic {
    public static final Material FLUID_QUICKSILVER_MATERIAL = new MaterialLiquid(MapColor.SILVER);

    public BlockFluidQuicksilver() {
        super(FluidQuicksilver.INSTANCE, FLUID_QUICKSILVER_MATERIAL);
        this.setRegistryName(ThaumicWonders.MODID, "fluid_quicksilver");
        this.setTranslationKey(this.getRegistryName().toString());
        this.setCreativeTab(ThaumicWonders.CREATIVE_TAB);
        this.setQuantaPerBlock(4);
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (entityIn instanceof EntityLivingBase) {
            ((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.POISON, 100));
        }
        super.onEntityCollision(worldIn, pos, state, entityIn);
    }
}
