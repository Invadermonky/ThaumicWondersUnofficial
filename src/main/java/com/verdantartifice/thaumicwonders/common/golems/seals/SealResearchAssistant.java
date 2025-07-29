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
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;

public class SealResearchAssistant implements ISeal, ISealConfigArea, ISealConfigToggles {
    public static final ResourceLocation ICON = new ResourceLocation(ThaumicWonders.MODID, "items/seal_research_assistant");

    /*
        TODO:
            In order for this seal to work, it needs to patch into ContainerResearchTable#enchantItem() and request items
            from the golem seal. The exact injection point will be before the return statement in the card.getRequiredItems() loop.
            If the method call is going to return false, it needs to request the required items from the golem.
            -
            The seal will request items and wait for the golem to put them in the player's inventory.
            -
            Maybe add toggles for restocking paper and refilling the scribing tools.
     */

    @Override
    public String getKey() {
        return "thaumicwonders:research_assistant";
    }

    @Override
    public boolean canPlaceAt(World world, BlockPos blockPos, EnumFacing enumFacing) {
        return world.isAirBlock(blockPos);
    }

    @Override
    public void tickSeal(World world, ISealEntity iSealEntity) {

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
    public void onRemoval(World world, BlockPos blockPos, EnumFacing enumFacing) {}

    @Override
    public Object returnContainer(World world, EntityPlayer entityPlayer, BlockPos blockPos, EnumFacing enumFacing, ISealEntity iSealEntity) {
        return null;
    }

    @Override
    public Object returnGui(World world, EntityPlayer entityPlayer, BlockPos blockPos, EnumFacing enumFacing, ISealEntity iSealEntity) {
        return null;
    }

    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return new EnumGolemTrait[]{EnumGolemTrait.SMART, EnumGolemTrait.DEFT};
    }

    @Override
    public EnumGolemTrait[] getForbiddenTags() {
        return new EnumGolemTrait[]{EnumGolemTrait.CLUMSY};
    }

    @Override
    public SealToggle[] getToggles() {
        return new SealToggle[0];
    }

    @Override
    public void setToggle(int i, boolean b) {

    }
}
