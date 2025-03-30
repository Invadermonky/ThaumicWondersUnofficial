package com.verdantartifice.thaumicwonders.common.tiles.base;

import com.verdantartifice.thaumicwonders.common.network.PacketHandler;
import com.verdantartifice.thaumicwonders.common.network.packets.PacketTileToClient;
import com.verdantartifice.thaumicwonders.common.network.packets.PacketTileToServer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;

public class TileTW extends TileEntity {
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.readFromTileNBT(compound);
    }
    
    protected void readFromTileNBT(NBTTagCompound compound) {}

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        return super.writeToNBT(this.writeToTileNBT(compound));
    }
    
    protected NBTTagCompound writeToTileNBT(NBTTagCompound compound) {
        return compound;
    }

    public void syncTile(boolean rerender) {
        IBlockState state = this.world.getBlockState(this.pos);
        this.world.notifyBlockUpdate(this.pos, state, state, (rerender ? 0x6 : 0x2));
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 1, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readFromTileNBT(pkt.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }
    
    public void sendMessageToClient(NBTTagCompound nbt, @Nullable EntityPlayerMP player) {
        if (player == null) {
            if (this.getWorld() != null) {
                PacketHandler.INSTANCE.sendToAllAround(
                    new PacketTileToClient(this.getPos(), nbt), 
                    new NetworkRegistry.TargetPoint(
                        this.getWorld().provider.getDimension(), 
                        this.pos.getX() + 0.5D,
                        this.pos.getY() + 0.5D, 
                        this.pos.getZ() + 0.5D, 
                        128.0D
                    )
                );
            }
        } else {
            PacketHandler.INSTANCE.sendTo(new PacketTileToClient(this.getPos(), nbt), player);
        }
    }
    
    public void sendMessageToServer(NBTTagCompound nbt) {
        PacketHandler.INSTANCE.sendToServer(new PacketTileToServer(this.getPos(), nbt));
    }

    public void messageFromServer(NBTTagCompound nbt) {
        // Do nothing by default
    }

    public void messageFromClient(NBTTagCompound nbt, EntityPlayerMP player) {
        // Do nothing by default
    }
    
    public EnumFacing getFacing() {
        try {
            return EnumFacing.byHorizontalIndex(this.getBlockMetadata() & 0x7);
        }
        catch (Exception ignored) {}
        return EnumFacing.UP;
    }
}
