package com.verdantartifice.thaumicwonders.common.tiles.devices;

import com.verdantartifice.thaumicwonders.common.config.ConfigHandlerTW;
import com.verdantartifice.thaumicwonders.common.tiles.base.TileTW;
import com.verdantartifice.thaumicwonders.common.utils.NBTHelper;
import com.verdantartifice.thaumicwonders.common.utils.OreHelper;
import com.verdantartifice.thaumicwonders.common.utils.PlayerHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.client.fx.FXDispatcher;

import java.awt.*;
import java.util.*;
import java.util.List;

public class TileOreDiviner extends TileTW implements ITickable {
    public static final int SCAN_RANGE = 20;

    protected static final Map<String, Color> ORE_COLORS = new HashMap<>();
    protected static final Color DEFAULT_ORE_COLOR = new Color(0xC0C0C0);
    protected EntityPlayer player;
    protected String[] searchStrings = new String[0];
    protected int searchTime = 0;
    protected List<BlockPos> targets = new ArrayList<>();

    /**
     * Begins a new search initialized by a player holding an item.
     *
     * @param player The player that started the search, used to send messages diviner messages
     * @param stack  The stack held by the player, used to specify search parameters
     */
    public void onInteract(EntityPlayer player, ItemStack stack) {
        this.player = player;
        this.searchStrings = this.getSearchString(stack);
        if (searchStrings.length != 0) {
            this.beginNewSearchTimer();
        } else {
            this.sendMessageToPlayer(new TextComponentTranslation("event.ore_diviner.invalid_item"));
        }
        this.markDirty();
        this.syncTile(false);
    }

    /**
     * This function consumes vis from the aura and starts a new search timer. It will always consume vis and reset the
     * timer regardless of remaining time. Use only when a new search is initialized.
     */
    public void beginNewSearchTimer() {
        //This function does not handle the time increments, it only starts a new timer.
        this.findNewTargets();
        if (this.targets.isEmpty()) {
            this.sendMessageToPlayer(new TextComponentTranslation("event.ore_diviner.not_found"));
        } else if (this.consumeVis()) {
            this.searchTime = ConfigHandlerTW.ore_diviner.searchDuration;
        } else {
            this.sendMessageToPlayer(new TextComponentTranslation("event.ore_diviner.insufficient_vis"));
        }
    }

    public void stopSearching() {
        this.player = null;
        this.searchTime = 0;
        this.searchStrings = new String[0];
        this.targets.clear();
        this.markDirty();
        this.syncTile(false);
    }

    public void findNewTargets() {
        this.targets.clear();
        outer:
        for (int dist = 1; dist <= ConfigHandlerTW.ore_diviner.searchRange; dist++) {
            Set<BlockPos> searchShell = this.generateShell(dist);
            for (BlockPos pos : searchShell) {
                if (this.isBlockValid(pos)) {
                    this.targets.add(pos);
                    if (this.targets.size() >= ConfigHandlerTW.ore_diviner.pingedOres) {
                        break outer;
                    }
                }
            }
        }
        if (!this.targets.isEmpty()) {
            BlockPos delta = this.targets.get(0).subtract(this.pos);
            EnumFacing facing = EnumFacing.getFacingFromVector(delta.getX(), delta.getY(), delta.getZ());
            this.sendMessageToPlayer(new TextComponentTranslation("event.ore_diviner.found." + facing.getName()));
        }
    }

    protected boolean consumeVis() {
        int drain = ConfigHandlerTW.ore_diviner.visDrain;
        if (drain <= 0) {
            return true;
        } else if (AuraHelper.drainVis(this.world, this.pos, drain, true) > 0) {
            AuraHelper.drainVis(this.world, this.pos, drain, false);
            return true;
        }
        return false;
    }

