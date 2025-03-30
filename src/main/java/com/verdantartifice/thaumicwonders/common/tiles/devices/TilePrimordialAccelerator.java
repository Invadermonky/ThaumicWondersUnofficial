package com.verdantartifice.thaumicwonders.common.tiles.devices;

import com.verdantartifice.thaumicwonders.common.blocks.BlocksTW;
import com.verdantartifice.thaumicwonders.common.items.ItemsTW;
import com.verdantartifice.thaumicwonders.common.tiles.base.TileTWInventory;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.lib.SoundsTC;

public class TilePrimordialAccelerator extends TileTWInventory implements ITickable {
    protected static final int MAX_TUNNELS = 10;
    
    public TilePrimordialAccelerator() {
        super(1);
    }
    
    @Override
    public int getInventoryStackLimit() {
        return 1;
    }
    
    @Override
    public void update() {
        IBlockState state = this.world.getBlockState(this.pos);
        if (!this.world.isRemote && state != null && state.getBlock() == BlocksTW.PRIMORDIAL_ACCELERATOR) {
            boolean powered = !state.getValue(IBlockEnabled.ENABLED);   // "enabled" means no redstone signal in TC
            ItemStack pearlStack = this.getStackInSlot(0);
            
            if (powered && pearlStack != null && !pearlStack.isEmpty() && pearlStack.getItem() == ItemsTC.primordialPearl && pearlStack.getItemDamage() < 7) {
                EnumFacing facing = state.getValue(IBlockFacingHorizontal.FACING);
                BlockPos curPos = this.pos;
                int tunnelCount = 0;
                int index = 0;
                boolean done = false;
                
                // Consume the loaded pearl and play sound effects
                this.setInventorySlotContents(0, ItemStack.EMPTY);
                this.world.playSound(null, this.pos, SoundsTC.zap, SoundCategory.BLOCKS, 1.0F, (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F + 1.0F);
                this.world.playSound(null, this.pos, SoundsTC.wind, SoundCategory.BLOCKS, 1.0F, (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F + 1.0F);
                
                while (index < (2 * MAX_TUNNELS) && !done) {
                    index++;
                    curPos = curPos.offset(facing);
                    IBlockState curState = this.world.getBlockState(curPos);
                    if (curState.getBlock() == BlocksTW.PRIMORDIAL_ACCELERATOR_TERMINUS) {
                        EnumFacing curFacing = curState.getValue(IBlockFacingHorizontal.FACING);
                        if (curFacing == facing) {
                            // Drop grains and stop safely
                            this.completeReaction(curPos, tunnelCount, pearlStack);
                        } else {
                            // Explode!
                            this.explode(curPos);
                        }
                        done = true;
                    } else if (curState.getBlock() == BlocksTW.PRIMORDIAL_ACCELERATOR_TUNNEL) {
                        EnumFacing curFacing = curState.getValue(IBlockFacingHorizontal.FACING);
                        if (curFacing == facing && tunnelCount < MAX_TUNNELS) {
                            // Increment tunnel count and continue
                            tunnelCount++;
                        } else {
                            // Explode!
                            this.explode(curPos);
                            done = true;
                        }
                    } else if (curState.getMaterial() == Material.AIR) {
                        // Stop safely
                        done = true;
                    } else {
                        // Explode!
                        this.explode(curPos);
                        done = true;
                    }
                }
                if (!done) {
                    this.explode(curPos);
                }
            }
        }
    }

    protected void completeReaction(BlockPos terminusPos, int tunnelCount, ItemStack pearlStack) {
        int count = MathHelper.clamp(pearlStack.getMaxDamage() - pearlStack.getItemDamage(), 0, 8);
        for (int index = 0; index < count; index++) {
            this.ejectGrain(terminusPos);
            if (this.world.rand.nextInt(2 * MAX_TUNNELS) < (MAX_TUNNELS + tunnelCount)) {
                // 50-100% chance of second grain
                this.ejectGrain(terminusPos);
            }
            if (this.world.rand.nextInt(2 * MAX_TUNNELS) < (tunnelCount)) {
                // 0-50% chance of third grain
                this.ejectGrain(terminusPos);
            }
        }
    }
    
    protected void ejectGrain(BlockPos ejectPos) {
        ItemStack stack = new ItemStack(ItemsTW.PRIMORDIAL_GRAIN);
        EntityItem entity = new EntityItem(this.world, ejectPos.getX() + 0.5D, ejectPos.getY() + 1.0D, ejectPos.getZ() + 0.5D, stack);
        entity.motionX = this.world.rand.nextGaussian() * 0.1D;
        entity.motionY = 0.3D;
        entity.motionZ = this.world.rand.nextGaussian() * 0.1D;
        world.spawnEntity(entity);
    }
    
    protected void explode(BlockPos explosionPos) {
        this.world.createExplosion(null, explosionPos.getX() + 0.5D, explosionPos.getY() + 0.5D, explosionPos.getZ() + 0.5D, 4.0F, true);
    }
}
