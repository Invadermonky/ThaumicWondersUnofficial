package com.verdantartifice.thaumicwonders.common.entities;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.verdantartifice.thaumicwonders.common.items.ItemsTW;
import com.verdantartifice.thaumicwonders.common.items.consumables.ItemPrimalArrow.PrimalArrowVariant;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.List;

public class EntityPrimalArrow extends EntityArrow {
    @SuppressWarnings("unchecked")
    private static final Predicate<Entity> ARROW_TARGETS = Predicates.and(EntitySelectors.NOT_SPECTATING, EntitySelectors.IS_ALIVE, Entity::canBeCollidedWith);

    private static final DataParameter<Integer> ARROW_TYPE = EntityDataManager.createKey(EntityPrimalArrow.class, DataSerializers.VARINT);

    private int knockbackStrength;
    private int ticksInAir = 0;
    private int ticksInGround = 0;
    private int xTile = -1;
    private int yTile = -1;
    private int zTile = -1;
    private Block inTile = Blocks.AIR;
    private int inData = 0;

    public EntityPrimalArrow(World worldIn) {
        super(worldIn);
        this.pickupStatus = PickupStatus.CREATIVE_ONLY;
    }

    public EntityPrimalArrow(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        this.pickupStatus = PickupStatus.CREATIVE_ONLY;
    }

    public EntityPrimalArrow(World worldIn, EntityLivingBase shooter) {
        super(worldIn, shooter);
        this.pickupStatus = PickupStatus.CREATIVE_ONLY;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(ARROW_TYPE, 0);
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        super.shoot(x, y, z, velocity, inaccuracy);
        this.ticksInGround = 0;
    }

