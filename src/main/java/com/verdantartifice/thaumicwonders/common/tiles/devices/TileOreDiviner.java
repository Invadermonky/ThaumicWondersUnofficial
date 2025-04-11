package com.verdantartifice.thaumicwonders.common.tiles.devices;

import com.verdantartifice.thaumicwonders.common.misc.OreHelper;
import com.verdantartifice.thaumicwonders.common.tiles.base.TileTW;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.fx.FXDispatcher;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class TileOreDiviner extends TileTW implements ITickable {
    public static final int SCAN_RANGE = 20;

    protected static final Map<String, Color> ORE_COLORS = new HashMap<>();
    protected static final Color DEFAULT_ORE_COLOR = new Color(0xC0C0C0);
    /**
     * Whether any client has activated this tile
     */
    protected boolean active = false;
    /**
     * The target position set by *this* client for ping
     */
    protected BlockPos target = null;
    protected ItemStack searchStack = null;
    protected int counter = -1;

    public void setTarget(@Nullable BlockPos newTarget) {
        this.target = newTarget;
        this.counter = 0;
    }

    public ItemStack getSearchStack() {
        return this.active ? this.searchStack : ItemStack.EMPTY;
    }

    public void setSearchStack(ItemStack stack) {
        this.searchStack = stack == null ? null : stack.copy();
        this.active = (this.searchStack != null && !this.searchStack.isEmpty());
        this.markDirty();
        this.syncTile(false);
    }

    @Override
    protected void readFromTileNBT(NBTTagCompound compound) {
        this.active = compound.getBoolean("active");
        this.searchStack = new ItemStack(compound.getCompoundTag("searchStack"));
    }

    @Override
    protected NBTTagCompound writeToTileNBT(NBTTagCompound compound) {
        compound.setBoolean("active", this.active);
        compound.setTag("searchStack", this.searchStack.writeToNBT(new NBTTagCompound()));
        return super.writeToTileNBT(compound);
    }

    @Override
    public void onLoad() {
        this.setSearchStack(ItemStack.EMPTY);
    }

    @Override
    public void update() {
        if (this.world.isRemote && this.active && this.target != null && this.counter % 44 == 0) {
            // If this client has a target position set, show the ping
            this.renderPing(this.target);
        }
        if (this.world.isRemote && this.active && this.counter % 5 == 0) {
            // If any client, not necessarily this one, has a target position set, show activity particles
            FXDispatcher.INSTANCE.visSparkle(
                    this.pos.getX() + this.world.rand.nextInt(3) - this.world.rand.nextInt(3),
                    this.pos.getY() + this.world.rand.nextInt(3),
                    this.pos.getZ() + this.world.rand.nextInt(3) - this.world.rand.nextInt(3),
                    this.pos.getX(),
                    this.pos.getY(),
                    this.pos.getZ(),
                    Aspect.MAGIC.getColor());
        }
        this.counter++;
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
        FXDispatcher.INSTANCE.drawGenericParticles(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, part);
    }

    protected Color getOreColor(BlockPos targetPos) {
        ItemStack stack = ItemStack.EMPTY;
        IBlockState state = world.getBlockState(targetPos);
        if (state.getBlock() != Blocks.AIR && state.getBlock() != Blocks.BEDROCK) {
            Item item = Item.getItemFromBlock(state.getBlock());
            stack = new ItemStack(item, 1, item.getHasSubtypes() ? state.getBlock().getMetaFromState(state) : 0);
        }
        if (stack.isEmpty() || stack.getItem() == null) {
            return DEFAULT_ORE_COLOR;
        } else {
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
            return DEFAULT_ORE_COLOR;
        }
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
