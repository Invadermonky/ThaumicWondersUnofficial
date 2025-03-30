package com.verdantartifice.thaumicwonders.common.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
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
import thaumcraft.common.blocks.IBlockFacing;

import java.awt.*;

public class PacketDimensionalRipperFx implements IMessage {
    protected BlockPos source;
    protected BlockPos target;
    
    public PacketDimensionalRipperFx() {
        this.source = null;
        this.target = null;
    }
    
    public PacketDimensionalRipperFx(BlockPos source, BlockPos target) {
        this.source = source;
        this.target = target;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.source = BlockPos.fromLong(buf.readLong());
        this.target = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.source.toLong());
        buf.writeLong(this.target.toLong());
    }

    public static class Handler implements IMessageHandler<PacketDimensionalRipperFx, IMessage> {
        @Override
        public IMessage onMessage(PacketDimensionalRipperFx message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }
        
        @SideOnly(Side.CLIENT)
        private void handle(PacketDimensionalRipperFx message, MessageContext ctx) {
            World world = FMLClientHandler.instance().getClient().world;
            IBlockState state = world.getBlockState(message.source);
            if (state.getBlock() instanceof IBlockFacing) {
                EnumFacing blockFacing = state.getValue(IBlockFacing.FACING);
                FXDispatcher.INSTANCE.beamBore(
                    message.source.getX() + 0.5D + (blockFacing.getXOffset() / 2.0D),
                    message.source.getY() + 0.5D + (blockFacing.getYOffset() / 2.0D),
                    message.source.getZ() + 0.5D + (blockFacing.getZOffset() / 2.0D),
                    message.target.getX() + 0.5D, 
                    message.target.getY() + 0.5D, 
                    message.target.getZ() + 0.5D, 
                    1, 
                    Color.RED.getRGB(), 
                    false, 
                    1.0F, 
                    null, 
                    1
                );
            }
        }
    }
}
