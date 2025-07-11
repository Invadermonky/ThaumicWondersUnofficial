package com.verdantartifice.thaumicwonders.common.tiles.devices;

import com.verdantartifice.thaumicwonders.common.blocks.devices.BlockAlkahestVat;
import com.verdantartifice.thaumicwonders.common.tiles.base.TileTW;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.client.fx.FXDispatcher;

public class TileAlkahestVat extends TileTW implements ITickable, IItemHandler {
    @Override
    public void update() {
        if (this.world.isRemote) {
            this.drawEffects();
        }
    }

    protected void drawEffects() {
        FXDispatcher.INSTANCE.crucibleFroth(
                this.pos.getX() + 0.2F + (this.world.rand.nextFloat() * 0.6F),
                this.pos.getY() + this.getFluidHeight(),
                this.pos.getZ() + 0.2F + (this.world.rand.nextFloat() * 0.6F)
        );
    }

    protected float getFluidHeight() {
        return 0.8F;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public @Nullable <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this);
        }
        return super.getCapability(capability, facing);
    }


    @Override
    public int getSlots() {
        return 1;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        if (!simulate) {
            IBlockState state = this.world.getBlockState(this.pos);
            if (state.getBlock() instanceof BlockAlkahestVat) {
                BlockAlkahestVat vat = (BlockAlkahestVat) state.getBlock();
                vat.releaseVis(this.world, this.pos, stack);
                vat.playHissSound(this.world, this.pos);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }
}
