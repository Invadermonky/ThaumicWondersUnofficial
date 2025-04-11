package com.verdantartifice.thaumicwonders.common.compat.expandedarcanum;

import com.mr208.ea.common.ModContent;
import com.mr208.ea.common.items.ItemCluster;
import com.verdantartifice.thaumicwonders.common.compat.IModPlugin;
import com.verdantartifice.thaumicwonders.common.crafting.catalyzationchamber.CatalyzationChamberRecipeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.registries.IForgeRegistry;

import java.lang.reflect.Field;

public class ExpandedArcanumPlugin implements IModPlugin {
    @Override
    public void registerRecipes(IForgeRegistry<IRecipe> forgeRegistry) {
        try {
            Field oreNameField = ItemCluster.class.getDeclaredField("oreName");
            oreNameField.setAccessible(true);
            ModContent.modClusters.forEach(cluster -> {
                try {
                    CatalyzationChamberRecipeRegistry.addAlchemistRecipe((String) oreNameField.get(cluster), new ItemStack(cluster));
                } catch (Exception ignored) {
                }
            });
        } catch (Exception ignored) {
        }
    }
}
