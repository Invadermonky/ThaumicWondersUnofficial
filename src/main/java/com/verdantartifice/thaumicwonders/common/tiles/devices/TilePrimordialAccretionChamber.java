package com.verdantartifice.thaumicwonders.common.tiles.devices;

import com.verdantartifice.thaumicwonders.common.blocks.BlocksTW;
import com.verdantartifice.thaumicwonders.common.items.ItemsTW;
import com.verdantartifice.thaumicwonders.common.tiles.base.TileTWInventory;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.lib.utils.InventoryUtils;

public class TilePrimordialAccretionChamber extends TileTWInventory implements IAspectContainer, ITickable {
    private static final int CAPACITY = 125;
    private static final AspectList REQUIRED_FUEL = new AspectList().add(Aspect.AIR, 125).add(Aspect.EARTH, 125).add(Aspect.FIRE, 125).add(Aspect.WATER, 125).add(Aspect.ORDER, 125).add(Aspect.ENTROPY, 125);
    private static final int PLAY_EFFECTS = 4;
    
    protected int refineTime = 0;
    protected int maxRefineTime = 0;
    protected int speedyTime = 0;
        
    protected AspectList essentia = new AspectList();
    
    public TilePrimordialAccretionChamber() {
        super(32);
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
    protected void readFromTileNBT(NBTTagCompound compound) {
        this.essentia.readFromNBT(compound, "essentia");
    }
    
    @Override
    protected NBTTagCompound writeToTileNBT(NBTTagCompound compound) {
        this.essentia.writeToNBT(compound, "essentia");
        return compound;
    }

    @Override
    public void update() {
        if (!this.world.isRemote) {
            boolean refinedFlag = false;
            if (this.refineTime > 0 && this.isEssentiaFull()) {
                // Only refine if all necessary essentia is present
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
                    if (stack != null && !stack.isEmpty() && this.isEssentiaFull()) {
                        ItemStack resultStack = this.getResultStack(stack);
                        if (this.speedyTime > 0) {
                            this.speedyTime--;
                        }
                        this.takeFromContainer(REQUIRED_FUEL);
                        this.ejectItem(resultStack.copy());
                        this.world.addBlockEvent(this.getPos(), BlocksTW.PRIMORDIAL_ACCRETION_CHAMBER, PLAY_EFFECTS, 0);
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
    
    private ItemStack getResultStack(ItemStack inputStack) {
        if (inputStack.getItem() == ItemsTW.PRIMORDIAL_GRAIN) {
            int chance = this.world.rand.nextInt(10);
            if (chance == 0) {
                return new ItemStack(ItemsTC.primordialPearl, 1, 5);
            } else if (chance < 3) {
                return new ItemStack(ItemsTC.primordialPearl, 1, 6);
            } else {
                return new ItemStack(ItemsTC.primordialPearl, 1, 7);
            }
        } else {
            return inputStack;
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

    private boolean canRefine(ItemStack items) {
        return (items != null && !items.isEmpty() && items.getItem() == ItemsTW.PRIMORDIAL_GRAIN && this.isEssentiaFull());
    }

    private void ejectItem(ItemStack itemStack) {
        if (!itemStack.isEmpty()) {
            EnumFacing facing = BlockStateUtils.getFacing(getBlockMetadata()).getOpposite();
            InventoryUtils.ejectStackAt(getWorld(), getPos(), facing, itemStack);
        }
    }

    private int calcRefineTime() {
        return this.speedyTime > 0 ? 80 : 140;
    }

    public Aspect getCurrentSuction() {
        for (Aspect aspect : REQUIRED_FUEL.getAspectsSortedByName()) {
            if (!this.doesContainerContainAmount(aspect, CAPACITY)) {
                return aspect;
            }
        }
        return null;
    }
    
    public boolean isEssentiaFull() {
        return this.doesContainerContain(REQUIRED_FUEL);
    }

    @Override
    public int addToContainer(Aspect aspect, int toAdd) {
        if (toAdd == 0) {
            return 0;
        } else if (this.essentia.getAmount(aspect) >= CAPACITY || !this.doesContainerAccept(aspect)) {
            // Incompatible addition; return all of it
            this.syncTile(false);
            this.markDirty();
            return toAdd;
        } else {
            // Add as much as possible and return the remainder
            int added = Math.min(toAdd, CAPACITY - this.essentia.getAmount(aspect));
            this.essentia.add(aspect, added);
            this.syncTile(false);
            this.markDirty();
            return (toAdd - added);
        }
    }

    @Override
    public int containerContains(Aspect aspect) {
        return this.doesContainerAccept(aspect) ? this.essentia.getAmount(aspect) : 0;
    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        return (REQUIRED_FUEL.getAmount(aspect) > 0);
    }

    @Override
    public boolean doesContainerContain(AspectList aspectList) {
        boolean satisfied = true;
        for (Aspect aspect : aspectList.getAspects()) {
            satisfied = satisfied && this.doesContainerContainAmount(aspect, aspectList.getAmount(aspect));
        }
        return satisfied;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect aspect, int amt) {
        return this.essentia.getAmount(aspect) >= amt;
    }

    @Override
    public AspectList getAspects() {
        return this.essentia.copy();
    }

    @Override
    public void setAspects(AspectList aspects) {
        if (aspects != null) {
            this.essentia = aspects.copy();
        }
    }

    @Override
    public boolean takeFromContainer(AspectList aspectList) {
        if (!this.doesContainerContain(aspectList)) {
            return false;
        } else {
            boolean satisfied = true;
            for (Aspect aspect : aspectList.getAspects()) {
                satisfied = satisfied && this.takeFromContainer(aspect, aspectList.getAmount(aspect));
            }
            return satisfied;
        }
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int amt) {
        if (this.doesContainerContainAmount(aspect, amt)) {
            this.essentia.reduce(aspect, amt);
            this.syncTile(false);
            this.markDirty();
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == PLAY_EFFECTS) {
            if (this.world.isRemote) {
                for (int i = 0; i < 5; i++) {
                    BlockPos targetPos = this.getPos().offset(this.getFacing().getOpposite(), 2);
                    FXDispatcher.INSTANCE.visSparkle(
                            this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), 
                            targetPos.getX(), targetPos.getY(), targetPos.getZ(), 
                            Aspect.MAGIC.getColor());
                }
            }
            this.world.playSound(null, this.getPos(), SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.8F, 0.9F + this.world.rand.nextFloat() * 0.2F);
            return true;
        } else {
            return super.receiveClientEvent(id, type);
        }
    }
}
