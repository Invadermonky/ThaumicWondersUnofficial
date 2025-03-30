package com.verdantartifice.thaumicwonders.common.tiles.devices;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.tiles.base.TileTW;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.lib.utils.RandomItemChooser;

import java.util.ArrayList;
import java.util.List;

public class TileMeatyOrb extends TileTW implements IAspectContainer, IEssentiaTransport, ITickable {
    private static final RandomItemChooser RIC = new RandomItemChooser();
    private static final int DURATION_TICKS = 300;
    private static final int CAPACITY = 250;
    public static final int MIN_FUEL = 250;

    protected static class MeatEntry implements RandomItemChooser.Item {
        public ItemStack itemStack;
        public int weight;
        
        public MeatEntry(ItemStack itemStack, int weight) {
            this.itemStack = itemStack;
            this.weight = weight;
        }
        
        @Override
        public double getWeight() {
            return this.weight;
        }
    }
    
    protected static List<RandomItemChooser.Item> meats = new ArrayList<RandomItemChooser.Item>();
    
    static {
        meats.add(new MeatEntry(new ItemStack(Items.BEEF, 1, 0), 30));
        meats.add(new MeatEntry(new ItemStack(Items.PORKCHOP, 1, 0), 25));
        meats.add(new MeatEntry(new ItemStack(Items.CHICKEN, 1, 0), 20));
        meats.add(new MeatEntry(new ItemStack(Items.MUTTON, 1, 0), 15));
        meats.add(new MeatEntry(new ItemStack(Items.RABBIT, 1, 0), 10));
    }
    
    protected int lifeEssentia = 0;
    protected int waterEssentia = 0;
    protected int eldritchEssentia = 0;
    protected int tickCounter = 0;
    protected int activeCounter = 0;

    @Override
    protected void readFromTileNBT(NBTTagCompound compound) {
        this.lifeEssentia = compound.getShort("lifeEssentia");
        this.waterEssentia = compound.getShort("waterEssentia");
        this.eldritchEssentia = compound.getShort("eldritchEssentia");
    }

    @Override
    protected NBTTagCompound writeToTileNBT(NBTTagCompound compound) {
        compound.setShort("lifeEssentia", (short)this.lifeEssentia);
        compound.setShort("waterEssentia", (short)this.waterEssentia);
        compound.setShort("eldritchEssentia", (short)this.eldritchEssentia);
        return compound;
    }
    
    public void setActive(boolean active) {
        this.activeCounter = (active ? DURATION_TICKS : 0);
    }

    @Override
    public void update() {
        if (!this.world.isRemote && (++this.tickCounter % 5 == 0)) {
            this.fill();
        }
        if (!this.world.isRemote && this.activeCounter > 0) {
            MeatEntry entry = (MeatEntry)RIC.chooseOnWeight(meats);
            if (entry != null) {
                double x = this.pos.getX() + 0.5D + (32.0D * (this.world.rand.nextDouble() - this.world.rand.nextDouble()));
                double z = this.pos.getZ() + 0.5D + (32.0D * (this.world.rand.nextDouble() - this.world.rand.nextDouble()));
                this.world.spawnEntity(new EntityItem(this.world, x, this.world.getActualHeight(), z, entry.itemStack.copy()));
            }
            this.activeCounter--;
        }
    }

    protected void fill() {
        for (EnumFacing face : EnumFacing.HORIZONTALS) {
            if (!this.canInputFrom(face) || this.getEssentiaAmount(face) >= CAPACITY) {
                continue;
            }
            TileEntity te = ThaumcraftApiHelper.getConnectableTile(this.world, this.pos, face);
            if (te instanceof IEssentiaTransport) {
                IEssentiaTransport otherTile = (IEssentiaTransport)te;
                if (!otherTile.canOutputTo(face.getOpposite())) {
                    continue;
                }
                if ( otherTile.getEssentiaType(face.getOpposite()) == this.getEssentiaType(face) &&
                     otherTile.getEssentiaAmount(face.getOpposite()) > 0 &&
                     this.getSuctionAmount(face) > otherTile.getSuctionAmount(face.getOpposite()) &&
                     this.getSuctionAmount(face) >= otherTile.getMinimumSuction() ) {
                    int taken = otherTile.takeEssentia(this.getEssentiaType(face), 1, face.getOpposite());
                    int leftover = this.addToContainer(this.getEssentiaType(face), taken);
                    if (leftover > 0) {
                        ThaumicWonders.LOGGER.info("Meaty Orb spilling {} essentia on fill", leftover);
                        AuraHelper.polluteAura(this.world, this.pos, leftover, true);
                    }
                    this.syncTile(false);
                    this.markDirty();
                }
            }
        }
    }
    
    /**
     * Get the relative facing for the given absolute facing.  The Meaty Orb's relative north is its control
     * panel, west is life input, east is water input, south is eldritch input.
     * @param absFace facing relative to world axes
     * @return facing relative to orb faces
     */
    public EnumFacing getRelativeFacing(EnumFacing absFace) {
        if (this.getBlockType() instanceof IBlockFacingHorizontal && absFace != EnumFacing.UP && absFace != EnumFacing.DOWN) {
            EnumFacing blockFacing = this.world.getBlockState(this.pos).getValue(IBlockFacingHorizontal.FACING);
            int rotations = 0;
            EnumFacing relativeFacing = absFace;
            while (blockFacing != EnumFacing.NORTH) {
                blockFacing = blockFacing.rotateY();
                rotations++;
            }
            for (int index = 0; index < rotations; index++) {
                relativeFacing = relativeFacing.rotateY();
            }
            return relativeFacing;
        } else {
            return absFace;
        }
    }
    
