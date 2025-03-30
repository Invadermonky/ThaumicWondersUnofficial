package com.verdantartifice.thaumicwonders.common.network.packets;

import com.verdantartifice.thaumicwonders.common.tiles.base.TileTW;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
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
import thaumcraft.common.lib.utils.Utils;

public class PacketTileToClient implements IMessage {
    private long pos;
    private NBTTagCompound nbt;
    
    public PacketTileToClient() {}
    
    public PacketTileToClient(BlockPos pos, NBTTagCompound compound) {
        this.pos = pos.toLong();
        this.nbt = compound;
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
    
    public static class Handler implements IMessageHandler<PacketTileToClient, IMessage> {
        @Override
        public IMessage onMessage(PacketTileToClient message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }
        
        @SideOnly(Side.CLIENT)
        private void handle(PacketTileToClient message, MessageContext ctx) {
            World world = FMLClientHandler.instance().getClient().world;
            BlockPos bp = BlockPos.fromLong(message.pos);
            if (world != null && bp != null) {
                TileEntity tile = world.getTileEntity(bp);
                if (tile instanceof TileTW) {
                    ((TileTW)tile).messageFromServer(message.nbt == null ? new NBTTagCompound() : message.nbt);
                }
            }
        }
    }
}
