package com.verdantartifice.thaumicwonders.common.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;

import java.awt.*;

public class PacketMeteorbFx implements IMessage {
    protected BlockPos source;
    protected int color;

    public PacketMeteorbFx() {
        this.source = null;
        this.color = 0;
    }

    public PacketMeteorbFx(BlockPos source, int color) {
        this.source = source;
        this.color = color;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.source = BlockPos.fromLong(buf.readLong());
        this.color = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.source.toLong());
        buf.writeInt(this.color);
    }

    public static class Handler implements IMessageHandler<PacketMeteorbFx, IMessage> {
        @Override
        public IMessage onMessage(PacketMeteorbFx message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        @SideOnly(Side.CLIENT)
        private void handle(PacketMeteorbFx message, MessageContext ctx) {
            World world = FMLClientHandler.instance().getClient().world;
            BlockPos targetPos = new BlockPos(message.source.getX(), world.getActualHeight(), message.source.getZ());
            Color color = new Color(message.color);
            float r = color.getRed() / 255.0F;
            float g = color.getGreen() / 255.0F;
            float b = color.getBlue() / 255.0F;
            FXDispatcher.INSTANCE.arcBolt(
                    message.source.getX() + 0.5D,
                    message.source.getY() + 0.5D,
                    message.source.getZ() + 0.5D,
                    targetPos.getX() + 0.5D,
                    targetPos.getY() - 0.5D,
                    targetPos.getZ() + 0.5D,
                    r, g, b, 0.6F);
        }
    }
}
