package com.verdantartifice.thaumicwonders.common.entities;

import com.verdantartifice.thaumicwonders.common.misc.FluxExplosion;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityHexamitePrimed extends EntityTNTPrimed {
    public EntityHexamitePrimed(World worldIn) {
        super(worldIn);
    }

    public EntityHexamitePrimed(World worldIn, double x, double y, double z, EntityLivingBase igniter) {
        super(worldIn, x, y, z, igniter);
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (!this.hasNoGravity()) {
            this.motionY -= 0.04D;
        }

        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.98D;
        this.motionY *= 0.98D;
        this.motionZ *= 0.98D;

        if (this.onGround) {
            this.motionX *= 0.7D;
            this.motionZ *= 0.7D;
            this.motionY *= -0.5D;
        }

        this.setFuse(this.getFuse() - 1);

        if (this.getFuse() <= 0) {
            this.setDead();
            if (!this.world.isRemote) {
                this.explode();
            }
        } else {
            this.handleWaterMovement();
            this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);
        }
    }

    protected void explode() {
        FluxExplosion.create(this.world, this, this.posX, this.posY + (double) (this.height / 16.0F), this.posZ, 6.0F, true, true, false);
    }
}
