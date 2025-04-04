package com.verdantartifice.thaumicwonders.common.entities;

import com.verdantartifice.thaumicwonders.common.misc.FluxExplosion;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityFluxFireball extends EntityFireball {
    public EntityFluxFireball(World world) {
        super(world);
        this.setSize(1.0F, 1.0F);
    }

    public EntityFluxFireball(World world, double x, double y, double z, double accelX, double accelY, double accelZ) {
        super(world, x, y, z, accelX, accelY, accelZ);
        this.setSize(1.0F, 1.0F);
    }

    public EntityFluxFireball(World world, EntityLivingBase shooter, double accelX, double accelY, double accelZ) {
        super(world, shooter, accelX, accelY, accelZ);
        this.setSize(1.0F, 1.0F);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!this.world.isRemote) {
            if (result.entityHit != null) {
                if (this.shootingEntity != null) {
                    if (result.entityHit.attackEntityFrom(
                            DamageSource.causeMobDamage(this.shootingEntity),
                            (float) this.shootingEntity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue())) {
                        if (result.entityHit.isEntityAlive()) {
                            this.applyEnchantments(this.shootingEntity, result.entityHit);
                        }
                    }
                } else {
                    result.entityHit.attackEntityFrom(DamageSource.MAGIC, 5.0F);
                }
            }
            FluxExplosion.create(this.world, this, this.posX, this.posY, this.posZ, 1.0F, false, true, true);
            this.setDead();
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }
}
