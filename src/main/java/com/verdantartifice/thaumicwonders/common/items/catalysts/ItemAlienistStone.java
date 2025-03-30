package com.verdantartifice.thaumicwonders.common.items.catalysts;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.items.ItemsTW;
import com.verdantartifice.thaumicwonders.common.items.base.ItemTW;
import com.verdantartifice.thaumicwonders.common.misc.OreHelper;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.ModConfig;

public class ItemAlienistStone extends ItemTW implements ICatalystStone {
    public ItemAlienistStone() {
        super("alienist_stone");
        this.setMaxDamage(63);  // Gets one last use at durability 0
        this.setMaxStackSize(1);
        this.setNoRepair();
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    public ItemStack getRefiningResult(ItemStack input) {
        if (OreHelper.isOreNamed(input, "oreIron")) {
            return new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 0);
        } else if (OreHelper.isOreNamed(input, "oreGold")) {
            return new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 1);
        } else if (ModConfig.foundCopperOre && OreHelper.isOreNamed(input, "oreCopper")) {
            return new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 2);
        } else if (ModConfig.foundTinOre && OreHelper.isOreNamed(input, "oreTin")) {
            return new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 3);
        } else if (ModConfig.foundSilverOre && OreHelper.isOreNamed(input, "oreSilver")) {
            return new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 4);
        } else if (ModConfig.foundLeadOre && OreHelper.isOreNamed(input, "oreLead")) {
            return new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 5);
        } else if (OreHelper.isOreNamed(input, "oreCinnabar")) {
            return new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 6);
        } else if (input.getItem() == ItemsTC.voidSeed) {
            return new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 8);
        } else {
            if (input != null && !input.isEmpty()) {
                ThaumicWonders.LOGGER.info("ALIENIST_STONE: Rejecting item {} with ore names {}", input.toString(), String.join(", ", OreHelper.getOreNames(input).toArray(new String[] {})));
            }
            return null;
        }
    }

    @Override
    public int getFluxChance() {
        return 10;
    }

    @Override
    public int getSparkleColor() {
        return Aspect.FLUX.getColor();
    }
    
    @Override
    public int getItemEnchantability() {
        return 10;
    }
    
    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }
}
