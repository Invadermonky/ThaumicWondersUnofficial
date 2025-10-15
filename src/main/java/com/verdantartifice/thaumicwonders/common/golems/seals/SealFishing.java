package com.verdantartifice.thaumicwonders.common.golems.seals;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.ISealGui;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.client.gui.SealBaseContainer;
import thaumcraft.common.golems.client.gui.SealBaseGUI;

public class SealFishing implements ISeal, ISealGui {
    public static final ResourceLocation ICON = new ResourceLocation(ThaumicWonders.MODID, "items/seal_fishing");

    private int fishingTaskId;

    public SealFishing() {
        this.fishingTaskId = Integer.MIN_VALUE;
    }

    @Override
    public String getKey() {
        return "thaumicwonders:fishing";
    }

    @Override
    public boolean canPlaceAt(World world, BlockPos blockPos, EnumFacing enumFacing) {
        //TODO: Must be adjacent to a body of water.
        return false;
    }

    @Override
    public void tickSeal(World world, ISealEntity iSealEntity) {

    }

    private boolean isValidSealPosition(World world, BlockPos sealPos) {

        return false;
    }

    /**
     * Checks to see whether the passed location connects to a valid fishing area and returns the fishing modifier for the pool.
     * Fishing pools must be a minimum of minimum of 3x3x2 area filled with water. Larger fishing pools return larger modifiers,
     * increasing the speed fish are caught.
     * <p>
     * Small bodies of water will return low values and large bodies of water will return large values.
     * <p>
     * If the fishing pool is not valid, this method will return a value of 0.
     *
     * @param world the world object
     * @param sealPos the seal position
     * @return Returns 0 if it is an invalid fishing pool or a value between 0.5 and 1.5 depending on the fishing pool size.
     */
    private float getFishingPoolModifier(World world, BlockPos sealPos) {
        return 0;
    }

    @Override
    public void onTaskStarted(World world, IGolemAPI iGolemAPI, Task task) {

    }

    @Override
    public boolean onTaskCompletion(World world, IGolemAPI iGolemAPI, Task task) {
        return false;
    }

    @Override
    public void onTaskSuspension(World world, Task task) {

    }

    @Override
    public boolean canGolemPerformTask(IGolemAPI iGolemAPI, Task task) {
        return false;
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
