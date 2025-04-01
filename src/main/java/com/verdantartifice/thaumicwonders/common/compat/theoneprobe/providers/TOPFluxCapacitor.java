package com.verdantartifice.thaumicwonders.common.compat.theoneprobe.providers;

import com.verdantartifice.thaumicwonders.common.blocks.BlocksTW;
import com.verdantartifice.thaumicwonders.common.blocks.devices.BlockFluxCapacitor;
import mcjty.theoneprobe.api.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;

import java.util.function.Function;

public class TOPFluxCapacitor implements Function<ITheOneProbe, Void> {
    @Override
    public Void apply(ITheOneProbe input) {
        input.registerProvider(new IProbeInfoProvider() {
            @Override
            public String getID() {
                return BlocksTW.FLUX_CAPACITOR.getRegistryName().toString();
            }

            @Override
            public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState state, IProbeHitData data) {
                if (mode == ProbeMode.EXTENDED) {
                    if (state.getBlock() instanceof BlockFluxCapacitor) {
                        int charge = state.getValue(BlockFluxCapacitor.CHARGE);
                        int maxCharge = 10;
                        probeInfo.progress(charge, maxCharge, probeInfo.defaultProgressStyle()
                                .showText(false)
                                .filledColor(0xFF800080)
                                .alternateFilledColor(0xff805080)
                                .borderColor(0xff555555)
                                .numberFormat(NumberFormat.FULL));
                    }
                }
            }
        });
        return null;
    }
}
