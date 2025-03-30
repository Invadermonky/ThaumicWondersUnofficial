package com.verdantartifice.thaumicwonders.common.network.packets;

import com.verdantartifice.thaumicwonders.common.tiles.devices.TileOreDiviner;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketOreDivinerStop implements IMessage {
    protected BlockPos origin;
    
    public PacketOreDivinerStop() {
        this.origin = null;
    }
    
    public PacketOreDivinerStop(BlockPos origin) {
        this.origin = origin;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.origin = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.origin.toLong());
    }

    public static class Handler implements IMessageHandler<PacketOreDivinerStop, IMessage> {
        @Override
        public IMessage onMessage(PacketOreDivinerStop message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }
        
        @SideOnly(Side.CLIENT)
        private void handle(PacketOreDivinerStop message, MessageContext ctx) {
            World world = FMLClientHandler.instance().getClient().world;
            TileEntity tile = world.getTileEntity(message.origin);
            if (tile instanceof TileOreDiviner) {
                ((TileOreDiviner)tile).setTarget(null);
            }
        }
    }
}
