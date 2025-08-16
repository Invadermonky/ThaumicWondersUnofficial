package com.verdantartifice.thaumicwonders.common.golems.seals;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.utils.StringHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.ThaumcraftInvHelper.InvFilter;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.*;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.golems.client.gui.SealBaseContainer;
import thaumcraft.common.golems.client.gui.SealBaseGUI;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.crafting.TileResearchTable;

import java.util.*;

public class SealResearchAssistant implements ISeal, ISealConfigArea, ISealConfigToggles, ISealGui {
    public static final ResourceLocation ICON = new ResourceLocation(ThaumicWonders.MODID, "items/seal_research_assistant");
    final int PROP_PAPER = 0;
    final int PROP_INK = 1;
    final int PROP_MATERIALS = 2;
    private final Map<UUID, NonNullList<ItemStack>> researchRequests = new HashMap<>();
    private final ISealConfigToggles.SealToggle[] props = new ISealConfigToggles.SealToggle[]{
            new ISealConfigToggles.SealToggle(true, "ppaper", StringHelper.getTranslationKey("golem_prop", "gui", "restock_paper")),
            new ISealConfigToggles.SealToggle(true, "pink", StringHelper.getTranslationKey("golem_prop", "gui", "refill_ink")),
            new ISealConfigToggles.SealToggle(true, "pmats", StringHelper.getTranslationKey("golem_prop", "gui", "provide_materials"))
    };
    protected ItemStackHandler stackHandler = new ItemStackHandler(12);
    protected Map<Integer, ItemStack> provideTaskCache = new HashMap<>();
    protected Map<Integer, Integer> emptyTasksCache = new HashMap<>();
    protected int emptyDelay = 0;
    private int delay = new Random(System.nanoTime()).nextInt(50);

    @Override
    public String getKey() {
        return "thaumicwonders:research_assistant";
    }

    @Override
    public boolean canPlaceAt(World world, BlockPos pos, EnumFacing facing) {
        IItemHandler inv = ThaumcraftInvHelper.getItemHandlerAt(world, pos, facing);
        return inv != null;
    }

    @Override
    public void tickSeal(World world, ISealEntity seal) {
        this.delay++;
        this.handleSealInventory(world, seal);
        this.cleanProvisionRequests(world);

        if (this.delay % 60 == 0) {
            AxisAlignedBB area = GolemHelper.getBoundsForArea(seal);
            List<TileResearchTable> tables = this.getNearbyResearchTables(world, seal, area);

            for (EntityPlayer player : world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                this.provideResearchMaterials(world, seal, player);
            }

            for (TileResearchTable table : tables) {
                if (this.shouldRefillInk(table)) {
                    this.refillInk(seal, table);
                }
            }

            if (this.delay % 300 == 0) {
                for (TileResearchTable table : tables) {
                    if (this.shouldRefillInk(table)) {
                        this.requestInk(seal, table);
                    }
                    if (this.shouldRestockPaper(table)) {
                        this.restockPaper(seal, table);
                    }
                }
                this.delay = 0;
            }
        }
    }

    @Override
    public void onTaskStarted(World world, IGolemAPI iGolemAPI, Task task) {}

    @Override
    public boolean onTaskCompletion(World world, IGolemAPI golem, Task task) {
        int slot = this.emptyTasksCache.getOrDefault(task.getId(), -1);
        if (slot != -1) {
            ItemStack extracted = this.stackHandler.extractItem(slot, this.stackHandler.getSlotLimit(slot), true);
            int limit = golem.canCarryAmount(extracted);
            if (limit > 0) {
                extracted = golem.holdItem(this.stackHandler.extractItem(slot, limit, false));
                if (!extracted.isEmpty()) {
                    InventoryUtils.ejectStackAt(world, task.getSealPos().pos.offset(task.getSealPos().face), task.getSealPos().face.getOpposite(), extracted);
                }
                golem.getGolemEntity().playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.125f, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                golem.swingArm();
            }
        }
        this.emptyTasksCache.remove(task.getId());
        task.setSuspended(true);
        return true;
    }

