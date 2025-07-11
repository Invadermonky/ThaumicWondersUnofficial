package com.verdantartifice.thaumicwonders.common.tiles.devices;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.config.ConfigHandlerTW;
import com.verdantartifice.thaumicwonders.common.crafting.voidbeacon.VoidBeaconEntryRegistry;
import com.verdantartifice.thaumicwonders.common.tiles.base.TileTW;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.lib.utils.EntityUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TileVoidBeacon extends TileTW implements ITickable, IAspectContainer, IEssentiaTransport {
    private static final int CAPACITY = 100;
    private static final int PLAY_EFFECTS = 4;

    protected final List<TileVoidBeacon.BeamSegment> beamSegments = new ArrayList<>();

    protected Aspect essentiaType = null;
    protected int essentiaAmount = 0;
    protected int tickCounter = 0;
    protected boolean validPlacement = false;
    protected int levels = -1;
    protected int progress = 0;

    @SideOnly(Side.CLIENT)
    private long beamRenderCounter;
    @SideOnly(Side.CLIENT)
    private float beamRenderScale;

    @Nullable
    public Aspect getEssentiaType() {
        return this.essentiaType;
    }

    public int getEssentiaAmount() {
        return this.essentiaAmount;
    }

    public void clearEssentia() {
        this.essentiaType = null;
        this.essentiaAmount = 0;
        this.markDirty();
        this.syncTile(false);
    }

    public int getLevels() {
        return this.levels;
    }

    public int getProgress() {
        return this.progress;
    }

    @Override
    protected void readFromTileNBT(NBTTagCompound compound) {
        this.essentiaType = Aspect.getAspect(compound.getString("essentiaType"));
        this.essentiaAmount = compound.getShort("essentiaAmount");
        this.levels = compound.getShort("levels");
        this.progress = compound.getShort("progress");
    }

    @Override
    protected NBTTagCompound writeToTileNBT(NBTTagCompound compound) {
        if (this.essentiaType != null) {
            compound.setString("essentiaType", this.essentiaType.getTag());
            compound.setShort("essentiaAmount", (short) this.essentiaAmount);
        }
        compound.setShort("levels", (short) this.levels);
        compound.setShort("progress", (short) this.progress);
        return compound;
    }

    @Override
    public void update() {
        this.tickCounter++;
        if (!this.world.isRemote && (this.tickCounter % 5 == 0)) {
            this.fill();
        }
        if (this.tickCounter % 80 == 0) {
            this.updateBeam();
            this.updateLevels();
        }
        if (!this.world.isRemote && this.tickCounter % 20 == 0) {
            if (this.canMakeProgress()) {
                this.drainRifts();
            }
            while (this.canConjureItem()) {
                this.eject(this.getConjuredItem(this.essentiaType));
                this.progress -= ConfigHandlerTW.void_beacon.riftPowerRequired;
                this.essentiaAmount -= this.getRequiredEssentia();
                if (this.essentiaAmount <= 0) {
                    this.essentiaType = null;
                }
                this.markDirty();
                this.syncTile(false);
            }
        }
    }

    protected boolean canMakeProgress() {
        return this.validPlacement &&
                BlockStateUtils.isEnabled(this.getBlockMetadata()) &&
                this.hasEnoughEssentia() &&
                this.progress < ConfigHandlerTW.void_beacon.riftPowerRequired;
    }

    protected boolean canConjureItem() {
        return this.validPlacement &&
                BlockStateUtils.isEnabled(this.getBlockMetadata()) &&
                this.hasEnoughEssentia() &&
                this.canEject() &&
                this.progress >= ConfigHandlerTW.void_beacon.riftPowerRequired;
    }

    protected boolean hasEnoughEssentia() {
        if (this.essentiaType == null || this.levels < 0) {
            return false;
        } else {
            return (this.essentiaAmount >= this.getRequiredEssentia());
        }
    }

    protected int getRequiredEssentia() {
        return ConfigHandlerTW.void_beacon.baseEssentiaCost / (int) (Math.pow(2, this.levels));
    }

    protected void eject(@Nonnull ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        for (EnumFacing face : EnumFacing.HORIZONTALS) {
            BlockPos otherPos = this.pos.offset(face);
            TileEntity te = this.world.getTileEntity(otherPos);
            if (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite())) {
                IItemHandler handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite());
                for (int slot = 0; slot < handler.getSlots(); slot++) {
                    stack = handler.insertItem(slot, stack, false);
                    if (stack.isEmpty()) {
                        return;
                    }
                }
            }
        }
        if (!stack.isEmpty()) {
            ThaumicWonders.LOGGER.warn("Void beacon failed to eject {}!", stack);
        }
    }

    @Nonnull
    protected ItemStack getConjuredItem(Aspect aspect) {
        return VoidBeaconEntryRegistry.getDropForAspect(this.world.rand, aspect);
    }

    protected boolean canEject() {
        for (EnumFacing face : EnumFacing.HORIZONTALS) {
            BlockPos otherPos = this.pos.offset(face);
            TileEntity te = this.world.getTileEntity(otherPos);
            if (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite())) {
                IItemHandler handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite());
                for (int slot = 0; slot < handler.getSlots(); slot++) {
                    if (handler.getStackInSlot(slot).isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected void drainRifts() {
        List<EntityFluxRift> riftList = this.getValidRifts();
        boolean found = false;
        for (EntityFluxRift rift : riftList) {
            double drained = Math.sqrt(rift.getRiftSize());
            this.progress += (int) drained;
            rift.setRiftStability(rift.getRiftStability() - (float) (drained / 15.0D));
            if (this.world.rand.nextInt(33) == 0) {
                rift.setRiftSize(rift.getRiftSize() - 1);
            }
            if (drained >= 1.0D) {
                found = true;
            }
        }
        if (found) {
            this.syncTile(false);
            this.markDirty();
            if (this.tickCounter % 40 == 0) {
                this.world.addBlockEvent(this.pos, this.getBlockType(), PLAY_EFFECTS, this.tickCounter);
            }
        }
    }

    protected List<EntityFluxRift> getValidRifts() {
        List<EntityFluxRift> retVal = new ArrayList<>();
        List<EntityFluxRift> riftList = EntityUtils.getEntitiesInRange(this.world, this.pos, null, EntityFluxRift.class, 16.0D);
        for (EntityFluxRift rift : riftList) {
            if (!rift.isDead && rift.getRiftSize() > 1) {
                Vec3d v1 = new Vec3d(this.pos.getX() + 0.5D, this.pos.getY() + 1.0D, this.pos.getZ() + 0.5D);
                Vec3d v2 = new Vec3d(rift.posX, rift.posY, rift.posZ);
                v1 = v1.add(v2.subtract(v1).normalize());
                if (EntityUtils.canEntityBeSeen(rift, v1.x, v1.y, v1.z)) {
                    retVal.add(rift);
                }
            }
        }
        return retVal;
    }

    protected void updateBeam() {
        this.beamSegments.clear();
        this.validPlacement = true;
        Color beamColor = new Color(Aspect.ELDRITCH.getColor());
        TileVoidBeacon.BeamSegment segment = new TileVoidBeacon.BeamSegment(beamColor.getRGBColorComponents(null));
        this.beamSegments.add(segment);
        BlockPos.MutableBlockPos mbp = new BlockPos.MutableBlockPos();

        for (int y = this.pos.getY() + 1; y < this.world.getActualHeight(); y++) {
            mbp.setPos(this.pos.getX(), y, this.pos.getZ());
            IBlockState blockState = this.world.getBlockState(mbp);
            if (blockState.getLightOpacity(this.world, mbp) >= 15 && blockState.getBlock() != Blocks.BEDROCK) {
                this.validPlacement = false;
                this.beamSegments.clear();
                break;
            } else {
                segment.incrementHeight();
            }
        }
    }

    protected void updateLevels() {
        this.levels = 0;
        if (this.validPlacement) {
            for (int yOffset = 1; yOffset <= 4; yOffset++) {
                if (this.isLevelComplete(yOffset)) {
                    this.levels++;
                } else {
                    break;
                }
            }
        }
    }

    protected boolean isLevelComplete(int yOffset) {
        for (int x = this.pos.getX() - yOffset; x <= this.pos.getX() + yOffset; x++) {
            for (int z = this.pos.getZ() - yOffset; z <= this.pos.getZ() + yOffset; z++) {
                int y = this.pos.getY() - yOffset;
                IBlockState state = this.world.getBlockState(new BlockPos(x, y, z));
                if (state.getBlock() != BlocksTC.metalBlockVoid) {
                    return false;
                }
            }
        }
        return true;
    }

    protected void fill() {
        for (EnumFacing face : EnumFacing.HORIZONTALS) {
            if (!this.canInputFrom(face) || this.getEssentiaAmount(face) >= CAPACITY) {
                continue;
            }
            TileEntity te = ThaumcraftApiHelper.getConnectableTile(this.world, this.pos, face);
            if (te instanceof IEssentiaTransport) {
                IEssentiaTransport otherTile = (IEssentiaTransport) te;
                if (!otherTile.canOutputTo(face.getOpposite())) {
                    continue;
                }
                Aspect type = otherTile.getEssentiaType(face.getOpposite());

                if (type != null && otherTile.getEssentiaAmount(face.getOpposite()) > 0
                        && (this.getEssentiaType(face) == null || type == this.getEssentiaType(face))
                        && this.getSuctionAmount(face) > otherTile.getSuctionAmount(face.getOpposite())
                        && this.getSuctionAmount(face) >= otherTile.getMinimumSuction()
                ) {
                    int taken = otherTile.takeEssentia(type, 1, face.getOpposite());
                    int leftover = this.addToContainer(type, taken);
                    if (leftover > 0) {
                        ThaumicWonders.LOGGER.info("Void beacon spilling {} essentia on fill", leftover);
                        AuraHelper.polluteAura(this.world, this.pos, leftover, true);
                    }
                    this.syncTile(false);
                    this.markDirty();
                }
            }
        }
    }

    @Override
    public boolean isConnectable(EnumFacing face) {
        return this.canInputFrom(face);
    }

    @Override
    public boolean canInputFrom(EnumFacing face) {
        return (face != EnumFacing.DOWN && face != EnumFacing.UP);
    }

    @Override
    public boolean canOutputTo(EnumFacing face) {
        return false;
    }

    @Override
    public void setSuction(Aspect aspect, int amt) {
        // Do nothing
    }

    @Override
    public Aspect getSuctionType(EnumFacing face) {
        return this.getEssentiaType(face);
    }

    @Override
    public int getSuctionAmount(EnumFacing face) {
        return (this.getEssentiaAmount(face) >= CAPACITY) ? 0 : 128;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amt, EnumFacing face) {
        // Can't output
        return 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amt, EnumFacing face) {
        if (this.canInputFrom(face) && (this.getEssentiaType(face) == null || aspect == this.getEssentiaType(face))) {
            return (amt - this.addToContainer(aspect, amt));
        } else {
            return 0;
        }
    }

    @Override
    public Aspect getEssentiaType(EnumFacing face) {
        return this.essentiaType;
    }

    @Override
    public int getEssentiaAmount(EnumFacing face) {
        return this.essentiaAmount;
    }

    @Override
    public int getMinimumSuction() {
        // Can't output, so no need for minimum suction
        return 0;
    }

    @Override
    public AspectList getAspects() {
        AspectList list = new AspectList();
        if (this.essentiaType != null) {
            list.add(this.essentiaType, this.essentiaAmount);
        }
        return list;
    }

    @Override
    public void setAspects(AspectList aspectList) {
        if (aspectList != null && aspectList.size() > 0) {
            this.essentiaType = aspectList.getAspectsSortedByAmount()[0];
            this.essentiaAmount = aspectList.getAmount(this.essentiaType);
            this.markDirty();
            this.syncTile(false);
        }
    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        return true;
    }

    @Override
    public int addToContainer(Aspect aspect, int toAdd) {
        int retVal;
        if (toAdd == 0) {
            return 0;
        } else if (this.essentiaAmount < CAPACITY && (this.essentiaType == null || this.essentiaType == aspect)) {
            // Add as much as possible and return the remainder
            int added = Math.min(toAdd, CAPACITY - this.essentiaAmount);
            this.essentiaAmount += added;
            this.essentiaType = aspect;
            retVal = (toAdd - added);
        } else {
            retVal = toAdd;
        }

        this.markDirty();
        this.syncTile(false);
        return retVal;
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int amt) {
        if (this.essentiaType == aspect && this.essentiaAmount >= amt) {
            this.essentiaAmount -= amt;
            if (this.essentiaAmount <= 0) {
                this.essentiaType = null;
            }
            this.markDirty();
            this.syncTile(false);
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
    public boolean doesContainerContainAmount(Aspect aspect, int amt) {
        return (this.essentiaType == aspect && this.essentiaAmount >= amt);
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
        return (this.essentiaType == aspect) ? this.essentiaAmount : 0;
    }

    @SideOnly(Side.CLIENT)
    public List<TileVoidBeacon.BeamSegment> getBeamSegments() {
        return this.beamSegments;
    }

    @SideOnly(Side.CLIENT)
    public float shouldBeamRender() {
        if (!this.validPlacement || !BlockStateUtils.isEnabled(this.getBlockMetadata())) {
            return 0.0F;
        } else {
            int i = (int) (this.world.getTotalWorldTime() - this.beamRenderCounter);
            this.beamRenderCounter = this.world.getTotalWorldTime();

            if (i > 1) {
                this.beamRenderScale -= (float) i / 40.0F;
                if (this.beamRenderScale < 0.0F) {
                    this.beamRenderScale = 0.0F;
                }
            }

            this.beamRenderScale += 0.025F;

            if (this.beamRenderScale > 1.0F) {
                this.beamRenderScale = 1.0F;
            }

            return this.beamRenderScale;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 65536.0D;
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == PLAY_EFFECTS) {
            if (this.world.isRemote) {
                List<EntityFluxRift> riftList = this.getValidRifts();
                for (EntityFluxRift rift : riftList) {
                    FXDispatcher.INSTANCE.voidStreak(
                            rift.posX,
                            rift.posY,
                            rift.posZ,
                            this.pos.getX() + 0.5D,
                            this.pos.getY() + 1.0D,
                            this.pos.getZ() + 0.5D,
                            type,
                            0.04F);
                }
            }
            return true;
        } else {
            return super.receiveClientEvent(id, type);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    public static class BeamSegment {
        /**
         * RGB (0 to 1.0) colors of this beam segment
         */
        private final float[] colors;
        private int height;

        public BeamSegment(float[] colorsIn) {
            this.colors = colorsIn;
            this.height = 1;
        }

        protected void incrementHeight() {
            ++this.height;
        }

        /**
         * Returns RGB (0 to 1.0) colors of this beam segment
         */
        public float[] getColors() {
            return this.colors;
        }

        @SideOnly(Side.CLIENT)
        public int getHeight() {
            return this.height;
        }
    }
}
