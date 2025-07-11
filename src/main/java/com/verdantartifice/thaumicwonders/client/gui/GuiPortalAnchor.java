package com.verdantartifice.thaumicwonders.client.gui;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.containers.ContainerPortalAnchor;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TilePortalAnchor;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiPortalAnchor extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(ThaumicWonders.MODID, "textures/gui/gui_portal_anchor.png");

    public GuiPortalAnchor(InventoryPlayer inventoryPlayer, TilePortalAnchor anchorTile) {
        super(new ContainerPortalAnchor(inventoryPlayer, anchorTile));
        this.xSize = 175;
        this.ySize = 232;
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
        int yStart = ((this.height - this.ySize) / 2) + 33;
        GlStateManager.enableBlend();
        drawTexturedModalRect(xStart, yStart, 0, 0, this.xSize, this.ySize);
    }
}