    protected Set<BlockPos> generateShell(int radius) {
        Set<BlockPos> posSet = new HashSet<>();
        this.generateXPlanes(this.pos, radius, posSet);
        this.generateYPlanes(this.pos, radius, posSet);
        this.generateZPlanes(this.pos, radius, posSet);
        return posSet;
    }

    protected void generateXPlanes(BlockPos origin, int radius, Set<BlockPos> posSet) {
        for (int yy = -radius; yy <= radius; yy++) {
            for (int zz = -radius; zz <= radius; zz++) {
                posSet.add(origin.add(radius, yy, zz));
                posSet.add(origin.add(-radius, yy, zz));
            }
        }
    }

    protected void generateYPlanes(BlockPos origin, int radius, Set<BlockPos> posSet) {
        for (int xx = -radius; xx <= radius; xx++) {
            for (int zz = -radius; zz <= radius; zz++) {
                posSet.add(origin.add(xx, radius, zz));
                posSet.add(origin.add(xx, -radius, zz));
            }
        }
    }

    protected void generateZPlanes(BlockPos origin, int radius, Set<BlockPos> posSet) {
        for (int xx = -radius; xx <= radius; xx++) {
            for (int yy = -radius; yy <= radius; yy++) {
                posSet.add(origin.add(xx, yy, radius));
                posSet.add(origin.add(xx, yy, -radius));
            }
        }
    }

    public boolean isBlockValid(BlockPos checkPos) {
        if (this.searchStrings != null && this.searchStrings.length > 0) {
            ItemStack stack = getStackAtPos(checkPos);
            if (!stack.isEmpty()) {
                for (int i : OreDictionary.getOreIDs(stack)) {
                    String oreDict = OreDictionary.getOreName(i);
                    for (String searchString : this.searchStrings) {
                        if (oreDict.matches(searchString)) {
                            return true;
                        }
                    }
                }
                return false;
            }
        }
        return false;
    }

    public ItemStack getStackAtPos(BlockPos pos) {
        ItemStack stack = ItemStack.EMPTY;
        IBlockState state = this.world.getBlockState(pos);
        if (state.getBlock() != Blocks.AIR && state.getBlock() != Blocks.BEDROCK) {
            Item item = Item.getItemFromBlock(state.getBlock());
            stack = new ItemStack(item, 1, item.getHasSubtypes() ? state.getBlock().getMetaFromState(state) : 0);
        }
        return stack;
    }

    protected String[] getSearchString(ItemStack stack) {
        List<String> searchStrings = new ArrayList<>();
        if (stack.isEmpty()) {
            for(String types : ConfigHandlerTW.ore_diviner.oreTypes) {
                searchStrings.add("^" + types + "[\\w]+$");
            }
        } else {
            int[] oreIds = OreDictionary.getOreIDs(stack);
            for (int oreId : oreIds) {
                String oreName = OreDictionary.getOreName(oreId);
                if (oreName.startsWith("ore")) {
                    for(String type : ConfigHandlerTW.ore_diviner.oreTypes) {
                        searchStrings.add(oreName.startsWith(type) ? oreName : oreName.replaceFirst("^ore", type));
                    }
                } else {
                    for (String oreAssociation : ConfigHandlerTW.ore_diviner.oreAssociations) {
                        if (oreName.startsWith(oreAssociation)) {
                            for(String type : ConfigHandlerTW.ore_diviner.oreTypes) {
                                searchStrings.add(oreName.replaceFirst(oreAssociation, type));
                            }
                        }
                    }
                }
            }
        }
        for(String oreType : ConfigHandlerTW.ore_diviner.oreTypes) {
            for(int i = 0; i < searchStrings.size(); i++) {

            }
        }
        return searchStrings.toArray(new String[0]);
    }

    protected void sendMessageToPlayer(ITextComponent message) {
        if (this.player != null) {
            this.player.sendStatusMessage(new TextComponentString(TextFormatting.DARK_PURPLE + message.getFormattedText()), true);
        }
    }