    @Override
    public void onTaskSuspension(World world, Task task) {}

    @Override
    public boolean canGolemPerformTask(IGolemAPI iGolemAPI, Task task) {
        return false;
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbtTagCompound) {
        this.stackHandler.deserializeNBT(nbtTagCompound.getCompoundTag("inventory"));
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbtTagCompound) {
        nbtTagCompound.setTag("inventory", this.stackHandler.serializeNBT());
    }

    @Override
    public ResourceLocation getSealIcon() {
        return ICON;
    }

    @Override
    public void onRemoval(World world, BlockPos pos, EnumFacing enumFacing) {
        for (int i = 0; i < this.stackHandler.getSlots(); i++) {
            ItemStack stack = this.stackHandler.extractItem(i, this.stackHandler.getSlotLimit(i), false);
            if (!stack.isEmpty()) {
                Block.spawnAsEntity(world, pos, stack);
            }
        }
    }

    @Override
    public Object returnContainer(World world, EntityPlayer player, BlockPos pos, EnumFacing facing, ISealEntity seal) {
        return new SealBaseContainer(player.inventory, world, seal);
    }

    @Override
    public Object returnGui(World world, EntityPlayer player, BlockPos pos, EnumFacing facing, ISealEntity seal) {
        return new SealBaseGUI(player.inventory, world, seal);
    }

    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return new EnumGolemTrait[]{EnumGolemTrait.SMART, EnumGolemTrait.DEFT};
    }

    @Override
    public EnumGolemTrait[] getForbiddenTags() {
        return new EnumGolemTrait[]{EnumGolemTrait.CLUMSY};
    }

    private List<TileResearchTable> getNearbyResearchTables(World world, ISealEntity seal, AxisAlignedBB area) {
        List<TileResearchTable> tables = new ArrayList<>();
        BlockPos checkPos;
        TileEntity checkTile;

        //Accounting for seal placed directly on a Research Tables
        checkTile = world.getTileEntity(seal.getSealPos().pos);
        if (checkTile instanceof TileResearchTable) {
            tables.add((TileResearchTable) checkTile);
        }

        for (int x = (int) area.minX; x < (int) area.maxX; x++) {
            for (int y = (int) area.minY; y < (int) area.maxY; y++) {
                for (int z = (int) area.minZ; z < (int) area.maxZ; z++) {
                    checkPos = new BlockPos(x, y, z);
                    checkTile = world.getTileEntity(checkPos);
                    if (checkTile instanceof TileResearchTable) {
                        tables.add((TileResearchTable) checkTile);
                    }
                }
            }
        }
        return tables;
    }

    private void cleanProvisionRequests(World world) {
        if (this.delay % 60 == 0 && GolemHelper.provisionRequests.containsKey(world.provider.getDimension())) {
            GolemHelper.provisionRequests.get(world.provider.getDimension()).removeIf(provisionRequest -> provisionRequest.isInvalid()
                    || provisionRequest.getLinkedTask() == null
                    || provisionRequest.getLinkedTask().isSuspended()
                    || provisionRequest.getLinkedTask().isCompleted()
                    || provisionRequest.getTimeout() < System.currentTimeMillis());
        }
    }

    private void handleSealInventory(World world, ISealEntity seal) {
        if (this.emptyDelay > 0) {
            this.emptyDelay--;
        } else {
            this.emptySeal(world, seal);
        }
    }

    public void requestResearchMaterials(ISealEntity seal, EntityPlayer player, NonNullList<ItemStack> researchStacks) {
        if (!this.props[PROP_MATERIALS].getValue())
            return;

        NonNullList<ItemStack> requestStacks = NonNullList.create();
        requestStacks.addAll(researchStacks);
        requestStacks.removeIf(stack -> stack == null || stack.isEmpty() || InventoryUtils.isPlayerCarryingAmount(player, stack, true));
        if (!researchStacks.isEmpty()) {
            if (!this.researchRequests.containsKey(player.getUniqueID())) {
                this.researchRequests.put(player.getUniqueID(), NonNullList.create());
            }
            for (ItemStack stack : requestStacks) {
                this.researchRequests.get(player.getUniqueID()).add(stack);
                this.requestProvisions(player.world, seal, stack);
            }
            this.emptyDelay = 1200;
        }
    }

    private void provideResearchMaterials(World world, ISealEntity seal, EntityPlayer player) {
        if (!this.researchRequests.containsKey(player.getUniqueID()) || this.researchRequests.get(player.getUniqueID()).isEmpty())
            return;

        NonNullList<ItemStack> researchMaterials = this.researchRequests.get(player.getUniqueID());
        IItemHandler handler = ThaumcraftInvHelper.getItemHandlerAt(world, seal.getSealPos().pos, seal.getSealPos().face);
        if (handler != null) {
            for (int chestSlot = 0; chestSlot < handler.getSlots(); chestSlot++) {
                ItemStack stack = handler.getStackInSlot(chestSlot);

                for (int researchIndex = 0; researchIndex < researchMaterials.size(); researchIndex++) {
                    ItemStack researchStack = researchMaterials.get(researchIndex);
                    if (InventoryUtils.areItemStacksEqual(stack, researchStack, new InvFilter(false, !researchStack.hasTagCompound(), true, false).setRelaxedNBT())) {
                        ItemStack extract = handler.extractItem(chestSlot, researchStack.getCount(), true);
                        if (!extract.isEmpty()) {
                            if (player.addItemStackToInventory(extract)) {
                                handler.extractItem(chestSlot, researchStack.getCount(), false);
                                researchMaterials.remove(researchStack);
                                break;
                            } else {
                                //If player inventory is full, no additional items can be inserted
                                return;
                            }
                        }
                    }
                }
                if (researchMaterials.isEmpty()) {
                    break;
                }
            }
        }
    }

    private void emptySeal(World world, ISealEntity seal) {
        if (this.delay % 100 == 0) {
            this.emptyTasksCache.keySet().removeIf(taskNum -> TaskHandler.getTask(world.provider.getDimension(), taskNum) == null);
        }

        if (this.delay % 20 == 0) {
            boolean isEmpty = true;
            for (int slot = 0; slot < this.stackHandler.getSlots(); slot++) {
                ItemStack stack = this.stackHandler.getStackInSlot(slot);
                if (!stack.isEmpty()) {
                    isEmpty = false;
                    Task task = new Task(seal.getSealPos(), seal.getSealPos().pos);
                    task.setPriority(seal.getPriority());
                    task.setLifespan((short) 5);
                    TaskHandler.addTask(world.provider.getDimension(), task);
                    this.emptyTasksCache.put(task.getId(), slot);
                }
            }
            if (isEmpty) {
                this.researchRequests.clear();
            }
        }
    }

    private boolean shouldRefillInk(TileResearchTable tileTable) {
        if (!this.props[PROP_INK].getValue())
            return false;

        //Scribing tools are stored in slot 0 in the research table.
        int scribingSlot = 0;
        IItemHandler handler = tileTable.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
        if (handler != null && handler.getSlots() > scribingSlot) {
            ItemStack scribingTools = handler.getStackInSlot(0);
            if (!scribingTools.isEmpty() && scribingTools.getItem() == ItemsTC.scribingTools) {
                return scribingTools.getItemDamage() >= scribingTools.getMaxDamage() - 10;
            }
        }
        return false;
    }

    private void requestInk(ISealEntity seal, TileResearchTable tileTable) {
        ItemStack inkStack = new ItemStack(Items.DYE, 1, 0);
        //Don't request an ink sac if there already is one in the seal
        for (int i = 0; i < this.stackHandler.getSlots(); i++) {
            if (ItemStack.areItemsEqual(inkStack, this.stackHandler.getStackInSlot(i))) {
                return;
            }
        }
        //Requesting an ink sac
        this.requestProvisions(tileTable.getWorld(), seal, inkStack);
    }

    private void refillInk(ISealEntity seal, TileResearchTable tileTable) {
        //Scribing tools are stored in slot 0 in the research table.
        int scribingSlot = 0;
        World world = tileTable.getWorld();
        IItemHandler handler = ThaumcraftInvHelper.getItemHandlerAt(world, seal.getSealPos().pos, seal.getSealPos().face);
        IItemHandler tableHandler = tileTable.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
        if (handler != null && tableHandler != null) {
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack inkStack = handler.getStackInSlot(i);
                //Using the ink sac to repair scribing tools
                if (!inkStack.isEmpty() && inkStack.getItem() == Items.DYE && inkStack.getItemDamage() == 0) {
                    //Extracting Ink Sac from seal inventory
                    handler.extractItem(i, 1, false);
                    //Extracting Scribing Tools from research table
                    tableHandler.extractItem(scribingSlot, 1, false);
                    //Inserting new Scribing Tools into research table.
                    tableHandler.insertItem(scribingSlot, new ItemStack(ItemsTC.scribingTools), false);
                    return;
                }
            }
        }
    }

    private boolean shouldRestockPaper(TileResearchTable tileTable) {
        return this.props[PROP_PAPER].getValue() && this.getRestockPaperAmount(tileTable) >= 16;
    }

    private int getRestockPaperAmount(TileResearchTable tileTable) {
        //Paper is stored in slot 1 in the research table.
        int paperSlot = 1;
        int toStock = 0;
        //Paper will always attempt to be inserted into the top of the machine.
        IItemHandler handler = tileTable.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
        if (handler != null && handler.getSlots() > paperSlot) {
            ItemStack paperStack = handler.getStackInSlot(paperSlot);
            if (paperStack.isEmpty()) {
                toStock = 64;
            } else {
                toStock = paperStack.getItem() == Items.PAPER ? 64 - paperStack.getCount() : 0;
            }
        }
        return toStock;
    }

    private void restockPaper(ISealEntity seal, TileResearchTable tileTable) {
        //Paper is stored in slot 1 in the research table.
        int paperSlot = 1;
        ItemStack restockStack = new ItemStack(Items.PAPER, this.getRestockPaperAmount(tileTable));

        //TODO: The item handler stuff is not needed once the items are stored in the seal itself.
        IItemHandler handler = ThaumcraftInvHelper.getItemHandlerAt(tileTable.getWorld(), seal.getSealPos().pos, seal.getSealPos().face);
        IItemHandler tableHandler = tileTable.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
        if (handler != null && tableHandler != null) {
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack checkStack = handler.getStackInSlot(i);
                if (!checkStack.isEmpty() && checkStack.getItem() == Items.PAPER) {
                    ItemStack extracted = handler.extractItem(i, restockStack.getCount(), true);
                    if (!extracted.isEmpty()) {
                        extracted = handler.extractItem(i, extracted.getCount(), false);
                        tableHandler.insertItem(paperSlot, extracted, false);
                        return;
                    }
                }
            }
        }

        GolemHelper.requestProvisioning(tileTable.getWorld(), tileTable.getPos(), EnumFacing.UP, restockStack);
    }

    private void requestProvisions(World world, ISealEntity seal, ItemStack stack) {
        //TODO: Change this to a request item task.
        GolemHelper.requestProvisioning(world, seal.getSealPos().pos, seal.getSealPos().face, stack);
    }

    @Override
    public SealToggle[] getToggles() {
        return this.props;
    }

    @Override
    public void setToggle(int index, boolean value) {
        this.props[index].setValue(value);
    }

    @Override
    public int[] getGuiCategories() {
        return new int[]{CAT_AREA, CAT_TOGGLES, CAT_PRIORITY, CAT_TAGS};
    }
}
