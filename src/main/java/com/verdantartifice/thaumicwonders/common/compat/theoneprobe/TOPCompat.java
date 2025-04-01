package com.verdantartifice.thaumicwonders.common.compat.theoneprobe;

import com.verdantartifice.thaumicwonders.common.compat.theoneprobe.providers.TOPFluxCapacitor;
import net.minecraftforge.fml.common.event.FMLInterModComms;

public class TOPCompat {
    public static void init() {
        FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", TOPFluxCapacitor.class.getName());
    }
}
