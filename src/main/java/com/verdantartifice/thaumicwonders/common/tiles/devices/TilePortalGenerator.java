package com.verdantartifice.thaumicwonders.common.tiles.devices;

import com.verdantartifice.thaumicwonders.common.tiles.base.TileTW;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.lib.utils.RandomItemChooser;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TilePortalGenerator extends TileTW implements ITickable, IAspectContainer {
    //TODO: Config capacity
    private static final AspectList ESSENTIA_CAPACITY = new AspectList().add(Aspect.MOTION, 100).add(Aspect.ELDRITCH, 100);

    protected static List<RandomItemChooser.Item> instabilityEvents = new ArrayList<>();
    protected static List<RandomItemChooser.Item> spawnEvents = new ArrayList<>();
    protected static List<RandomItemChooser.Item> subvertEvents = new ArrayList<>();
    protected int sparkCounter = 0;
    protected int ticksExisted = 0;
    protected boolean lastEnabled = true;
    protected AspectList essentia = new AspectList();


    @Override
    protected void readFromTileNBT(NBTTagCompound compound) {
        this.essentia.readFromNBT(compound.getCompoundTag("essentia"));
    }

    @Override
    protected NBTTagCompound writeToTileNBT(NBTTagCompound compound) {
        NBTTagCompound essentiaTag = new NBTTagCompound();
        this.essentia.writeToNBT(essentiaTag);
        compound.setTag("essentia", essentiaTag);
        return compound;
    }

    @Override
    public void update() {
        this.sparkCounter++;
        this.ticksExisted++;
        if (this.world.isRemote && this.sparkCounter == 20) {
            BlockPos sourcePos = this.pos.up();
            if (this.world.rand.nextInt() % 2 == 0) {
                sourcePos = sourcePos.south();
            }
            if (this.world.rand.nextInt() % 2 == 0) {
                sourcePos = sourcePos.east();
            }
            Color color = new Color(Aspect.FLUX.getColor());
            float r = color.getRed() / 255.0F;
            float g = color.getGreen() / 255.0F;
            float b = color.getBlue() / 255.0F;
            FXDispatcher.INSTANCE.drawLightningFlash(sourcePos.getX(), sourcePos.getY(), sourcePos.getZ(), r, g, b, 1.0F, 2.5F);
            this.sparkCounter -= (20 + this.world.rand.nextInt(80));
        }
    }

    public boolean isEssentiaFull() {
        return this.doesContainerContain(ESSENTIA_CAPACITY);
    }

    @Override
    public AspectList getAspects() {
        return this.essentia.copy();
    }

    @Override
    public void setAspects(AspectList aspectList) {
        if (aspectList != null) {
            this.essentia = aspectList.copy();
        }
    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        return aspect == Aspect.ELDRITCH || aspect == Aspect.MOTION;
    }

    @Override
    public int addToContainer(Aspect aspect, int toAdd) {
        //TODO: Config capacity
        int capacity = 100;
        if (toAdd == 0) {
            return 0;
        } else if (this.essentia.getAmount(aspect) >= capacity || !this.doesContainerAccept(aspect)) {
            // Incompatible addition; return all of it
            this.syncTile(false);
            this.markDirty();
            return toAdd;
        } else {
            // Add as much as possible and return the remainder
            int added = Math.min(toAdd, capacity - this.essentia.getAmount(aspect));
            this.essentia.add(aspect, added);
            this.syncTile(false);
            this.markDirty();
            return (toAdd - added);
        }
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int amount) {
        if (this.doesContainerContainAmount(aspect, amount)) {
            this.essentia.reduce(aspect, amount);
            this.syncTile(false);
            this.markDirty();
            return true;
        } else {
            return false;
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
    public boolean doesContainerContainAmount(Aspect aspect, int amount) {
        return this.essentia.getAmount(aspect) >= amount;
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
    public int containerContains(Aspect aspect) {
        return this.doesContainerAccept(aspect) ? this.essentia.getAmount(aspect) : 0;
    }

    public float drainVis(float visDrain, boolean simulate) {
        return AuraHelper.drainVis(this.world, this.pos, visDrain, simulate);
    }

    public boolean drainEssentia(int eldritchDrain, int motusDrain, boolean simulate) {
        AspectList drain = new AspectList().add(Aspect.ELDRITCH, eldritchDrain).add(Aspect.MOTION, motusDrain);
        if (this.doesContainerContain(drain)) {
            if (!simulate) {
                this.takeFromContainer(drain);
            }
            return true;
        }
        return false;
    }

    protected static class InstabilityEventEntry implements RandomItemChooser.Item {
        public int eventId;
        public int weight;
        public float requiredInstability;

        protected InstabilityEventEntry(int eventId, int weight, float reqInstability) {
            this.eventId = eventId;
            this.weight = weight;
            this.requiredInstability = reqInstability;
        }

        @Override
        public double getWeight() {
            return this.weight;
        }
    }

    static {
        instabilityEvents.add(new InstabilityEventEntry(0, 60, 0.0F));  // Flux burst
        instabilityEvents.add(new InstabilityEventEntry(1, 35, 0.0F));  // Creature spawn
        instabilityEvents.add(new InstabilityEventEntry(2, 5, -50.0F)); // Portal subversion

        spawnEvents.add(new InstabilityEventEntry(0, 40, 0.0F));    // Wisps
        spawnEvents.add(new InstabilityEventEntry(1, 30, -20.0F));  // Zombie pigmen
        spawnEvents.add(new InstabilityEventEntry(2, 20, -40.0F));  // Endermen
        spawnEvents.add(new InstabilityEventEntry(3, 7, -60.0F));   // Inhabited zombie
        spawnEvents.add(new InstabilityEventEntry(4, 3, -80.0F));   // Eldritch guardian

        subvertEvents.add(new InstabilityEventEntry(0, 75, -50.0F));    // Lesser crimson portal
        subvertEvents.add(new InstabilityEventEntry(1, 25, -75.0F));    // Flux rift
    }
}
