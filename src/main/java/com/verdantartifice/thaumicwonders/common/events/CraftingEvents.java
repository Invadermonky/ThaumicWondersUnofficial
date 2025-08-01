package com.verdantartifice.thaumicwonders.common.events;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.crafting.recipes.RecipeDisjunctionClothUse;
import com.verdantartifice.thaumicwonders.common.items.ItemsTW;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import thaumcraft.api.aura.AuraHelper;

@Mod.EventBusSubscriber(modid = ThaumicWonders.MODID)
public class CraftingEvents {
    @SubscribeEvent
    public static void onCrafting(PlayerEvent.ItemCraftedEvent event) {
        // If this was a disenchantment using a Disjunction Cloth, disperse vis into the aura
        if (!event.player.world.isRemote && RecipeDisjunctionClothUse.matches(event.craftMatrix)) {
            for (int index = 0; index < event.craftMatrix.getSizeInventory(); index++) {
                ItemStack stack = event.craftMatrix.getStackInSlot(index);
                if (!stack.isEmpty() && stack.getItem() != ItemsTW.DISJUNCTION_CLOTH && stack.isItemEnchanted()) {
                    int totalLevels = 0;
                    for (int level : EnchantmentHelper.getEnchantments(stack).values()) {
                        totalLevels += level;
                    }
                    if (totalLevels > 0) {
                        AuraHelper.addVis(event.player.world, event.player.getPosition(), (totalLevels * 5.0F));
                    }
                    break;
                }
            }
        }
    }
}
