package com.verdantartifice.thaumicwonders.proxy;

import com.verdantartifice.thaumicwonders.client.config.KeyBindings;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
    private ProxyEntities proxyEntities = new ProxyEntities();
    private ProxyTESR proxyTESR = new ProxyTESR();
    
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        this.proxyEntities.setupEntityRenderers();
    }
    
    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        KeyBindings.init();
        this.proxyTESR.setupTESR();
    }
}
