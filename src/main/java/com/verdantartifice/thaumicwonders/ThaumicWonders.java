package com.verdantartifice.thaumicwonders;

import com.verdantartifice.thaumicwonders.common.misc.CreativeTabTW;
import com.verdantartifice.thaumicwonders.proxy.IProxyTW;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = ThaumicWonders.MODID, name = ThaumicWonders.NAME, version = ThaumicWonders.VERSION, dependencies = ThaumicWonders.DEPENDENCIES, updateJSON = ThaumicWonders.UPDATE_URL)
public class ThaumicWonders {
    public static final String MODID = "thaumicwonders";
    public static final String NAME = "Thaumic Wonders";
    public static final String VERSION = "1.8.4";
    public static final String DEPENDENCIES = "required-after:thaumcraft";
    public static final String UPDATE_URL = "https://pastebin.com/raw/7QELk9HJ";

    public static final CreativeTabs CREATIVE_TAB = new CreativeTabTW(CreativeTabs.getNextID(), ThaumicWonders.MODID);

    public static Logger LOGGER;
    
    @Mod.Instance(ThaumicWonders.MODID)
    public static ThaumicWonders INSTANCE;
    
    @SidedProxy(clientSide="com.verdantartifice.thaumicwonders.proxy.ClientProxy", serverSide="com.verdantartifice.thaumicwonders.proxy.ServerProxy")
    public static IProxyTW proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER = event.getModLog();
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
    
    static {
        FluidRegistry.enableUniversalBucket();
    }
}
