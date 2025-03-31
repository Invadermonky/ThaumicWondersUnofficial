package com.verdantartifice.thaumicwonders.common.crafting.voidbeacon;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.aspects.Aspect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class VoidBeaconRegistry {
    private static final List<VoidBeaconEntry> VOID_BEACON_ENTRIES = new ArrayList<>();

    public static void addEntry(ItemStack stack) {
        if(stack.isEmpty()) {
            ThaumicWonders.LOGGER.error("ItemStack cannot be empty");
        } else {
            VOID_BEACON_ENTRIES.add(new VoidBeaconEntry(stack));
        }
    }

    public static void addEntry(String oreDict) {
        NonNullList<ItemStack> oreStacks = OreDictionary.getOres(oreDict);
        if(oreStacks.isEmpty()) {
            ThaumicWonders.LOGGER.error("There are no items registered with the {} ore dictionary string.", oreDict);
        } else {
            //Only add the first result to prevent ore dictionary strings from skewing the output.
            addEntry(oreStacks.get(0));
        }
    }

    public static void addEntry(VoidBeaconEntry entry) {
        VOID_BEACON_ENTRIES.add(entry);
    }

    public static void removeEntry(ItemStack stack) {
        VOID_BEACON_ENTRIES.removeIf(entry -> ItemStack.areItemsEqual(entry.getStack(), stack) && ItemStack.areItemStackTagsEqual(entry.getStack(), stack));
    }

    public static void removeEntry(String oreDict) {
        for(ItemStack stack : OreDictionary.getOres(oreDict)) {
            removeEntry(stack);
        }
    }

    public static void removeAll() {
        VOID_BEACON_ENTRIES.clear();
    }

    private static List<VoidBeaconEntry> getEntriesForAspect(Aspect aspect) {
        return VOID_BEACON_ENTRIES.stream().filter(entry -> entry.getAspectValue(aspect) > 0).collect(Collectors.toList());
    }

    public static ItemStack getDropForAspect(Random rand, Aspect aspect) {
        List<VoidBeaconEntry> aspectEntries = getEntriesForAspect(aspect);
        int totalWeight = aspectEntries.stream().mapToInt(entry -> entry.getAspectValue(aspect)).sum();
        if(aspectEntries.isEmpty() || totalWeight <= 0) {
            return !VOID_BEACON_ENTRIES.isEmpty() ? VOID_BEACON_ENTRIES.get(rand.nextInt(VOID_BEACON_ENTRIES.size())).getStack() : ItemStack.EMPTY;
        }

        int weight = rand.nextInt(totalWeight);
        for(VoidBeaconEntry entry : VOID_BEACON_ENTRIES) {
            int aspectWeight = entry.getAspectValue(aspect);
            if(weight < aspectWeight) {
                return entry.getStack();
            } else {
                weight -= aspectWeight;
            }
        }
        return ItemStack.EMPTY;
    }
}
