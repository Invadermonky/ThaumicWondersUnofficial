package com.verdantartifice.thaumicwonders.common.crafting.voidbeacon;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.crafting.WeightedEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.aspects.Aspect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class VoidBeaconEntryRegistry {
    private static final List<VoidBeaconEntry> VOID_BEACON_ENTRIES = new ArrayList<>();

    public static List<VoidBeaconEntry> getVoidBeaconEntries() {
        return VOID_BEACON_ENTRIES;
    }

    public static void addEntry(ItemStack stack, int weight) {
        if(stack.isEmpty()) {
            ThaumicWonders.LOGGER.error("ItemStack cannot be empty");
        } else if(weight <= 0) {
            ThaumicWonders.LOGGER.error("Weight must be greater than 0");
        } else {
            VOID_BEACON_ENTRIES.add(new VoidBeaconEntry(stack));
        }
    }

    public static void addEntry(ItemStack stack) {
        addEntry(stack, 10);
    }

    public static void addEntry(String oreDict, int weight) {
        NonNullList<ItemStack> oreStacks = OreDictionary.getOres(oreDict);
        if(oreStacks.isEmpty()) {
            ThaumicWonders.LOGGER.error("There are no items registered with the {} ore dictionary string.", oreDict);
        } else {
            //Only add the first result to prevent ore dictionary strings from skewing the output.
            addEntry(oreStacks.get(0), weight);
        }

    }

    public static void addEntry(String oreDict) {
        addEntry(oreDict, 10);
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
        return VOID_BEACON_ENTRIES.stream().filter(entry -> entry.matches(aspect)).collect(Collectors.toList());
    }

    public static ItemStack getDropForAspect(Random rand, Aspect aspect) {
        List<VoidBeaconEntry> aspectEntries = getEntriesForAspect(aspect);
        int totalWeight = aspectEntries.stream().mapToInt(WeightedEntry::getWeight).sum();
        if(aspectEntries.isEmpty() || totalWeight <= 0) {
            return ItemStack.EMPTY;
        }

        int weight = rand.nextInt(totalWeight);
        for(VoidBeaconEntry entry : VOID_BEACON_ENTRIES) {
            if(weight < entry.getWeight()) {
                return entry.getStack();
            } else {
                weight -= entry.getWeight();
            }
        }
        return ItemStack.EMPTY;
    }
}
