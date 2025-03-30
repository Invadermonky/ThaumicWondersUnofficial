package com.verdantartifice.thaumicwonders.common.fluids;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

import java.awt.*;

public class FluidQuicksilver extends Fluid {
    public static final FluidQuicksilver INSTANCE = new FluidQuicksilver();
    
    private FluidQuicksilver() {
        super("fluid_quicksilver", new ResourceLocation(ThaumicWonders.MODID, "blocks/fluid_quicksilver"), new ResourceLocation(ThaumicWonders.MODID, "blocks/fluid_quicksilver"));
        this.setViscosity(1560);    // Real-life dynamic viscosity of liquid mercury in Ns/m^2
        this.setDensity(13593);     // Real-life density of liquid mercury in kg/m^3
    }
    
    @Override
    public int getColor() {
        return new Color(197, 197, 243).getRGB();
    }
}
