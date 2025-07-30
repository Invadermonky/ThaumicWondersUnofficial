package com.verdantartifice.thaumicwonders.common.tiles.devices;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.config.ConfigHandlerTW;
import com.verdantartifice.thaumicwonders.common.registry.SoundsTW;
import com.verdantartifice.thaumicwonders.common.tiles.base.TileTW;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.lib.potions.PotionWarpWard;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.lib.utils.EntityUtils;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TileVoidBeacon extends TileTW implements ITickable, IAspectContainer, IEssentiaTransport {
    private static final int ESSENTIA_CAPACITY = 100;
    private static final int RIFT_POWER_CAPACITY = 1000;
    private static final int PLAY_EFFECTS = 4;

    protected final List<TileVoidBeacon.BeamSegment> beamSegments = new ArrayList<>();

    protected VoidBeaconTier tier = VoidBeaconTier.ZERO;
    protected int tickCounter = 0;
    protected int essentiaAmount = 0;
    protected int riftPower = 0;

    protected int progress = 0;

    @SideOnly(Side.CLIENT)
    private long beamRenderCounter;
    @SideOnly(Side.CLIENT)
    private float beamRenderScale;

    @Nullable
    public Aspect getEssentiaType() {
        return this.essentiaAmount > 0 ? Aspect.AURA : null;
    }

    public int getEssentiaAmount() {
        return this.essentiaAmount;
    }

    public void clearEssentia() {
        this.essentiaAmount = 0;
        this.markDirty();
        this.syncTile(false);
    }

    public VoidBeaconTier getTier() {
        return this.tier;
    }

    @Override
    public void update() {
        boolean did = false;
        this.tickCounter++;
        if (this.tickCounter % 80 == 0) {
            this.updateBeam();
            VoidBeaconTier checkTier = this.checkTier();
            if (this.tier != checkTier) {
                if (this.isBeaconEnabled()) {
                    if (checkTier == VoidBeaconTier.ZERO) {
                        this.playDeactivateSound();
                    } else if (this.tier == VoidBeaconTier.ZERO) {
                        this.playActivateSound();
                    }
                }
                this.tier = checkTier;
                did = true;
            }
        }

        if (this.tickCounter % 60 == 0) {
            this.playAmbientSound();
        }

        if (!this.world.isRemote) {
            if (this.canMakeProgress()) {
                this.addWarpWardToPlayers();

                if (this.tickCounter % 5 == 0) {
                    int essentiaToAdd = this.fill();
                    if (essentiaToAdd > 0) {
                        this.essentiaAmount += essentiaToAdd;
                        did = true;
                    }
                }

                if (this.tickCounter % 20 == 0) {
                    int riftPowerToAdd = this.drainRifts();
                    if (riftPowerToAdd > 0) {
                        this.riftPower += riftPowerToAdd;
                        did = true;
                    }
                }

                if (this.tickCounter % 200 == 0 && this.hasEnoughEssentia() && this.hasEnoughRiftPower()) {
                    if (this.addVisToAura()) {
                        this.essentiaAmount -= this.getRequiredEssentia();
                        this.riftPower -= this.getRequiredRiftPower();
                        did = true;
                    }
                }
            }
        }

        if (did) {
            this.markDirty();
            this.syncTile(false);
        }
    }

    @Override
    protected void readFromTileNBT(NBTTagCompound compound) {
        this.tier = VoidBeaconTier.values()[compound.getShort("beaconTier")];
        this.essentiaAmount = compound.getShort("essentiaAmount");
        this.riftPower = compound.getShort("riftPower");
    }

    @Override
    protected NBTTagCompound writeToTileNBT(NBTTagCompound compound) {
        compound.setShort("beaconTier", (short) this.tier.ordinal());
        compound.setShort("essentiaAmount", (short) this.essentiaAmount);
        compound.setShort("riftPower", (short) this.riftPower);
        return compound;
    }

    protected boolean canMakeProgress() {
        return this.isBeaconEnabled() && this.isValidTier();
    }

    protected int getRequiredEssentia() {
        return ConfigHandlerTW.void_beacon.essentiaCost;
    }

    protected boolean hasEnoughEssentia() {
        return this.essentiaAmount >= this.getRequiredEssentia();
    }

    protected int getRequiredRiftPower() {
        return ConfigHandlerTW.void_beacon.riftPowerRequired;
    }

    protected boolean hasEnoughRiftPower() {
        return this.riftPower >= this.getRequiredRiftPower();
    }

    protected void addWarpWardToPlayers() {
        int level = this.tier.ordinal();
        double range = level * 10 + 10;
        int duration = (9 + level * 2) * 20;
        int x = this.pos.getX();
        int y = this.pos.getY();
        int z = this.pos.getZ();
        AxisAlignedBB searchArea = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1).grow(range).expand(0, this.world.getHeight(), 0);
        for (EntityPlayer player : this.world.getEntitiesWithinAABB(EntityPlayer.class, searchArea)) {
            if (!player.isPotionActive(PotionWarpWard.instance) || player.getActivePotionEffect(PotionWarpWard.instance).getDuration() < (duration - 80)) {
                player.addPotionEffect(new PotionEffect(PotionWarpWard.instance, duration, 0, true, false));
            }
        }
    }

    protected int fill() {
        if (this.essentiaAmount >= ESSENTIA_CAPACITY)
            return 0;

        for (EnumFacing face : EnumFacing.HORIZONTALS) {
            if (!this.canInputFrom(face)) {
                continue;
            }
            TileEntity tile = ThaumcraftApiHelper.getConnectableTile(this.world, this.pos, face);
            if (tile instanceof IEssentiaTransport) {
                IEssentiaTransport otherTile = (IEssentiaTransport) tile;
                if (!otherTile.canOutputTo(face.getOpposite())) {
                    continue;
                }
                Aspect type = otherTile.getEssentiaType(face.getOpposite());

                if (type != null && otherTile.getEssentiaAmount(face.getOpposite()) > 0
                        && (this.getEssentiaType(face) == null || type == this.getEssentiaType(face))
                        && this.getSuctionAmount(face) > otherTile.getSuctionAmount(face.getOpposite())
                        && this.getSuctionAmount(face) >= otherTile.getMinimumSuction()
                ) {
                    int taken = otherTile.takeEssentia(type, 1, face.getOpposite());
                    int leftover = this.addToContainer(type, taken);
                    if (leftover > 0) {
                        ThaumicWonders.LOGGER.info("Void beacon spilling {} essentia on fill", leftover);
                        AuraHelper.polluteAura(this.world, this.pos, leftover, true);
                    }
                    return taken - leftover;
                }
            }
        }
        return 0;
    }

    protected int drainRifts() {
        if (this.riftPower >= RIFT_POWER_CAPACITY) {
            return 0;
        }

        List<EntityFluxRift> riftList = this.getValidRifts();
        double drained = 0;
        boolean found = false;
        for (EntityFluxRift rift : riftList) {
            drained = Math.sqrt(rift.getRiftSize());
            rift.setRiftStability(rift.getRiftStability() - (float) (drained / 15.0D));
            if (this.world.rand.nextInt(33) == 0) {
                rift.setRiftSize(rift.getRiftSize() - 1);
            }
            if (drained >= 1.0D) {
                found = true;
            }
        }
        if (found) {
            if (this.tickCounter % 40 == 0) {
                this.world.addBlockEvent(this.pos, this.getBlockType(), PLAY_EFFECTS, this.tickCounter);
            }
        }
        return (int) Math.ceil(drained);
    }

    protected List<EntityFluxRift> getValidRifts() {
        List<EntityFluxRift> retVal = new ArrayList<>();
        List<EntityFluxRift> riftList = EntityUtils.getEntitiesInRange(this.world, this.pos, null, EntityFluxRift.class, 16.0D);
        for (EntityFluxRift rift : riftList) {
            if (!rift.isDead && rift.getRiftSize() > 1) {
                Vec3d v1 = new Vec3d(this.pos.getX() + 0.5D, this.pos.getY() + 1.0D, this.pos.getZ() + 0.5D);
                Vec3d v2 = new Vec3d(rift.posX, rift.posY, rift.posZ);
                v1 = v1.add(v2.subtract(v1).normalize());
                if (EntityUtils.canEntityBeSeen(rift, v1.x, v1.y, v1.z)) {
                    retVal.add(rift);
                }
            }
        }
        return retVal;
    }

    protected VoidBeaconTier checkTier() {
        return VoidBeaconTier.getVoidBeaconTier(this.world, this.pos);
    }

    protected float getVisAdded() {
        switch (this.tier) {
            case ONE:
                return (float) ConfigHandlerTW.void_beacon.tierOneVisGenerated;
            case TWO:
                return (float) ConfigHandlerTW.void_beacon.tierTwoVisGenerated;
            case THREE:
                return (float) ConfigHandlerTW.void_beacon.tierThreeVisGenerated;
            case FOUR:
                return (float) ConfigHandlerTW.void_beacon.tierFourVisGenerated;
            default:
                return 0;
        }
    }

    protected int getChunkRange() {
        switch (this.tier) {
            case ZERO:
                return -1;
            case ONE:
                return ConfigHandlerTW.void_beacon.tierOneRange;
            case TWO:
                return ConfigHandlerTW.void_beacon.tierTwoRange;
            case THREE:
                return ConfigHandlerTW.void_beacon.tierThreeRange;
            case FOUR:
                return ConfigHandlerTW.void_beacon.tierFourRange;
        }
        return 0;
    }

    protected boolean addVisToAura() {
        boolean did = false;
        if (this.isValidTier()) {
            int chunkRange = this.getChunkRange();
            float visAdded = this.getVisAdded();
            if (visAdded > 0 && chunkRange >= 0) {
                for (int x = -chunkRange; x <= chunkRange; x++) {
                    for (int z = -chunkRange; z <= chunkRange; z++) {
                        BlockPos chunkPos = this.pos.add((x << 4), 0, (z << 4));
                        int baseAura = AuraHelper.getAuraBase(this.world, chunkPos);
                        float currFlux = AuraHelper.getFlux(this.world, chunkPos);
                        float currAura = AuraHelper.getVis(this.world, chunkPos);
                        float overflow = MathHelper.clamp(((float) baseAura * 0.1f), 10.0f, 20.0f);
                        if ((currAura + currFlux) < ((float) baseAura + overflow)) {
                            AuraHelper.addVis(this.world, chunkPos, visAdded);
                            did = true;
                        }
                    }
                }
            }
        }
        return did;
    }

    //##################################################
    //  IAspectContainer

    @Override
    public AspectList getAspects() {
        return new AspectList().add(Aspect.AURA, this.essentiaAmount);
    }

    @Override
    public void setAspects(AspectList aspectList) {
        this.essentiaAmount = aspectList != null ? aspectList.getAmount(Aspect.AURA) : 0;
        this.markDirty();
        this.syncTile(false);
    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        return true;
    }

    @Override
    public int addToContainer(Aspect aspect, int toAdd) {
        int retVal;
        if (toAdd == 0) {
            return 0;
        } else if (this.essentiaAmount < ESSENTIA_CAPACITY && aspect == Aspect.AURA) {
            // Add as much as possible and return the remainder
            int added = Math.min(toAdd, ESSENTIA_CAPACITY - this.essentiaAmount);
            this.essentiaAmount += added;
            retVal = (toAdd - added);
        } else {
            retVal = toAdd;
        }

        this.markDirty();
        this.syncTile(false);
        return retVal;
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int amt) {
        if (aspect == Aspect.AURA && this.essentiaAmount >= amt) {
            this.essentiaAmount -= amt;
            this.markDirty();
            this.syncTile(false);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean takeFromContainer(AspectList aspectList) {
        if (!this.doesContainerContain(aspectList)) {
            return false;
        } else {
            boolean satisfied = true;
            for (Aspect aspect : aspectList.getAspects()) {
                satisfied = satisfied && this.takeFromContainer(aspect, aspectList.getAmount(aspect));
            }
            return satisfied;
        }
    }

    @Override
    public boolean doesContainerContainAmount(Aspect aspect, int amt) {
        return aspect == Aspect.AURA && this.essentiaAmount >= amt;
    }

    @Override
    public boolean doesContainerContain(AspectList aspectList) {
        boolean satisfied = true;
        for (Aspect aspect : aspectList.getAspects()) {
            satisfied = satisfied && this.doesContainerContainAmount(aspect, aspectList.getAmount(aspect));
        }
        return satisfied;
    }

    @Override
    public int containerContains(Aspect aspect) {
        return aspect == Aspect.AURA ? this.essentiaAmount : 0;
    }


    //##################################################
    //  IEssentiaTransport

    @Override
    public boolean isConnectable(EnumFacing face) {
        return this.canInputFrom(face);
    }

    @Override
    public boolean canInputFrom(EnumFacing face) {
        return (face != EnumFacing.DOWN && face != EnumFacing.UP);
    }

    @Override
    public boolean canOutputTo(EnumFacing face) {
        return false;
    }

    @Override
    public void setSuction(Aspect aspect, int amt) {
        // Do nothing
    }

    @Override
    public Aspect getSuctionType(EnumFacing face) {
        return this.getEssentiaType(face);
    }

    @Override
    public int getSuctionAmount(EnumFacing face) {
        return (this.getEssentiaAmount(face) >= ESSENTIA_CAPACITY) ? 0 : 128;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amt, EnumFacing face) {
        // Can't output
        return 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amt, EnumFacing face) {
        if (this.canInputFrom(face) && aspect == Aspect.AURA) {
            return (amt - this.addToContainer(aspect, amt));
        } else {
            return 0;
        }
    }

    @Override
    public Aspect getEssentiaType(EnumFacing face) {
        return Aspect.AURA;
    }

    @Override
    public int getEssentiaAmount(EnumFacing face) {
        return this.essentiaAmount;
    }

    @Override
    public int getMinimumSuction() {
        // Can't output, so no need for minimum suction
        return 0;
    }

    //##################################################
    //  Beacon Stuff

    public boolean isValidTier() {
        return this.tier != VoidBeaconTier.ZERO;
    }

    public boolean isBeaconEnabled() {
        return BlockStateUtils.isEnabled(this.getBlockMetadata());
    }

    public void playActivateSound() {
        this.world.playSound(null, this.pos, SoundsTW.VOID_BEACON_ACTIVATE, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    public void playAmbientSound() {
        if (!ConfigHandlerTW.void_beacon.disableAmbient && this.isValidTier() && this.isBeaconEnabled()) {
            this.world.playSound(null, this.pos, SoundsTW.VOID_BEACON_AMBIENT, SoundCategory.BLOCKS, 0.6f, 1.0f);
        }
    }

    public void playDeactivateSound() {
        this.world.playSound(null, this.pos, SoundsTW.VOID_BEACON_DEACTIVATE, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    protected void updateBeam() {
        this.beamSegments.clear();
        Color beamColor = new Color(Aspect.ELDRITCH.getColor());
        TileVoidBeacon.BeamSegment segment = new TileVoidBeacon.BeamSegment(beamColor.getRGBColorComponents(null));
        this.beamSegments.add(segment);
        BlockPos.MutableBlockPos mbp = new BlockPos.MutableBlockPos();

        for (int y = this.pos.getY() + 1; y < this.world.getActualHeight(); y++) {
            mbp.setPos(this.pos.getX(), y, this.pos.getZ());
            IBlockState blockState = this.world.getBlockState(mbp);
            if (blockState.getLightOpacity(this.world, mbp) >= 15 && blockState.getBlock() != Blocks.BEDROCK) {
                this.beamSegments.clear();
                break;
            } else {
                segment.incrementHeight();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public List<TileVoidBeacon.BeamSegment> getBeamSegments() {
        return this.beamSegments;
    }

    @SideOnly(Side.CLIENT)
    public float shouldBeamRender() {
        if (!this.isValidTier() || !this.isBeaconEnabled()) {
            return 0.0F;
        } else {
            int i = (int) (this.world.getTotalWorldTime() - this.beamRenderCounter);
            this.beamRenderCounter = this.world.getTotalWorldTime();

            if (i > 1) {
                this.beamRenderScale -= (float) i / 40.0F;
                if (this.beamRenderScale < 0.0F) {
                    this.beamRenderScale = 0.0F;
                }
            }

            this.beamRenderScale += 0.025F;

            if (this.beamRenderScale > 1.0F) {
                this.beamRenderScale = 1.0F;
            }

            return this.beamRenderScale;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 65536.0D;
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == PLAY_EFFECTS) {
            if (this.world.isRemote) {
                List<EntityFluxRift> riftList = this.getValidRifts();
                for (EntityFluxRift rift : riftList) {
                    FXDispatcher.INSTANCE.voidStreak(
                            rift.posX,
                            rift.posY,
                            rift.posZ,
                            this.pos.getX() + 0.5D,
                            this.pos.getY() + 1.0D,
                            this.pos.getZ() + 0.5D,
                            type,
                            0.04F);
                }
            }
            return true;
        } else {
            return super.receiveClientEvent(id, type);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    public enum VoidBeaconTier {
        ZERO,
        ONE,
        TWO,
        THREE,
        FOUR;

        public static VoidBeaconTier getVoidBeaconTier(World world, BlockPos beaconPos) {
            VoidBeaconTier tier = ZERO;
            TileEntity tile = world.getTileEntity(beaconPos);
            if (tile instanceof TileVoidBeacon) {
                BlockPos searchPos;
                int tierRadius = 1;
                checkLoop:
                for (int y = -1; y >= -4; y--) {
                    for (int x = -tierRadius; x <= tierRadius; x++) {
                        for (int z = -tierRadius; z <= tierRadius; z++) {
                            BlockPos checkPos = beaconPos.add(x, y, z);
                            IBlockState checkState = world.getBlockState(checkPos);
                            if (!checkState.getBlock().isBeaconBase(world, checkPos, beaconPos)) {
                                break checkLoop;
                            }
                        }
                    }
                    tierRadius++;
                    tier = tier.next();
                }
            }
            return tier;
        }

        public VoidBeaconTier next() {
            return VoidBeaconTier.values()[(this.ordinal() + 1) % VoidBeaconTier.values().length];
        }
    }

    public static class BeamSegment {
        /**
         * RGB (0 to 1.0) colors of this beam segment
         */
        private final float[] colors;
        private int height;

        public BeamSegment(float[] colorsIn) {
            this.colors = colorsIn;
            this.height = 1;
        }

        protected void incrementHeight() {
            ++this.height;
        }

        /**
         * Returns RGB (0 to 1.0) colors of this beam segment
         */
        public float[] getColors() {
            return this.colors;
        }

        @SideOnly(Side.CLIENT)
        public int getHeight() {
            return this.height;
        }
    }
}
