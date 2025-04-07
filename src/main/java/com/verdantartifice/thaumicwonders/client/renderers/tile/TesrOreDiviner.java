package com.verdantartifice.thaumicwonders.client.renderers.tile;

import com.verdantartifice.thaumicwonders.common.tiles.devices.TileOreDiviner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TesrOreDiviner extends TileEntitySpecialRenderer<TileOreDiviner> {
    @Override
    public void render(TileOreDiviner te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        if (te != null && te.getWorld() != null && te.getSearchStack() != null && !te.getSearchStack().isEmpty()) {
            int rem = (int) (te.getWorld().getWorldTime() % 360);
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5D, y + 0.0625D, z + 0.5D);
            GlStateManager.scale(0.75D, 0.75D, 0.75D);
            GlStateManager.rotate(rem, 0.0F, 1.0F, 0.0F);

            ItemStack stack = te.getSearchStack().copy();
            stack.setCount(1);
            EntityItem ei = new EntityItem(te.getWorld(), 0.0D, 0.0D, 0.0D, stack);
            ei.hoverStart = 0.0F;

            RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
            renderManager.renderEntity(ei, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);

            GlStateManager.popMatrix();
        }
    }
}
