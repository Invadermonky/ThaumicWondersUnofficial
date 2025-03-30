package com.verdantartifice.thaumicwonders.common.network.packets;

import com.verdantartifice.thaumicwonders.common.misc.OreHelper;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TileOreDiviner;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.lib.utils.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class PacketOreDivinerSearch implements IMessage {
    protected BlockPos origin;
    protected ItemStack searchStack;
    
    public PacketOreDivinerSearch() {
        this.origin = null;
        this.searchStack = null;
    }
    
    public PacketOreDivinerSearch(BlockPos origin, ItemStack searchStack) {
        this.origin = origin;
        this.searchStack = searchStack;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.origin = BlockPos.fromLong(buf.readLong());
        this.searchStack = new ItemStack(Utils.readNBTTagCompoundFromBuffer(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.origin.toLong());
        Utils.writeNBTTagCompoundToBuffer(buf, this.searchStack.writeToNBT(new NBTTagCompound()));
    }

    public static class Handler implements IMessageHandler<PacketOreDivinerSearch, IMessage> {
        @Override
        public IMessage onMessage(PacketOreDivinerSearch message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }
        
        @SideOnly(Side.CLIENT)
        private void handle(PacketOreDivinerSearch message, MessageContext ctx) {
            World world = FMLClientHandler.instance().getClient().world;
            EntityPlayer entityPlayer = Minecraft.getMinecraft().player;
            if (message.origin != null && message.searchStack != null) {
                TileEntity tile = world.getTileEntity(message.origin);
                if (tile instanceof TileOreDiviner && OreHelper.isOreBlock(message.searchStack)) {
                    BlockPos target = this.search(world, message.origin, message.searchStack, TileOreDiviner.SCAN_RANGE);
                    if (target == null) {
                        entityPlayer.sendStatusMessage(new TextComponentString(TextFormatting.DARK_PURPLE + I18n.format("event.ore_diviner.not_found")), true);
                    } else {
                        BlockPos delta = target.subtract(message.origin);
                        EnumFacing facing = EnumFacing.getFacingFromVector(delta.getX(), delta.getY(), delta.getZ());
                        entityPlayer.sendStatusMessage(new TextComponentString(TextFormatting.DARK_PURPLE + I18n.format("event.ore_diviner.found." + facing.getName())), true);
                    }
                    ((TileOreDiviner)tile).setTarget(target);
                }
            }
        }
        
        @SideOnly(Side.CLIENT)
        @Nullable
        private BlockPos search(World world, BlockPos origin, ItemStack searchStack, int maxRange) {
            BlockPos target = null;
            for (int dist = 1; dist <= maxRange; dist++) {
                target = this.searchShell(world, origin, searchStack, dist);
                if (target != null) {
                    break;
                }
            }
            return target;
        }
        
        @SideOnly(Side.CLIENT)
        @Nullable
        private BlockPos searchShell(World world, BlockPos origin, ItemStack searchStack, int distance) {
            Set<BlockPos> posSet = this.generateShell(origin, distance);
            for (BlockPos pos : posSet) {
                ItemStack posStack = this.getStackAtPos(world, pos);
                if (posStack != null && OreHelper.isOreMatch(posStack, searchStack)) {
                    return pos;
                }
            }
            return null;
        }
        
        @SideOnly(Side.CLIENT)
        private Set<BlockPos> generateShell(BlockPos origin, int radius) {
            Set<BlockPos> posSet = new HashSet<>();
            this.generateXPlanes(origin, radius, posSet);
            this.generateYPlanes(origin, radius, posSet);
            this.generateZPlanes(origin, radius, posSet);
            return posSet;
        }
        
        @SideOnly(Side.CLIENT)
        private void generateXPlanes(BlockPos origin, int radius, Set<BlockPos> posSet) {
            for (int yy = -radius; yy <= radius; yy++) {
                for (int zz = -radius; zz <= radius; zz++) {
                    posSet.add(origin.add(radius, yy, zz));
                    posSet.add(origin.add(-radius, yy, zz));
                }
            }
        }
        
        @SideOnly(Side.CLIENT)
        private void generateYPlanes(BlockPos origin, int radius, Set<BlockPos> posSet) {
            for (int xx = -radius; xx <= radius; xx++) {
                for (int zz = -radius; zz <= radius; zz++) {
                    posSet.add(origin.add(xx, radius, zz));
                    posSet.add(origin.add(xx, -radius, zz));
                }
            }
        }
        
        @SideOnly(Side.CLIENT)
        private void generateZPlanes(BlockPos origin, int radius, Set<BlockPos> posSet) {
            for (int xx = -radius; xx <= radius; xx++) {
                for (int yy = -radius; yy <= radius; yy++) {
                    posSet.add(origin.add(xx, yy, radius));
                    posSet.add(origin.add(xx, yy, -radius));
                }
            }
        }
        
        @SideOnly(Side.CLIENT)
        @Nonnull
        private ItemStack getStackAtPos(World world, BlockPos pos) {
            ItemStack stack = ItemStack.EMPTY;
            IBlockState state = world.getBlockState(pos);
            if (state.getBlock() != Blocks.AIR && state.getBlock() != Blocks.BEDROCK) {
                Item item = Item.getItemFromBlock(state.getBlock());
                stack = new ItemStack(item, 1, item.getHasSubtypes() ? state.getBlock().getMetaFromState(state) : 0);
            }
            return stack;
        }
    }
}
