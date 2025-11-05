package com.verdantartifice.thaumicwonders.common.golems.seals;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.utils.GolemHelperTW;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.ISealGui;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.client.gui.SealBaseContainer;
import thaumcraft.common.golems.client.gui.SealBaseGUI;

import java.util.Random;

//TODO: Finish this
public class SealFishing implements ISeal, ISealGui {
    public static final ResourceLocation ICON = new ResourceLocation(ThaumicWonders.MODID, "items/seal_fishing");
    private static final ItemStack FISHING_ROD = new ItemStack(Items.FISHING_ROD, 1, OreDictionary.WILDCARD_VALUE);

    private int delay;
    private int fishingTimer;
    private int fishingTaskId;
    private boolean wait = false;

    public SealFishing() {
        this.delay = new Random(System.nanoTime()).nextInt(50);
        this.fishingTimer = 0;
        this.fishingTaskId = Integer.MIN_VALUE;
        this.wait = false;
    }

    @Override
    public String getKey() {
        return "thaumicwonders:fishing";
    }

    @Override
    public boolean canPlaceAt(World world, BlockPos blockPos, EnumFacing facing) {
        if(facing == EnumFacing.UP && world.isAirBlock(blockPos.offset(facing))) {
            for (EnumFacing horizontal : EnumFacing.HORIZONTALS) {
                if (isValidFishingPool(world, blockPos, horizontal)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void tickSeal(World world, ISealEntity iSealEntity) {
        this.delay++;
        if(this.fishingTimer > 0) {
            this.fishingTimer--;
        } else {

        }

        if(!this.wait && this.fishingTimer <= 0 && this.delay % 200 == 0) {

        }
    }

    @Override
    public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
        return !GolemHelperTW.isCarryingStack(golem, FISHING_ROD);
    }

    @Override
    public void onTaskStarted(World world, IGolemAPI iGolemAPI, Task task) {

    }

    @Override
    public boolean onTaskCompletion(World world, IGolemAPI golem, Task task) {
        if(this.fishingTimer <= 0) {
            task.setSuspended(true);
            golem.swingArm();
            golem.addRankXp(1);
        } else {
            task.setReserved(true);
            task.setLifespan((short) 10);
        }
        return false;
    }

    @Override
    public void onTaskSuspension(World world, Task task) {
        this.wait = false;
    }

    private boolean isValidFishingPool(World world, BlockPos sealPos, EnumFacing direction) {
        BlockPos offsetPos = sealPos.offset(direction);
        for(int i = 0; i > -2; i--) {
            for (int j = 0; j < 3; j++) {
                BlockPos center = offsetPos.add(direction.getXOffset() * j, i, direction.getZOffset() * j);
                BlockPos right = center.offset(direction.rotateY());
                BlockPos left = center.offset(direction.rotateYCCW());
                if(!(isWaterSourceBlock(world, center) && isWaterSourceBlock(world, right) && isWaterSourceBlock(world, left))) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isWaterSourceBlock(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return state.getMaterial() == Material.WATER && state.getBlock() == Blocks.WATER && state.getValue(BlockLiquid.LEVEL) == 0;
    }

    private ItemStack getFishingResult(World world, IGolemAPI golem) {
        return ItemStack.EMPTY;
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbtTagCompound) {

    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbtTagCompound) {

    }

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
        return new EnumGolemTrait[] {EnumGolemTrait.SMART, EnumGolemTrait.DEFT};
    }

    @Override
    public EnumGolemTrait[] getForbiddenTags() {
        return new EnumGolemTrait[] {EnumGolemTrait.CLUMSY, EnumGolemTrait.LIGHT};
    }

    @Override
    public int[] getGuiCategories() {
        return new int[] {CAT_PRIORITY, CAT_TAGS};
    }
}