    @Override
    protected void readFromTileNBT(NBTTagCompound compound) {
        if (compound.hasKey("player")) {
            this.player = PlayerHelper.getPlayerFromUUID(compound.getUniqueId("player"));
        }

        List<String> searchList = new ArrayList<>();
        NBTTagCompound searchTag = compound.getCompoundTag("searchStrings");
        for (String key : searchTag.getKeySet()) {
            searchList.add(searchTag.getString(key));
        }
        this.searchStrings = searchList.toArray(new String[0]);
        this.searchTime = compound.getInteger("searchTime");
        this.targets.clear();
        NBTTagCompound targetsTag = compound.getCompoundTag("targets");
        for (String key : targetsTag.getKeySet()) {
            NBTTagCompound tag = targetsTag.getCompoundTag(key);
            this.targets.add(NBTHelper.deserializeBlockPos(tag));
        }
    }

    @Override
    protected NBTTagCompound writeToTileNBT(NBTTagCompound compound) {
        super.writeToTileNBT(compound);
        if (this.player != null) {
            compound.setUniqueId("player", PlayerHelper.getUUIDFromPlayer(this.player));
        }

        NBTTagCompound searchList = new NBTTagCompound();
        for (int i = 0; i < this.searchStrings.length; i++) {
            searchList.setString(Integer.toString(i), this.searchStrings[i]);
        }
        compound.setTag("searchStrings", searchList);
        compound.setInteger("searchTime", this.searchTime);
        NBTTagCompound targetsTag = new NBTTagCompound();
        for (int i = 0; i < this.targets.size(); i++) {
            targetsTag.setTag("target" + i, NBTHelper.serializeBlockPos(this.targets.get(i)));
        }
        compound.setTag("targets", targetsTag);
        return compound;
    }

    @Override
    public void update() {
        boolean did = false;
        if (!this.world.isRemote) {
            //The diviner only updates once every second
            if (this.searchTime > 0 && this.world.getTotalWorldTime() % 20L == 0) {
                this.searchTime--;
                //Every 3 seconds the diviner will check if any blocks have been mined or changed and will update the ping targets
                if (this.world.getTotalWorldTime() % 60L == 0) {
                    boolean shouldRefreshTargets = this.targets.stream().anyMatch(targetPos -> this.world.isAirBlock(targetPos) || !this.isBlockValid(targetPos));
                    if (shouldRefreshTargets) {
                        this.findNewTargets();
                    }
                }
                did = true;
            } else if (this.world.isBlockPowered(this.pos)) {
                //If the diviner has a redstone signal it will automatically restart the search timer
                this.beginNewSearchTimer();
                did = true;
            }
        } else {
            if (this.searchTime > 0) {
                if (this.world.getTotalWorldTime() % 5L == 0) {
                    this.renderDivinerSparkle();
                }
                if (this.world.getTotalWorldTime() % 44L == 0) {
                    for (BlockPos pingPos : this.targets) {
                        this.renderPing(pingPos);
                    }
                }
            }
        }

        if (did) {
            this.markDirty();
            this.syncTile(false);
        }
    }

    protected void renderDivinerSparkle() {
        if (this.world.getTotalWorldTime() % 5L == 0) {
            FXDispatcher.INSTANCE.visSparkle(
                    this.pos.up().getX() + this.world.rand.nextInt(3) - this.world.rand.nextInt(3),
                    this.pos.up().getY() + this.world.rand.nextInt(3),
                    this.pos.up().getZ() + this.world.rand.nextInt(3) - this.world.rand.nextInt(3),
                    this.pos.up().getX(),
                    this.pos.up().getY(),
                    this.pos.up().getZ(),
                    Aspect.MAGIC.getColor());

            FXDispatcher.INSTANCE.visSparkle(
                    this.pos.getX() + this.world.rand.nextInt(3) - this.world.rand.nextInt(3),
                    this.pos.getY() + this.world.rand.nextInt(3),
                    this.pos.getZ() + this.world.rand.nextInt(3) - this.world.rand.nextInt(3),
                    this.pos.getX(),
                    this.pos.getY(),
                    this.pos.getZ(),
                    Aspect.MAGIC.getColor());

            if (this.world.isAirBlock(this.pos.down())) {
                FXDispatcher.INSTANCE.visSparkle(
                        this.pos.down().getX() + this.world.rand.nextInt(3) - this.world.rand.nextInt(3),
                        this.pos.down().getY() + this.world.rand.nextInt(3),
                        this.pos.down().getZ() + this.world.rand.nextInt(3) - this.world.rand.nextInt(3),
                        this.pos.down().getX(),
                        this.pos.down().getY(),
                        this.pos.down().getZ(),
                        Aspect.MAGIC.getColor());
            }
        }
    }