    @Override
    public int addEssentia(Aspect aspect, int amt, EnumFacing face) {
        if (this.canInputFrom(face) && aspect == this.getEssentiaType(face)) {
            return (amt - this.addToContainer(aspect, amt));
        } else {
            return 0;
        }
    }

    @Override
    public boolean canInputFrom(EnumFacing face) {
        EnumFacing relFace = this.getRelativeFacing(face);
        return (relFace == EnumFacing.WEST || relFace == EnumFacing.EAST || relFace == EnumFacing.SOUTH);
    }

    @Override
    public boolean canOutputTo(EnumFacing face) {
        return false;
    }

    @Override
    public int getEssentiaAmount(EnumFacing face) {
        EnumFacing relFace = this.getRelativeFacing(face);
        if (relFace == EnumFacing.WEST) {
            return this.lifeEssentia;
        } else if (relFace == EnumFacing.EAST) {
            return this.waterEssentia;
        } else if (relFace == EnumFacing.SOUTH) {
            return this.eldritchEssentia;
        } else {
            return 0;
        }
    }

    @Override
    public Aspect getEssentiaType(EnumFacing face) {
        EnumFacing relFace = this.getRelativeFacing(face);
        if (relFace == EnumFacing.WEST) {
            return Aspect.LIFE;
        } else if (relFace == EnumFacing.EAST) {
            return Aspect.WATER;
        } else if (relFace == EnumFacing.SOUTH) {
            return Aspect.ELDRITCH;
        } else {
            return null;
        }
    }

    @Override
    public int getMinimumSuction() {
        // Can't output, so no need for minimum suction
        return 0;
    }

    @Override
    public int getSuctionAmount(EnumFacing face) {
        return (this.getEssentiaAmount(face) >= CAPACITY) ? 0 : 128;
    }

    @Override
    public Aspect getSuctionType(EnumFacing face) {
        return this.getEssentiaType(face);
    }

    @Override
    public boolean isConnectable(EnumFacing face) {
        return this.canInputFrom(face);
    }

    @Override
    public void setSuction(Aspect aspect, int amt) {
        // Do nothing
    }

    @Override
    public int takeEssentia(Aspect aspect, int amt, EnumFacing face) {
        // Can't output
        return 0;
    }

    @Override
    public int addToContainer(Aspect aspect, int toAdd) {
        int retVal = 0;
        if (toAdd == 0) {
            return 0;
        } else if (this.lifeEssentia < CAPACITY && aspect == Aspect.LIFE) {
            // Add as much life as possible and return the remainder
            int added = Math.min(toAdd, CAPACITY - this.lifeEssentia);
            this.lifeEssentia += added;
            retVal = (toAdd - added);
        } else if (this.waterEssentia < CAPACITY && aspect == Aspect.WATER) {
            // Add as much water as possible and return the remainder
            int added = Math.min(toAdd, CAPACITY - this.waterEssentia);
            this.waterEssentia += added;
            retVal = (toAdd - added);
        } else if (this.eldritchEssentia < CAPACITY && aspect == Aspect.ELDRITCH) {
            // Add as much eldritch as possible and return the remainder
            int added = Math.min(toAdd, CAPACITY - this.eldritchEssentia);
            this.eldritchEssentia += added;
            retVal = (toAdd - added);
        } else {
            retVal = toAdd;
        }
        
        this.syncTile(false);
        this.markDirty();
        return retVal;
    }

    @Override
    public int containerContains(Aspect aspect) {
        if (aspect == Aspect.LIFE) {
            return this.lifeEssentia;
        } else if (aspect == Aspect.WATER) {
            return this.waterEssentia;
        } else if (aspect == Aspect.ELDRITCH) {
            return this.eldritchEssentia;
        } else {
            return 0;
        }
    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        return (aspect == Aspect.LIFE || aspect == Aspect.WATER || aspect == Aspect.ELDRITCH);
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
        return (aspect == Aspect.LIFE && this.lifeEssentia >= amt) ||
               (aspect == Aspect.WATER && this.waterEssentia >= amt) ||
               (aspect == Aspect.ELDRITCH && this.eldritchEssentia >= amt);
    }

    @Override
    public AspectList getAspects() {
        AspectList list = new AspectList();
        if (this.lifeEssentia > 0) {
            list.add(Aspect.LIFE, this.lifeEssentia);
        }
        if (this.waterEssentia > 0) {
            list.add(Aspect.WATER, this.waterEssentia);
        }
        if (this.eldritchEssentia > 0) {
            list.add(Aspect.ELDRITCH, this.eldritchEssentia);
        }
        return list;
    }

    @Override
    public void setAspects(AspectList aspects) {
        if (aspects != null && aspects.size() > 0) {
            this.lifeEssentia = aspects.getAmount(Aspect.LIFE);
            this.waterEssentia = aspects.getAmount(Aspect.WATER);
            this.eldritchEssentia = aspects.getAmount(Aspect.ELDRITCH);
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
        if (aspect == Aspect.LIFE && this.lifeEssentia >= amt) {
            this.lifeEssentia -= amt;
        } else if (aspect == Aspect.WATER && this.waterEssentia >= amt) {
            this.waterEssentia -= amt;
        } else if (aspect == Aspect.ELDRITCH && this.eldritchEssentia >= amt) {
            this.eldritchEssentia -= amt;
        } else {
            return false;
        }
        this.syncTile(false);
        this.markDirty();
        return true;
    }
}
