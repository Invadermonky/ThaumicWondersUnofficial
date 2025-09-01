package com.verdantartifice.thaumicwonders.common.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateHeldItem implements IMessage {
    private ItemStack heldItem;
    private EnumHand hand;

    public PacketUpdateHeldItem(ItemStack heldItem, EnumHand hand) {
        this.heldItem = heldItem;
        this.hand = hand;
    }

    public PacketUpdateHeldItem() {
        this(ItemStack.EMPTY, EnumHand.MAIN_HAND);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.heldItem = ByteBufUtils.readItemStack(buf);
        this.hand = EnumHand.values()[buf.readByte()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, this.heldItem);
        buf.writeByte((byte) this.hand.ordinal());
    }

    public static class Handler implements IMessageHandler<PacketUpdateHeldItem, IMessage> {
        @Override
        public IMessage onMessage(PacketUpdateHeldItem message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
                EntityPlayer player = ctx.getServerHandler().player;
                player.setHeldItem(message.hand, message.heldItem);
            });
            return null;
        }
    }
}
