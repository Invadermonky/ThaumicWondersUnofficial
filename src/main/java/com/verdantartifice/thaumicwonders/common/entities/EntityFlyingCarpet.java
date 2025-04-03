package com.verdantartifice.thaumicwonders.common.entities;

import com.verdantartifice.thaumicwonders.common.items.ItemsTW;
import com.verdantartifice.thaumicwonders.common.items.entities.ItemFlyingCarpet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.RechargeHelper;

import javax.annotation.Nullable;
import java.util.List;

public class EntityFlyingCarpet extends Entity {
    private static final DataParameter<Integer> VIS_CHARGE = EntityDataManager.createKey(EntityFlyingCarpet.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> ENERGY = EntityDataManager.createKey(EntityFlyingCarpet.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> DYE_COLOR = EntityDataManager.createKey(EntityFlyingCarpet.class, DataSerializers.VARINT);

    private float momentum;
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYaw;
    private double lerpPitch;
    private boolean forwardInputDown;
    private boolean backInputDown;

    public EntityFlyingCarpet(World worldIn) {
        super(worldIn);
        this.preventEntitySpawning = true;
        this.setSize(1.0F, 0.0625F);
        this.setNoGravity(true);
    }

    public EntityFlyingCarpet(World worldIn, double x, double y, double z)
    {
        this(worldIn);
        this.setPosition(x, y, z);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
    }
    
    public EntityFlyingCarpet(World worldIn, BlockPos pos) {
        this(worldIn, pos.getX(), pos.getY(), pos.getZ());
    }
    
    @Override
    protected void entityInit() {
        this.dataManager.register(VIS_CHARGE, 0);
        this.dataManager.register(ENERGY, 0);
        this.dataManager.register(DYE_COLOR, -1);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        this.setVisCharge(compound.getInteger("VisCharge"));
        this.setEnergy(compound.getInteger("Energy"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("VisCharge", this.getVisCharge());
        compound.setInteger("Energy", this.getEnergy());
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox() {
        return this.getEntityBoundingBox();
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBox(Entity entityIn) {
        return entityIn.canBePushed() ? entityIn.getEntityBoundingBox() : null;
    }

    @Override
    public boolean canBePushed() {
        return true;
    }

    @Override
    public double getMountedYOffset() {
        return -0.15D;
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isDead;
    }

    @Override
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYaw = yaw;
        this.lerpPitch = pitch;
        this.lerpSteps = 10;
    }

    @Override
    public EnumFacing getAdjustedHorizontalFacing() {
        return this.getHorizontalFacing().rotateY();
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        super.onUpdate();
        this.tickLerp();

        if (this.isBeingRidden() && !this.world.isRemote && this.ticksExisted % 20 == 0) {
            this.consumeEnergy();
            this.setNoGravity(this.hasEnergy());
        }
        if (this.isBeingRidden() && this.canPassengerSteer()) {
            this.updateMotion();
            if (this.world.isRemote && this.hasEnergy()) {
                this.controlCarpet();
            }
            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
        } else {
            this.motionX = 0.0D;
            this.motionY = 0.0D;
            this.motionZ = 0.0D;
            if (this.world.isRemote) {
                this.updateInputs(false, false);
            }
        }
        
        this.doBlockCollisions();
    }
    
    private void consumeEnergy() {
        int energy = this.getEnergy();
        if (energy > 0) {
            energy--;
        } else {
            int visCharge = this.getVisCharge();
            if (visCharge > 0) {
                energy = 30;
                this.setVisCharge(visCharge - 1); 
            }
        }
        this.setEnergy(energy);
    }
    
    private boolean hasEnergy() {
        return (this.getEnergy() > 0 || this.getVisCharge() > 0);
    }
    
    private void tickLerp() {
        if (this.lerpSteps > 0 && !this.canPassengerSteer()) {
            double newX = this.posX + ((this.lerpX - this.posX) / (double)this.lerpSteps);
            double newY = this.posY + ((this.lerpY - this.posY) / (double)this.lerpSteps);
            double newZ = this.posZ + ((this.lerpZ - this.posZ) / (double)this.lerpSteps);
            double deltaYaw = MathHelper.wrapDegrees(this.lerpYaw - (double)this.rotationYaw);
            this.rotationYaw = (float)((double)this.rotationYaw + (deltaYaw / (double)this.lerpSteps));
            this.rotationPitch = (float)((double)this.rotationPitch + ((this.lerpPitch - (double)this.rotationPitch) / (double)this.lerpSteps));
            this.lerpSteps--;
            this.setPosition(newX, newY, newZ);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }
    }
    
    /**
     * Update the carpet's speed, based on momentum
     */
    private void updateMotion() {
        /*
        this.momentum = 0.9F;
        this.motionX *= this.momentum;
        this.motionY *= this.momentum;
        this.motionZ *= this.momentum;
         */
        //TODO: This is controlling the carpet movement speed. Currently caps at 90% running speed.
        //  Need to make this adjustable so it can be used.
        this.momentum = 0.9f;
        this.motionX = MathHelper.clamp(this.motionX * this.momentum, -1.2f, 1.2f);
        this.motionY = MathHelper.clamp(this.motionY * this.momentum, -1.2f, 1.2f);
        this.motionZ = MathHelper.clamp(this.motionZ * this.momentum, -1.2f, 1.2f);
        this.motionY += (this.hasNoGravity() ? 0.0D : -0.04D);
    }
    
    private void controlCarpet() {
        if (this.isBeingRidden()) {
            Entity pilot = this.getControllingPassenger();
            this.prevRotationYaw = this.rotationYaw;
            this.rotationYaw = pilot.rotationYaw;
            
            float f = 0.0F;
            if (this.forwardInputDown) {
                f += 0.03F;
            }
            if (this.backInputDown) {
                f -= 0.005F;
            }
            this.motionX += (MathHelper.sin(-this.rotationYaw * (float)(Math.PI / 180.0D)) * f);
            this.motionY += (MathHelper.sin(-pilot.rotationPitch * (float)(Math.PI / 180.0D)) * f);
            this.motionZ += (MathHelper.cos(this.rotationYaw * (float)(Math.PI / 180.0D)) * f);
        }
    }
    
    public void updateInputs(boolean forwardDown, boolean backwardDown) {
        this.forwardInputDown = forwardDown;
        this.backInputDown = backwardDown;
    }

    /**
     * Applies this carpet's yaw to the given entity. Used to update the orientation of its passenger.
     */
    protected void applyYawToEntity(Entity entityToUpdate) {
        entityToUpdate.setRenderYawOffset(this.rotationYaw);
        float f = MathHelper.wrapDegrees(entityToUpdate.rotationYaw - this.rotationYaw);
        float f1 = MathHelper.clamp(f, -105.0F, 105.0F);
        entityToUpdate.prevRotationYaw += f1 - f;
        entityToUpdate.rotationYaw += f1 - f;
        entityToUpdate.setRotationYawHead(entityToUpdate.rotationYaw);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void applyOrientationToEntity(Entity entityToUpdate) {
        this.applyYawToEntity(entityToUpdate);
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        if (!this.world.isRemote) {
            if (player.isSneaking()) {
                ItemStack itemStack = new ItemStack(ItemsTW.FLYING_CARPET, 1, 0);
                EnumDyeColor color = this.getDyeColor();
                if (color != null) {
                    ((ItemFlyingCarpet)itemStack.getItem()).setDyeColor(itemStack, color);
                }
                itemStack.setTagInfo(RechargeHelper.NBT_TAG, new NBTTagInt(this.getVisCharge()));
                itemStack.setTagInfo("energy", new NBTTagInt(this.getEnergy()));
                this.entityDropItem(itemStack, 0.0F);
                this.setDead();
            } else {
                player.startRiding(this);
            }
        }
        return true;
    }

    public void setVisCharge(int visCharge) {
        this.dataManager.set(VIS_CHARGE, visCharge);
    }
    
    public int getVisCharge() {
        return this.dataManager.get(VIS_CHARGE);
    }
    
    public void setEnergy(int energy) {
        this.dataManager.set(ENERGY, energy);
    }
    
    public int getEnergy() {
        return this.dataManager.get(ENERGY);
    }
    
    public void setDyeColor(EnumDyeColor color) {
        if (color == null) {
            this.dataManager.set(DYE_COLOR, -1);
        } else {
            this.dataManager.set(DYE_COLOR, color.getMetadata());
        }
    }
    
    public EnumDyeColor getDyeColor() {
        int value = this.dataManager.get(DYE_COLOR);
        if (value == -1) {
            return null;
        } else {
            return EnumDyeColor.byMetadata(value);
        }
    }

    @Override
    @Nullable
    public Entity getControllingPassenger() {
        List<Entity> list = this.getPassengers();
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (this.canPassengerSteer() && this.lerpSteps > 0) {
            this.lerpSteps = 0;
            this.posX = this.lerpX;
            this.posY = this.lerpY;
            this.posZ = this.lerpZ;
            this.rotationYaw = (float)this.lerpYaw;
            this.rotationPitch = (float)this.lerpPitch;
        }
    }
    
    @Override
    protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
        // Shield the carpet and passenger from falling damage
        this.fallDistance = 0;
        if (this.isBeingRidden()) {
            this.getControllingPassenger().fallDistance = 0;
        }
    }
}
