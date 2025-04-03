package com.verdantartifice.thaumicwonders.common.init;

import com.verdantartifice.thaumicwonders.common.entities.LootTablesTW;
import net.minecraft.world.storage.loot.LootTableList;

public class InitLootTables {
    public static void initLootTables() {
        LootTableList.register(LootTablesTW.CORRUPTION_AVATAR);
    }
}
