package com.verdantartifice.thaumicwonders.common.compat;

import com.verdantartifice.thaumicwonders.common.items.ItemsTW;
import com.verdantartifice.thaumicwonders.common.items.consumables.ItemPrimalArrow.PrimalArrowVariant;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class CompatHelper {
    /**
     * Gets the Primal Arrow ItemStack when New Crimson Revelations is installed. This function allows the recipes to
     * automatically swap to NCR's arrows in the place of Thaumic Wonders to avoid duplicate items.
     *
     * @param variant Primal arrow variant type
     * @param count   The ItemStack count
     * @return An ItemStack of the specified arrow type based on the loaded mods
     */
    public static ItemStack getPrimalArrow(PrimalArrowVariant variant, int count) {
        Item item = getPrimalArrowItem(variant);
        int meta = item == ItemsTW.PRIMAL_ARROW ? variant.ordinal() : 0;
        return new ItemStack(item, count, meta);
    }

    /**
     * Gets the Primal Arrow ItemStack when New Crimson Revelations is installed. This function allows the recipes to
     * automatically swap to NCR's arrows in the place of Thaumic Wonders to avoid duplicate items.
     *
     * @param variant Primal arrow variant type
     * @return An ItemStack with a count of 1 of the specified arrow type based on the loaded mods
     */
    public static ItemStack getPrimalArrow(PrimalArrowVariant variant) {
        return getPrimalArrow(variant, 1);
    }

    private static Item getPrimalArrowItem(PrimalArrowVariant variant) {
        Item item = Items.AIR;
        if (ModIds.new_crimson_revelations.isLoaded) {
            switch (variant) {
                case AIR:
                    item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ModIds.new_crimson_revelations.modId, "aer_arrow"));
                    break;
                case EARTH:
                    item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ModIds.new_crimson_revelations.modId, "terra_arrow"));
                    break;
                case FIRE:
                    item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ModIds.new_crimson_revelations.modId, "ignis_arrow"));
                    break;
                case WATER:
                    item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ModIds.new_crimson_revelations.modId, "aqua_arrow"));
                    break;
                case ORDER:
                    item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ModIds.new_crimson_revelations.modId, "ordo_arrow"));
                    break;
                case ENTROPY:
                    item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ModIds.new_crimson_revelations.modId, "perditio_arrow"));
                    break;
            }
        }
        return item != Items.AIR ? item : ItemsTW.PRIMAL_ARROW;
    }
}
