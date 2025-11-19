package com.verdantartifice.thaumicwonders.common.items.foci;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXFocusPartImpact;

// Courtesy of IcarussOne
// TODO: More unique sounds? Might need polishing
public class FocusEffectBlink extends FocusEffect {
    @Override
    public Aspect getAspect() {
        return Aspect.ELDRITCH;
    }

    @Override
    public String getKey() {
        return "focus." + ThaumicWonders.MOD_ID + ".blink";
    }

    @Override
    public NodeSetting[] createSettings() {
        int[] values = {0, 1};
        String[] desc = {"focus.common.no", "focus.common.yes"};
        return new NodeSetting[]{new NodeSetting("swap", "focus.common.swap", new NodeSetting.NodeSettingIntList(values, desc)), new NodeSetting("telefrag", "focus.common.telefrag", new NodeSetting.NodeSettingIntList(values, desc))};
    }

    @Override
    public int getComplexity() {
        return 15 + getSettingValue("swap") * 5 + this.getSettingValue("telefrag") * 10;
    }

    @Override
    public float getDamageForDisplay(float finalPower) {
        return 0.0F + this.getSettingValue("telefrag") * 10.0F;
    }

    // TODO: Put it in its own research, mainly done this way for testing purposes
    @Override
    public String getResearch() {
        return "TWOND_FOCUS_TELEPORT_HOME";
    }

    @Override
    public boolean execute(RayTraceResult target, Trajectory trajectory, float finalPower, int num) {
        PacketHandler.INSTANCE.sendToAllAround(new PacketFXFocusPartImpact(target.hitVec.x, target.hitVec.y, target.hitVec.z, new String[]{this.getKey()}), new NetworkRegistry.TargetPoint(this.getPackage().world.provider.getDimension(), target.hitVec.x, target.hitVec.y, target.hitVec.z, 64.0D));
        this.getPackage().world.playSound(null, target.hitVec.x, target.hitVec.y, target.hitVec.z, SoundsTC.hhon, SoundCategory.PLAYERS, 0.8F, 0.85F + (float) (this.getPackage().getCaster().world.rand.nextGaussian() * 0.05F));

        if ((target.typeOfHit == RayTraceResult.Type.ENTITY && target.entityHit != null && target.entityHit != this.getPackage().getCaster() || target.typeOfHit == RayTraceResult.Type.BLOCK) &&
                !FocusEngine.doesPackageContainElement(this.getPackage(), "thaumcraft.PLAN")) {
            if (!this.getPackage().world.isRemote) {
                if (this.getPackage().getCaster() instanceof EntityPlayerMP) {
                    EntityPlayerMP player = (EntityPlayerMP) this.getPackage().getCaster();

                    if (getSettingValue("swap") > 0) {
                        teleportOther(target);
                    }

                    if (player.connection.getNetworkManager().isChannelOpen() && player.world == this.getPackage().world && !player.isPlayerSleeping()) {
                        EnderTeleportEvent event = new EnderTeleportEvent(player, target.hitVec.x, target.hitVec.y, target.hitVec.z, 0.0F);
                        if (!MinecraftForge.EVENT_BUS.post(event)) {

                            if (this.getPackage().getCaster().isRiding()) {
                                this.getPackage().getCaster().dismountRidingEntity();
                            }

                            PacketHandler.INSTANCE.sendToAllAround(new PacketFXFocusPartImpact(player.posX, player.posY, player.posZ, new String[]{this.getKey()}), new NetworkRegistry.TargetPoint(this.getPackage().world.provider.getDimension(), target.hitVec.x, target.hitVec.y, target.hitVec.z, 64.0D));
                            this.getPackage().getCaster().setPositionAndUpdate(event.getTargetX(), event.getTargetY(), event.getTargetZ());
                            this.getPackage().getCaster().fallDistance = 0.0F;
                            this.getPackage().world.playSound(null, player.posX, player.posY, player.posZ, SoundsTC.wandfail, SoundCategory.PLAYERS, 0.7F, 1.25F + (float) (this.getPackage().getCaster().world.rand.nextGaussian() * 0.05F));
                        }
                    }
                } else if (this.getPackage().getCaster() != null) {
                    if (getSettingValue("swap") > 0) {
                        teleportOther(target);
                    }

                    this.getPackage().getCaster().setPositionAndUpdate(target.hitVec.x, target.hitVec.y, target.hitVec.z);
                    this.getPackage().getCaster().fallDistance = 0.0F;
                }
            }

            return true;
        }

        return false;
    }

