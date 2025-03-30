package com.verdantartifice.thaumicwonders.common.misc;

import com.verdantartifice.thaumicwonders.common.entities.EntityPrimalArrow;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BehaviorDispensePrimalArrow extends BehaviorProjectileDispense {
    @Override
    protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
        EntityPrimalArrow entity = new EntityPrimalArrow(worldIn, position.getX(), position.getY(), position.getZ());
        entity.setArrowType(stackIn.getMetadata());
        return entity;
    }
}
