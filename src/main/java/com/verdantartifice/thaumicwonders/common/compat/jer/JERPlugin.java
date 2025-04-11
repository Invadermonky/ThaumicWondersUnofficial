package com.verdantartifice.thaumicwonders.common.compat.jer;

import com.verdantartifice.thaumicwonders.common.compat.IModPlugin;
import com.verdantartifice.thaumicwonders.common.entities.LootTablesTW;
import com.verdantartifice.thaumicwonders.common.entities.monsters.EntityCorruptionAvatar;
import jeresources.api.IJERAPI;
import net.minecraft.world.World;

public class JERPlugin implements IModPlugin {
    @jeresources.api.JERPlugin
    public static IJERAPI jerApi;
    public static World jerWorld;

    private static void initEntities() {
        jerApi.getMobRegistry().register(new EntityCorruptionAvatar(jerWorld), LootTablesTW.CORRUPTION_AVATAR);
    }

    @Override
    public void initClient() {
        jerWorld = jerApi.getWorld();
        initEntities();
    }
}
