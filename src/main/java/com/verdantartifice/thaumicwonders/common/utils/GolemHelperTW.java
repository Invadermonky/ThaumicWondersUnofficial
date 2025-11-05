package com.verdantartifice.thaumicwonders.common.utils;

import com.cleanroommc.groovyscript.compat.vanilla.OreDict;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.tasks.TaskHandler;

public class GolemHelperTW {
    public static ItemStack getCarriedMatchingStack(IGolemAPI golem, ItemStack stack) {
        if(stack.isEmpty()) return ItemStack.EMPTY;
        for(ItemStack carried : golem.getCarrying()) {

            if(OreDictionary.itemMatches(stack, carried, false) && ItemStack.areItemStackTagsEqual(stack, carried)) {
                if(stack.getCount() == 1 || carried.getCount() >= stack.getCount()) {
                    return carried;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    public static boolean isCarryingStack(IGolemAPI golem, ItemStack stack) {
        return !getCarriedMatchingStack(golem, stack).isEmpty();
    }

    public static boolean isTaskActive(World world, int taskId) {
        if (taskId == Integer.MIN_VALUE) return false;
        Task task = TaskHandler.getTask(world.provider.getDimension(), taskId);
        return !(task == null || task.isCompleted() || task.isSuspended() || task.isReserved());
    }

    public static int createMoveToSealTask(World world, ISealEntity seal) {
        Task task = new Task(seal.getSealPos(), seal.getSealPos().pos);
        task.setPriority(seal.getPriority());
        task.setLifespan((short) 10);
        TaskHandler.addTask(world.provider.getDimension(), task);
        return task.getId();
    }

    public static int createMoveToEntityTask(Entity entity, ISealEntity seal) {
        Task task = new Task(seal.getSealPos(), entity);
        task.setPriority(seal.getPriority());
        task.setLifespan((short) 10);
        TaskHandler.addTask(entity.world.provider.getDimension(), task);
        return task.getId();
    }

    public static int createMoveToPositionTask(World world, ISealEntity seal, BlockPos pos) {
        Task task = new Task(seal.getSealPos(), pos);
        task.setPriority(seal.getPriority());
        task.setLifespan((short) 10);
        TaskHandler.addTask(world.provider.getDimension(), task);
        return task.getId();
    }

    public static boolean isGolemValidForTask(IGolemAPI golem, Task task, int taskId, ItemStack requiredStack) {
        return task.getId() == taskId && !GolemHelperTW.getCarriedMatchingStack(golem, requiredStack).isEmpty();
    }

    public static void giveItemToPlayer(EntityPlayer player, IGolemAPI golem, ItemStack stack) {
        ItemStack heldStack = getCarriedMatchingStack(golem, stack);
        if(!heldStack.isEmpty()) {
            stack = heldStack.splitStack(stack.getCount());
            if(!player.inventory.addItemStackToInventory(stack)) {
                if(!player.world.isRemote) {
                    EntityItem entityItem = new EntityItem(player.world, golem.getGolemEntity().posX, golem.getGolemEntity().posY + 0.1, golem.getGolemEntity().posZ, stack);
                    entityItem.motionY += 0.1;
                    player.world.spawnEntity(entityItem);
                }
            }
            golem.swingArm();
            golem.addRankXp(1);
        }
    }
}
