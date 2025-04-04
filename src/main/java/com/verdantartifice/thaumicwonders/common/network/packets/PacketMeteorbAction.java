package com.verdantartifice.thaumicwonders.common.network.packets;

import com.verdantartifice.thaumicwonders.common.network.PacketHandler;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TileMeteorb;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.api.aspects.Aspect;

public class PacketMeteorbAction implements IMessage {
    private int targetWeather;
    private BlockPos tilePos;

    public PacketMeteorbAction() {
        this.targetWeather = -1;
        this.tilePos = null;
    }

    public PacketMeteorbAction(int targetWeather, BlockPos tilePos) {
        this.targetWeather = targetWeather;
        this.tilePos = tilePos;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.targetWeather = buf.readInt();
        this.tilePos = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.targetWeather);
        buf.writeLong(this.tilePos.toLong());
    }

    public static class Handler implements IMessageHandler<PacketMeteorbAction, IMessage> {
        @Override
        public IMessage onMessage(PacketMeteorbAction message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketMeteorbAction message, MessageContext ctx) {
            EntityPlayerMP entityPlayer = ctx.getServerHandler().player;
            World world = entityPlayer.getEntityWorld();
            WorldInfo worldinfo = world.getWorldInfo();
            TileEntity tileEntity = world.getTileEntity(message.tilePos);
            if (tileEntity instanceof TileMeteorb) {
                TileMeteorb tile = (TileMeteorb) tileEntity;
                int duration = (300 + world.rand.nextInt(600)) * 20;
                if (message.targetWeather == 0 && tile.doesContainerContainAmount(Aspect.AIR, TileMeteorb.MIN_FUEL)) {
                    tile.takeFromContainer(Aspect.AIR, TileMeteorb.MIN_FUEL);
                    worldinfo.setCleanWeatherTime(duration);
                    worldinfo.setRainTime(0);
                    worldinfo.setThunderTime(0);
                    worldinfo.setRaining(false);
                    worldinfo.setThundering(false);
                    PacketHandler.INSTANCE.sendToDimension(new PacketLocalizedMessage("event.meteorb.used"), world.provider.getDimension());
                    PacketHandler.INSTANCE.sendToAllAround(
                            new PacketMeteorbFx(message.tilePos, Aspect.AIR.getColor()),
                            new NetworkRegistry.TargetPoint(world.provider.getDimension(), message.tilePos.getX(), message.tilePos.getY(), message.tilePos.getZ(), 32.0D));
                } else if (message.targetWeather == 1 && tile.doesContainerContainAmount(Aspect.WATER, TileMeteorb.MIN_FUEL)) {
                    tile.takeFromContainer(Aspect.WATER, TileMeteorb.MIN_FUEL);
                    worldinfo.setCleanWeatherTime(0);
                    worldinfo.setRainTime(duration);
                    worldinfo.setThunderTime(duration);
                    worldinfo.setRaining(true);
                    worldinfo.setThundering(false);
                    PacketHandler.INSTANCE.sendToDimension(new PacketLocalizedMessage("event.meteorb.used"), world.provider.getDimension());
                    PacketHandler.INSTANCE.sendToAllAround(
                            new PacketMeteorbFx(message.tilePos, Aspect.WATER.getColor()),
                            new NetworkRegistry.TargetPoint(world.provider.getDimension(), message.tilePos.getX(), message.tilePos.getY(), message.tilePos.getZ(), 32.0D));
                } else if (message.targetWeather == 2 && tile.doesContainerContainAmount(Aspect.ENERGY, TileMeteorb.MIN_FUEL)) {
                    tile.takeFromContainer(Aspect.ENERGY, TileMeteorb.MIN_FUEL);
                    worldinfo.setCleanWeatherTime(0);
                    worldinfo.setRainTime(duration);
                    worldinfo.setThunderTime(duration);
                    worldinfo.setRaining(true);
                    worldinfo.setThundering(true);
                    PacketHandler.INSTANCE.sendToDimension(new PacketLocalizedMessage("event.meteorb.used"), world.provider.getDimension());
                    PacketHandler.INSTANCE.sendToAllAround(
                            new PacketMeteorbFx(message.tilePos, Aspect.ENERGY.getColor()),
                            new NetworkRegistry.TargetPoint(world.provider.getDimension(), message.tilePos.getX(), message.tilePos.getY(), message.tilePos.getZ(), 32.0D));
                } else {
                    PacketHandler.INSTANCE.sendTo(new PacketLocalizedMessage("event.meteorb.unfueled"), entityPlayer);
                }
            }
        }
    }
}
