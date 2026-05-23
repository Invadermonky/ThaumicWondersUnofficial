package com.verdantartifice.thaumicwonders.common.tiles.devices;

import com.google.common.collect.ImmutableMap;
import com.verdantartifice.thaumicwonders.common.blocks.BlocksTW;
import com.verdantartifice.thaumicwonders.common.blocks.misc.BlockArcanePillar;
import com.verdantartifice.thaumicwonders.common.blocks.misc.BlockArcanePillar.EnumDirection;
import com.verdantartifice.thaumicwonders.common.config.ConfigHandlerTW;
import com.verdantartifice.thaumicwonders.common.registry.SoundsTW;
import com.verdantartifice.thaumicwonders.common.sounds.EnchanterSoundLoop;
import com.verdantartifice.thaumicwonders.common.sounds.EssentiaSoundLoop;
import com.verdantartifice.thaumicwonders.common.tiles.base.TileTW;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.events.EssentiaHandler;

import java.util.Map;

public class TileEssentiaEnchanter extends TileTW implements ITickable, IAspectContainer {
    public static final BlockPos[] OFFSET_OBSIDIAN;
    public static final BlockPos[] OFFSET_BRICKS;
    public static final ImmutableMap<EnumDirection, BlockPos> OFFSET_PILLARS;
    public static final int PROGRESS_MAX = 270;
    public static final int COOLDOWN_MAX = 40;

    public final ItemStackHandler stackHandler = new ItemStackHandler() {
        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return isEnchanterActive() && this.isItemValid(slot, stack) ? super.insertItem(slot, stack, simulate) : stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return (!isCrafting() || !ConfigHandlerTW.essentia_enchanter.lockSlot) ? super.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.isItemEnchantable();
        }

