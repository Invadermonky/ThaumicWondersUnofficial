package com.verdantartifice.thaumicwonders.proxy;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.client.config.KeyBindings;
import com.verdantartifice.thaumicwonders.common.compat.jer.JERPlugin;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
    private ProxyEntities proxyEntities = new ProxyEntities();
    private ProxyTESR proxyTESR = new ProxyTESR();

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        this.proxyEntities.setupEntityRenderers();
        OBJLoader.INSTANCE.addDomain(ThaumicWonders.MODID);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        KeyBindings.init();
        this.proxyTESR.setupTESR();
        if (Loader.isModLoaded("jeresources")) {
            JERPlugin.init();
        }
    }
}
