package com.verdantartifice.thaumicwonders.proxy;

import com.verdantartifice.thaumicwonders.client.config.KeyBindings;
import com.verdantartifice.thaumicwonders.common.compat.ModPlugins;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
    private ProxyEntities proxyEntities = new ProxyEntities();
    private ProxyTESR proxyTESR = new ProxyTESR();

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        this.proxyEntities.setupEntityRenderers();
        ModPlugins.preInitClient(event);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        KeyBindings.init();
        this.proxyTESR.setupTESR();
        ModPlugins.initClient(event);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        ModPlugins.postInitClient(event);
    }
}