        @Override
        protected void onContentsChanged(int slot) {
            syncTile(false);
            markDirty();
        }
    };
    private AspectList recipeEssentiaCache = new AspectList();
    public AspectList recipeEssentia = new AspectList();
    private ItemStack recipeOutput = ItemStack.EMPTY;
    private boolean isActive;
    private boolean isCrafting;
    private int progress;
    private int cooldown;
    private int count;

    @Override
    protected void readFromTileNBT(NBTTagCompound compound) {
        this.recipeEssentiaCache.readFromNBT(compound, "aspectsCache");
        this.recipeEssentia.readFromNBT(compound, "aspects");
        this.stackHandler.deserializeNBT(compound.getCompoundTag("inventory"));
        this.recipeOutput = new ItemStack(compound.getCompoundTag("recipeOutput"));
        this.isCrafting = compound.getBoolean("isCrafting");
        this.isActive = compound.getBoolean("isActive");
        this.progress = compound.getInteger("progress");
        this.cooldown = compound.getInteger("cooldown");
    }

    @Override
    protected NBTTagCompound writeToTileNBT(NBTTagCompound compound) {
        this.recipeEssentiaCache.writeToNBT(compound, "aspectsCache");
        this.recipeEssentia.writeToNBT(compound, "aspects");
        compound.setTag("inventory", this.stackHandler.serializeNBT());
        compound.setTag("recipeOutput", this.recipeOutput.serializeNBT());
        compound.setBoolean("isCrafting", this.isCrafting);
        compound.setBoolean("isActive", this.isActive);
        compound.setInteger("progress", this.progress);
        compound.setInteger("cooldown", this.cooldown);
        return compound;
    }

    @Override
    public void update() {
        this.count++;
        boolean did = false;

        if(!this.world.isRemote) {
            if(this.isActive) {
                if(!this.isStructureValid()) {
                    this.deconstructStructure();
                    did = true;
                } else if(this.isCrafting) {
                    if(this.getItemToEnchant().isEmpty()) {
                        if(this.recipeEssentia.visSize() > 0 || this.progress > 0) {
                            this.world.playSound(null, this.pos, SoundsTC.craftfail, SoundCategory.BLOCKS, 0.5f, 1.0f);
                        }
                        int flux = Math.min(20, this.getAspectsToRender().visSize() / 2);
                        AuraHelper.polluteAura(this.world, this.pos, flux, true);
                        this.resetCrafting();

                        did = true;
                    } else if(this.recipeEssentia.visSize() > 0) {
                        did |= this.drainEssentia();
                    } else {
                        did |= this.handleProgressTick();
                        if(this.getProgress() == PROGRESS_MAX) {
                            this.world.playSound(null, this.pos, SoundsTW.ENCHANT_START, SoundCategory.BLOCKS, 2.0f, 1.0f);
                            Minecraft.getMinecraft().getSoundHandler().playSound(new EnchanterSoundLoop(SoundsTW.ENCHANT_LOOP, this, 0.5f));
                        }
                        if(this.getProgress() <= 0) {
                            this.completeEnchantment();
                            if(this.cooldown <= 0) {
                                this.resetCrafting();
                            }
                        }
                    }
                }
            } else {
                if(this.count % 20 == 0 && this.isStructureValid()) {
                    this.setEnchanterActive(true);
                }
            }
        }

        if(did) {
            this.syncTile(false);
            this.markDirty();
        }
    }

    public ItemStack getItemToEnchant() {
        return this.stackHandler.getStackInSlot(0);
    }

    public void completeEnchantment() {
        if(!this.recipeOutput.isEmpty()) {
            this.stackHandler.setStackInSlot(0, this.recipeOutput);
            this.world.playSound(null, this.pos, SoundsTC.wand, SoundCategory.BLOCKS, 0.5f, 1.0f);
            this.recipeOutput = ItemStack.EMPTY;
        }
    }

    public int getProgress() {
        return this.progress;
    }

    public int getMaxProgress() {
        return PROGRESS_MAX;
    }

    protected boolean handleProgressTick() {
        if(this.progress <= 0 && this.cooldown <= 0) {
            AuraHelper.drainVis(this.world, this.pos, 10, false);
            this.progress = PROGRESS_MAX;
            this.cooldown = COOLDOWN_MAX;
        } else {
            if(this.progress > 0) {
                this.progress--;
            } else {
                this.cooldown--;
            }
        }
        return true;
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public int getMaxCooldown() {
        return COOLDOWN_MAX;
    }

    public ItemStack getRecipeOutput() {
        return this.recipeOutput;
    }

    public AspectList getAspectsToRender() {
        AspectList aspectList = new AspectList();
        Aspect[] sortedAspects = this.getAspectsCache().getAspectsSortedByAmount();
        for(Aspect aspect : sortedAspects) {
            int max = this.getAspectsCache().getAmount(aspect);
            int rem = this.getAspects().getAmount(aspect);
            aspectList.add(aspect, max - rem);
        }
        return aspectList;
    }

    public AspectList getAspectsCache() {
        return this.recipeEssentiaCache;
    }

    public boolean isEnchanterActive() {
        return this.isActive;
    }

    protected void setEnchanterActive(boolean isActive) {
        if(this.isActive != isActive) {
            this.isActive = isActive;
            this.syncTile(false);
            this.markDirty();
        }
    }

    public boolean isCrafting() {
        return this.isCrafting;
    }

    public void startCrafting(ItemStack output, AspectList requiredEssentia) {
        this.recipeEssentiaCache = requiredEssentia.copy();
        this.recipeEssentia = requiredEssentia;
        this.recipeOutput = output;
        this.isCrafting = true;
        this.progress = 0;
        this.cooldown = 0;
        this.world.playSound(null, this.pos, SoundsTC.craftstart, SoundCategory.BLOCKS, 0.5f, 1.0f);
        Minecraft.getMinecraft().getSoundHandler().playSound(new EssentiaSoundLoop(SoundsTW.ESSENTIA_LOOP, this, 0.8f));
        this.syncTile(false);
        this.markDirty();
    }

    protected void resetCrafting() {
        this.recipeEssentiaCache = new AspectList();
        this.recipeEssentia = new AspectList();
        this.recipeOutput = ItemStack.EMPTY;
        this.isCrafting = false;
        this.progress = 0;
        this.cooldown = 0;
    }

    public boolean validateStructure() {
        this.setEnchanterActive(this.isStructureValid());
        return this.isActive;
    }

    public void deconstructStructure() {
        this.setEnchanterActive(false);
        this.resetCrafting();
        for(BlockPos offset : OFFSET_PILLARS.values()) {
            BlockArcanePillar.revertPillar(this.world, this.pos.add(offset));
        }
        if(!this.getItemToEnchant().isEmpty()) {
            ItemStack stack = this.getItemToEnchant().copy();
            this.stackHandler.setStackInSlot(0, ItemStack.EMPTY);
            EntityItem entityItem = new EntityItem(this.world, this.pos.getX() + 0.5, this.pos.getY() + 1.0, this.pos.getZ() + 0.5, stack);
            entityItem.motionX = 0;
            entityItem.motionY = 0.15;
            entityItem.motionZ = 0;
            entityItem.velocityChanged = true;
            this.world.spawnEntity(entityItem);
        }
    }

    @SuppressWarnings("ConstantConditions")
    protected boolean isStructureValid() {
        //Pillars
        for(Map.Entry<EnumDirection, BlockPos> entry : OFFSET_PILLARS.entrySet()) {
            IBlockState state = this.world.getBlockState(this.pos.add(entry.getValue()));
            if(state.getBlock() != BlocksTW.ARCANE_PILLAR || state.getValue(BlockArcanePillar.TOP) || state.getValue(BlockArcanePillar.DIRECTION) != entry.getKey()) {
                return false;
            }
        }
        //Obsidian
        for(BlockPos offset : OFFSET_OBSIDIAN) {
            if(this.world.getBlockState(this.pos.add(offset)).getBlock() != Blocks.OBSIDIAN) {
                return false;
            }
        }
        //Arcane Stone Bricks
        for(BlockPos offset : OFFSET_BRICKS) {
            if(this.world.getBlockState(this.pos.add(offset)).getBlock() != BlocksTC.stoneArcaneBrick) {
                return false;
            }
        }
        return true;
    }

    protected boolean drainEssentia() {
        if(this.count % 2 == 0 && !this.recipeEssentia.aspects.isEmpty()) {
            for (Aspect aspect : this.recipeEssentia.getAspects()) {
                if (EssentiaHandler.drainEssentia(this, aspect, null, 12, 1)) {
                    this.recipeEssentia.remove(aspect, 1);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    @Override
    public @Nullable <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.stackHandler);
        }
        return super.getCapability(capability, facing);
    }

    //##########################################################
    // IAspectContainer

    @Override
    public AspectList getAspects() {
        return this.recipeEssentia;
    }

    @Override
    public void setAspects(AspectList aspectList) {}

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        return true;
    }

    @Override
    public int addToContainer(Aspect aspect, int i) {
        return 0;
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int i) {
        return false;
    }

    @Override
    public boolean takeFromContainer(AspectList aspectList) {
        return false;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect aspect, int i) {
        return false;
    }

    @Override
    public boolean doesContainerContain(AspectList aspectList) {
        return false;
    }

    @Override
    public int containerContains(Aspect aspect) {
        return 0;
    }

    static {
        OFFSET_OBSIDIAN = new BlockPos[] {
                new BlockPos(2, -1, 0),
                new BlockPos(-2, -1, 0),
                new BlockPos(0, -1, 2),
                new BlockPos(0, -1, -2),

                new BlockPos(1, -1, 0),
                new BlockPos(-1, -1, 0),
                new BlockPos(0, -1, 1),
                new BlockPos(0, -1, -1),

                new BlockPos(1, -1, 1),
                new BlockPos(1, -1, -1),
                new BlockPos(-1, -1, 1),
                new BlockPos(-1, -1, -1),

                new BlockPos(0, -1, 0)
        };
        OFFSET_BRICKS = new BlockPos[] {
                new BlockPos(3, -1, 0),
                new BlockPos(-3, -1, 0),
                new BlockPos(0, -1, 3),
                new BlockPos(0, -1, -3),

                new BlockPos(3, -1, 1),
                new BlockPos(-3, -1, 1),
                new BlockPos(1, -1, 3),
                new BlockPos(1, -1, -3),

                new BlockPos(2, -1, 1),
                new BlockPos(-2, -1, 1),
                new BlockPos(1, -1, 2),
                new BlockPos(1, -1, -2),

                new BlockPos(2, -1, -1),
                new BlockPos(-2, -1, -1),
                new BlockPos(-1, -1, 2),
                new BlockPos(-1, -1, -2),

                new BlockPos(2, -1, -2),
                new BlockPos(-2, -1, -2),
                new BlockPos(-2, -1, 2),
                new BlockPos(-2, -1, -2)
        };
        OFFSET_PILLARS = new ImmutableMap.Builder<EnumDirection, BlockPos>()
                .put(EnumDirection.NORTH, new BlockPos(0, 0, 3))
                .put(EnumDirection.SOUTH, new BlockPos(0, 0, -3))
                .put(EnumDirection.WEST, new BlockPos(3, 0, 0))
                .put(EnumDirection.EAST, new BlockPos(-3, 0, 0))
                .put(EnumDirection.NORTH_WEST, new BlockPos(2, 0, 2))
                .put(EnumDirection.SOUTH_WEST, new BlockPos(2, 0, -2))
                .put(EnumDirection.NORTH_EAST, new BlockPos(-2, 0, 2))
                .put(EnumDirection.SOUTH_EAST, new BlockPos(-2, 0, -2))
                .build();
    }
}
