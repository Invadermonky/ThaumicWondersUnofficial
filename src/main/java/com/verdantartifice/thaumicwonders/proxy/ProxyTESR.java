package com.verdantartifice.thaumicwonders.proxy;

import com.verdantartifice.thaumicwonders.client.renderers.tile.TesrDimensionalRipper;
import com.verdantartifice.thaumicwonders.client.renderers.tile.TesrPrimordialSiphon;
import com.verdantartifice.thaumicwonders.client.renderers.tile.TesrVoidBeacon;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TileDimensionalRipper;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TilePrimordialSiphon;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TileVoidBeacon;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ProxyTESR {
    public void setupTESR() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileDimensionalRipper.class, new TesrDimensionalRipper());
        ClientRegistry.bindTileEntitySpecialRenderer(TilePrimordialSiphon.class, new TesrPrimordialSiphon());
        ClientRegistry.bindTileEntitySpecialRenderer(TileVoidBeacon.class, new TesrVoidBeacon());
    }
}
