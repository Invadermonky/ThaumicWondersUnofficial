package com.verdantartifice.thaumicwonders.client.gui;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.containers.ContainerPrimordialAccelerator;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TilePrimordialAccelerator;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiPrimordialAccelerator extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(ThaumicWonders.MODID, "textures/gui/gui_primordial_accelerator.png");
    
    public GuiPrimordialAccelerator(InventoryPlayer inventoryPlayer, TilePrimordialAccelerator acceleratorTile) {
        super(new ContainerPrimordialAccelerator(inventoryPlayer, acceleratorTile));
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
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int xStart = (this.width - this.xSize) / 2;
        int yStart = (this.height - this.ySize) / 2;
        GL11.glEnable(GL11.GL_BLEND);
        drawTexturedModalRect(xStart, yStart, 0, 0, this.xSize, this.ySize);
    }
}
