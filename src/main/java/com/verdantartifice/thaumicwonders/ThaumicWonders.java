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

@Mod(
        modid = ThaumicWonders.MODID,
        name = ThaumicWonders.NAME,
        version = ThaumicWonders.VERSION,
        dependencies = ThaumicWonders.DEPENDENCIES
)
public class ThaumicWonders {
    public static final String MODID = "thaumicwonders";
    public static final String NAME = "Thaumic Wonders Unofficial";
    public static final String VERSION = "2.0.0";
    public static final String DEPENDENCIES = "required-after:thaumcraft";

    public static final CreativeTabs CREATIVE_TAB = new CreativeTabTW(CreativeTabs.getNextID(), ThaumicWonders.MODID);

    public static Logger LOGGER;

    @Mod.Instance(ThaumicWonders.MODID)
    public static ThaumicWonders INSTANCE;

    @SidedProxy(clientSide = "com.verdantartifice.thaumicwonders.proxy.ClientProxy", serverSide = "com.verdantartifice.thaumicwonders.proxy.ServerProxy")
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


    //TODO: Future Release
    //  Thauma Llama
    //  Check out Gadomancy to see if any additions can be made to TW (specifically the Infusion Claw for infusion automation)
    //  Heximite explosion converts blocks into tainted variants (add CrT/GS integration for additional conversions)
    //  Golem Seal "Research Assistant"
    //      refills research table paper
    //      searches for items needed for research
    //  Research Brews + Crucible addon - create brews that randomly grant research after being consumed.
    //  Teleport Home focus effect
    //  Portal Generator instability events and custom summons
    //  New Dimensional Ripper model
    //  Improved Madness Engine model
    //  Improved Inspiration Engine model
    //  Rework Timewinder to be block machine with new model
    //  Make the Meteorb into a handheld device (maybe use the timewinder compass model)
    //  Flux distiller model
    //  JEI Support
    //      Void Beacon
    //  Enchanter Multiblock (disablable)
    //      Mutltiblock enchanter, use crafttweaker for additional enchants
    //      Offshoot of Disjunction Cloth
    //  Bosses:
    //      crimson_cult_models - cult golem
    //          See about getting permission to use one of these hammers https://github.com/TheCodex6824/ThaumicAugmentation/issues/170#issuecomment-875600063
    //      crimson_cult_models - taintabomination5 (floating one)


}
