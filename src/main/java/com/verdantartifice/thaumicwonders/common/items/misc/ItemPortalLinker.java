package com.verdantartifice.thaumicwonders.common.items.misc;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.blocks.BlocksTW;
import com.verdantartifice.thaumicwonders.common.items.base.ItemTW;
import com.verdantartifice.thaumicwonders.common.network.PacketHandler;
import com.verdantartifice.thaumicwonders.common.network.packets.PacketUpdateHeldItem;
import com.verdantartifice.thaumicwonders.common.utils.NBTHelper;
import com.verdantartifice.thaumicwonders.common.utils.StringHelper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemPortalLinker extends ItemTW {
    public ItemPortalLinker() {
        super("portal_linker");
        this.setMaxStackSize(1);
        this.addPropertyOverride(
                new ResourceLocation(ThaumicWonders.MODID, "linked"),
                (stack, worldIn, entityIn) -> this.isLinked(stack) ? 1 : 0
        );
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if (playerIn.isSneaking()) {
            ItemStack stack = playerIn.getHeldItem(handIn);
            RayTraceResult trace = this.rayTrace(worldIn, playerIn, false);
            if (stack.getItem() == this && (trace == null || trace.typeOfHit == RayTraceResult.Type.MISS)) {
                this.removeLinkPosition(stack);
                return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (GuiScreen.isShiftKeyDown()) {
            tooltip.add(StringHelper.getLocalizedString("portal_linker", "tooltip", "info1"));
            tooltip.add(StringHelper.getLocalizedString("portal_linker", "tooltip", "info2"));
        }
        if (this.isLinked(stack)) {
            String dimension = WordUtils.capitalizeFully(this.getAnchorDimensionName(stack).replace("_", " "));
            BlockPos pos = this.getAnchorPosition(stack);

            tooltip.add(StringHelper.getLocalizedString("portal_linker", "tooltip", "linked"));
            tooltip.add("  " + dimension);
            tooltip.add(String.format("  x: %d, y: %d, z: %d", pos.getX(), pos.getY(), pos.getZ()));
        } else {
            tooltip.add(StringHelper.getLocalizedString("portal_linker", "tooltip", "not_linked"));
        }
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking() && stack.getItem() == this && world.getBlockState(pos).getBlock() == BlocksTW.PORTAL_ANCHOR) {
            if (world.isRemote) {
                this.setLinkPosition(stack, world, pos);
                PacketHandler.INSTANCE.sendToServer(new PacketUpdateHeldItem(stack, hand));
                player.sendMessage(new TextComponentTranslation(StringHelper.getTranslationKey("portal_linker", "chat", "bound"), this.getAnchorDimensionName(stack), pos.getX(), pos.getY(), pos.getZ()));
            }
            return EnumActionResult.SUCCESS;
        }
        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }

    public boolean isLinked(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey("dimension") && stack.getTagCompound().hasKey("position");
    }

    public int getAnchorDimension(ItemStack stack) {
        NBTHelper.initNBT(stack);
        return stack.getTagCompound().getInteger("dimension");
    }

    public String getAnchorDimensionName(ItemStack stack) {
        int dim = this.getAnchorDimension(stack);
        return StringHelper.getDimensionName(dim);
    }

    public BlockPos getAnchorPosition(ItemStack stack) {
        NBTHelper.initNBT(stack);
        return BlockPos.fromLong(stack.getTagCompound().getLong("position"));
    }

    @SideOnly(Side.CLIENT)
    public void setLinkPosition(ItemStack stack, World world, BlockPos pos) {
        int dimId = world.provider.getDimension();
        String dimName = world.getProviderName();
        long posLong = pos.toLong();
        NBTHelper.initNBT(stack);
        stack.getTagCompound().setInteger("dimension", dimId);
        stack.getTagCompound().setLong("position", posLong);
    }

    public void removeLinkPosition(ItemStack stack) {
        if (stack.hasTagCompound()) {
            stack.getTagCompound().removeTag("dimension");
            stack.getTagCompound().removeTag("position");
        }
    }
}
