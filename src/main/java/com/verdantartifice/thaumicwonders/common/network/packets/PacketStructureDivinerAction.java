package com.verdantartifice.thaumicwonders.common.network.packets;

import com.verdantartifice.thaumicwonders.common.items.misc.ItemStructureDiviner;
import com.verdantartifice.thaumicwonders.common.network.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class PacketStructureDivinerAction implements IMessage {
    private int targetType;

    public PacketStructureDivinerAction() {
        this.targetType = -1;
    }

    public PacketStructureDivinerAction(int type) {
        this.targetType = type;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.targetType = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.targetType);
    }

    public static class Handler implements IMessageHandler<PacketStructureDivinerAction, IMessage> {
        @Override
        public IMessage onMessage(PacketStructureDivinerAction message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketStructureDivinerAction message, MessageContext ctx) {
            EntityPlayerMP entityPlayer = ctx.getServerHandler().player;
            ItemStack divinerStack = this.getDivinerStack(entityPlayer);
            if (divinerStack.isEmpty()) {
                return;
            }

            String targetStr = this.getTargetStr(message.targetType);
            if (targetStr == null) {
                if (divinerStack.hasTagCompound()) {
                    divinerStack.getTagCompound().removeTag("targetPoint");
                }
            } else {
                BlockPos targetPos = entityPlayer.getEntityWorld().findNearestStructure(targetStr, entityPlayer.getPosition(), true);
                if (targetPos == null) {
                    if (divinerStack.hasTagCompound()) {
                        divinerStack.getTagCompound().removeTag("targetPoint");
                    }
                    PacketHandler.INSTANCE.sendTo(new PacketLocalizedMessage("event.structure_diviner.not_found"), entityPlayer);
                } else {
                    if (!divinerStack.hasTagCompound()) {
                        divinerStack.setTagCompound(new NBTTagCompound());
                    }
                    divinerStack.getTagCompound().setLong("targetPoint", targetPos.toLong());

                    BlockPos fudgedPos = new BlockPos(targetPos.getX(), entityPlayer.getPosition().getY(), targetPos.getZ());
                    double distanceSq = entityPlayer.getPosition().distanceSq(fudgedPos);
                    String key;
                    if (distanceSq < (10.0D * 10.0D)) {
                        key = "event.structure_diviner.found.here";
                    } else if (distanceSq < (100.0D * 100.0D)) {
                        key = "event.structure_diviner.found.near";
                    } else if (distanceSq < (1000.0D * 1000.0D)) {
                        key = "event.structure_diviner.found.far";
                    } else if (distanceSq < (10000.0D * 10000.0D)) {
                        key = "event.structure_diviner.found.very_far";
                    } else {
                        key = "event.structure_diviner.found.extreme";
                    }
                    PacketHandler.INSTANCE.sendTo(new PacketLocalizedMessage(key), entityPlayer);
                }
            }
        }

        private ItemStack getDivinerStack(EntityPlayerMP entityPlayer) {
            ItemStack stack = entityPlayer.getHeldItemMainhand();
            if (!stack.isEmpty() && stack.getItem() instanceof ItemStructureDiviner) {
                return stack;
            } else {
                stack = entityPlayer.getHeldItemOffhand();
                if (!stack.isEmpty() && stack.getItem() instanceof ItemStructureDiviner) {
                    return stack;
                } else {
                    return ItemStack.EMPTY;
                }
            }
        }

        @Nullable
        private String getTargetStr(int targetType) {
            switch (targetType) {
                case 0:
                    return "Village";
                case 1:
                    return "Temple";
                case 2:
                    return "Mineshaft";
                case 3:
                    return "Monument";
                case 4:
                    return "Mansion";
                case 5:
                    return "Stronghold";
                case 6:
                    return "Fortress";
                case 7:
                    return "EndCity";
                default:
                    return null;
            }
        }
    }
}
