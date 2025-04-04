package com.verdantartifice.thaumicwonders.common.tiles.devices;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.entities.EntityVoidPortal;
import com.verdantartifice.thaumicwonders.common.network.PacketHandler;
import com.verdantartifice.thaumicwonders.common.network.packets.PacketLocalizedMessage;
import com.verdantartifice.thaumicwonders.common.tiles.base.TileTW;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.items.IGogglesDisplayExtended;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;
import thaumcraft.common.entities.monster.EntityInhabitedZombie;
import thaumcraft.common.entities.monster.EntityWisp;
import thaumcraft.common.entities.monster.cult.EntityCultistPortalLesser;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.RandomItemChooser;
import thaumcraft.common.tiles.devices.TileStabilizer;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TilePortalGenerator extends TileTW implements ITickable, IGogglesDisplayExtended {
    protected static DecimalFormat decFormatter = new DecimalFormat("#######.##");
    protected static List<RandomItemChooser.Item> instabilityEvents = new ArrayList<RandomItemChooser.Item>();
    protected static List<RandomItemChooser.Item> spawnEvents = new ArrayList<RandomItemChooser.Item>();
    protected static List<RandomItemChooser.Item> subvertEvents = new ArrayList<RandomItemChooser.Item>();
    protected int linkX = 0;
    protected int linkY = 0;
    protected int linkZ = 0;
    protected int linkDim = 0;
    protected float stability = 0.0F;
    protected int sparkCounter = 0;
    protected int ticksExisted = 0;
    protected boolean lastEnabled = true;
    protected float lastStabilityIncrease = 0.0F;
    protected float lastStabilityDecrease = 0.0F;

    @Override
    protected void readFromTileNBT(NBTTagCompound compound) {
        this.linkX = compound.getInteger("linkX");
        this.linkY = compound.getInteger("linkY");
        this.linkZ = compound.getInteger("linkZ");
        this.linkDim = compound.getInteger("linkDim");
        this.stability = compound.getFloat("stability");
        this.lastStabilityIncrease = compound.getFloat("lastStabilityIncrease");
        this.lastStabilityDecrease = compound.getFloat("lastStabilityDecrease");
    }

    @Override
    protected NBTTagCompound writeToTileNBT(NBTTagCompound compound) {
        compound.setInteger("linkX", this.linkX);
        compound.setInteger("linkY", this.linkY);
        compound.setInteger("linkZ", this.linkZ);
        compound.setInteger("linkDim", this.linkDim);
        compound.setFloat("stability", this.stability);
        compound.setFloat("lastStabilityIncrease", this.lastStabilityIncrease);
        compound.setFloat("lastStabilityDecrease", this.lastStabilityDecrease);
        return super.writeToTileNBT(compound);
    }

    public void setLink(int linkX, int linkY, int linkZ, int linkDim) {
        this.linkX = linkX;
        this.linkY = linkY;
        this.linkZ = linkZ;
        this.linkDim = linkDim;
    }

    public float getStability() {
        return this.stability;
    }

    public void setStability(float stability) {
        this.stability = MathHelper.clamp(stability, -100.0f, 100.0f);
    }

    public Stability getStabilityLevel() {
        if (this.stability >= 50.0F) {
            return Stability.VERY_STABLE;
        } else if (this.stability >= 0.0F) {
            return Stability.STABLE;
        } else if (this.stability >= -25.0F) {
            return Stability.UNSTABLE;
        } else {
            return Stability.VERY_UNSTABLE;
        }
    }

    public void spawnPortal() {
        if (!this.world.isRemote) {
            double posX = this.pos.up().getX() + 0.5D;
            double posY = this.pos.up().getY();
            double posZ = this.pos.up().getZ() + 0.5D;
            EntityVoidPortal portal = new EntityVoidPortal(this.world);
            portal.setPosition(posX, posY, posZ);
            portal.setLinkX(this.linkX);
            portal.setLinkY(this.linkY);
            portal.setLinkZ(this.linkZ);
            portal.setLinkDim(this.linkDim);
            this.world.spawnEntity(portal);
        }
    }

    public void despawnPortal(boolean playSound) {
        if (!this.world.isRemote) {
            EntityVoidPortal portal = this.getActivePortal();
            if (portal != null) {
                portal.setDead();
                if (playSound) {
                    this.world.playSound(null, this.pos, SoundsTC.shock, SoundCategory.BLOCKS, 1.0F, (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F + 1.0F);
                }
            }
        }
    }

    protected EntityVoidPortal getActivePortal() {
        AxisAlignedBB bb = new AxisAlignedBB(this.pos.up());
        List<EntityVoidPortal> portalList = this.world.getEntitiesWithinAABB(EntityVoidPortal.class, bb);
        return (!portalList.isEmpty()) ? portalList.get(0) : null;
    }

    protected boolean isPortalActive() {
        return (this.getActivePortal() != null);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        IBlockState state = this.world.getBlockState(this.pos);
        this.lastEnabled = state.getValue(IBlockEnabled.ENABLED);
    }

    @Override
    public void update() {
        this.sparkCounter++;
        this.ticksExisted++;
        if (this.world.isRemote && this.sparkCounter == 20) {
            BlockPos sourcePos = this.pos.up();
            if (this.world.rand.nextInt() % 2 == 0) {
                sourcePos = sourcePos.south();
            }
            if (this.world.rand.nextInt() % 2 == 0) {
                sourcePos = sourcePos.east();
            }
            Color color = new Color(Aspect.FLUX.getColor());
            float r = color.getRed() / 255.0F;
            float g = color.getGreen() / 255.0F;
            float b = color.getBlue() / 255.0F;
            FXDispatcher.INSTANCE.drawLightningFlash(sourcePos.getX(), sourcePos.getY(), sourcePos.getZ(), r, g, b, 1.0F, 2.5F);
            this.sparkCounter -= (20 + this.world.rand.nextInt(80));
        }
        if (!this.world.isRemote) {
            // Enable or disable the portal if redstone signal has changed
            IBlockState state = this.world.getBlockState(this.pos);
            boolean enabled = state.getValue(IBlockEnabled.ENABLED);
            if (enabled != this.lastEnabled) {
                if (enabled && !this.isPortalActive()) {
                    this.spawnPortal();
                } else if (!enabled && this.isPortalActive()) {
                    this.despawnPortal(true);
                }
            }
            this.lastEnabled = enabled;

            // Increase/decrease stability
            float lastStability = this.stability;
            boolean active = this.isPortalActive();
            if (this.ticksExisted % 120 == 0) {
                this.lastStabilityIncrease = 0.0F;
                this.lastStabilityDecrease = 0.0F;

                if (active) {
                    for (BlockPos.MutableBlockPos mbp : BlockPos.getAllInBoxMutable(this.pos.add(-8, -8, -8), this.pos.add(8, 8, 8))) {
                        TileEntity tile = this.world.getTileEntity(mbp);
                        if (tile instanceof TileStabilizer) {
                            TileStabilizer stabilizer = (TileStabilizer) tile;
                            if (this.getStabilityLevel() != Stability.VERY_STABLE && stabilizer.mitigate(1)) {
                                this.lastStabilityIncrease += 0.75F;
                                this.setStability(this.stability + 0.75F);
                                stabilizer.markDirty();
                                stabilizer.syncTile(false);
                                if (stabilizer.getEnergy() == 0) {
                                    break;
                                }
                            }
                        }
                    }

                    List<EntityVoidPortal> portals = this.world.getEntitiesWithinAABB(EntityVoidPortal.class, new AxisAlignedBB(this.pos.up()).grow(16.0D));
                    if (!portals.isEmpty()) {
                        this.lastStabilityDecrease = 0.04F * portals.size() * portals.size();
                        this.setStability(this.stability - this.lastStabilityDecrease);
                    }
                }
            }
            if (this.stability != lastStability) {
                this.markDirty();
                this.syncTile(false);
            }

            // Execute instability event if applicable
            if (active && this.ticksExisted % 600 == 0 && this.getStability() < 0.0F && (this.world.rand.nextInt(1000) < Math.abs(this.getStability()))) {
                this.executeInstabilityEvent();
            }
        }
    }

    protected void executeInstabilityEvent() {
        if (instabilityEvents.size() <= 0) {
            return;
        }
        RandomItemChooser ric = new RandomItemChooser();
        InstabilityEventEntry event = (InstabilityEventEntry) ric.chooseOnWeight(instabilityEvents);
        if (event == null) {
            return;
        }
        if (this.stability >= event.requiredInstability) {
            return;
        }
        switch (event.eventId) {
            case 0:
                this.fluxBurst();
                break;
            case 1:
                this.spawnInvader();
                break;
            case 2:
                this.subvertPortal();
                break;
            default:
                ThaumicWonders.LOGGER.warn("Unexpected instability event ID {}", event.eventId);
        }
    }

    protected void fluxBurst() {
        float amount = (float) Math.sqrt(Math.abs(this.stability));
        AuraHelper.polluteAura(this.world, this.pos, amount, true);
    }

    protected void spawnInvader() {
        if (spawnEvents.size() <= 0) {
            return;
        }
        RandomItemChooser ric = new RandomItemChooser();
        InstabilityEventEntry event = (InstabilityEventEntry) ric.chooseOnWeight(spawnEvents);
        if (event == null) {
            return;
        }
        if (this.stability >= event.requiredInstability) {
            return;
        }

        BlockPos portalPos = this.pos.up();
        int count = 0;
        switch (event.eventId) {
            case 0:
                count = 1 + (int) Math.floor(Math.abs(this.getStability()) / 20.0F);
                break;
            case 1:
                count = 1 + (int) Math.floor(Math.abs(this.getStability()) / 30.0F);
                break;
            case 2:
                count = 1 + (int) Math.floor(Math.abs(this.getStability()) / 40.0F);
                break;
            case 3:
            case 4:
                count = 1;
                break;
            default:
                ThaumicWonders.LOGGER.warn("Unexpected spawn event ID {}", event.eventId);
                return;
        }

        boolean spawned = false;
        for (int i = 0; i < count; i++) {
            EntityLiving entity = this.getInvader(event.eventId);
            entity.setLocationAndAngles(
                    portalPos.getX() + this.world.rand.nextGaussian() * 3.0D,
                    portalPos.getY() + this.world.rand.nextGaussian() * 3.0D,
                    portalPos.getZ() + this.world.rand.nextGaussian() * 3.0D,
                    0.0F, 0.0F);
            if (entity.getCanSpawnHere()) {
                if (this.world.spawnEntity(entity)) {
                    spawned = true;
                }
            }
        }
        if (spawned) {
            PacketHandler.INSTANCE.sendToAllAround(
                    new PacketLocalizedMessage("event.void_portal.invader"),
                    new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), this.pos.getX(), this.pos.getY(), this.pos.getZ(), 16.0D));
        }
    }

    protected EntityLiving getInvader(int type) {
        switch (type) {
            case 0:
                return new EntityWisp(this.world);
            case 1:
                return new EntityPigZombie(this.world);
            case 2:
                return new EntityEnderman(this.world);
            case 3:
                return new EntityInhabitedZombie(this.world);
            case 4:
                return new EntityEldritchGuardian(this.world);
            default:
                ThaumicWonders.LOGGER.warn("No invader known for type {}", type);
                return null;
        }
    }

    protected void subvertPortal() {
        if (subvertEvents.size() <= 0) {
            return;
        }
        RandomItemChooser ric = new RandomItemChooser();
        InstabilityEventEntry event = (InstabilityEventEntry) ric.chooseOnWeight(subvertEvents);
        if (event == null) {
            return;
        }
        if (this.stability >= event.requiredInstability) {
            return;
        }

        switch (event.eventId) {
            case 0:
                EntityCultistPortalLesser lesserPortal = new EntityCultistPortalLesser(this.world);
                lesserPortal.setPosition(this.pos.getX() + 0.5D, this.pos.getY() + 1.0D, this.pos.getZ() + 0.5D);
                this.despawnPortal(false);
                lesserPortal.onInitialSpawn(this.world.getDifficultyForLocation(new BlockPos(lesserPortal)), null);
                this.world.spawnEntity(lesserPortal);
                PacketHandler.INSTANCE.sendToAllAround(
                        new PacketLocalizedMessage("event.void_portal.subvert.lesser_crimson"),
                        new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), this.pos.getX(), this.pos.getY(), this.pos.getZ(), 16.0D));
                break;
            case 1:
                EntityFluxRift rift = new EntityFluxRift(this.world);
                rift.setRiftSeed(this.world.rand.nextInt());
                rift.setLocationAndAngles(this.pos.getX() + 0.5D, this.pos.getY() + 1.5D, this.pos.getZ() + 0.5D, (float) this.world.rand.nextInt(360), 0.0F);
                double size = Math.sqrt((2 * Math.abs(this.getStability())) * 3.0F);
                this.despawnPortal(false);
                if (this.world.spawnEntity(rift)) {
                    rift.setRiftSize((int) size);
                }
                PacketHandler.INSTANCE.sendToAllAround(
                        new PacketLocalizedMessage("event.void_portal.subvert.flux_rift"),
                        new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), this.pos.getX(), this.pos.getY(), this.pos.getZ(), 16.0D));
                break;
            default:
                ThaumicWonders.LOGGER.warn("Unexpected subversion event ID {}", event.eventId);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String[] getIGogglesText() {
        return new String[]{
                TextFormatting.BOLD + I18n.format("stability." + this.getStabilityLevel().name()),
                TextFormatting.GOLD + "" + TextFormatting.ITALIC + decFormatter.format(this.lastStabilityIncrease) + " " + I18n.format("stability.gain"),
                TextFormatting.RED + "" + TextFormatting.ITALIC + decFormatter.format(this.lastStabilityDecrease) + " " + I18n.format("stability.loss")
        };
    }

    public enum Stability {
        VERY_STABLE, STABLE, UNSTABLE, VERY_UNSTABLE;

        Stability() {
        }
    }

    protected static class InstabilityEventEntry implements RandomItemChooser.Item {
        public int eventId;
        public int weight;
        public float requiredInstability;

        protected InstabilityEventEntry(int eventId, int weight, float reqInstability) {
            this.eventId = eventId;
            this.weight = weight;
            this.requiredInstability = reqInstability;
        }

        @Override
        public double getWeight() {
            return this.weight;
        }
    }

    static {
        instabilityEvents.add(new InstabilityEventEntry(0, 60, 0.0F));  // Flux burst
        instabilityEvents.add(new InstabilityEventEntry(1, 35, 0.0F));  // Creature spawn
        instabilityEvents.add(new InstabilityEventEntry(2, 5, -50.0F)); // Portal subversion

        spawnEvents.add(new InstabilityEventEntry(0, 40, 0.0F));    // Wisps
        spawnEvents.add(new InstabilityEventEntry(1, 30, -20.0F));  // Zombie pigmen
        spawnEvents.add(new InstabilityEventEntry(2, 20, -40.0F));  // Endermen
        spawnEvents.add(new InstabilityEventEntry(3, 7, -60.0F));   // Inhabited zombie
        spawnEvents.add(new InstabilityEventEntry(4, 3, -80.0F));   // Eldritch guardian

        subvertEvents.add(new InstabilityEventEntry(0, 75, -50.0F));    // Lesser crimson portal
        subvertEvents.add(new InstabilityEventEntry(1, 25, -75.0F));    // Flux rift
    }
}
