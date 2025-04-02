package com.verdantartifice.thaumicwonders.common.tiles.devices;

import com.verdantartifice.thaumicwonders.common.blocks.BlocksTW;
import com.verdantartifice.thaumicwonders.common.crafting.catalyzationchamber.CatalyzationChamberRecipe;
import com.verdantartifice.thaumicwonders.common.crafting.catalyzationchamber.CatalyzationChamberRegistry;
import com.verdantartifice.thaumicwonders.common.tiles.base.TileTWInventory;
import net.minecraft.block.Block;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
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

import java.util.Arrays;

public class TileCatalyzationChamber extends TileTWInventory implements ITickable {
    private static final int PLAY_EFFECTS = 4;
    
    protected int refineTime = 0;
    protected int maxRefineTime = 0;
    protected int speedyTime = 0;
    
    protected int facingX = -5;
    protected int facingZ = -5;

    protected ItemStack equippedStone = ItemStack.EMPTY;
    protected CatalyzationChamberRecipe recipe = null;
    
    public TileCatalyzationChamber() {
        super(32);
    }
    
    public ItemStack getEquippedStone() {
        return this.equippedStone;
    }
    
    public boolean setEquippedStone(ItemStack stack) {
        if (stack != null && !stack.isEmpty()) {
            if(CatalyzationChamberRegistry.isValidCatalyst(stack)) {
                this.equippedStone = stack;
                return true;
            } else {
                return false;
            }
        } else {
            this.equippedStone = ItemStack.EMPTY;
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
                        ItemStack catalyst = this.getEquippedStone();
                        this.recipe = CatalyzationChamberRegistry.getRecipe(stack, catalyst);
                        if(this.recipe != null) {
                            ItemStack output = this.recipe.getOutput();
                            if(output.isEmpty()) {
                                ItemStack copy = stack.copy();
                                copy.setCount(1);
                                this.ejectItem(copy);
                                this.decrStackSize(slot, 1);
                                continue;
                            }
                            if(catalyst.isItemStackDamageable()) {
                                if(catalyst.attemptDamageItem(1, this.world.rand, null)) {
                                    catalyst.shrink(1);
                                }
                            } else if(catalyst.getItem().hasContainerItem(catalyst)) {
                                ItemStack container = catalyst.getItem().getContainerItem(catalyst);
                                if(!this.setEquippedStone(container)) {
                                    this.ejectItem(container);
                                    catalyst.shrink(1);
                                }
                            } else {
                                catalyst.shrink(1);
                            }
                            if(this.speedyTime > 0) {
                                this.speedyTime--;
                            }
                            this.ejectItem(output.copy());
                            this.world.addBlockEvent(this.getPos(), BlocksTW.CATALYZATION_CHAMBER, PLAY_EFFECTS, 0);
                            if(this.recipe.getFluxChance() > 0 && this.world.rand.nextInt(this.recipe.getFluxChance()) == 0) {
                                AuraHelper.polluteAura(this.world, this.getPos().offset(this.getFacing().getOpposite()), 1.0f, true);
                            }
                            int count = Arrays.stream(this.recipe.getInput().getMatchingStacks())
                                    .filter(input -> !input.isEmpty()).findFirst()
                                    .map(ItemStack::getCount).orElse(1);
                            this.decrStackSize(slot, count);
                            break;
                        }
                    } else {
                        this.recipe = null;
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

    public void dropInventoryContents() {
        if(!this.world.isRemote) {
            if(!this.getEquippedStone().isEmpty()) {
                Block.spawnAsEntity(this.world, this.getPos(), this.getEquippedStone().copy());
            }
            InventoryHelper.dropInventoryItems(this.world, this.getPos(), this);
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
        if(this.getEquippedStone() != null && !this.getEquippedStone().isEmpty()) {
            return CatalyzationChamberRegistry.getRecipe(stack, this.getEquippedStone()) != null;
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
                if (this.recipe != null) {
                    for (int i = 0; i < 5; i++) {
                        BlockPos targetPos = this.getPos().offset(this.getFacing().getOpposite(), 2);
                        FXDispatcher.INSTANCE.visSparkle(
                                this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(),
                                targetPos.getX(), targetPos.getY(), targetPos.getZ(),
                                this.recipe.getSparkleColor());
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
