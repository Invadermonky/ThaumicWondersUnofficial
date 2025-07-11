package com.verdantartifice.thaumicwonders.common.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PlayerHelper {
    @Nullable
    public static EntityPlayer getPlayerFromUUID(UUID uuid) {
        return FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT ? null : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(uuid);
    }

    public static UUID getUUIDFromPlayer(EntityPlayer player) {
        return player.getGameProfile().getId();
    }

    public static String getUsernameFromUUID(UUID uuid) {
        return UsernameCache.getLastKnownUsername(uuid);
    }
}
