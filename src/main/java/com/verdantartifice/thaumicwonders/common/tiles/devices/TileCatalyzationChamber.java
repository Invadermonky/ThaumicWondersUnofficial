package com.verdantartifice.thaumicwonders.common.tiles.devices;

import com.verdantartifice.thaumicwonders.common.blocks.BlocksTW;
import com.verdantartifice.thaumicwonders.common.items.catalysts.ICatalystStone;
import com.verdantartifice.thaumicwonders.common.tiles.base.TileTWInventory;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.devices.TileBellows;

public class TileCatalyzationChamber extends TileTWInventory implements ITickable {
    private static final int PLAY_EFFECTS = 4;
    
    protected int refineTime = 0;
    protected int maxRefineTime = 0;
    protected int speedyTime = 0;
    
    protected int facingX = -5;
    protected int facingZ = -5;
    
    protected ItemStack equippedStone = ItemStack.EMPTY;
    
    public TileCatalyzationChamber() {
        super(32);
    }
    
    public ItemStack getEquippedStone() {
        return this.equippedStone;
    }
    
    public boolean setEquippedStone(ItemStack stack) {
        if (stack != null && !stack.isEmpty()) {
            if (stack.getItem() instanceof ICatalystStone) {
                this.equippedStone = stack;
                return true;
            } else {
                return false;
            }
        } else {
            this.equippedStone = stack;
            return true;
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(
            this.getPos().getX() - 1.3D, this.getPos().getY() - 1.3D, this.getPos().getZ() - 1.3D,
            this.getPos().getX() + 2.3D, this.getPos().getY() + 2.3D, this.getPos().getZ() + 1.3D
        );
    }
    
    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return side == EnumFacing.UP ? super.getSlotsForFace(side) : new int[0];
    }
    
    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return false;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.refineTime = compound.getShort("RefineTime");
        this.speedyTime = compound.getShort("SpeedyTime");
        this.equippedStone = new ItemStack(compound.getCompoundTag("EquippedStone"));
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setShort("RefineTime", (short)this.refineTime);
        compound.setShort("SpeedyTime", (short)this.speedyTime);
        compound.setTag("EquippedStone", this.equippedStone.writeToNBT(new NBTTagCompound()));
        return compound;
    }
    
    @Override
    public void update() {
        if (this.facingX == -5) {
            this.setFacing();
        }
        if (!this.world.isRemote) {
            boolean refinedFlag = false;
            if (this.refineTime > 0) {
                this.refineTime--;
                refinedFlag = true;
            }
            if (this.maxRefineTime <= 0) {
                this.maxRefineTime = this.calcRefineTime();
            }
            if (this.refineTime > this.maxRefineTime) {
                this.refineTime = this.maxRefineTime;
            }
            if (this.refineTime <= 0 && refinedFlag) {
                for (int slot = 0; slot < this.getSizeInventory(); slot++) {
                    ItemStack stack = this.getStackInSlot(slot);
                    if (stack != null && !stack.isEmpty()) {
                        ICatalystStone stone = null;
                        if (this.getEquippedStone() != null && !this.getEquippedStone().isEmpty() && this.getEquippedStone().getItem() instanceof ICatalystStone) {
                            stone = (ICatalystStone)this.getEquippedStone().getItem();
                        }
                        
                        // Return the input stack if no catalyst stone is available
                        ItemStack resultStack = (stone != null) ? stone.getRefiningResult(stack) : stack;
                        if (resultStack == null || resultStack.isEmpty()) {
                            resultStack = stack;
                        }
                        if (this.getEquippedStone().attemptDamageItem(1, this.world.rand, null)) {
                            this.getEquippedStone().shrink(1);
                        }
                        if (this.speedyTime > 0) {
                            this.speedyTime--;
                        }
                        this.ejectItem(resultStack.copy());
                        this.world.addBlockEvent(this.getPos(), BlocksTW.CATALYZATION_CHAMBER, PLAY_EFFECTS, 0);
                        if (stone != null) {
                            if (this.world.rand.nextInt(stone.getFluxChance()) == 0) {
                                AuraHelper.polluteAura(this.world, this.getPos().offset(this.getFacing().getOpposite()), 1.0F, true);
                            }
                        }
                        this.decrStackSize(slot, resultStack.getCount());
                        break;
                    }
                }
            }
            if (this.speedyTime <= 0) {
                this.speedyTime = (int)AuraHelper.drainVis(this.getWorld(), this.getPos(), 20.0F, false);
            }
            if (this.refineTime == 0 && !refinedFlag) {
                for (int slot = 0; slot < this.getSizeInventory(); slot++) {
                    if (this.canRefine(this.getStackInSlot(slot))) {
                        this.maxRefineTime = this.calcRefineTime();
                        this.refineTime = this.maxRefineTime;
                        break;
                    } else {
                        this.ejectItem(this.getStackInSlot(slot).copy());
                        this.setInventorySlotContents(slot, ItemStack.EMPTY);
                    }
                }
            }
        }
    }
    
    public ItemStack addItemsToInventory(ItemStack items) {
        if (this.canRefine(items)) {
            return ThaumcraftInvHelper.insertStackAt(this.getWorld(), this.getPos(), EnumFacing.UP, items, false);
        } else {
            this.ejectItem(items);
            return ItemStack.EMPTY;
        }
    }

    private boolean canRefine(ItemStack stack) {
        ICatalystStone stone = null;
        if (this.getEquippedStone() != null && !this.getEquippedStone().isEmpty() && this.getEquippedStone().getItem() instanceof ICatalystStone) {
            stone = (ICatalystStone)this.getEquippedStone().getItem();
        }
        if (stone != null) {
            ItemStack resultStack = stone.getRefiningResult(stack);
            return (resultStack != null) && !resultStack.isEmpty();
        } else {
            return false;
        }
    }

    private void ejectItem(ItemStack itemStack) {
        if (!itemStack.isEmpty()) {
            EnumFacing facing = BlockStateUtils.getFacing(getBlockMetadata()).getOpposite();
            InventoryUtils.ejectStackAt(getWorld(), getPos(), facing, itemStack);
        }
    }

    private int calcRefineTime() {
        int count = this.getBellows();
        int bonus = (count > 0) ? (20 - (count - 1)) * count : 0;
        return Math.max(10, (this.speedyTime > 0 ? 80 : 140) - bonus);
    }

    private int getBellows() {
        int count = 0;
        for (EnumFacing dir : EnumFacing.VALUES) {
            if (dir != EnumFacing.UP) {
                BlockPos tilePos = this.pos.offset(dir, 2);
                TileEntity tile = this.world.getTileEntity(tilePos);
                if (tile instanceof TileBellows &&
                        BlockStateUtils.getFacing(this.world.getBlockState(tilePos)) == dir.getOpposite() &&
                        this.world.getRedstonePowerFromNeighbors(tilePos) == 0) {
                    count++;
                }
            }
        }
        return Math.min(4, count);
    }

    private void setFacing() {
        EnumFacing face = this.getFacing().getOpposite();
        this.facingX = face.getXOffset();
        this.facingZ = face.getZOffset();
    }
    
    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == PLAY_EFFECTS) {
            if (this.world.isRemote) {
                ICatalystStone stone = null;
                if (this.getEquippedStone() != null && !this.getEquippedStone().isEmpty() && this.getEquippedStone().getItem() instanceof ICatalystStone) {
                    stone = (ICatalystStone)this.getEquippedStone().getItem();
                }
                if (stone != null) {
                    for (int i = 0; i < 5; i++) {
                        BlockPos targetPos = this.getPos().offset(this.getFacing().getOpposite(), 2);
                        FXDispatcher.INSTANCE.visSparkle(
                                this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), 
                                targetPos.getX(), targetPos.getY(), targetPos.getZ(), 
                                stone.getSparkleColor());
                    }
                }
            }
            this.world.playSound(null, this.getPos(), SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.8F, 0.9F + this.world.rand.nextFloat() * 0.2F);
            return true;
        } else {
            return super.receiveClientEvent(id, type);
        }
    }
}
