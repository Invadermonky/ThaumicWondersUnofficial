package com.verdantartifice.thaumicwonders.common.crafting.meatyorb;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MeatyOrbRegistry {
    private static final List<MeatyOrbEntry> MEAT_ENTRIES = new ArrayList<>();
    private static int totalWeight = -1;

    public static void addEntry(ItemStack meatStack, int weight) {
        if(meatStack.isEmpty()) {
            ThaumicWonders.LOGGER.error("Error adding Meaty Orb entry. ItemStack cannot be empty.");
        } else if(weight <= 0) {
            ThaumicWonders.LOGGER.error("Error adding Meaty Orb entry. Weight must be greater than 0.");
        } else {
            meatStack.setCount(1);
            MEAT_ENTRIES.add(new MeatyOrbEntry(meatStack, weight));
        }
    }

    public static void addEntry(MeatyOrbEntry entry) {
        MEAT_ENTRIES.add(entry);
    }

    public static void removeEntry(ItemStack toRemove) {
        if(toRemove.isEmpty()) return;

        toRemove.setCount(1);
        MEAT_ENTRIES.removeIf(entry -> entry.equals(toRemove));
    }

    public static void removeAll() {
        MEAT_ENTRIES.clear();
        totalWeight = -1;
    }

    private static void initializeWeight() {
        if(totalWeight < 0) {
            totalWeight = 0;
            MEAT_ENTRIES.forEach(entry -> totalWeight += entry.getWeight());
        }
    }

    public static ItemStack getMeatDrop(Random rand) {
        initializeWeight();

        if(totalWeight <= 0)
            return ItemStack.EMPTY;

        int weight = rand.nextInt(totalWeight);
        for(MeatyOrbEntry entry : MEAT_ENTRIES) {
            if(weight < entry.getWeight()) {
                return entry.getMeatStack();
            } else {
                weight -= entry.getWeight();
            }
        }

        return ItemStack.EMPTY;
    }
}
