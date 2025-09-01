package com.verdantartifice.thaumicwonders.common.init;

import com.verdantartifice.thaumicwonders.common.fluids.FluidQuicksilver;
import com.verdantartifice.thaumicwonders.common.items.ItemsTW;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectEventProxy;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.internal.CommonInternals;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

@SuppressWarnings("ConstantConditions")
public class InitAspects {
    public static void initAspects(AspectEventProxy registry) {
        registerItemAspects(registry);
        registerEntityAspects(registry);
    }

    private static void registerItemAspects(AspectEventProxy registry) {
        appendAspects(registry, FluidUtil.getFilledBucket(new FluidStack(FluidQuicksilver.INSTANCE, 1000)), new AspectList().add(Aspect.DEATH, 15).add(Aspect.ALCHEMY, 15));
        appendAspects(registry, new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 0), new AspectList().add(Aspect.METAL, 20).add(Aspect.EARTH, 5).add(Aspect.FLUX, 10));
        appendAspects(registry, new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 1), new AspectList().add(Aspect.METAL, 20).add(Aspect.EARTH, 5).add(Aspect.FLUX, 10).add(Aspect.DESIRE, 15));
        appendAspects(registry, new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 2), new AspectList().add(Aspect.METAL, 20).add(Aspect.EARTH, 5).add(Aspect.FLUX, 10).add(Aspect.EXCHANGE, 15));
        appendAspects(registry, new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 3), new AspectList().add(Aspect.METAL, 20).add(Aspect.EARTH, 5).add(Aspect.FLUX, 10).add(Aspect.CRYSTAL, 15));
        appendAspects(registry, new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 4), new AspectList().add(Aspect.METAL, 20).add(Aspect.EARTH, 5).add(Aspect.FLUX, 10).add(Aspect.DESIRE, 15));
        appendAspects(registry, new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 5), new AspectList().add(Aspect.METAL, 20).add(Aspect.EARTH, 5).add(Aspect.FLUX, 10).add(Aspect.ORDER, 15));
        appendAspects(registry, new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 6), new AspectList().add(Aspect.METAL, 20).add(Aspect.EARTH, 5).add(Aspect.FLUX, 10).add(Aspect.ALCHEMY, 10).add(Aspect.DEATH, 10));
        appendAspects(registry, new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 7), new AspectList().add(Aspect.FLUX, 10).add(Aspect.CRYSTAL, 15));
        appendAspects(registry, new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 8), new AspectList().add(Aspect.METAL, 10).add(Aspect.FLUX, 20));
    }

    @SuppressWarnings("deprecation")
    private static void registerEntityAspects(AspectEventProxy registry) {
        ThaumcraftApi.registerEntityTag("thaumicwonders.corruption_avatar", new AspectList().add(Aspect.ELDRITCH, 40).add(Aspect.MAN, 40).add(Aspect.FLUX, 40));
    }

    private static void appendAspects(AspectEventProxy registry, ItemStack stack, AspectList toAdd) {
        toAdd = toAdd.copy();
        AspectList existing = ThaumcraftCraftingManager.getObjectTags(stack);
        if (existing != null) {
            toAdd = toAdd.add(existing);
        }
        registry.registerObjectTag(stack, toAdd);
    }
}