    @Override
    public void setVelocity(double x, double y, double z) {
        super.setVelocity(x, y, z);
        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
            this.ticksInGround = 0;
        }
    }

    @Override
    public void onUpdate() {
        if (!this.world.isRemote) {
            this.setFlag(6, this.isGlowing());
        }

        this.onEntityUpdate();

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
            float hVelocity = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float) (MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));
            this.rotationPitch = (float) (MathHelper.atan2(this.motionY, hVelocity) * (180D / Math.PI));
            this.prevRotationYaw = this.rotationYaw;
            this.prevRotationPitch = this.rotationPitch;
        }

        BlockPos blockpos = new BlockPos(this.xTile, this.yTile, this.zTile);
        IBlockState iblockstate = this.world.getBlockState(blockpos);
        Block block = iblockstate.getBlock();

        if (iblockstate.getMaterial() != Material.AIR) {
            AxisAlignedBB axisalignedbb = iblockstate.getCollisionBoundingBox(this.world, blockpos);
            if (axisalignedbb != Block.NULL_AABB && axisalignedbb.offset(blockpos).contains(new Vec3d(this.posX, this.posY, this.posZ))) {
                this.inGround = true;
            }
        }

        if (this.arrowShake > 0) {
            --this.arrowShake;
        }

        if (this.inGround) {
            if ((block != this.inTile || block.getMetaFromState(iblockstate) != this.inData) && !this.world.collidesWithAnyBlock(this.getEntityBoundingBox().grow(0.05D))) {
                this.inGround = false;
                this.motionX *= (this.rand.nextFloat() * 0.2F);
                this.motionY *= (this.rand.nextFloat() * 0.2F);
                this.motionZ *= (this.rand.nextFloat() * 0.2F);
                this.ticksInGround = 0;
                this.ticksInAir = 0;
            } else {
                this.ticksInGround++;
                if (this.ticksInGround >= 1200) {
                    this.setDead();
                }
            }
            this.timeInGround++;
        } else {
            this.timeInGround = 0;
            this.ticksInAir++;
            Vec3d vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
            Vec3d vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            RayTraceResult raytraceresult = this.world.rayTraceBlocks(vec3d1, vec3d, false, true, false);
            vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
            vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

            if (raytraceresult != null) {
                vec3d = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);
            }

            Entity entity = this.findEntityOnPath(vec3d1, vec3d);

            if (entity != null) {
                raytraceresult = new RayTraceResult(entity);
            }

            if (raytraceresult != null && raytraceresult.entityHit instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer) raytraceresult.entityHit;
                if (this.shootingEntity instanceof EntityPlayer && !((EntityPlayer) this.shootingEntity).canAttackPlayer(entityplayer)) {
                    raytraceresult = null;
                }
            }

            if (raytraceresult != null && !ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
                this.onHit(raytraceresult);
            }

            if (this.getIsCritical()) {
                for (int k = 0; k < 4; ++k) {
                    this.world.spawnParticle(EnumParticleTypes.CRIT, this.posX + this.motionX * (double) k / 4.0D, this.posY + this.motionY * (double) k / 4.0D, this.posZ + this.motionZ * (double) k / 4.0D, -this.motionX, -this.motionY + 0.2D, -this.motionZ);
                }
            }

            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            float hVelocity = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float) (MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));

            //noinspection StatementWithEmptyBody
            for (this.rotationPitch = (float) (MathHelper.atan2(this.motionY, hVelocity) * (180D / Math.PI)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {

            }

            while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
                this.prevRotationPitch += 360.0F;
            }
            while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
                this.prevRotationYaw -= 360.0F;
            }
            while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
                this.prevRotationYaw += 360.0F;
            }

            this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
            this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
            float motionMultiplier = 0.99F;

            if (this.isInWater()) {
                for (int i = 0; i < 4; ++i) {
                    this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * 0.25D, this.posY - this.motionY * 0.25D, this.posZ - this.motionZ * 0.25D, this.motionX, this.motionY, this.motionZ);
                }
                PrimalArrowVariant variant = this.getArrowType();
                if (variant != PrimalArrowVariant.WATER && variant != PrimalArrowVariant.AIR) {
                    // Water arrows fly normally underwater
                    motionMultiplier = 0.6F;
                }
            }

            if (this.isWet()) {
                this.extinguish();
            }

            this.motionX *= motionMultiplier;
            this.motionY *= motionMultiplier;
            this.motionZ *= motionMultiplier;

            if (!this.hasNoGravity()) {
                this.motionY -= 0.05D;
            }

            this.setPosition(this.posX, this.posY, this.posZ);
            this.doBlockCollisions();
        }
    }

    @Override
    protected void onHit(RayTraceResult raytraceResultIn) {
        Entity entity = raytraceResultIn.entityHit;

        if (entity != null) {
            int fireDuration = this.getFireDuration();
            if (fireDuration > 0 && !(entity instanceof EntityEnderman)) {
                entity.setFire(fireDuration);
            }

            if (entity.attackEntityFrom(this.getDamageSource(), this.computeTotalDamage())) {
                if (entity instanceof EntityLivingBase) {
                    EntityLivingBase entitylivingbase = (EntityLivingBase) entity;
                    if (!this.world.isRemote) {
                        entitylivingbase.setArrowCountInEntity(entitylivingbase.getArrowCountInEntity() + 1);
                    }

                    if (this.knockbackStrength > 0) {
                        float hVelocity = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
                        if (hVelocity > 0.0F) {
                            entitylivingbase.addVelocity(this.motionX * (double) this.knockbackStrength * 0.6D / (double) hVelocity, 0.1D, this.motionZ * (double) this.knockbackStrength * 0.6D / (double) hVelocity);
                        }
                    }

                    if (this.shootingEntity instanceof EntityLivingBase) {
                        EnchantmentHelper.applyThornEnchantments(entitylivingbase, this.shootingEntity);
                        EnchantmentHelper.applyArthropodEnchantments((EntityLivingBase) this.shootingEntity, entitylivingbase);
                    }

                    this.arrowHit(entitylivingbase);

                    if (this.shootingEntity != null && entitylivingbase != this.shootingEntity && entitylivingbase instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayerMP) {
                        ((EntityPlayerMP) this.shootingEntity).connection.sendPacket(new SPacketChangeGameState(6, 0.0F));
                    }
                }

                this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));

                if (!(entity instanceof EntityEnderman)) {
                    this.setDead();
                }
            } else {
                this.motionX *= -0.1D;
                this.motionY *= -0.1D;
                this.motionZ *= -0.1D;
                this.rotationYaw += 180.0F;
                this.prevRotationYaw += 180.0F;
                this.ticksInAir = 0;

                if (!this.world.isRemote && this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ < 0.001D) {
                    if (this.pickupStatus == EntityArrow.PickupStatus.ALLOWED) {
                        this.entityDropItem(this.getArrowStack(), 0.1F);
                    }
                    this.setDead();
                }
            }
        } else {
            BlockPos blockpos = raytraceResultIn.getBlockPos();
            this.xTile = blockpos.getX();
            this.yTile = blockpos.getY();
            this.zTile = blockpos.getZ();
            IBlockState iblockstate = this.world.getBlockState(blockpos);
            this.inTile = iblockstate.getBlock();
            this.inData = this.inTile.getMetaFromState(iblockstate);
            this.motionX = ((float) (raytraceResultIn.hitVec.x - this.posX));
            this.motionY = ((float) (raytraceResultIn.hitVec.y - this.posY));
            this.motionZ = ((float) (raytraceResultIn.hitVec.z - this.posZ));
            float f2 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
            this.posX -= this.motionX / (double) f2 * 0.05D;
            this.posY -= this.motionY / (double) f2 * 0.05D;
            this.posZ -= this.motionZ / (double) f2 * 0.05D;
            this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
            this.inGround = true;
            this.arrowShake = 7;
            this.setIsCritical(false);

            if (iblockstate.getMaterial() != Material.AIR) {
                this.inTile.onEntityCollision(this.world, blockpos, iblockstate, this);
            }
        }
    }

    @Override
    public void move(MoverType type, double x, double y, double z) {
        super.move(type, x, y, z);
        if (this.inGround) {
            this.xTile = MathHelper.floor(this.posX);
            this.yTile = MathHelper.floor(this.posY);
            this.zTile = MathHelper.floor(this.posZ);
        }
    }

    @Override
    protected void arrowHit(EntityLivingBase living) {
        super.arrowHit(living);
        switch (this.getArrowType()) {
            case WATER:
                // Water arrows apply a slowing effect
                living.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200, 4));
                break;
            case ORDER:
                // Order arrows apply a weakening effect
                living.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 200, 4));
                break;
            case ENTROPY:
                // Entropy arrows apply a withering effect
                living.addPotionEffect(new PotionEffect(MobEffects.WITHER, 200, 2));
                break;
        }
    }

    @Override
    protected Entity findEntityOnPath(Vec3d start, Vec3d end) {
        Entity retVal = null;
        List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0D), ARROW_TARGETS);
        double minDistSq = 0.0D;

        for (Entity entity : list) {
            if (entity != this.shootingEntity || this.ticksInAir >= 5) {
                AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().grow(0.3D);
                RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(start, end);

                if (raytraceresult != null) {
                    double distSq = start.squareDistanceTo(raytraceresult.hitVec);
                    if (distSq < minDistSq || minDistSq == 0.0D) {
                        retVal = entity;
                        minDistSq = distSq;
                    }
                }
            }
        }

        return retVal;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("xTile", this.xTile);
        compound.setInteger("yTile", this.yTile);
        compound.setInteger("zTile", this.zTile);
        compound.setShort("life", (short) this.ticksInGround);
        ResourceLocation resourcelocation = Block.REGISTRY.getNameForObject(this.inTile);
        compound.setString("inTile", resourcelocation == null ? "" : resourcelocation.toString());
        compound.setByte("inData", (byte) this.inData);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.xTile = compound.getInteger("xTile");
        this.yTile = compound.getInteger("yTile");
        this.zTile = compound.getInteger("zTile");
        this.ticksInGround = compound.getShort("life");
        if (compound.hasKey("inTile", 8)) {
            this.inTile = Block.getBlockFromName(compound.getString("inTile"));
        } else {
            this.inTile = Block.getBlockById(compound.getByte("inTile") & 255);
        }
        this.inData = compound.getByte("inData") & 255;
    }

    @Override
    protected ItemStack getArrowStack() {
        return new ItemStack(ItemsTW.PRIMAL_ARROW, 1, this.getArrowType().ordinal());
    }

    @Override
    public void setKnockbackStrength(int knockbackStrength) {
        if (this.knockbackStrength < knockbackStrength) {
            this.knockbackStrength = knockbackStrength;
        }
    }

    public PrimalArrowVariant getArrowType() {
        return PrimalArrowVariant.values()[this.dataManager.get(ARROW_TYPE)];
    }

    public void setArrowType(PrimalArrowVariant type) {
        this.setArrowType(type.ordinal());
    }

    public void setArrowType(int type) {
        this.dataManager.set(ARROW_TYPE, type);
    }

    protected float computeTotalDamage() {
        float motionMagnitude = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
        int baseDamage = MathHelper.ceil((double) motionMagnitude * this.getDamage());
        if (this.getIsCritical()) {
            baseDamage += this.rand.nextInt(baseDamage / 2 + 2);
        }
        double retVal = baseDamage;
        switch (this.getArrowType()) {
            case FIRE:
                //Fire Arrows deal additional damage
                retVal *= 1.25;
                break;
            case ORDER:
            case ENTROPY:
                //Reduced Damage - Entropy/Order
                retVal *= 0.8;
                break;
        }
        return (float) retVal;
    }

    protected DamageSource getDamageSource() {
        Entity shooter = (this.shootingEntity == null) ? this : this.shootingEntity;
        DamageSource damageSource = new EntityDamageSourceIndirect("arrow", this, shooter);
        switch (this.getArrowType()) {
            case FIRE:
                // Fire Damage - Fire
                damageSource = damageSource.setProjectile().setFireDamage();
                break;
            case EARTH:
                // Ignore Armor - Earth
                damageSource = damageSource.setProjectile().setDamageBypassesArmor();
                break;
            case AIR:
            case WATER:
            case ORDER:
            case ENTROPY:
            default:
                // Default Damage - Air, water, order, and earth
                damageSource = damageSource.setProjectile();
        }
        return damageSource;
    }

    protected int getFireDuration() {
        int duration = 0;
        if (this.isBurning() && this.getArrowType() != PrimalArrowVariant.WATER) {
            // Water arrows can't light targets on fire, even if burning
            duration += 5;
        }
        if (this.getArrowType() == PrimalArrowVariant.FIRE) {
            // Fire arrows always light the target on fire, extending duration if the arrow is burning
            duration += 5;
        }
        return duration;
    }
}
