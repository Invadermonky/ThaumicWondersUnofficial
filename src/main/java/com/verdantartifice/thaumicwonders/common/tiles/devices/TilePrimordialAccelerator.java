package com.verdantartifice.thaumicwonders.common.tiles.devices;

import com.verdantartifice.thaumicwonders.common.blocks.BlocksTW;
import com.verdantartifice.thaumicwonders.common.crafting.accelerator.AcceleratorRecipe;
import com.verdantartifice.thaumicwonders.common.crafting.accelerator.AcceleratorRecipeRegistry;
import com.verdantartifice.thaumicwonders.common.tiles.base.TileTWInventory;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.lib.SoundsTC;

import java.util.Arrays;

public class TilePrimordialAccelerator extends TileTWInventory implements ITickable {
    public static final int MAX_TUNNELS = 10;

    private @Nullable AcceleratorRecipe recipe = null;

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
            ItemStack input = this.getStackInSlot(0);
            this.recipe = AcceleratorRecipeRegistry.getRecipe(input);

            if (powered && input != null && !input.isEmpty() && this.recipe != null) {
                EnumFacing facing = state.getValue(IBlockFacingHorizontal.FACING);
                BlockPos curPos = this.pos;
                int tunnelCount = 0;
                int index = 0;
                boolean done = false;


                // Consume the loaded item and play sound effects
                int count = Arrays.stream(this.recipe.getInput().getMatchingStacks())
                        .filter(ingredient -> !ingredient.isEmpty()).findFirst()
                        .map(ItemStack::getCount).orElse(1);
                if (input.getItem().hasContainerItem(input)) {
                    ItemStack container = input.getItem().getContainerItem(input).copy();
                    container.setCount(count);
                    this.ejectInput(container, facing);
                }
                this.decrStackSize(0, count);
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
                            this.completeReaction(curPos, tunnelCount);
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

    protected void completeReaction(BlockPos terminusPos, int tunnelCount) {
        if (this.recipe != null) {
            NonNullList<ItemStack> outputs = this.recipe.getOutputs(this.world.rand, tunnelCount);
            outputs.forEach(output -> this.ejectOutput(output, terminusPos));
        }
    }

    protected void ejectInput(ItemStack input, EnumFacing facing) {
        BlockPos outputPos = this.getPos().offset(facing.getOpposite());
        EntityItem entityItem = new EntityItem(this.world, outputPos.getX() + 0.5D, outputPos.getY() + 0.5D, outputPos.getZ() + 0.5D, input.copy());
        entityItem.motionX = 0.15 * facing.getOpposite().getDirectionVec().getX();
        entityItem.motionY = 0.1;
        entityItem.motionZ = 0.15 * facing.getOpposite().getDirectionVec().getZ();
        world.spawnEntity(entityItem);
    }

    protected void ejectOutput(ItemStack output, BlockPos ejectPos) {
        EntityItem entity = new EntityItem(this.world, ejectPos.getX() + 0.5D, ejectPos.getY() + 1.0D, ejectPos.getZ() + 0.5D, output.copy());
        entity.motionX = this.world.rand.nextGaussian() * 0.1D;
        entity.motionY = 0.3D;
        entity.motionZ = this.world.rand.nextGaussian() * 0.1D;
        world.spawnEntity(entity);
    }

    protected void explode(BlockPos explosionPos) {
        this.world.createExplosion(null, explosionPos.getX() + 0.5D, explosionPos.getY() + 0.5D, explosionPos.getZ() + 0.5D, 4.0F, true);
    }

    public void dropInventoryContents() {
        if (!this.world.isRemote) {
            ItemStack stack = this.getStackInSlot(0);
            if (!stack.isEmpty()) {
                Block.spawnAsEntity(this.world, this.getPos(), stack.copy());
            }
        }
    }
}
