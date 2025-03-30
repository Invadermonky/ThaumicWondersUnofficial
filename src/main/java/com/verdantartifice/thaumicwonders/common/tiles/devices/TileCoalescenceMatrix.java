package com.verdantartifice.thaumicwonders.common.tiles.devices;

import com.verdantartifice.thaumicwonders.common.blocks.BlocksTW;
import com.verdantartifice.thaumicwonders.common.blocks.devices.BlockCoalescenceMatrix;
import com.verdantartifice.thaumicwonders.common.entities.monsters.EntityCorruptionAvatar;
import com.verdantartifice.thaumicwonders.common.tiles.base.TileTW;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.casters.IInteractWithCaster;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.basic.BlockPillar;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.EntityUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TileCoalescenceMatrix extends TileTW implements ITickable, IInteractWithCaster {
    protected static final int PROGRESS_PER_CHARGE = 50;
    protected static final int MAX_CHARGE = 10;
    protected static final int PLAY_EFFECTS = 4;
    
    protected int tickCounter = 0;
    protected int progress = 0;
    
    public int getProgress() {
        return this.progress;
    }
    
    public void incrementProgress(int amount) {
        this.progress += amount;
        if (this.progress >= PROGRESS_PER_CHARGE) {
            int charge = this.getCharge();
            if (charge < MAX_CHARGE) {
                this.setCharge(charge + 1);
                this.progress -= PROGRESS_PER_CHARGE;
            }
        }
        this.progress = MathHelper.clamp(this.progress, 0, PROGRESS_PER_CHARGE);
    }
    
    public void decrementProgress(int amount) {
        this.progress -= amount;
        if (this.progress < 0) {
            int charge = this.getCharge();
            if (charge > 0) {
                this.setCharge(charge - 1);
                this.progress += PROGRESS_PER_CHARGE;
            }
        }
        this.progress = MathHelper.clamp(this.progress, 0, PROGRESS_PER_CHARGE);
    }
    
    public int getCharge() {
        IBlockState state = this.world.getBlockState(this.pos);
        if (state.getBlock() == BlocksTW.COALESCENCE_MATRIX) {
            return state.getBlock().getMetaFromState(state);
        } else {
            return 0;
        }
    }
    
    public void setCharge(int amount) {
        IBlockState state = this.world.getBlockState(this.pos);
        if (state.getBlock() == BlocksTW.COALESCENCE_MATRIX) {
            this.world.setBlockState(this.pos, state.withProperty(BlockCoalescenceMatrix.CHARGE, Integer.valueOf(amount)));
        }
    }
    
    public boolean isProgressFull() {
        return (this.getCharge() >= MAX_CHARGE) && (this.progress >= PROGRESS_PER_CHARGE);
    }
    
    @Override
    protected void readFromTileNBT(NBTTagCompound compound) {
        this.progress = compound.getShort("progress");
    }
    
    @Override
    protected NBTTagCompound writeToTileNBT(NBTTagCompound compound) {
        compound.setShort("progress", (short)this.progress);
        return compound;
    }
    
    @Override
    public void update() {
        this.tickCounter++;
        if (!this.world.isRemote && this.tickCounter % 20 == 0) {
            if (this.canMakeProgress()) {
                this.drainRifts();
            } else {
                this.decrementProgress(1);
            }
        }
    }

    protected void drainRifts() {
        List<EntityFluxRift> riftList = this.getValidRifts();
        boolean found = false;
        for (EntityFluxRift rift : riftList) {
            double drained = Math.sqrt(rift.getRiftSize());
            this.incrementProgress((int)drained);
            rift.setRiftStability(rift.getRiftStability() - (float)(drained / 15.0D));
            if (this.world.rand.nextInt(33) == 0) {
                rift.setRiftSize(rift.getRiftSize() - 1);
            }
            if (drained >= 1.0D) {
                found = true;
            }
        }
        if (found) {
            this.syncTile(false);
            this.markDirty();
            if (this.tickCounter % 40 == 0) {
                this.world.addBlockEvent(this.pos, this.getBlockType(), PLAY_EFFECTS, this.tickCounter);
            }
        }
    }

    protected List<EntityFluxRift> getValidRifts() {
        List<EntityFluxRift> retVal = new ArrayList<EntityFluxRift>();
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
    
    protected boolean canMakeProgress() {
        return this.isValidPlacement() && !this.isProgressFull() && !this.getValidRifts().isEmpty();
    }
    
    protected boolean isValidPlacement() {
        int i, k;
        
        // Check main void metal blocks
        for (i = -2; i <= 2; i++) {
            for (k = -3; k <= 3; k++) {
                if (this.world.getBlockState(this.pos.add(i, -1, k)).getBlock() != BlocksTC.metalBlockVoid) {
                    return false;
                }
            }
            if (this.world.getBlockState(this.pos.add(-3, -1, i)).getBlock() != BlocksTC.metalBlockVoid) {
                return false;
            }
            if (this.world.getBlockState(this.pos.add(3, -1, i)).getBlock() != BlocksTC.metalBlockVoid) {
                return false;
            }
        }
        
        // Check arcane brick blocks
        for (i = -2; i <= 2; i++) {
            if (this.world.getBlockState(this.pos.add(i, -1, -4)).getBlock() != BlocksTC.stoneArcaneBrick) {
                return false;
            }
            if (this.world.getBlockState(this.pos.add(i, -1, 4)).getBlock() != BlocksTC.stoneArcaneBrick) {
                return false;
            }
            if (this.world.getBlockState(this.pos.add(-4, -1, i)).getBlock() != BlocksTC.stoneArcaneBrick) {
                return false;
            }
            if (this.world.getBlockState(this.pos.add(4, -1, i)).getBlock() != BlocksTC.stoneArcaneBrick) {
                return false;
            }
        }
        if (this.world.getBlockState(this.pos.add(-3, -1, -3)).getBlock() != BlocksTC.stoneArcaneBrick) {
            return false;
        }
        if (this.world.getBlockState(this.pos.add(-3, -1, 3)).getBlock() != BlocksTC.stoneArcaneBrick) {
            return false;
        }
        if (this.world.getBlockState(this.pos.add(3, -1, -3)).getBlock() != BlocksTC.stoneArcaneBrick) {
            return false;
        }
        if (this.world.getBlockState(this.pos.add(3, -1, 3)).getBlock() != BlocksTC.stoneArcaneBrick) {
            return false;
        }
        
        // Check pillars
        if (!(this.world.getBlockState(this.pos.add(-4, 0, -2)).getBlock() instanceof BlockPillar)) {
            return false;
        }
        if (!(this.world.getBlockState(this.pos.add(-4, 0, 2)).getBlock() instanceof BlockPillar)) {
            return false;
        }
        if (!(this.world.getBlockState(this.pos.add(4, 0, -2)).getBlock() instanceof BlockPillar)) {
            return false;
        }
        if (!(this.world.getBlockState(this.pos.add(4, 0, 2)).getBlock() instanceof BlockPillar)) {
            return false;
        }
        if (!(this.world.getBlockState(this.pos.add(-2, 0, -4)).getBlock() instanceof BlockPillar)) {
            return false;
        }
        if (!(this.world.getBlockState(this.pos.add(-2, 0, 4)).getBlock() instanceof BlockPillar)) {
            return false;
        }
        if (!(this.world.getBlockState(this.pos.add(2, 0, -4)).getBlock() instanceof BlockPillar)) {
            return false;
        }
        if (!(this.world.getBlockState(this.pos.add(2, 0, 4)).getBlock() instanceof BlockPillar)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean onCasterRightClick(World world, ItemStack stack, EntityPlayer player, BlockPos pos, EnumFacing facing, EnumHand hand) {
        if (this.getCharge() >= MAX_CHARGE) {
            List<BlockPos> pillarList = new ArrayList<BlockPos>();
            pillarList.add(pos.add(-4, 1, -2));
            pillarList.add(pos.add(-4, 1, 2));
            pillarList.add(pos.add(4, 1, -2));
            pillarList.add(pos.add(4, 1, 2));
            pillarList.add(pos.add(-2, 1, -4));
            pillarList.add(pos.add(-2, 1, 4));
            pillarList.add(pos.add(2, 1, -4));
            pillarList.add(pos.add(2, 1, 4));

            if (this.world.isRemote) {
                // Zap the matrix from the pillars
                Color color = new Color(Aspect.FLUX.getColor());
                float r = color.getRed() / 255.0F;
                float g = color.getGreen() / 255.0F;
                float b = color.getBlue() / 255.0F;
                for (BlockPos pillarPos : pillarList) {
                    FXDispatcher.INSTANCE.arcBolt(
                            pillarPos.getX() + 0.5D, 
                            pillarPos.getY() + 1.0D, 
                            pillarPos.getZ() + 0.5D, 
                            pos.getX() + 0.5D, 
                            pos.getY() + 1.0D, 
                            pos.getZ() + 0.5D, 
                            r, g, b, 0.6F);            
                }
            } else {
                // Play sound effects
                world.playSound(null, pos, SoundsTC.zap, SoundCategory.BLOCKS, 1.0F, 1.0F);
                
                // Detonate
                world.setBlockToAir(pos);
                world.createExplosion(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 2.0F, true);
                for (BlockPos pillarPos : pillarList) {
                    world.createExplosion(null, pillarPos.getX() + 0.5D, pillarPos.getY() + 0.5D, pillarPos.getZ() + 0.5D, 2.0F, true);
                }
                
                // Summon the avatar
                EntityCorruptionAvatar avatar = new EntityCorruptionAvatar(world);
                avatar.setLocationAndAngles(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, (float)world.rand.nextInt(360), 0.0F);
                world.spawnEntity(avatar);
            }
        }
        return true;
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
}
