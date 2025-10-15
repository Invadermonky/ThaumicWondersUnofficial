package com.verdantartifice.thaumicwonders.common.golems.seals;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.utils.GolemHelperTW;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
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

public class SealShearing implements ISeal, ISealGui, ISealConfigArea {
    public static final ResourceLocation ICON = new ResourceLocation(ThaumicWonders.MODID, "items/seal_shearing");

    private final ItemStack SHEARS;
    private int delay;
    private int shearTaskId;
    private boolean wait;

    public SealShearing() {
        this.SHEARS = new ItemStack(Items.SHEARS);
        this.delay = new Random(System.nanoTime()).nextInt(200);
        this.wait = false;
        this.shearTaskId = Integer.MIN_VALUE;
    }

    @Override
    public String getKey() {
        return "thaumicwonders:shearing";
    }

    @Override
    public boolean canPlaceAt(World world, BlockPos blockPos, EnumFacing enumFacing) {
        return !world.isAirBlock(blockPos);
    }

    @Override
    public void tickSeal(World world, ISealEntity seal) {
        this.delay++;
        if(this.delay % 300 == 0 && !this.wait) {
            AxisAlignedBB area = GolemHelper.getBoundsForArea(seal);
            for(EntityLivingBase entityLiving : world.getEntitiesWithinAABB(EntityLivingBase.class, area)) {
                if(this.isShearable(entityLiving)) {
                    this.shearTaskId = GolemHelperTW.createMoveToEntityTask(entityLiving, seal);
                    this.wait = true;
                    return;
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

    @Override
    public void onTaskStarted(World world, IGolemAPI iGolemAPI, Task task) {}

    @Override
    public boolean onTaskCompletion(World world, IGolemAPI golem, Task task) {
        Entity entity = task.getEntity();
        if(this.isShearable(entity)) {
            IShearable shearable = (IShearable) entity;
            List<ItemStack> drops = shearable.onSheared(this.getShears(), world, entity.getPosition(), 0);
            for(ItemStack stack : drops) {
                entity.entityDropItem(stack, 1.0f);
            }
            golem.addRankXp(1);
            golem.swingArm();
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
