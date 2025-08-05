package com.verdantartifice.thaumicwonders.proxy;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.compat.ModIds;
import com.verdantartifice.thaumicwonders.common.compat.ModPlugins;
import com.verdantartifice.thaumicwonders.common.init.InitAspects;
import com.verdantartifice.thaumicwonders.common.init.InitItems;
import com.verdantartifice.thaumicwonders.common.init.InitResearch;
import com.verdantartifice.thaumicwonders.common.items.ItemsTW;
import com.verdantartifice.thaumicwonders.common.misc.BehaviorDispensePrimalArrow;
import com.verdantartifice.thaumicwonders.common.network.PacketHandler;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy implements IProxyTW, IGuiHandler {
    private ProxyGUI proxyGUI = new ProxyGUI();

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        InitItems.initSeals();
        ModPlugins.preInit(event);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        PacketHandler.registerMessages();
        InitResearch.initResearch();
        NetworkRegistry.INSTANCE.registerGuiHandler(ThaumicWonders.INSTANCE, this);
        if (!ModIds.new_crimson_revelations.isLoaded) {
            BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ItemsTW.PRIMAL_ARROW, new BehaviorDispensePrimalArrow());
        }
        ModPlugins.init(event);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        InitAspects.initAspects();
        ModPlugins.postInit(event);
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return this.proxyGUI.getServerGuiElement(ID, player, world, x, y, z);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return this.proxyGUI.getClientGuiElement(ID, player, world, x, y, z);
    }
}
