package com.verdantartifice.thaumicwonders.common.misc;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import thaumcraft.api.aura.AuraHelper;

import javax.annotation.Nullable;
import java.util.List;

public class FluxExplosion extends Explosion {
    protected World world;
    protected boolean causesFlux;
    
    public static FluxExplosion create(World world, @Nullable Entity entity, double x, double y, double z, float strength, boolean isFlaming, boolean isSmoking, boolean causesFlux) {
        FluxExplosion explosion = new FluxExplosion(world, entity, x, y, z, strength, isFlaming, isSmoking, causesFlux);
        if (!ForgeEventFactory.onExplosionStart(world, explosion)) {
            explosion.doExplosionA();
            explosion.doExplosionB(true);
        }
        return explosion;
    }
    
    public FluxExplosion(World worldIn, Entity entityIn, double x, double y, double z, float size, List<BlockPos> affectedPositions) {
        super(worldIn, entityIn, x, y, z, size, affectedPositions);
        this.world = worldIn;
        this.causesFlux = false;
    }

    public FluxExplosion(World worldIn, Entity entityIn, double x, double y, double z, float size, boolean flaming, boolean damagesTerrain, boolean causesFlux) {
        super(worldIn, entityIn, x, y, z, size, flaming, damagesTerrain);
        this.world = worldIn;
        this.causesFlux = causesFlux;
    }

    public FluxExplosion(World worldIn, Entity entityIn, double x, double y, double z, float size, boolean causesFire, boolean damagesTerrain, boolean causesFlux, List<BlockPos> affectedPositions) {
        super(worldIn, entityIn, x, y, z, size, causesFire, damagesTerrain, affectedPositions);
        this.world = worldIn;
        this.causesFlux = causesFlux;
    }

    @Override
    public void doExplosionB(boolean spawnParticles) {
        // Generate flux from destroyed blocks before they're replaced by air in the super method
        for (BlockPos pos : this.getAffectedBlockPositions()) {
            IBlockState state = this.world.getBlockState(pos);
            if (this.causesFlux && state.getMaterial() != Material.AIR) {
                AuraHelper.polluteAura(this.world, pos, 1.0F, true);
            }
            if (state.getMaterial() == Material.AIR) {
                AuraHelper.drainVis(this.world, pos, 1.0F, false);
            }
        }
        super.doExplosionB(spawnParticles);
    }
}
