package com.verdantartifice.thaumicwonders.common.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.fx.FXDispatcher;

import java.awt.*;

public class PacketAvatarZapFx implements IMessage {
    protected int source;
    protected int target;

    public PacketAvatarZapFx() {
        this.source = 0;
        this.target = 0;
    }

    public PacketAvatarZapFx(int source, int target) {
        this.source = source;
        this.target = target;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.source = buf.readInt();
        this.target = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.source);
        buf.writeInt(this.target);
    }

    public static class Handler implements IMessageHandler<PacketAvatarZapFx, IMessage> {
        @Override
        public IMessage onMessage(PacketAvatarZapFx message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        @SideOnly(Side.CLIENT)
        private void handle(PacketAvatarZapFx message, MessageContext ctx) {
            WorldClient world = FMLClientHandler.instance().getClient().world;
            Entity sourceEntity = world.getEntityByID(message.source);
            Entity targetEntity = world.getEntityByID(message.target);
            Color color = new Color(Aspect.FLUX.getColor());
            float r = color.getRed() / 255.0F;
            float g = color.getGreen() / 255.0F;
            float b = color.getBlue() / 255.0F;
            if (sourceEntity != null && targetEntity != null) {
                FXDispatcher.INSTANCE.arcBolt(
                        sourceEntity.posX,
                        sourceEntity.posY + sourceEntity.getEyeHeight(),
                        sourceEntity.posZ,
                        targetEntity.posX,
                        targetEntity.posY + targetEntity.getEyeHeight(),
                        targetEntity.posZ,
                        r, g, b, 0.6F);
            }
        }
    }
}
