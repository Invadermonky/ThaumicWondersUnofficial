package com.verdantartifice.thaumicwonders.common.compat.theoneprobe.providers;

import com.verdantartifice.thaumicwonders.common.blocks.BlocksTW;
import com.verdantartifice.thaumicwonders.common.config.ConfigHandlerTW;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TilePrimordialSiphon;
import mcjty.theoneprobe.api.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.function.Function;

public class TOPPrimordialSiphon implements Function<ITheOneProbe, Void> {
    @Override
    public Void apply(ITheOneProbe input) {
        input.registerProvider(new IProbeInfoProvider() {
            @Override
            public String getID() {
                return BlocksTW.PRIMORDIAL_SIPHON.getRegistryName().toString();
            }

            @Override
            public void addProbeInfo(ProbeMode mode, IProbeInfo info, EntityPlayer player, World world, IBlockState state, IProbeHitData hitData) {
                if(mode == ProbeMode.EXTENDED) {
                    TileEntity tile = world.getTileEntity(hitData.getPos());
                    if(tile instanceof TilePrimordialSiphon) {
                        int progress = ((TilePrimordialSiphon) tile).progress;
                        int maxProgress = ConfigHandlerTW.primordial_siphon.requiredRiftDrain;
                        int percent = (int) ((float) progress / maxProgress * 100);
                        info.progress(percent, 100, info.defaultProgressStyle()
                                //.filledColor(0xeab8d3)
                                //.alternateFilledColor(0x9ad095)
                                //.borderColor(0xff555555)
                                .suffix("%"));
                    }
                }
            }
        });
        return null;
    }
}
