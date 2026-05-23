package com.verdantartifice.thaumicwonders.client.renderers.tile;

import com.verdantartifice.thaumicwonders.common.blocks.devices.BlockEssentiaEnchanter;
import com.verdantartifice.thaumicwonders.common.blocks.misc.BlockArcanePillar.EnumDirection;
import com.verdantartifice.thaumicwonders.common.registry.SoundsTW;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TileEssentiaEnchanter;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;

import java.awt.*;

@SideOnly(Side.CLIENT)
public class TesrEssentiaEnchanter extends TileEntitySpecialRenderer<TileEssentiaEnchanter> {
    @Override
    public void render(TileEssentiaEnchanter tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        this.renderItem(tile, partialTicks);
        GlStateManager.popMatrix();
        this.renderLightning(tile, partialTicks);
    }

    private void renderItem(TileEssentiaEnchanter tile, float partialTicks) {
        ItemStack stack = tile.getItemToEnchant();
        if(!stack.isEmpty()) {
            EnumFacing facing = tile.getWorld().getBlockState(tile.getPos()).getValue(BlockEssentiaEnchanter.FACING);
            RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();
            GlStateManager.translate(0.5, 0.78125, 0.5);
            GlStateManager.scale(0.5, 0.5, 0.5);
            switch (facing) {
                case SOUTH:
                    GlStateManager.rotate(180.0f, 0, 1, 0);
                    break;
                case WEST:
                    GlStateManager.rotate(270.0f, 0, 1, 0);
                    break;
                case EAST:
                    GlStateManager.rotate(90.0f, 0, 1, 0);
                    break;
            }

            float progress = 0;
            float tmp = 0;
            if(tile.getProgress() > 0) {
                //Stack raising to enchant position
                progress = Math.min(1.0f, ((float) (tile.getMaxProgress() - tile.getProgress()) / 225.0f));
                tmp = 0.7f * progress;
            } else if(tile.getCooldown() > 0) {
                //Stack returning to normal position
                progress = (float) tile.getCooldown() / (float) tile.getMaxCooldown();
                tmp = 0.7f - (0.7f * (1.0f - progress));
            }

            GlStateManager.rotate(90.0f * (1.0f - progress), 1, 0, 0);
            GlStateManager.translate(0, tmp, 0);
            GlStateManager.pushAttrib();
            RenderHelper.enableStandardItemLighting();
            renderItem.renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popAttrib();
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }

    private int lastProgress = -1;
    private boolean playedEndSound = false;
    private void renderLightning(TileEssentiaEnchanter tile, float partialTicks) {
        int currentProgress = tile.getProgress();

        if(currentProgress > 0) {
            if(currentProgress % (tile.getMaxProgress() / 9) == 0 && currentProgress > 30 && currentProgress != lastProgress) {
                    tile.getWorld().playSound(tile.getPos().getX() + 0.5, tile.getPos().getY() + 0.5, tile.getPos().getZ() + 0.5,
                            SoundsTW.ENCHANT_ZAP, SoundCategory.BLOCKS, 0.5f, 1.0f, false);
                lastProgress = currentProgress;
            }
            if(tile.getProgress() == 30) {
                if (!playedEndSound) {
                    tile.getWorld().playSound(tile.getPos().getX() + 0.5, tile.getPos().getY() + 0.5, tile.getPos().getZ() + 0.5,
                            SoundsTW.ENCHANT_END, SoundCategory.BLOCKS, 0.5f, 1.0f, false);
                    playedEndSound = true;
                }
            } else {
                playedEndSound = false;
            }

            int pillars = (int) ((1.0 - (double) tile.getProgress() / tile.getMaxProgress()) * 9.0);
            Vec3d center = new Vec3d(tile.getPos().getX() + 0.5, tile.getPos().getY() + 1.15, tile.getPos().getZ() + 0.5);
            for(EnumDirection direction : EnumDirection.VALUES) {
                if(pillars < direction.ordinal()) break;
                EnumDirection next = direction.next();
                Vec3d start = direction.getNitorOffset(tile.getPos().add(TileEssentiaEnchanter.OFFSET_PILLARS.get(direction)));
                Vec3d end = next.getNitorOffset(tile.getPos().add(TileEssentiaEnchanter.OFFSET_PILLARS.get(next)));
                this.renderLightning(start, end);
                if(pillars >= EnumDirection.VALUES.length) {
                    this.renderLightning(start, center);
                }
            }
        } else {
            lastProgress = -1;
            playedEndSound = false;
        }
    }

    private void renderLightning(Vec3d start, Vec3d end) {
        Color color = new Color(MapColor.GOLD.colorValue);
        FXDispatcher.INSTANCE.arcLightning(start.x, start.y, start.z, end.x, end.y, end.z,
                color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 0.1f);
    }
}