    public void teleportOther(RayTraceResult target) {
        if (target.entityHit instanceof EntityPlayerMP && target.entityHit != this.getPackage().getCaster()) {
            EntityPlayerMP playerHit = (EntityPlayerMP) target.entityHit;

            if (playerHit.connection.getNetworkManager().isChannelOpen() && playerHit.world == this.getPackage().world && !playerHit.isPlayerSleeping()) {
                EnderTeleportEvent event = new EnderTeleportEvent(playerHit, this.getPackage().getCaster().posX, this.getPackage().getCaster().posY, this.getPackage().getCaster().posZ, 0.0F);
                if (!MinecraftForge.EVENT_BUS.post(event)) {

                    if (target.entityHit.isRiding()) {
                        target.entityHit.dismountRidingEntity();
                    }

                    PacketHandler.INSTANCE.sendToAllAround(new PacketFXFocusPartImpact(playerHit.posX, playerHit.posY, playerHit.posZ, new String[]{this.getKey()}), new NetworkRegistry.TargetPoint(this.getPackage().world.provider.getDimension(), target.hitVec.x, target.hitVec.y, target.hitVec.z, 64.0D));
                    playerHit.setPositionAndUpdate(event.getTargetX(), event.getTargetY(), event.getTargetZ());
                    playerHit.fallDistance = 0.0F;
                    playerHit.world.playSound(null, playerHit.posX, playerHit.posY, playerHit.posZ, SoundsTC.wandfail, SoundCategory.PLAYERS, 0.7F, 1.25F + (float) (this.getPackage().getCaster().world.rand.nextGaussian() * 0.05F));

                    if (getSettingValue("telefrag") > 0) {
                        target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage((target.entityHit != null) ? target.entityHit : this.getPackage().getCaster(), this.getPackage().getCaster()).setDamageBypassesArmor().setDamageIsAbsolute(), 10.0F);
                    }
                }
            }
        } else if (target.entityHit instanceof EntityLivingBase && target.entityHit != this.getPackage().getCaster()) {
            EnderTeleportEvent event = new EnderTeleportEvent((EntityLivingBase) target.entityHit, this.getPackage().getCaster().posX, this.getPackage().getCaster().posY, this.getPackage().getCaster().posZ, 20.0F);
            target.entityHit.setPositionAndUpdate(event.getTargetX(), event.getTargetY(), event.getTargetZ());
            target.entityHit.fallDistance = 0.0F;
            target.entityHit.world.playSound(null, target.entityHit.posX, target.entityHit.posY, target.entityHit.posZ, SoundsTC.wandfail, SoundCategory.PLAYERS, 0.7F, 1.25F + (float) (this.getPackage().getCaster().world.rand.nextGaussian() * 0.05F));

            if (getSettingValue("telefrag") > 0) {
                target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage((target.entityHit != null) ? target.entityHit : this.getPackage().getCaster(), this.getPackage().getCaster()).setDamageBypassesArmor().setDamageIsAbsolute(), 10.0F);
            }
        }
    }

    @Override
    public void onCast(Entity caster) {
        caster.world.playSound(null, caster.getPosition().up(), SoundsTC.hhoff, SoundCategory.PLAYERS, 0.8F, 0.45F + (float) (caster.world.rand.nextGaussian() * 0.05F));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderParticleFX(World world, double posX, double posY, double posZ, double velX, double velY, double velZ) {
        FXGeneric fb = new FXGeneric(world, posX, posY, posZ, velX, velY, velZ);
        int color = 740674;

        fb.setMaxAge(16 + world.rand.nextInt(16));
        fb.setParticles(384 + world.rand.nextInt(16), 1, 1);
        fb.setSlowDown(0.75F);
        fb.setAlphaF(1.0F, 0.0F);
        fb.setScale((float) (0.7F + world.rand.nextGaussian() * 0.3F));
        fb.setRandomMovementScale(0.01F, 0.01F, 0.01F);
        ParticleEngine.addEffectWithDelay(world, fb, 0);

        FXGeneric fb2 = new FXGeneric(world, posX, posY, posZ, velX + world.rand.nextGaussian() * 0.01D, velY + world.rand.nextGaussian() * 0.01D, velZ + world.rand.nextGaussian() * 0.01D);
        fb2.setMaxAge((int) (15.0F + 10.0F * world.rand.nextFloat()));
        fb2.setRBGColorF(((color >> 16) & 0xFF) / 255.0F, ((color >> 8) & 0xFF) / 255.0F, (color & 0xFF) / 255.0F);
        fb2.setAlphaF(new float[]{0.0F, 1.0F, 1.0F, 0.0F});
        fb2.setGridSize(64);
        fb2.setParticles(128, 14, 1);
        fb2.setScale(new float[]{4.0F + world.rand.nextFloat(), 0.25F + world.rand.nextFloat() * 0.25F});
        fb2.setLoop(true);
        fb2.setSlowDown(0.9);
        fb2.setGravity((float) (world.rand.nextGaussian() * 0.1D));
        fb2.setRandomMovementScale(0.0125F, 0.0125F, 0.0125F);
        fb2.setRotationSpeed((float) world.rand.nextGaussian());
        ParticleEngine.addEffectWithDelay(world, (Particle) fb2, world.rand.nextInt(4));
    }
}