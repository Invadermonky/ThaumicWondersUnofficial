package com.verdantartifice.thaumicwonders.common.items.catalysts;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.items.base.ItemTW;
import com.verdantartifice.thaumicwonders.common.misc.OreHelper;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.ModConfig;

public class ItemAlchemistStone extends ItemTW implements ICatalystStone {
    public ItemAlchemistStone() {
        super("alchemist_stone");
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
            return new ItemStack(ItemsTC.clusters, 1, 0);
        } else if (OreHelper.isOreNamed(input, "oreGold")) {
            return new ItemStack(ItemsTC.clusters, 1, 1);
        } else if (ModConfig.foundCopperOre && OreHelper.isOreNamed(input, "oreCopper")) {
            return new ItemStack(ItemsTC.clusters, 1, 2);
        } else if (ModConfig.foundTinOre && OreHelper.isOreNamed(input, "oreTin")) {
            return new ItemStack(ItemsTC.clusters, 1, 3);
        } else if (ModConfig.foundSilverOre && OreHelper.isOreNamed(input, "oreSilver")) {
            return new ItemStack(ItemsTC.clusters, 1, 4);
        } else if (ModConfig.foundLeadOre && OreHelper.isOreNamed(input, "oreLead")) {
            return new ItemStack(ItemsTC.clusters, 1, 5);
        } else if (OreHelper.isOreNamed(input, "oreCinnabar")) {
            return new ItemStack(ItemsTC.clusters, 1, 6);
        } else {
            if (input != null && !input.isEmpty()) {
                ThaumicWonders.LOGGER.info("ALCHEMIST_STONE: Rejecting item {} with ore names {}", input.toString(), String.join(", ", OreHelper.getOreNames(input).toArray(new String[] {})));
            }
            return null;
        }
    }
    
    @Override
    public int getFluxChance() {
        return 50;
    }
    
    @Override
    public int getSparkleColor() {
        return Aspect.ORDER.getColor();
    }
    
    @Override
    public int getItemEnchantability() {
        return 10;
    }
}
