package com.verdantartifice.thaumicwonders.common.network.packets;

import com.verdantartifice.thaumicwonders.common.tiles.base.TileTW;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.common.lib.utils.Utils;

public class PacketTileToServer implements IMessage {
    private long pos;
    private NBTTagCompound nbt;

    public PacketTileToServer() {
    }

    public PacketTileToServer(BlockPos pos, NBTTagCompound nbt) {
        this.pos = pos.toLong();
        this.nbt = nbt;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = buf.readLong();
        this.nbt = Utils.readNBTTagCompoundFromBuffer(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.pos);
        Utils.writeNBTTagCompoundToBuffer(buf, this.nbt);
    }

    public static class Handler implements IMessageHandler<PacketTileToServer, IMessage> {
        @Override
        public IMessage onMessage(PacketTileToServer message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketTileToServer message, MessageContext ctx) {
            World world = ctx.getServerHandler().player.getServerWorld();
            BlockPos bp = BlockPos.fromLong(message.pos);
            if (world != null && bp != null) {
                TileEntity tile = world.getTileEntity(bp);
                if (tile instanceof TileTW) {
                    ((TileTW) tile).messageFromClient(message.nbt == null ? new NBTTagCompound() : message.nbt, ctx.getServerHandler().player);
                }
            }
        }
    }
}
