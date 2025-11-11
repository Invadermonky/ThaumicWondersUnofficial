package com.verdantartifice.thaumicwonders.common.tiles.devices;

import com.verdantartifice.thaumicwonders.common.blocks.BlocksTW;
import com.verdantartifice.thaumicwonders.common.crafting.catalyzationchamber.CatalyzationChamberRecipe;
import com.verdantartifice.thaumicwonders.common.crafting.catalyzationchamber.CatalyzationChamberRecipeRegistry;
import com.verdantartifice.thaumicwonders.common.tiles.base.TileTW;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.devices.TileBellows;

import java.util.Arrays;

public class TileCatalyzationChamber extends TileTW implements ITickable {
    private static final int PLAY_EFFECTS = 4;

    protected ItemStackHandler stackHandler = new ItemStackHandler(33) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return slot == 0 && CatalyzationChamberRecipeRegistry.isValidCatalyst(stack);
        }
    };
    protected RangedWrapper catalystWrapper;
    protected RangedWrapper inputWrapper;
    protected int refineTime = 0;
    protected int maxRefineTime = 0;
    protected int speedyTime = 0;

    protected int facingX = -5;
    protected int facingZ = -5;

    protected CatalyzationChamberRecipe recipe = null;

    public TileCatalyzationChamber() {
        this.catalystWrapper = new RangedWrapper(this.stackHandler, 0, 1);
        this.inputWrapper = new RangedWrapper(this.stackHandler, 1, 32);
    }

    public ItemStack getEquippedStone(boolean extract) {
        if(extract) {
            return this.catalystWrapper.extractItem(0, this.catalystWrapper.getSlotLimit(0), false);
        }
        return this.catalystWrapper.getStackInSlot(0);
    }

    public void setEquippedStone(ItemStack stack) {
        this.catalystWrapper.setStackInSlot(0, stack);
    }

    public IItemHandler getCatalystHandler() {
        return this.catalystWrapper;
    }

    public IItemHandler getInputHandler() {
        return this.inputWrapper;
    }

    @Override
    public void update() {
        if (this.facingX == -5) {
            this.setFacing();
        }
        if (!this.world.isRemote) {
            this.maxRefineTime = this.calcRefineTime();
            if (this.refineTime > this.maxRefineTime || this.refineTime <= 0) {
                this.refineTime = this.maxRefineTime;
            }
            if (this.refineTime > 0) {
                this.refineTime--;
            }

            IItemHandler inputHandler = this.getInputHandler();
            for (int slot = 0; slot < inputHandler.getSlots(); slot++) {
                ItemStack input = inputHandler.getStackInSlot(slot);
                if (!input.isEmpty()) {
                    //Compares Input and Catalyst to current recipe and updates if recipe does not match
                    ItemStack catalyst = this.getEquippedStone(false);
                    if(this.recipe == null || !this.recipe.matches(input, catalyst)) {
                        this.recipe = CatalyzationChamberRecipeRegistry.getRecipe(input, catalyst);
                        this.refineTime = this.maxRefineTime;
                    }

                    if(recipe == null || this.recipe.getOutput().isEmpty()) {
                        //Ejecting Input if not a valid recipe item
                        this.ejectItem(inputHandler.extractItem(slot, input.getCount(), false));
                    } else if (this.refineTime <= 0) {
                        //Only output if refining is complete and recipe is valid

                        //Ejecting Output
                        ItemStack output = this.recipe.getOutput();
                        this.ejectItem(output);
                        this.world.addBlockEvent(this.getPos(), BlocksTW.CATALYZATION_CHAMBER, PLAY_EFFECTS, 0);
                        if (this.recipe.getFluxChance() > 0 && this.world.rand.nextInt(this.recipe.getFluxChance()) == 0) {
                            AuraHelper.polluteAura(this.world, this.getPos().offset(this.getFacing().getOpposite()), 1.0f, true);
                        }

                        //Consuming Catalyst
                        catalyst = this.getEquippedStone(true);
                        if (catalyst.getItem().isDamageable()) {
                            //Damage and set catalyst
                            if (catalyst.attemptDamageItem(1, this.world.rand, null)) {
                                catalyst.shrink(1);
                            }
                            this.setEquippedStone(catalyst);
                        } else if (catalyst.getItem().hasContainerItem(catalyst)) {
                            //Consume contained item and eject container
                            ItemStack container = catalyst.getItem().getContainerItem(catalyst);
                            catalyst.shrink(1);
                            if(catalyst.isEmpty() && CatalyzationChamberRecipeRegistry.isValidCatalyst(container)) {
                                this.setEquippedStone(container);
                            } else {
                                this.setEquippedStone(catalyst);
                                this.ejectItem(container);
                            }
                        } else {
                            catalyst.shrink(1);
                            this.setEquippedStone(catalyst);
                        }

                        //Consuming input item
                        int count = Arrays.stream(this.recipe.getInput().getMatchingStacks())
                                .filter(ingredient -> !ingredient.isEmpty()).findFirst()
                                .map(ItemStack::getCount).orElse(1);
                        if (input.getItem().hasContainerItem(input)) {
                            ItemStack container = input.getItem().getContainerItem(input).copy();
                            container.setCount(count);
                            this.ejectItem(container);
                        }
                        inputHandler.extractItem(slot, count, false);

                        if (this.speedyTime <= 0) {
                            this.speedyTime = (int) AuraHelper.drainVis(this.getWorld(), this.getPos(), 20.0F, false);
                        } else {
                            this.speedyTime--;
                        }
                        break;
                    }
                }
            }
            this.syncTile(false);
        }
    }

    @Override
    protected void readFromTileNBT(NBTTagCompound compound) {
        this.refineTime = compound.getShort("RefineTime");
        this.speedyTime = compound.getShort("SpeedyTime");
        this.stackHandler.deserializeNBT(compound.getCompoundTag("Inventory"));
    }

    @Override
    protected NBTTagCompound writeToTileNBT(NBTTagCompound compound) {
        compound.setShort("RefineTime", (short) this.refineTime);
        compound.setShort("SpeedyTime", (short) this.speedyTime);
        compound.setTag("Inventory", this.stackHandler.serializeNBT());
        return compound;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    @Override
    public @Nullable <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.inputWrapper);
        }
        return super.getCapability(capability, facing);
    }

    public void dropInventoryContents() {
        if (!this.world.isRemote) {
            for(int i = 0; i < this.stackHandler.getSlots(); i++) {
                ItemStack slotStack = this.stackHandler.getStackInSlot(i);
                if(!slotStack.isEmpty()) {
                    Block.spawnAsEntity(this.world, this.getPos(), slotStack.copy());
                }
            }
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
    public void syncTile(boolean rerender) {
        super.syncTile(rerender);
        this.syncSlots(null);
    }

    @Override
    public void messageFromServer(NBTTagCompound nbt) {
        this.stackHandler.deserializeNBT(nbt.getCompoundTag("Inventory"));
    }

    @Override
    public void messageFromClient(NBTTagCompound nbt, EntityPlayerMP player) {
        if(nbt.hasKey("requestSync")) {
            nbt.removeTag("requestSync");
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

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(
                this.getPos().getX() - 1.3D, this.getPos().getY() - 1.3D, this.getPos().getZ() - 1.3D,
                this.getPos().getX() + 2.3D, this.getPos().getY() + 2.3D, this.getPos().getZ() + 1.3D
        );
    }
}
