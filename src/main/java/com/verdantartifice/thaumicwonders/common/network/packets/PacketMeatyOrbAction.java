package com.verdantartifice.thaumicwonders.common.network.packets;

import com.verdantartifice.thaumicwonders.common.network.PacketHandler;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TileMeatyOrb;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aura.AuraHelper;

public class PacketMeatyOrbAction implements IMessage {
    private BlockPos tilePos;
    
    public PacketMeatyOrbAction() {
        this.tilePos = null;
    }
    
    public PacketMeatyOrbAction(BlockPos tilePos) {
        this.tilePos = tilePos;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.tilePos = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.tilePos.toLong());
    }

    public static class Handler implements IMessageHandler<PacketMeatyOrbAction, IMessage> {
        @Override
        public IMessage onMessage(PacketMeatyOrbAction message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }
        
        private void handle(PacketMeatyOrbAction message, MessageContext ctx) {
            EntityPlayerMP entityPlayer = ctx.getServerHandler().player;
            World world = entityPlayer.getEntityWorld();
            TileEntity tileEntity = world.getTileEntity(message.tilePos);
            if (tileEntity instanceof TileMeatyOrb) {
                TileMeatyOrb tile = (TileMeatyOrb)tileEntity;
                if ( tile.doesContainerContainAmount(Aspect.WATER, TileMeatyOrb.MIN_FUEL) &&
                     tile.doesContainerContainAmount(Aspect.LIFE, TileMeatyOrb.MIN_FUEL) &&
                     tile.doesContainerContainAmount(Aspect.ELDRITCH, TileMeatyOrb.MIN_FUEL) ) {
                    tile.takeFromContainer(Aspect.WATER, TileMeatyOrb.MIN_FUEL);
                    tile.takeFromContainer(Aspect.LIFE, TileMeatyOrb.MIN_FUEL);
                    tile.takeFromContainer(Aspect.ELDRITCH, TileMeatyOrb.MIN_FUEL);
                    AuraHelper.polluteAura(world, message.tilePos, 10.0F, true);
                    tile.setActive(true);
                    PacketHandler.INSTANCE.sendToAllAround(
                            new PacketLocalizedMessage("event.meaty_orb.used"), 
                            new NetworkRegistry.TargetPoint(world.provider.getDimension(), message.tilePos.getX(), message.tilePos.getY(), message.tilePos.getZ(), 32.0D));
                    PacketHandler.INSTANCE.sendToAllAround(
                            new PacketMeteorbFx(message.tilePos, Aspect.ELDRITCH.getColor()), 
                            new NetworkRegistry.TargetPoint(world.provider.getDimension(), message.tilePos.getX(), message.tilePos.getY(), message.tilePos.getZ(), 32.0D));
                } else {
                    PacketHandler.INSTANCE.sendTo(new PacketLocalizedMessage("event.meaty_orb.unfueled"), entityPlayer);
                }
            }
        }
    }
}
