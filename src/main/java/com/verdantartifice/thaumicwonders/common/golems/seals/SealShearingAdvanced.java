package com.verdantartifice.thaumicwonders.common.golems.seals;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.utils.GolemHelperTW;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.util.Constants;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.ISealGui;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.client.gui.SealBaseContainer;
import thaumcraft.common.golems.client.gui.SealBaseGUI;

import java.util.List;
import java.util.Random;

public class SealShearingAdvanced implements ISeal, ISealGui, ISealConfigArea {
    public static final ResourceLocation ICON = new ResourceLocation(ThaumicWonders.MODID, "items/seal_shearing_advanced");

    private final ItemStack SHEARS;
    private int delay;
    private int shearTaskId;
    private boolean wait;

    public SealShearingAdvanced() {
        this.SHEARS = new ItemStack(Items.SHEARS);
        this.delay = new Random(System.nanoTime()).nextInt(200);
        this.wait = false;
        this.shearTaskId = Integer.MIN_VALUE;
    }

    @Override
    public String getKey() {
        return "thaumicwonders:shearing_advanced";
    }

    @Override
    public boolean canPlaceAt(World world, BlockPos blockPos, EnumFacing enumFacing) {
        return !world.isAirBlock(blockPos);
    }

    @Override
    public void tickSeal(World world, ISealEntity seal) {
        this.delay++;
        if(this.delay % 200 == 0 && !this.wait) {
            AxisAlignedBB area = GolemHelper.getBoundsForArea(seal);
            //Searching for shearable entities
            for(EntityLivingBase entityLiving : world.getEntitiesWithinAABB(EntityLivingBase.class, area)) {
                if(this.isShearable(entityLiving)) {
                    this.shearTaskId = GolemHelperTW.createMoveToEntityTask(entityLiving, seal);
                    this.wait = true;
                    return;
                }
            }
            //Searching for harvestable blocks
            for(int x = (int) area.minX; x <= area.maxX; x++) {
                for(int y = (int) area.minY; y <= area.maxY; y++) {
                    for(int z = (int) area.minZ; z <= area.maxZ; z++) {
                        BlockPos checkPos = new BlockPos(x, y, z);
                        IBlockState state = world.getBlockState(checkPos);
                        if(!world.isAirBlock(checkPos) && this.isShearable(state)) {
                            this.shearTaskId = GolemHelperTW.createMoveToPositionTask(world, seal, checkPos);
                            this.wait = true;
                            return;
                        }
                    }
                }
            }
        }
    }

    private ItemStack getShears() {
        SHEARS.setItemDamage(0);
        return SHEARS;
    }

    private boolean isShearable(Entity target) {
        return target instanceof EntityLivingBase && target.isEntityAlive() && target instanceof IShearable
                && ((IShearable) target).isShearable(this.getShears(), target.world, target.getPosition());
    }

    private boolean isShearable(IBlockState state) {
        return state.getBlock() instanceof IShearable;
    }

    @Override
    public void onTaskStarted(World world, IGolemAPI iGolemAPI, Task task) {}

    @Override
    public boolean onTaskCompletion(World world, IGolemAPI golem, Task task) {
        Entity entity = task.getEntity();
        if(entity != null) {
            if (this.isShearable(entity)) {
                IShearable shearable = (IShearable) entity;
                List<ItemStack> drops = shearable.onSheared(this.getShears(), world, entity.getPosition(), 0);
                for (ItemStack stack : drops) {
                    entity.entityDropItem(stack, 1.0f);
                }
                golem.addRankXp(1);
                golem.swingArm();
            }
        } else if(task.getPos() != null){
            BlockPos shearPos = task.getPos();
            IBlockState state = world.getBlockState(shearPos);
            if(this.isShearable(state)) {
                IShearable shearable = (IShearable) state.getBlock();
                List<ItemStack> drops = shearable.onSheared(this.getShears(), world, shearPos, 0);
                for(ItemStack drop : drops) {
                    Block.spawnAsEntity(world, shearPos, drop);
                }
                world.playSound(null, shearPos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS, 1.0f, 1.0f);
                world.playEvent(Constants.WorldEvents.BREAK_BLOCK_EFFECTS, shearPos, Block.getStateId(state));
                world.setBlockToAir(shearPos);
                golem.addRankXp(1);
                golem.swingArm();
            }
        }
        task.setSuspended(true);
        this.wait = false;
        return true;
    }

    @Override
    public void onTaskSuspension(World world, Task task) {
        this.wait = false;
    }

    @Override
    public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
        return golem.getProperties().getArms().key.equals("CLAWS");
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbtTagCompound) {}

    @Override
    public void writeCustomNBT(NBTTagCompound nbtTagCompound) {}

    @Override
    public ResourceLocation getSealIcon() {
        return ICON;
    }

    @Override
    public void onRemoval(World world, BlockPos blockPos, EnumFacing enumFacing) {
        this.wait = false;
    }

    @Override
    public Object returnContainer(World world, EntityPlayer entityPlayer, BlockPos blockPos, EnumFacing enumFacing, ISealEntity iSealEntity) {
        return new SealBaseContainer(entityPlayer.inventory, world, iSealEntity);
    }

    @Override
    public Object returnGui(World world, EntityPlayer entityPlayer, BlockPos blockPos, EnumFacing enumFacing, ISealEntity iSealEntity) {
        return new SealBaseGUI(entityPlayer.inventory, world, iSealEntity);
    }

    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return new EnumGolemTrait[] {EnumGolemTrait.SMART};
    }

    @Override
    public EnumGolemTrait[] getForbiddenTags() {
        return new EnumGolemTrait[0];
    }

    @Override
    public int[] getGuiCategories() {
        return new int[] {CAT_AREA, CAT_PRIORITY, CAT_TAGS};
    }
}
