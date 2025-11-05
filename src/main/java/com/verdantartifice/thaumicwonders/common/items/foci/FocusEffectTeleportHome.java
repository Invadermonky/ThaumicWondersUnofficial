package com.verdantartifice.thaumicwonders.common.items.foci;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXFocusPartImpact;

public class FocusEffectTeleportHome extends FocusEffect {
    private int bedDimension;
    private BlockPos bedPosition;

    @Override
    public Aspect getAspect() {
        return Aspect.MOTION;
    }

    @Override
    public String getKey() {
        return "focus." + ThaumicWonders.MODID + ".teleport_home";
    }

    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[]{};
    }

    @Override
    public int getComplexity() {
        return this.getSettingValue("knockback") * 3;
    }

    @Override
    public float getDamageForDisplay(float finalPower) {
        return 0.0F;
    }

    @Override
    public String getResearch() {
        return "TWOND_FOCUS_TELEPORT_HOME";
    }

    @Override
    public boolean execute(RayTraceResult trace, Trajectory trajectory, float finalPower, int num) {
        PacketHandler.INSTANCE.sendToAllAround(new PacketFXFocusPartImpact(trace.hitVec.x, trace.hitVec.y, trace.hitVec.z, new String[]{this.getKey()}), new NetworkRegistry.TargetPoint(this.getPackage().world.provider.getDimension(), trace.hitVec.x, trace.hitVec.y, trace.hitVec.z, 64.0D));
        this.getPackage().world.playSound(null, trace.hitVec.x, trace.hitVec.y, trace.hitVec.z, SoundsTC.hhon, SoundCategory.PLAYERS, 0.8F, 0.85F + (float) (this.getPackage().getCaster().world.rand.nextGaussian() * 0.05F));
        if (trace != null && trace.typeOfHit == RayTraceResult.Type.ENTITY && trace.entityHit instanceof EntityPlayer && !trace.entityHit.world.isRemote) {
            BlockPos pos = this.getAndSetBedPosition(trace.entityHit);
            World targetWorld = this.getTargetWorld(trace.entityHit.world, pos);
            if(pos != null && targetWorld != null) {
                EntityLivingBase target = (EntityLivingBase) trace.entityHit;
                if(target.dimension != this.bedDimension) {
                    target.changeDimension(this.bedDimension, (world, entity, yaw) -> {});
                }
                target.setPositionAndUpdate(this.bedPosition.getX() + 0.5, this.bedPosition.getY() + 0.6, this.bedPosition.getZ() + 0.5);
                target.world.playSound(null, target.getPosition(), SoundsTC.wandfail, SoundCategory.PLAYERS, 0.75F, 0.9F);
                this.resetBedPosition();
                return true;
            }
        }
        this.resetBedPosition();
        return false;
    }

    @Override
    public void onCast(Entity caster) {
        this.getAndSetBedPosition(caster);
        caster.world.playSound(null, caster.getPosition().up(), SoundsTC.hhoff, SoundCategory.PLAYERS,
                0.8F, 0.45F + (float) (caster.world.rand.nextGaussian() * 0.05F));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderParticleFX(World world, double posX, double posY, double posZ, double velX, double velY, double velZ) {
        FXGeneric particle = new FXGeneric(world, posX, posY, posZ, velX, velY, velZ);
        int color = 16357381;
        particle.setAlphaF(0.5F);
        particle.setMaxAge(5 + world.rand.nextInt(5));
        int q = world.rand.nextInt(4);
        particle.setParticles(704 + q * 3, 3, 1);
        particle.setRBGColorF(((color >> 16) & 0xFF) / 255.0F, ((color >> 8) & 0xFF) / 255.0F, (color & 0xFF) / 255.0F);
        particle.setSlowDown(0.5D);
        particle.setScale((float) (2.0F + world.rand.nextGaussian() * 0.2F), 3.0F);
        ParticleEngine.addEffect(world, particle);
    }

    @Nullable
    private BlockPos getAndSetBedPosition(Entity entity) {
        if(entity instanceof EntityPlayer) {
            this.bedDimension = ((EntityPlayer) entity).getSpawnDimension();
            this.bedPosition = ((EntityPlayer) entity).getBedLocation(this.bedDimension);
        }
        return this.bedPosition;
    }

    private void resetBedPosition() {
        this.bedDimension = 0;
        this.bedPosition = null;
    }

    private World getTargetWorld(World world, @Nullable BlockPos pos) {
        if(!world.isRemote && pos != null) {
            WorldServer targetWorld = DimensionManager.getWorld(this.bedDimension);
            if(targetWorld == null) {
                DimensionManager.initDimension(this.bedDimension);
                targetWorld = DimensionManager.getWorld(this.bedDimension);
            }
            return targetWorld;
        }
        return null;
    }
}
