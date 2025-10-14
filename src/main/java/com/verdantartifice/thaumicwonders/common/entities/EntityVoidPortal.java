package com.verdantartifice.thaumicwonders.common.entities;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TilePortalAnchor;
import com.verdantartifice.thaumicwonders.common.utils.StringHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.lib.SoundsTC;

public class EntityVoidPortal extends Entity {
    private static final DataParameter<Integer> LINK_X = EntityDataManager.createKey(EntityVoidPortal.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> LINK_Y = EntityDataManager.createKey(EntityVoidPortal.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> LINK_Z = EntityDataManager.createKey(EntityVoidPortal.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> LINK_DIM = EntityDataManager.createKey(EntityVoidPortal.class, DataSerializers.VARINT);

    private int soundTime = 0;
    private int cooldownTicks = 0;

    public EntityVoidPortal(World worldIn) {
        super(worldIn);
        this.preventEntitySpawning = true;
        this.setSize(1.0F, 2.0F);
    }

    @Override
    protected void entityInit() {
        this.dataManager.register(LINK_X, 0);
        this.dataManager.register(LINK_Y, 0);
        this.dataManager.register(LINK_Z, 0);
        this.dataManager.register(LINK_DIM, 0);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.cooldownTicks = Math.max(0, --this.cooldownTicks);
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();
        // Play ambient sound at most every 540 ticks
        if (!this.isDead && this.rand.nextInt(1000) < this.soundTime++) {
            this.soundTime = -540;
            this.playSound(SoundsTC.monolith, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
        }
        // If portal anchor is missing, destroy portal
        if(this.getPortalAnchor() == null) {
            this.setDead();
        }
    }

    @Override
    public void move(MoverType type, double x, double y, double z) {
        // Do nothing
    }

    @Override
    public float getBrightness() {
        return 1.0F;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        this.setLinkX(compound.getInteger("linkX"));
        this.setLinkY(compound.getInteger("linkY"));
        this.setLinkZ(compound.getInteger("linkZ"));
        this.setLinkDim(compound.getInteger("linkDim"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("linkX", this.getLinkX());
        compound.setInteger("linkY", this.getLinkY());
        compound.setInteger("linkZ", this.getLinkZ());
        compound.setInteger("linkDim", this.getLinkDim());
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        if (!this.world.isRemote && this.cooldownTicks <= 0) {
            this.cooldownTicks = 3; // Prevent multiple events per click with a short cooldown

            BlockPos linkPos = new BlockPos(this.getLinkX(), this.getLinkY(), this.getLinkZ());
            World sourceWorld = this.world;
            WorldServer targetWorld = DimensionManager.getWorld(this.getLinkDim());
            if (targetWorld == null) {
                // If the dimension isn't loaded (e.g. the Nether when nobody's there), then force-load it and try again
                DimensionManager.initDimension(this.getLinkDim());
                targetWorld = DimensionManager.getWorld(this.getLinkDim());
            }
            if (targetWorld != null) {
                TilePortalAnchor anchor = this.getPortalAnchor();
                if (anchor != null && anchor.drainForTeleport()) {
                    if (player.world.provider.getDimension() != this.getLinkDim()) {
                        // Change dimensions without spawning a nether portal at the other end
                        player.changeDimension(this.getLinkDim(), (world, entity, yaw) -> {
                        });
                    }
                    player.setPositionAndUpdate(linkPos.getX() + 0.5D, linkPos.getY() + 1.0D, linkPos.getZ() + 0.5D);
                    if (player.world.provider.getDimension() == this.getLinkDim()) {
                        // Only play the portal sound at the target location if not changing dimensions; it will be played automatically otherwise
                        player.world.playSound(null, linkPos.up(), SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.NEUTRAL, 0.6F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                    }
                }
            } else {
                if (player instanceof EntityPlayerMP) {
                    player.sendStatusMessage(StringHelper.getTranslatedTextComponent("void_portal", "chat", "no_world"), true);
                }
                ThaumicWonders.LOGGER.error("Target dimension {} not found!", this.getLinkDim());
            }
        }
        return super.processInitialInteract(player, hand);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (this.world.isRemote) {
            TilePortalAnchor anchor = this.getPortalAnchor();
            if (anchor != null) {
                anchor.doPortalSpawnEffect();
            }
        }
    }

    public @Nullable TilePortalAnchor getPortalAnchor() {
        TileEntity tile = this.world.getTileEntity(this.getPosition().down());
        return tile instanceof TilePortalAnchor ? (TilePortalAnchor) tile : null;
    }

    public int getLinkX() {
        return this.dataManager.get(LINK_X);
    }

    public void setLinkX(int linkX) {
        this.dataManager.set(LINK_X, linkX);
    }

    public int getLinkY() {
        return this.dataManager.get(LINK_Y);
    }

    public void setLinkY(int linkY) {
        this.dataManager.set(LINK_Y, linkY);
    }

    public int getLinkZ() {
        return this.dataManager.get(LINK_Z);
    }

    public void setLinkZ(int linkZ) {
        this.dataManager.set(LINK_Z, linkZ);
    }

    public int getLinkDim() {
        return this.dataManager.get(LINK_DIM);
    }

    public void setLinkDim(int linkDim) {
        this.dataManager.set(LINK_DIM, linkDim);
    }

    public BlockPos getLinkPos() {
        return new BlockPos(this.getLinkX(), this.getLinkY(), this.getLinkZ());
    }

    public void setLinkPos(BlockPos pos) {
        this.setLinkX(pos.getX());
        this.setLinkY(pos.getY());
        this.setLinkZ(pos.getZ());
    }
}
