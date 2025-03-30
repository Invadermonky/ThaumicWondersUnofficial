package com.verdantartifice.thaumicwonders.common.entities;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.network.PacketHandler;
import com.verdantartifice.thaumicwonders.common.network.packets.PacketLocalizedMessage;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TilePortalAnchor;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TilePortalGenerator;
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
import thaumcraft.api.aura.AuraHelper;
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
        this.setSize(1.5F, 3.0F);
    }

    @Override
    protected void entityInit() {
        this.dataManager.register(LINK_X, 0);
        this.dataManager.register(LINK_Y, 0);
        this.dataManager.register(LINK_Z, 0);
        this.dataManager.register(LINK_DIM, 0);
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
    
    public int getLinkX() {
        return this.dataManager.get(LINK_X);
    }
    
    public int getLinkY() {
        return this.dataManager.get(LINK_Y);
    }
    
    public int getLinkZ() {
        return this.dataManager.get(LINK_Z);
    }
    
    public int getLinkDim() {
        return this.dataManager.get(LINK_DIM);
    }
    
    public void setLinkX(int linkX) {
        this.dataManager.set(LINK_X, linkX);
    }
    
    public void setLinkY(int linkY) {
        this.dataManager.set(LINK_Y, linkY);
    }
    
    public void setLinkZ(int linkZ) {
        this.dataManager.set(LINK_Z, linkZ);
    }
    
    public void setLinkDim(int linkDim) {
        this.dataManager.set(LINK_DIM, linkDim);
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
    public void move(MoverType type, double x, double y, double z) {
        // Do nothing
    }
    
    @Override
    public float getBrightness() {
        return 1.0F;
    }
    
    public float getGeneratorStability() {
        TileEntity generatorTile = this.world.getTileEntity(this.getPosition().down());
        if (generatorTile instanceof TilePortalGenerator) {
            return ((TilePortalGenerator)generatorTile).getStability();
        } else {
            return 0.0F;
        }
    }
    
    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        if (!this.world.isRemote && this.cooldownTicks <= 0) {
            this.cooldownTicks = 3; // Prevent multiple events per click with a short cooldown
            
            // Compute target variance due to instability
            int dx = 0;
            int dz = 0;
            float stability = this.getGeneratorStability();
            if (stability < -25.0F) {
                int max = (int)(12.5F + (0.5F * Math.abs(stability) - 12.5F) * (0.5F * Math.abs(stability) - 12.5F));
                if (max > 0) {
                    dx = this.world.rand.nextInt(max) - this.world.rand.nextInt(max);
                    dz = this.world.rand.nextInt(max) - this.world.rand.nextInt(max);
                }
            } else if (stability < 0.0F) {
                int max = (int)(0.5F * Math.abs(stability));
                if (max > 0) {
                    dx = this.world.rand.nextInt(max) - this.world.rand.nextInt(max);
                    dz = this.world.rand.nextInt(max) - this.world.rand.nextInt(max);
                }
            }
            
            BlockPos linkPos = new BlockPos(this.getLinkX(), this.getLinkY(), this.getLinkZ());
            BlockPos targetPos = linkPos.add(dx, 0, dz);
            World sourceWorld = this.world;
            WorldServer targetWorld = DimensionManager.getWorld(this.getLinkDim());
            if (targetWorld == null) {
                // If the dimension isn't loaded (e.g. the Nether when nobody's there), then force-load it and try again
                DimensionManager.initDimension(this.getLinkDim());
                targetWorld = DimensionManager.getWorld(this.getLinkDim());
            }
            if (targetWorld != null) {
                TileEntity tile = targetWorld.getTileEntity(linkPos);
                if (tile instanceof TilePortalAnchor) {
                    // Generate source world flux before leaving
                    AuraHelper.polluteAura(sourceWorld, this.getPosition(), 5.0F, true);
                    
                    if (player.world.provider.getDimension() != this.getLinkDim()) {
                        // Change dimensions without spawning a nether portal at the other end
                        player.changeDimension(this.getLinkDim(), (world, entity, yaw) -> {});
                    }
                    player.setPositionAndUpdate(targetPos.getX() + 0.5D, targetPos.getY() + 1.0D, targetPos.getZ() + 0.5D);
                    if (player.world.provider.getDimension() == this.getLinkDim()) {
                        // Only play the portal sound at the target location if not changing dimensions; it will be played automatically otherwise
                        player.world.playSound(null, targetPos.up(), SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.NEUTRAL, 0.25F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                    }
                    
                    // Generate target world flux after leaving
                    AuraHelper.polluteAura(targetWorld, targetPos.up(), 5.0F, true);
                } else {
                    if (player instanceof EntityPlayerMP) {
                        PacketHandler.INSTANCE.sendTo(new PacketLocalizedMessage("event.void_portal.no_anchor"), (EntityPlayerMP)player);
                    }
                }
            } else {
                if (player instanceof EntityPlayerMP) {
                    PacketHandler.INSTANCE.sendTo(new PacketLocalizedMessage("event.void_portal.no_world"), (EntityPlayerMP)player);
                }
                ThaumicWonders.LOGGER.error("Target dimension {} not found!", this.getLinkDim());
            }
        }
        return super.processInitialInteract(player, hand);
    }
    
    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();
        
        // Play ambient sound at most every 540 ticks
        if (!this.isDead && this.rand.nextInt(1000) < this.soundTime++) {
            this.soundTime = -540;
            this.playSound(SoundsTC.monolith, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
        }
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        this.cooldownTicks = Math.max(0, --this.cooldownTicks);
    }
}