    public boolean isActive() {
        return this.searchTime > 0;
    }

    @SideOnly(Side.CLIENT)
    protected void renderPing(BlockPos pos) {
        Color color = this.getOreColor(pos);
        float r = color.getRed() / 255.0F;
        float g = color.getGreen() / 255.0F;
        float b = color.getBlue() / 255.0F;
        float colorAvg = (r + g + b) / 3.0F;

        FXDispatcher.GenPart part = new FXDispatcher.GenPart();
        part.age = 44;
        part.redStart = r;
        part.redEnd = r;
        part.greenStart = g;
        part.greenEnd = g;
        part.blueStart = b;
        part.blueEnd = b;
        part.alpha = new float[]{0.0F, 1.0F, 0.8F, 0.0F};
        part.loop = true;
        part.partStart = 240;
        part.partNum = 15;
        part.partInc = 1;
        part.scale = new float[]{9.0F};
        part.layer = colorAvg < 0.25F ? 3 : 2;
        part.grid = 16;
        FXDispatcher.INSTANCE.drawGenericParticles(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, part);
    }

    protected Color getOreColor(BlockPos targetPos) {
        ItemStack stack = ItemStack.EMPTY;
        IBlockState state = world.getBlockState(targetPos);
        if (state.getBlock() != Blocks.AIR && state.getBlock() != Blocks.BEDROCK) {
            Item item = Item.getItemFromBlock(state.getBlock());
            stack = new ItemStack(item, 1, item.getHasSubtypes() ? state.getBlock().getMetaFromState(state) : 0);
        }
        if (!stack.isEmpty()) {
            for (String oreName : OreHelper.getOreNames(stack)) {
                if (oreName != null) {
                    oreName = oreName.toUpperCase();
                    for (String key : ORE_COLORS.keySet()) {
                        if (oreName.contains(key)) {
                            return ORE_COLORS.getOrDefault(key, DEFAULT_ORE_COLOR);
                        }
                    }
                }
            }
        }
        return DEFAULT_ORE_COLOR;
    }

    static {
        ORE_COLORS.put("IRON", new Color(0xD8AF93));
        ORE_COLORS.put("COAL", new Color(0x101010));
        ORE_COLORS.put("REDSTONE", new Color(0xFF0000));
        ORE_COLORS.put("GOLD", new Color(0xFCEE4B));
        ORE_COLORS.put("LAPIS", new Color(0x1445BC));
        ORE_COLORS.put("DIAMOND", new Color(0x5DECF5));
        ORE_COLORS.put("EMERALD", new Color(0x17DD62));
        ORE_COLORS.put("QUARTZ", new Color(0xE5DED5));
        ORE_COLORS.put("SILVER", new Color(0xDAD9FD));
        ORE_COLORS.put("LEAD", new Color(0x6B6B6B));
        ORE_COLORS.put("TIN", new Color(0xEFEFFB));
        ORE_COLORS.put("COPPER", new Color(0xFD9C55));
        ORE_COLORS.put("AMBER", new Color(0xFDB325));
        ORE_COLORS.put("CINNABAR", new Color(0x9B0508));
    }
}
