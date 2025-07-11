package com.verdantartifice.thaumicwonders.common.compat.theoneprobe.providers;

import com.verdantartifice.thaumicwonders.common.blocks.BlocksTW;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TileVisCapacitor;
import mcjty.theoneprobe.api.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.function.Function;

public class TOPVisCapacitor implements Function<ITheOneProbe, Void> {
    @Override
    public Void apply(ITheOneProbe input) {
        input.registerProvider(new IProbeInfoProvider() {
            @Override
            public String getID() {
                return BlocksTW.VIS_CAPACITOR.getRegistryName().toString();
            }

            @Override
            public void addProbeInfo(ProbeMode mode, IProbeInfo info, EntityPlayer entityPlayer, World world, IBlockState state, IProbeHitData hitData) {
                if (mode == ProbeMode.EXTENDED && hitData.getPos() != null) {
                    TileEntity tile = world.getTileEntity(hitData.getPos());
                    if (tile instanceof TileVisCapacitor) {
                        int visStored = ((TileVisCapacitor) tile).getStoredVis();
                        int capacity = ((TileVisCapacitor) tile).getVisCapacity();
                        info.progress(visStored, capacity, info.defaultProgressStyle()
                                .filledColor(0xffdf78f8)
                                .alternateFilledColor(0xffae53c5)
                                .borderColor(0xff555555)
                                .numberFormat(NumberFormat.FULL)
                                .suffix(" Vis"));
                    }
                }
            }
        });
        return null;
    }
}
