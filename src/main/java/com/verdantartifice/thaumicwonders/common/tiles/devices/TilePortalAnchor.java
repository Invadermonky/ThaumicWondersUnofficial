package com.verdantartifice.thaumicwonders.common.tiles.devices;

import com.verdantartifice.thaumicwonders.common.config.ConfigHandlerTW;
import com.verdantartifice.thaumicwonders.common.entities.EntityVoidPortal;
import com.verdantartifice.thaumicwonders.common.items.linkers.ItemPortalLinker;
import com.verdantartifice.thaumicwonders.common.tiles.base.TileTW;
import com.verdantartifice.thaumicwonders.common.utils.StringHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.IInteractWithCaster;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.items.casters.CasterManager;
import thaumcraft.common.lib.SoundsTC;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TilePortalAnchor extends TileTW implements IInteractWithCaster {
    public ItemStackHandler stackHandler = new ItemStackHandler() {
        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if(!simulate && !super.extractItem(slot, amount, true).isEmpty()) {
                removePortals(false);
                setPrimaryAnchor(false);
            }
            return super.extractItem(slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
    };
    private BlockPos generatorPos;
    private boolean isPrimaryAnchor;

    @Override
    protected void readFromTileNBT(NBTTagCompound compound) {
        if (compound.hasKey("generator")) {
            this.generatorPos = BlockPos.fromLong(compound.getLong("generator"));
        }
        this.isPrimaryAnchor = compound.getBoolean("primary");
        this.stackHandler.deserializeNBT(compound.getCompoundTag("inventory"));
    }

    @Override
    protected NBTTagCompound writeToTileNBT(NBTTagCompound compound) {
        if (this.generatorPos != null) {
            compound.setLong("generator", this.generatorPos.toLong());
        }
        compound.setBoolean("primary", this.isPrimaryAnchor);
        compound.setTag("inventory", this.stackHandler.serializeNBT());
        return compound;
    }

    @Override
    public boolean onCasterRightClick(World world, ItemStack itemStack, EntityPlayer player, BlockPos blockPos, EnumFacing enumFacing, EnumHand enumHand) {
        if (!this.world.isRemote) {
            if (this.getPortalEntity() == null) {
                TilePortalGenerator generator = this.getPortalGenerator();
                if (generator != null) {
                    double activationCost = ConfigHandlerTW.portal_network.activationVisCost;
                    if (this.drainVisFromGenerator(player, activationCost, true) <= 0) {
                        if (this.spawnPortal(player)) {
                            this.drainVisFromGenerator(player, activationCost, false);
                            this.setPrimaryAnchor(true);
                            this.syncTile(false);
                            this.markDirty();
                        }
                    }
                } else {
                    player.sendStatusMessage(StringHelper.getTranslatedTextComponent("void_portal", "chat", "no_generator"), true);
                }
            } else {
                this.removePortals(false);
                this.setPrimaryAnchor(false);
                this.syncTile(false);
                this.markDirty();
            }
        }
        return true;
    }


    public void onBlockBreak() {
        this.dropInventoryContents();
        this.removePortals(false);
    }

    protected void dropInventoryContents() {
        if (!this.world.isRemote) {
            ItemStack stack = this.getPortalLinker();
            if (!stack.isEmpty()) {
                Block.spawnAsEntity(this.world, this.pos, stack.copy());
            }
        }
    }

    public ItemStack getPortalLinker() {
        return this.stackHandler.getStackInSlot(0);
    }

    public BlockPos getPortalLinkerPosition() {
        ItemStack stack = this.getPortalLinker();
        if (!stack.isEmpty() && stack.getItem() instanceof ItemPortalLinker) {
            return ((ItemPortalLinker) stack.getItem()).getAnchorPosition(stack);
        }
        return new BlockPos(0, 0, 0);
    }

    public int getPortalLinkerDimension() {
        ItemStack stack = this.getPortalLinker();
        if (stack.getItem() instanceof ItemPortalLinker) {
            return ((ItemPortalLinker) stack.getItem()).getAnchorDimension(stack);
        }
        return 0;
    }


    public void doPortalSpawnEffect() {
        TilePortalGenerator generator = this.getPortalGenerator();
        if (generator != null) {
            Color color = new Color(Aspect.VOID.getColor());
            float r = color.getRed() / 255.0F;
            float g = color.getGreen() / 255.0F;
            float b = color.getBlue() / 255.0F;
            FXDispatcher.INSTANCE.arcLightning(
                    generator.getPos().getX() + 0.5, generator.getPos().getY() + 1.0, generator.getPos().getZ() + 0.5,
                    this.pos.getX() + 0.5, this.pos.getY() + 0.25, this.pos.getZ() + 0.5,
                    r, g, b, 0.6f);
        }
    }

    public void doPortalUpkeepEffect() {
        if (this.isPrimaryAnchor) {
            TilePortalGenerator generator = this.getPortalGenerator();
            if (generator != null) {
                Color color = new Color(Aspect.VOID.getColor());
                float r = color.getRed() / 255.0F;
                float g = color.getGreen() / 255.0F;
                float b = color.getBlue() / 255.0F;
                FXDispatcher.INSTANCE.arcBolt(
                        generator.getPos().getX() + 0.5, generator.getPos().getY() + 1.0, generator.getPos().getZ() + 0.5,
                        this.pos.getX() + 0.5, this.pos.getY() + 0.25, this.pos.getZ() + 0.5,
                        r, g, b, 0.6f);
            }
        }
    }

    public boolean drainForTeleport() {
        //TODO: Teleport cost.
        return true;
    }

    protected @Nullable TilePortalGenerator getPortalGenerator() {
        if (this.generatorPos != null && this.world.getTileEntity(this.generatorPos) instanceof TilePortalGenerator) {
            return (TilePortalGenerator) this.world.getTileEntity(this.generatorPos);
        } else {
            this.generatorPos = null;
        }

        TileEntity tile;
        for (int x = -8; x <= 8; x++) {
            for (int z = -8; z <= 8; z++) {
                for (int y = -3; y <= 3; y++) {
                    tile = this.world.getTileEntity(this.pos.add(x, y, z));
                    if (tile instanceof TilePortalGenerator) {
                        this.generatorPos = tile.getPos();
                        this.markDirty();
                        this.syncTile(false);
                        return (TilePortalGenerator) tile;
                    }
                }
            }
        }
        return null;
    }

    protected double drainVisFromGenerator(EntityPlayer player, double visDrain, boolean simulate) {
        TilePortalGenerator generator = this.getPortalGenerator();
        if (generator != null) {
            double discounted = (float) (visDrain * (1.0 - CasterManager.getTotalVisDiscount(player)));
            return discounted - generator.drainVis((float) discounted, simulate);
        }
        return visDrain;
    }

    protected boolean drainEssentiaFromGenerator(int alienisDrain, int motusDrain, boolean simulate) {
        TilePortalGenerator generator = this.getPortalGenerator();
        if (generator != null) {
            return generator.drainEssentia(alienisDrain, motusDrain, simulate);
        }
        return false;
    }


    public WorldServer getLinkedWorld(int linkedDimension) {
        WorldServer targetWorld = DimensionManager.getWorld(linkedDimension);
        if (targetWorld == null) {
            DimensionManager.initDimension(linkedDimension);
            targetWorld = DimensionManager.getWorld(linkedDimension);
        }
        return targetWorld;
    }

    public @Nullable TilePortalAnchor getLinkedAnchor(WorldServer linkedWorld, BlockPos linkedPos) {
        //Retrieve linked world
        if (linkedWorld != null) {
            TileEntity tile = linkedWorld.getTileEntity(linkedPos);
            return tile instanceof TilePortalAnchor ? (TilePortalAnchor) tile : null;
        }
        return null;
    }

    public @Nullable TilePortalAnchor getLinkedAnchor(int linkedDimension, BlockPos linkedPos) {
        WorldServer linkedWorld = this.getLinkedWorld(linkedDimension);
        return linkedWorld != null ? this.getLinkedAnchor(linkedWorld, linkedPos) : null;
    }


    public @Nullable TilePortalAnchor getPrimaryAnchor() {
        if (this.isPrimaryAnchor) {
            return this;
        } else {
            EntityVoidPortal portal = this.getPortalEntity();
            if (portal != null) {
                WorldServer linkedWorld = this.getLinkedWorld(portal.getLinkDim());
                if (linkedWorld != null) {
                    return this.getLinkedAnchor(linkedWorld, new BlockPos(portal.getLinkX(), portal.getLinkY(), portal.getLinkZ()));
                }
            }
        }
        return null;
    }

    public boolean isPrimaryAnchor() {
        return this.isPrimaryAnchor;
    }

    public void setPrimaryAnchor(boolean isPrimary) {
        this.isPrimaryAnchor = isPrimary;
    }

    public boolean isTargetingSelf(int linkedDim, BlockPos linkedPos) {
        return this.world.provider.getDimension() == linkedDim
                && this.pos.getX() == linkedPos.getX()
                && this.pos.getY() == linkedPos.getY()
                && this.pos.getZ() == linkedPos.getZ();
    }


    public @Nullable EntityVoidPortal getPortalEntity() {
        List<EntityVoidPortal> portals = this.getPortalEntities();
        return !portals.isEmpty() ? portals.get(0) : null;
    }

    public List<EntityVoidPortal> getPortalEntities() {
        return this.world.getEntitiesWithinAABB(EntityVoidPortal.class, new AxisAlignedBB(this.pos, this.pos.add(1, 2, 1)));
    }

    public boolean canPortalSpawn() {
        return this.world.isAirBlock(this.pos.up()) && this.world.isAirBlock(this.pos.up(2));
    }

    public boolean spawnPortal(EntityPlayer player) {
        if (!this.world.isRemote) {
            ItemStack linker = this.getPortalLinker();
            if (linker.getItem() instanceof ItemPortalLinker && ((ItemPortalLinker) linker.getItem()).isLinked(linker)) {
                int linkedDim = this.getPortalLinkerDimension();
                BlockPos linkedPos = this.getPortalLinkerPosition();
                if (!this.isTargetingSelf(linkedDim, linkedPos)) {
                    return this.spawnPortal(player, linkedDim, linkedPos);
                } else {
                    player.sendStatusMessage(StringHelper.getTranslatedTextComponent("void_portal", "chat", "no_self"), true);
                }
            } else {
                player.sendStatusMessage(StringHelper.getTranslatedTextComponent("void_portal", "chat", "no_linker"), true);
            }
        }
        return false;
    }

    public boolean spawnPortal(EntityPlayer player, int linkedDimension, BlockPos linkedPos) {
        if (!this.world.isRemote) {
            WorldServer linkedWorld = this.getLinkedWorld(linkedDimension);
            if (linkedWorld != null) {
                TilePortalAnchor linkedAnchor = this.getLinkedAnchor(linkedWorld, linkedPos);
                if (this.canPortalSpawn() && linkedAnchor != null && linkedAnchor.canPortalSpawn()) {
                    World targetWorld = linkedAnchor.getWorld();

                    //Removing existing portals just in case
                    this.removePortals(false);

                    //Spawning portal
                    EntityVoidPortal portal = new EntityVoidPortal(this.world);
                    portal.setPosition(this.pos.getX() + 0.5, this.pos.getY() + 0.75, this.pos.getZ() + 0.5);
                    portal.setLinkDim(linkedDimension);
                    portal.setLinkPos(linkedPos);

                    //Spawning linked portal
                    EntityVoidPortal linkedPortal = new EntityVoidPortal(targetWorld);
                    linkedPortal.setPosition(linkedPos.getX() + 0.5, linkedPos.getY() + 0.75, linkedPos.getZ() + 0.5);
                    linkedPortal.setLinkDim(this.world.provider.getDimension());
                    linkedPortal.setLinkPos(this.pos);

                    return this.world.spawnEntity(portal) && targetWorld.spawnEntity(linkedPortal);
                } else {
                    player.sendStatusMessage(StringHelper.getTranslatedTextComponent("void_portal", "chat", "no_anchor"), true);
                }
            } else {
                player.sendStatusMessage(StringHelper.getTranslatedTextComponent("void_portal", "chat", "no_world"), true);
            }
        }
        return false;
    }

    public void removePortals(boolean requirePrimary) {
        if (!this.world.isRemote) {
            TilePortalAnchor primaryAnchor = this.getPrimaryAnchor();

            //Checking to determine if this portal deactivation can only occur from the primary anchor
            if (requirePrimary && primaryAnchor != this)
                return;

            List<EntityVoidPortal> portals = new ArrayList<>();
            if (primaryAnchor != null) {
                portals.addAll(primaryAnchor.getPortalEntities());

                TilePortalAnchor linkedAnchor = primaryAnchor.getLinkedAnchor(primaryAnchor.getPortalLinkerDimension(), primaryAnchor.getPortalLinkerPosition());
                if (linkedAnchor != null) {
                    portals.addAll(linkedAnchor.getPortalEntities());
                }
            } else {
                portals.addAll(this.getPortalEntities());
            }
            if (!portals.isEmpty()) {
                this.world.playSound(null, this.pos, SoundsTC.shock, SoundCategory.BLOCKS, 0.6f, (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F + 1.0F);
                portals.forEach(EntityVoidPortal::setDead);
            }
        }
    }

    @Override
    public void syncTile(boolean rerender) {
        super.syncTile(rerender);
        this.syncSlots(null);
    }

    @Override
    public void messageFromServer(NBTTagCompound nbt) {
        this.stackHandler.deserializeNBT(nbt.getCompoundTag("inventory"));
    }

    @Override
    public void messageFromClient(NBTTagCompound nbt, EntityPlayerMP player) {
        if(nbt.hasKey("requestSync")) {
            this.syncSlots(player);
        }
    }

    @Override
    public void onLoad() {
        if(!this.world.isRemote) {
            this.syncSlots(null);
        } else {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setBoolean("requestSync", true);
            this.sendMessageToServer(tag);
        }
    }

    protected void syncSlots(@Nullable EntityPlayerMP player) {
        this.sendMessageToClient(this.writeToNBT(new NBTTagCompound()), player);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return facing == EnumFacing.DOWN && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    @Override
    public @Nullable <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if(facing == EnumFacing.DOWN && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.stackHandler);
        }
        return super.getCapability(capability, facing);
    }
}
