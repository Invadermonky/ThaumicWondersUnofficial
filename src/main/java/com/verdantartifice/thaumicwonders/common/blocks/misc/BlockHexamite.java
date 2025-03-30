package com.verdantartifice.thaumicwonders.common.blocks.misc;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.entities.EntityHexamitePrimed;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockHexamite extends BlockTNT {
    public BlockHexamite() {
        super();
        this.setRegistryName(ThaumicWonders.MODID, "hexamite");
        this.setTranslationKey(this.getRegistryName().toString());
        this.setCreativeTab(ThaumicWonders.CREATIVE_TAB);
    }

    @Override
    public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn) {
        if (!worldIn.isRemote) {
            EntityHexamitePrimed entityPrimed = new EntityHexamitePrimed(worldIn, ((float)pos.getX() + 0.5F), pos.getY(), ((float)pos.getZ() + 0.5F), explosionIn.getExplosivePlacedBy());
            entityPrimed.setFuse((short)(worldIn.rand.nextInt(entityPrimed.getFuse() / 4) + entityPrimed.getFuse() / 8));
            worldIn.spawnEntity(entityPrimed);
        }
    }

    @Override
    public void explode(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase igniter) {
        if (!worldIn.isRemote) {
            if (state.getValue(EXPLODE)) {
                EntityHexamitePrimed entityPrimed = new EntityHexamitePrimed(worldIn, ((float)pos.getX() + 0.5F), pos.getY(), ((float)pos.getZ() + 0.5F), igniter);
                worldIn.spawnEntity(entityPrimed);
                worldIn.playSound(null, entityPrimed.posX, entityPrimed.posY, entityPrimed.posZ, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        }
    }
}
