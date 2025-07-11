package com.verdantartifice.thaumicwonders.common.config;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigTags {
    public static final Map<Potion, Integer> WARP_RING_REMOVALS = new HashMap<>();

    public static String getOreSearchString(ItemStack stack) {
        if (stack.isEmpty()) {
            return "^ore[\\w]+$";
        } else {
            int[] oreIds = OreDictionary.getOreIDs(stack);
            for (int oreId : oreIds) {
                String oreName = OreDictionary.getOreName(oreId);
                if (oreName.startsWith("ore")) {
                    return oreName;
                } else {
                    for (String oreType : ConfigHandlerTW.ore_diviner.oreAssociations) {
                        if (oreName.startsWith(oreType)) {
                            return oreName.replaceFirst(oreType, "ore");
                        }
                    }
                }
            }
        }
        return "";
    }

    public static void syncConfig() {
        syncWarpRingPotions();
    }

    private static void syncWarpRingPotions() {
        WARP_RING_REMOVALS.clear();
        Pattern pattern = Pattern.compile("^(.+?)=(\\d+)$");
        for (String configStr : ConfigHandlerTW.warp_ring.removalRanks) {
            try {
                Matcher matcher = pattern.matcher(configStr);
                if (matcher.find()) {
                    ResourceLocation loc = new ResourceLocation(matcher.group(1));
                    Potion potion = ForgeRegistries.POTIONS.getValue(loc);
                    if (potion != null) {
                        WARP_RING_REMOVALS.put(potion, MathHelper.clamp(Integer.parseInt(matcher.group(2)), 0, 5));
                    } else {
                        throw new IllegalArgumentException("No registered potion for warp ring configuration string: " + configStr);
                    }
                } else {
                    throw new IllegalArgumentException("Invalid warp ring configuration string: " + configStr);
                }
            } catch (IllegalArgumentException e) {
                ThaumicWonders.LOGGER.error(e);
            }
        }
    }

    static {
        syncConfig();
    }
}
