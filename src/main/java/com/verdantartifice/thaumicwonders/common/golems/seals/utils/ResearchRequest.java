package com.verdantartifice.thaumicwonders.common.golems.seals.utils;

import com.verdantartifice.thaumicwonders.common.utils.GolemHelperTW;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.lib.utils.InventoryUtils;

import java.util.Objects;

public class ResearchRequest {
    public EntityPlayer player;
    public ItemStack stack;
    public int taskId;
    public boolean isCompleted;

    public ResearchRequest(EntityPlayer player, ItemStack requested) {
        this.player = player;
        this.stack = requested;
        this.taskId = Integer.MIN_VALUE;
        this.isCompleted = false;
    }

    public void createTask(ISealEntity seal) {
        this.taskId = GolemHelperTW.createMoveToEntityTask(this.player, seal);
    }

    public void updateTask(ISealEntity seal) {
        GolemHelper.requestProvisioning(this.player.world, seal, this.stack);
        if(!GolemHelperTW.isTaskActive(this.player.world, this.taskId)) {
            this.taskId = GolemHelperTW.createMoveToEntityTask(this.player, seal);
        }
    }

    public boolean isGolemValidForTask(IGolemAPI golem, Task task) {
        return GolemHelperTW.isGolemValidForTask(golem, task, this.taskId, this.stack);
    }

    public boolean tryCompleteTask(EntityPlayer player, ISealEntity seal, IGolemAPI golem, Task task) {
        if(this.matches(player) && !GolemHelperTW.getCarriedMatchingStack(golem, this.stack).isEmpty()) {
            GolemHelperTW.giveItemToPlayer(player, golem, this.stack);
            this.isCompleted = true;
            return true;
        }
        return false;
    }

    public boolean shouldRemoveTask(World world) {
        return this.stack == null
                || this.stack.isEmpty()
                || InventoryUtils.isPlayerCarryingAmount(this.player, this.stack, true)
                || this.isCompleted
                || (this.taskId != Integer.MIN_VALUE && !GolemHelperTW.isTaskActive(world, this.taskId));
    }

    public boolean matches(EntityPlayer player) {
        return this.player.getUniqueID().equals(player.getUniqueID());
    }

    public boolean matches(ItemStack stack) {
        return ItemStack.areItemsEqual(this.stack, stack) && ItemStack.areItemStackTagsEqual(this.stack, stack);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ResearchRequest)) return false;
        ResearchRequest that = (ResearchRequest) object;
        return this.matches(that.stack) && this.matches(that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, stack);
    }
}
