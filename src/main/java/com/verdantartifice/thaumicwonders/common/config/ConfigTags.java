package com.verdantartifice.thaumicwonders.common.config;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigTags {
    public static final Map<Potion, Tuple<Integer, Integer>> WARP_RING_REMOVALS = new HashMap<>();

    public static void syncConfig() {
        syncWarpRingPotions();
    }

    private static void syncWarpRingPotions() {
        WARP_RING_REMOVALS.clear();
        Pattern pattern = Pattern.compile("(.+?);(\\d+);(\\d+)");
        for (String configStr : ConfigHandlerTW.warp_ring.removalRanks) {
            try {
                Matcher matcher = pattern.matcher(configStr);
                if (matcher.find()) {
                    ResourceLocation loc = new ResourceLocation(matcher.group(1));
                    Potion potion = ForgeRegistries.POTIONS.getValue(loc);
                    if (potion != null) {
                        WARP_RING_REMOVALS.put(potion, new Tuple<>(Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3))));
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
