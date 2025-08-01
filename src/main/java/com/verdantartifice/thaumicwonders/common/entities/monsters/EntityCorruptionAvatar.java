package com.verdantartifice.thaumicwonders.common.entities.monsters;

import com.google.common.base.Predicate;
import com.verdantartifice.thaumicwonders.common.config.ConfigHandlerTW;
import com.verdantartifice.thaumicwonders.common.entities.EntityFluxFireball;
import com.verdantartifice.thaumicwonders.common.entities.LootTablesTW;
import com.verdantartifice.thaumicwonders.common.items.ItemsTW;
import com.verdantartifice.thaumicwonders.common.misc.FluxExplosion;
import com.verdantartifice.thaumicwonders.common.network.PacketHandler;
import com.verdantartifice.thaumicwonders.common.network.packets.PacketAvatarZapFx;
import com.verdantartifice.thaumicwonders.common.network.packets.PacketLocalizedMessage;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.entities.ai.combat.AILongRangeAttack;
import thaumcraft.common.entities.monster.boss.EntityThaumcraftBoss;
import thaumcraft.common.entities.monster.tainted.EntityTaintSeed;
import thaumcraft.common.entities.monster.tainted.EntityTaintSeedPrime;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.potions.PotionInfectiousVisExhaust;
import thaumcraft.common.lib.utils.EntityUtils;

import java.util.List;

public class EntityCorruptionAvatar extends EntityThaumcraftBoss implements IRangedAttackMob, IEldritchMob, ITaintedMob {
    protected static final Predicate<Entity> NOT_HORROR = input -> !(input instanceof IEldritchMob) && !(input instanceof ITaintedMob);
    protected int seedCooldown = 0;
    protected boolean isSuffocating = false;

    //TODO: Give Avatar a death animation. (See about reusing dragon death animation)
    //  https://github.com/Lykrast/DefiledLands/blob/f4909ea8aeb14a7b460a7a0239110ec01db0850b/src/main/java/lykrast/defiledlands/common/entity/boss/EntityDestroyer.java#L217
    //  https://github.com/Lykrast/DefiledLands/blob/master/src/main/java/lykrast/defiledlands/client/render/entity/RenderDestroyer.java
    //  https://github.com/Lykrast/DefiledLands/blob/master/src/main/resources/assets/defiledlands/textures/entity/the_destroyer_explosion.png

