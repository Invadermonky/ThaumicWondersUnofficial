package com.verdantartifice.thaumicwonders.common.golems.seals;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.golems.seals.utils.ResearchRequest;
import com.verdantartifice.thaumicwonders.common.utils.GolemHelperTW;
import com.verdantartifice.thaumicwonders.common.utils.StringHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
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
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.ISealGui;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.golems.client.gui.SealBaseContainer;
import thaumcraft.common.golems.client.gui.SealBaseGUI;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.crafting.TileResearchTable;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class SealResearchAssistant implements ISeal, ISealConfigToggles, ISealGui {
    public static final ResourceLocation ICON = new ResourceLocation(ThaumicWonders.MODID, "items/seal_research_assistant");
    private static final ItemStack INK_STACK = new ItemStack(Items.DYE, 1, EnumDyeColor.BLACK.getDyeDamage());
    protected final int PROP_PAPER = 0;
    protected final int PROP_INK = 1;
    protected final int PROP_MATERIALS = 2;
    protected final int PLAYER_SEARCH_AREA = 12;
    private final ISealConfigToggles.SealToggle[] props = new ISealConfigToggles.SealToggle[]{
            new ISealConfigToggles.SealToggle(true, "ppaper", StringHelper.getTranslationKey("golem_prop", "gui", "restock_paper")),
            new ISealConfigToggles.SealToggle(true, "pink", StringHelper.getTranslationKey("golem_prop", "gui", "refill_ink")),
            new ISealConfigToggles.SealToggle(true, "pmats", StringHelper.getTranslationKey("golem_prop", "gui", "provide_materials"))
    };
    private int restockDelay = new Random(System.nanoTime()).nextInt(50);
    private int requestDelay = new Random(System.nanoTime()).nextInt(50);
    private int refillInkTaskId = Integer.MIN_VALUE;
    private final Set<ResearchRequest> researchRequests = new HashSet<>();


    @Override
    public String getKey() {
        return "thaumicwonders:research_assistant";
    }

    @Override
    public ResourceLocation getSealIcon() {
        return ICON;
    }

    @Override
    public boolean canPlaceAt(World world, BlockPos pos, EnumFacing facing) {
        return this.getResearchTable(world, pos) != null;
    }

    @Override
    public void tickSeal(World world, ISealEntity seal) {
        this.restockDelay++;
        this.requestDelay++;
        this.cleanProvisionRequests(world);

        if(this.requestDelay % 300 == 0) {
            this.cleanResearchRequests(world, seal);
            this.updateResearchRequests(world, seal);
        }
        if (this.restockDelay % 300 == 0) {
            TileResearchTable table = this.getResearchTable(world, seal.getSealPos().pos);
            if(table != null) {
                if(this.shouldRefillInk(table)) {
                    GolemHelper.requestProvisioning(world, seal, INK_STACK);
                    if(!GolemHelperTW.isTaskActive(world, this.refillInkTaskId)) {
                        this.refillInkTaskId = GolemHelperTW.createMoveToSealTask(world, seal);
                    }
                }
                if(this.shouldRestockPaper(table)) {
                    this.restockPaper(seal, table);
                }
                this.restockDelay = 0;
            }
        }
    }

    @Override
    public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
        if(GolemHelperTW.isGolemValidForTask(golem, task, this.refillInkTaskId, INK_STACK)) {
            return true;
        }
        for (ResearchRequest request : this.researchRequests) {
            if (request.isGolemValidForTask(golem, task)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onTaskStarted(World world, IGolemAPI golem, Task task) {}

    @Override
    public boolean onTaskCompletion(World world, IGolemAPI golem, Task task) {
        ISealEntity seal = GolemHelper.getSealEntity(world.provider.getDimension(), task.getSealPos());
        //Handle refill ink task
        if(GolemHelperTW.isGolemValidForTask(golem, task, this.refillInkTaskId, INK_STACK)) {
            ItemStack dyeStack = GolemHelperTW.getCarriedMatchingStack(golem, INK_STACK);
            if(!dyeStack.isEmpty()) {
                dyeStack.shrink(1);
                golem.swingArm();
                golem.addRankXp(1);
                this.refillInk(world, seal);
                this.refillInkTaskId = Integer.MIN_VALUE;
                return true;
            }
        }
        //Handle research material tasks
        for(EntityPlayer player : world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(seal.getSealPos().pos).grow(PLAYER_SEARCH_AREA))) {
            for(ResearchRequest request : this.researchRequests) {
                if(request.tryCompleteTask(player, seal, golem, task)) {
                    this.cleanResearchRequests(world, seal);
                    this.updateResearchRequests(world, seal);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onTaskSuspension(World world, Task task) {}

    public void requestResearchMaterials(ISealEntity seal, EntityPlayer player, NonNullList<ItemStack> researchStacks) {
        if (!this.props[PROP_MATERIALS].getValue())
            return;

        NonNullList<ItemStack> missingStacks = NonNullList.create();
        missingStacks.addAll(researchStacks);
        missingStacks.removeIf(stack -> stack == null || stack.isEmpty() || InventoryUtils.isPlayerCarryingAmount(player, stack, true));
        if (!missingStacks.isEmpty()) {
            for(ItemStack stack : missingStacks) {
                ResearchRequest request = new ResearchRequest(player, stack);
                this.researchRequests.add(request);
            }
            this.updateResearchRequests(player.world, seal);
        }
    }

    private void cleanProvisionRequests(World world) {
        if (this.restockDelay % 60 == 0 && GolemHelper.provisionRequests.containsKey(world.provider.getDimension())) {
            GolemHelper.provisionRequests.get(world.provider.getDimension()).removeIf(provisionRequest -> provisionRequest.isInvalid()
                    || provisionRequest.getLinkedTask() == null
                    || provisionRequest.getLinkedTask().isSuspended()
                    || provisionRequest.getLinkedTask().isCompleted()
                    || provisionRequest.getTimeout() < System.currentTimeMillis());
        }
    }

    private void cleanResearchRequests(World world, ISealEntity seal) {
        AxisAlignedBB area = new AxisAlignedBB(seal.getSealPos().pos).grow(PLAYER_SEARCH_AREA);
        List<EntityPlayer> nearbyPlayers = world.getEntitiesWithinAABB(EntityPlayer.class, area);
        this.researchRequests.removeIf(request -> request.shouldRemoveTask(world) || nearbyPlayers.stream().noneMatch(request::matches));
    }

    private void updateResearchRequests(World world, ISealEntity seal) {
        for(EntityPlayer player : world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(seal.getSealPos().pos).grow(PLAYER_SEARCH_AREA))) {
            for(ResearchRequest request : this.researchRequests) {
                if(request.matches(player)) {
                    request.updateTask(seal);
                    this.requestDelay = 0;
                    return;
                }
            }
        }
    }

    private @Nullable TileResearchTable getResearchTable(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileResearchTable ? (TileResearchTable) tile : null;
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

    private boolean shouldRestockPaper(TileResearchTable tileTable) {
        return this.props[PROP_PAPER].getValue() && this.getRestockPaperAmount(tileTable) >= 16;
    }

    private void restockPaper(ISealEntity seal, TileResearchTable tileTable) {
        //Paper is stored in slot 1 in the research table.
        int paperSlot = 1;
        ItemStack restockStack = new ItemStack(Items.PAPER, this.getRestockPaperAmount(tileTable));
        GolemHelper.requestProvisioning(tileTable.getWorld(), tileTable.getPos(), EnumFacing.UP, restockStack);
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

    private void refillInk(World world, ISealEntity seal) {
        //Scribing tools are stored in slot 0 in the research table.
        int scribingSlot = 0;
        TileResearchTable table = this.getResearchTable(world, seal.getSealPos().pos);
        if(table != null) {
            IItemHandler tableHandler = table.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
            if (tableHandler != null) {
                //Extracting Scribing Tools from research table
                tableHandler.extractItem(scribingSlot, 1, false);
                //Inserting new Scribing Tools into research table.
                tableHandler.insertItem(scribingSlot, new ItemStack(ItemsTC.scribingTools), false);
            }
        }
    }

    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return new EnumGolemTrait[]{EnumGolemTrait.SMART, EnumGolemTrait.DEFT};
    }

    @Override
    public EnumGolemTrait[] getForbiddenTags() {
        return new EnumGolemTrait[]{EnumGolemTrait.CLUMSY};
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbtTagCompound) {}

    @Override
    public void writeCustomNBT(NBTTagCompound nbtTagCompound) {}

    @Override
    public void onRemoval(World world, BlockPos pos, EnumFacing enumFacing) {}

    @Override
    public Object returnContainer(World world, EntityPlayer player, BlockPos pos, EnumFacing facing, ISealEntity seal) {
        return new SealBaseContainer(player.inventory, world, seal);
    }

    @Override
    public Object returnGui(World world, EntityPlayer player, BlockPos pos, EnumFacing facing, ISealEntity seal) {
        return new SealBaseGUI(player.inventory, world, seal);
    }

    @Override
    public int[] getGuiCategories() {
        return new int[]{CAT_TOGGLES, CAT_PRIORITY, CAT_TAGS};
    }

    @Override
    public SealToggle[] getToggles() {
        return this.props;
    }

    @Override
    public void setToggle(int index, boolean value) {
        this.props[index].setValue(value);
    }
}
