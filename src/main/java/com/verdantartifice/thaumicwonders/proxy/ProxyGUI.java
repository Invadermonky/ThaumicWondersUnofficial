package com.verdantartifice.thaumicwonders.proxy;

import com.verdantartifice.thaumicwonders.client.gui.*;
import com.verdantartifice.thaumicwonders.common.containers.ContainerCatalyzationChamber;
import com.verdantartifice.thaumicwonders.common.containers.ContainerPortalAnchor;
import com.verdantartifice.thaumicwonders.common.containers.ContainerPrimordialSiphon;
import com.verdantartifice.thaumicwonders.common.misc.GuiIds;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TileCatalyzationChamber;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TilePortalAnchor;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TilePrimordialSiphon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ProxyGUI {
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        switch (id) {
            case GuiIds.TIMEWINDER:
                return new GuiTimewinder();
            case GuiIds.CATALYZATION_CHAMBER:
                return new GuiCatalyzationChamber(player.inventory, (TileCatalyzationChamber) world.getTileEntity(new BlockPos(x, y, z)));
            case GuiIds.METEORB:
                return new GuiMeteorb(new BlockPos(x, y, z));
            case GuiIds.MEATY_ORB:
                return new GuiMeatyOrb(new BlockPos(x, y, z));
            case GuiIds.STRUCTURE_DIVINER:
                return new GuiStructureDiviner();
            case GuiIds.PRIMORDIAL_SYPHON:
                return new GuiPrimordialSiphon(player.inventory, (TilePrimordialSiphon) world.getTileEntity(new BlockPos(x, y, z)));
            case GuiIds.PORTAL_ANCHOR:
                return new GuiPortalAnchor(player.inventory, (TilePortalAnchor) world.getTileEntity(new BlockPos(x, y, z)));
            default:
                return null;
        }
    }

    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        switch (id) {
            case GuiIds.CATALYZATION_CHAMBER:
                return new ContainerCatalyzationChamber(player.inventory, (TileCatalyzationChamber) world.getTileEntity(new BlockPos(x, y, z)));
            case GuiIds.PRIMORDIAL_SYPHON:
                return new ContainerPrimordialSiphon(player.inventory, (TilePrimordialSiphon) world.getTileEntity(new BlockPos(x, y, z)));
            case GuiIds.PORTAL_ANCHOR:
                return new ContainerPortalAnchor(player.inventory, (TilePortalAnchor) world.getTileEntity(new BlockPos(x, y, z)));
            default:
                return null;
        }
    }
}
