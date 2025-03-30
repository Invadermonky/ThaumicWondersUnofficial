package com.verdantartifice.thaumicwonders.common.items.catalysts;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.items.base.ItemTW;
import com.verdantartifice.thaumicwonders.common.misc.OreHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.items.ItemsTC;

public class ItemTransmuterStone extends ItemTW implements ICatalystStone {
    public ItemTransmuterStone() {
        super("transmuter_stone");
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
        if (OreHelper.isOreNamed(input, "oreIron")) {   // Iron to gold
            return new ItemStack(Blocks.GOLD_ORE);
        } else if (OreHelper.isOreNamed(input, "ingotIron")) {
            return new ItemStack(Items.GOLD_INGOT);
        } else if (OreHelper.isOreNamed(input, "blockIron")) {
            return new ItemStack(Blocks.GOLD_BLOCK);
        } else if (OreHelper.isOreNamed(input, "nuggetIron")) {
            return new ItemStack(Items.GOLD_NUGGET);
        } else if (OreHelper.isOreNamed(input, "clusterIron")) {
            return new ItemStack(ItemsTC.clusters, 1, 1);
        } else if (OreHelper.isOreNamed(input, "oreGold")) {    // Gold to iron
            return new ItemStack(Blocks.IRON_ORE);
        } else if (OreHelper.isOreNamed(input, "ingotGold")) {
            return new ItemStack(Items.IRON_INGOT);
        } else if (OreHelper.isOreNamed(input, "blockGold")) {
            return new ItemStack(Blocks.IRON_BLOCK);
        } else if (OreHelper.isOreNamed(input, "nuggetGold")) {
            return new ItemStack(Items.IRON_NUGGET);
        } else if (OreHelper.isOreNamed(input, "clusterGold")) {
            return new ItemStack(ItemsTC.clusters, 1, 0);
        } else if (OreDictionary.doesOreNameExist("oreTin") && OreHelper.isOreNamed(input, "oreCopper")) {  // Copper to tin
            return OreDictionary.getOres("oreTin", false).get(0);
        } else if (OreDictionary.doesOreNameExist("ingotTin") && OreHelper.isOreNamed(input, "ingotCopper")) {
            return OreDictionary.getOres("ingotTin", false).get(0);
        } else if (OreDictionary.doesOreNameExist("blockTin") && OreHelper.isOreNamed(input, "blockCopper")) {
            return OreDictionary.getOres("blockTin", false).get(0);
        } else if (OreDictionary.doesOreNameExist("nuggetTin") && OreHelper.isOreNamed(input, "nuggetCopper")) {
            return OreDictionary.getOres("nuggetTin", false).get(0);
        } else if (OreHelper.isOreNamed(input, "clusterCopper")) {
            return new ItemStack(ItemsTC.clusters, 1, 3);
        } else if (OreDictionary.doesOreNameExist("oreCopper") && OreHelper.isOreNamed(input, "oreTin")) {  // Tin to copper
            return OreDictionary.getOres("oreCopper", false).get(0);
        } else if (OreDictionary.doesOreNameExist("ingotCopper") && OreHelper.isOreNamed(input, "ingotTin")) {
            return OreDictionary.getOres("ingotCopper", false).get(0);
        } else if (OreDictionary.doesOreNameExist("blockCopper") && OreHelper.isOreNamed(input, "blockTin")) {
            return OreDictionary.getOres("blockCopper", false).get(0);
        } else if (OreDictionary.doesOreNameExist("nuggetCopper") && OreHelper.isOreNamed(input, "nuggetTin")) {
            return OreDictionary.getOres("nuggetCopper", false).get(0);
        } else if (OreHelper.isOreNamed(input, "clusterTin")) {
            return new ItemStack(ItemsTC.clusters, 1, 2);
        } else if (OreDictionary.doesOreNameExist("oreSilver") && OreHelper.isOreNamed(input, "oreLead")) { // Lead to silver
            return OreDictionary.getOres("oreSilver", false).get(0);
        } else if (OreDictionary.doesOreNameExist("ingotSilver") && OreHelper.isOreNamed(input, "ingotLead")) {
            return OreDictionary.getOres("ingotSilver", false).get(0);
        } else if (OreDictionary.doesOreNameExist("blockSilver") && OreHelper.isOreNamed(input, "blockLead")) {
            return OreDictionary.getOres("blockSilver", false).get(0);
        } else if (OreDictionary.doesOreNameExist("nuggetSilver") && OreHelper.isOreNamed(input, "nuggetLead")) {
            return OreDictionary.getOres("nuggetSilver", false).get(0);
        } else if (OreHelper.isOreNamed(input, "clusterLead")) {
            return new ItemStack(ItemsTC.clusters, 1, 4);
        } else if (OreDictionary.doesOreNameExist("oreLead") && OreHelper.isOreNamed(input, "oreSilver")) { // Silver to lead
            return OreDictionary.getOres("oreLead", false).get(0);
        } else if (OreDictionary.doesOreNameExist("ingotLead") && OreHelper.isOreNamed(input, "ingotSilver")) {
            return OreDictionary.getOres("ingotLead", false).get(0);
        } else if (OreDictionary.doesOreNameExist("blockLead") && OreHelper.isOreNamed(input, "blockSilver")) {
            return OreDictionary.getOres("blockLead", false).get(0);
        } else if (OreDictionary.doesOreNameExist("nuggetLead") && OreHelper.isOreNamed(input, "nuggetSilver")) {
            return OreDictionary.getOres("nuggetLead", false).get(0);
        } else if (OreHelper.isOreNamed(input, "clusterSilver")) {
            return new ItemStack(ItemsTC.clusters, 1, 5);
        } else {
            if (input != null && !input.isEmpty()) {
                ThaumicWonders.LOGGER.info("TRANSMUTER_STONE: Rejecting item {} with ore names {}", input.toString(), String.join(", ", OreHelper.getOreNames(input).toArray(new String[] {})));
            }
            return null;
        }
    }

    @Override
    public int getFluxChance() {
        return 33;
    }

    @Override
    public int getSparkleColor() {
        return Aspect.EXCHANGE.getColor();
    }
    
    @Override
    public int getItemEnchantability() {
        return 10;
    }
}
