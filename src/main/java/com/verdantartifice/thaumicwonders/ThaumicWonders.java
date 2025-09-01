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
    public static final String VERSION = "2.2.0";
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
    //  Shearing golem seal
    //  Fishing golem seal
    //  Crafting golem seal
    //  Eldritch cluster harvest infusion enchantment (or just a single pickaxe that does it
    //  Register for eldritch cluster harvesting
    //  Item retriveal golem seal (like corporea crystal)
    //  Heximite explosion converts blocks into tainted variants (add CrT/GS integration for additional conversions)
    //  Research Brews + Crucible addon - create brews that randomly grant research after being consumed.
    //  Teleport Home focus effect
    //  Portal Generator instability events and custom summons
    //  New Dimensional Ripper model
    //  Improved Madness Engine model
    //  Improved Inspiration Engine model
    //  Rework Timewinder to be block machine with new model
    //  Sneaking causes the voidcaller's armor to desync from the player
    //  Make the Meteorb into a handheld device (maybe use the timewinder compass model)
    //  Flux distiller model
    //  Shattered portal linker - random teleport portal
    //  Look into adding a replacement for the original Void Beacon that generates items from raw essentia
    //  Modified ore diviner that infuses essentia into stone and transforms it into ores
    //  Enchanter Multiblock (disablable)
    //      Mutltiblock enchanter, use crafttweaker for additional enchants
    //      Offshoot of Disjunction Cloth
    //  Bosses:
    //      crimson_cult_models - cult golem
    //          See about getting permission to use one of these hammers https://github.com/TheCodex6824/ThaumicAugmentation/issues/170#issuecomment-875600063
    //      crimson_cult_models - taintabomination5 (floating one)


}