    public EntityCorruptionAvatar(World world) {
        super(world);
        this.setSize(0.75F, 2.25F);
        this.experienceValue = 200;
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new AILongRangeAttack(this, 4.0D, 0.8D, 30, 40, 24.0F));
        this.tasks.addTask(3, new EntityAIAttackMelee(this, 0.8D, false));
        this.tasks.addTask(6, new EntityAIMoveTowardsRestriction(this, 0.8D));
        this.tasks.addTask(7, new EntityAIWander(this, 0.8D));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(9, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityLiving.class, 0, false, false, NOT_HORROR));
    }

    @Override
    public boolean canAttackClass(Class<? extends EntityLivingBase> cls) {
        return !IEldritchMob.class.isAssignableFrom(cls) && !ITaintedMob.class.isAssignableFrom(cls) && super.canAttackClass(cls);
    }

    @Override
    public int getTalkInterval() {
        return 500;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundsTC.egidle;
    }

    @Override
    protected @Nullable ResourceLocation getLootTable() {
        return LootTablesTW.CORRUPTION_AVATAR;
    }

    @Override
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(ItemsTW.VOIDCALLER_HELM));
        this.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(ItemsTW.VOIDCALLER_CHEST));
        this.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(ItemsTW.VOIDCALLER_LEGS));
        this.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(ItemsTC.voidBoots));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.32);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(ConfigHandlerTW.avatar_of_corruption.entityHealth);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(8.0);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(2.0);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(0);
    }

    @Override
    protected void updateAITasks() {
        if (!this.world.isRemote) {
            // Generate flux, 4/sec
            int timer = ConfigHandlerTW.avatar_of_corruption.pollutionTimer;
            if (timer != 0 && this.ticksExisted % timer == 0) {
                AuraHelper.polluteAura(this.world, this.getPosition().up(), 1.0F, true);
            }

            // Regenerate based on local flux
            if (this.ticksExisted % 40 == 0) {
                float flux = Math.min(100.0F, AuraHelper.getFlux(this.world, this.getPosition()));
                int amp = Math.min((int) (0.5F * MathHelper.sqrt(flux)), ConfigHandlerTW.avatar_of_corruption.maxRegenAmplifier);
                this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 40, amp, false, false));
            }

            // Generate flux phage and taint poison aura
            if (this.ticksExisted % 20 == 0) {
                List<EntityLivingBase> livingNearby = EntityUtils.getEntitiesInRange(this.world, this.getPosition(), this, EntityLivingBase.class, 8.0D);
                for (EntityLivingBase creature : livingNearby) {
                    creature.addPotionEffect(new PotionEffect(PotionInfectiousVisExhaust.instance, 100, 3));
                    creature.addPotionEffect(new PotionEffect(PotionFluxTaint.instance, 100, 0));
                }
            }

            // Spawn taint seeds
            timer = ConfigHandlerTW.avatar_of_corruption.taintSeedTimer;
            if (timer != 0 && ++this.seedCooldown >= timer) {
                EnumDifficulty diff = this.world.getDifficulty();
                int maxSeeds = diff == EnumDifficulty.EASY ? 1 : (diff == EnumDifficulty.HARD ? 4 : 2);
                List<EntityTaintSeed> seedsNearby = EntityUtils.getEntitiesInRange(this.world, this.getPosition(), this, EntityTaintSeed.class, 16.0);
                if (seedsNearby.size() < maxSeeds) {
                    int primeThreshold = diff == EnumDifficulty.EASY ? -1 : (diff == EnumDifficulty.HARD ? 1 : 0);
                    int boost = diff == EnumDifficulty.EASY ? 50 : (diff == EnumDifficulty.HARD ? 200 : 100);
                    EntityTaintSeed seed = this.rand.nextInt(10) <= primeThreshold ?
                            new EntityTaintSeedPrime(this.world) :
                            new EntityTaintSeed(this.world);
                    seed.boost = boost;
                    seed.setLocationAndAngles(
                            (int) (this.posX + this.rand.nextGaussian() * 5.0D) + 0.5D,
                            (int) (this.posY + this.rand.nextGaussian() * 5.0D),
                            (int) (this.posZ + this.rand.nextGaussian() * 5.0D) + 0.5D,
                            this.rand.nextInt(360), 0.0F);
                    if (diff != EnumDifficulty.PEACEFUL && seed.isNotColliding() && this.world.spawnEntity(seed)) {
                        this.getLookHelper().setLookPositionWithEntity(seed, this.getHorizontalFaceSpeed(), this.getVerticalFaceSpeed());
                        PacketHandler.INSTANCE.sendToAllAround(
                                new PacketAvatarZapFx(this.getEntityId(), seed.getEntityId()),
                                new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), this.posX, this.posY, this.posZ, 32.0D));
                        this.playSound(SoundsTC.zap, 1.0F, 1.0F);
                        this.seedCooldown = 0;
                    }
                }
            }

            // Empower if near a rift
            if (this.ticksExisted % 200 == 0) {
                List<EntityFluxRift> riftsNearby = EntityUtils.getEntitiesInRange(this.world, this.getPosition(), this, EntityFluxRift.class, 16.0D);
                int riftCount = riftsNearby.size();
                if (riftCount > 0) {
                    this.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 200, 3 + riftCount));
                    this.addPotionEffect(new PotionEffect(MobEffects.HASTE, 200, riftCount));
                    for (EntityFluxRift rift : riftsNearby) {
                        PacketHandler.INSTANCE.sendToAllAround(
                                new PacketAvatarZapFx(rift.getEntityId(), this.getEntityId()),
                                new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), rift.posX, rift.posY, rift.posZ, 32.0D));
                        rift.playSound(SoundsTC.zap, 1.0F, 1.0F);
                    }
                    PacketHandler.INSTANCE.sendToAllAround(
                            new PacketLocalizedMessage("event.corruption_avatar.empower"),
                            new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), this.posX, this.posY, this.posZ, 32.0D));
                }
            }

            // Explode if suffocating
            if (this.isSuffocating && this.ticksExisted % 20 == 0) {
                FluxExplosion.create(this.world, this, this.posX, this.posY, this.posZ, 7.0F, false, true, true);
                this.isSuffocating = false;
            }
        }
        super.updateAITasks();
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance diff, IEntityLivingData data) {
        this.setEquipmentBasedOnDifficulty(diff);
        this.setEnchantmentBasedOnDifficulty(diff);
        return super.onInitialSpawn(diff, data);
    }

    @Override
    public boolean isOnSameTeam(Entity el) {
        return (el instanceof IEldritchMob) || (el instanceof ITaintedMob) || super.isOnSameTeam(el);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source == DamageSource.DROWN || source.getTrueSource() instanceof EntityCorruptionAvatar) {
            return false;
        } else {
            if (source == DamageSource.IN_WALL) {
                this.isSuffocating = true;
            }
            return super.attackEntityFrom(source, amount);
        }
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
        if (this.canEntityBeSeen(target)) {
            this.swingArm(this.getActiveHand());
            this.getLookHelper().setLookPosition(target.posX, target.getEntityBoundingBox().minY + target.height / 2.0F, target.posZ, 30.0F, 30.0F);

            double sourceY = this.posY + this.height / 2.0F;
            double deltaX = target.posX - this.posX;
            double deltaY = target.getEntityBoundingBox().minY + target.height / 2.0F - sourceY;
            double deltaZ = target.posZ - this.posZ;

            EntityFluxFireball fireball = new EntityFluxFireball(this.world, this, deltaX, deltaY, deltaZ);
            fireball.posX = this.posX;
            fireball.posY = sourceY;
            fireball.posZ = this.posZ;

            this.playSound(SoundsTC.egattack, 1.0F, 1.0F + this.rand.nextFloat() * 0.1F);
            this.world.spawnEntity(fireball);
        }
    }

    @Override
    public void setSwingingArms(boolean swingingArms) {
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundsTC.egdeath;
    }
}
