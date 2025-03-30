package com.verdantartifice.thaumicwonders.common.network;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.network.packets.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(ThaumicWonders.MODID);
    
    private static int packetId = 0;
    
    private static int nextId() {
        return packetId++;
    }
    
    public static void registerMessages() {
        INSTANCE.registerMessage(PacketTimewinderAction.Handler.class, PacketTimewinderAction.class, nextId(), Side.SERVER);
        INSTANCE.registerMessage(PacketTileToServer.Handler.class, PacketTileToServer.class, nextId(), Side.SERVER);
        INSTANCE.registerMessage(PacketTileToClient.Handler.class, PacketTileToClient.class, nextId(), Side.CLIENT);
        INSTANCE.registerMessage(PacketLocalizedMessage.Handler.class, PacketLocalizedMessage.class, nextId(), Side.CLIENT);
        INSTANCE.registerMessage(PacketMeteorbAction.Handler.class, PacketMeteorbAction.class, nextId(), Side.SERVER);
        INSTANCE.registerMessage(PacketOreDivinerSearch.Handler.class, PacketOreDivinerSearch.class, nextId(), Side.CLIENT);
        INSTANCE.registerMessage(PacketOreDivinerStop.Handler.class, PacketOreDivinerStop.class, nextId(), Side.CLIENT);
        INSTANCE.registerMessage(PacketDimensionalRipperFx.Handler.class, PacketDimensionalRipperFx.class, nextId(), Side.CLIENT);
        INSTANCE.registerMessage(PacketMeteorbFx.Handler.class, PacketMeteorbFx.class, nextId(), Side.CLIENT);
        INSTANCE.registerMessage(PacketMeatyOrbAction.Handler.class, PacketMeatyOrbAction.class, nextId(), Side.SERVER);
        INSTANCE.registerMessage(PacketStructureDivinerAction.Handler.class, PacketStructureDivinerAction.class, nextId(), Side.SERVER);
        INSTANCE.registerMessage(PacketAvatarZapFx.Handler.class, PacketAvatarZapFx.class, nextId(), Side.CLIENT);
    }
}
