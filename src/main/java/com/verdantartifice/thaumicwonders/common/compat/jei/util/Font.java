package com.verdantartifice.thaumicwonders.common.compat.jei.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;

public enum Font {
    SMALL(true),
    LARGE(false);

    private FontRenderer fontRenderer;

    Font(boolean small) {
        Minecraft mc = Minecraft.getMinecraft();
        this.fontRenderer = new FontRenderer(mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), mc.getTextureManager(), small);
        ((IReloadableResourceManager) mc.getResourceManager()).registerReloadListener(this.fontRenderer);
    }

    public void print(Object object, int x, int y, int color, boolean shadow) {
        this.fontRenderer.drawString(String.valueOf(object), (float) x, (float) y, color, shadow);
    }

    public void print(Object object, int x, int y, int color) {
        print(object, x, y, color, false);
    }

    public void print(Object object, int x, int y) {
        print(object, x, y, 8);
    }

    public int getStringWidth(String string) {
        return this.fontRenderer.getStringWidth(string);
    }
}
