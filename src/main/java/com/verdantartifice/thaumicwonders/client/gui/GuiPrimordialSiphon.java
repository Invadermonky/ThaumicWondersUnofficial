package com.verdantartifice.thaumicwonders.client.gui;

import com.verdantartifice.thaumicwonders.common.containers.ContainerPrimordialSiphon;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TilePrimordialSiphon;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.Thaumcraft;

public class GuiPrimordialSiphon extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Thaumcraft.MODID, "textures/gui/gui_void_siphon.png");

    public GuiPrimordialSiphon(InventoryPlayer inventoryPlayer, TilePrimordialSiphon siphonTile) {
        super(new ContainerPrimordialSiphon(inventoryPlayer, siphonTile));
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.mc.renderEngine.bindTexture(TEXTURE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        int xStart = (this.width - this.xSize) / 2;
        int yStart = ((this.height - this.ySize) / 2);
        GlStateManager.enableBlend();
        drawTexturedModalRect(xStart, yStart, 0, 0, this.xSize, this.ySize);
    }
}
