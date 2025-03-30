package com.verdantartifice.thaumicwonders.client.gui;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.network.PacketHandler;
import com.verdantartifice.thaumicwonders.common.network.packets.PacketStructureDivinerAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

public class GuiStructureDiviner extends GuiScreen {
    private static final ResourceLocation BG_TEXTURE = new ResourceLocation(ThaumicWonders.MODID, "textures/gui/gui_structure_diviner_background.png");

    public GuiStructureDiviner() {
        super();
    }
    
    @Override
    public void initGui() {
        if (this.mc == null) {
            this.mc = Minecraft.getMinecraft();
        }
        this.buttonList.clear();
        
        int baseX = (this.width - 16) / 2;
        int baseY = (this.height - 16) / 2;
        
        // out of order for z-ordering
        this.buttonList.add(new GuiSelectorButton(2, baseX + 32, baseY, 152, 120, 16, 16, I18n.format("thaumicwonders.gui.structure_diviner.2")));
        this.buttonList.add(new GuiSelectorButton(1, baseX + 23, baseY - 23, 143, 97, 16, 16, I18n.format("thaumicwonders.gui.structure_diviner.1")));
        this.buttonList.add(new GuiSelectorButton(3, baseX + 23, baseY + 23, 143, 143, 16, 16, I18n.format("thaumicwonders.gui.structure_diviner.3")));
        this.buttonList.add(new GuiSelectorButton(0, baseX, baseY - 32, 120, 88, 16, 16, I18n.format("thaumicwonders.gui.structure_diviner.0")));
        this.buttonList.add(new GuiSelectorButton(4, baseX, baseY + 32, 120, 152, 16, 16, I18n.format("thaumicwonders.gui.structure_diviner.4")));
        this.buttonList.add(new GuiSelectorButton(8, baseX, baseY, 120, 120, 16, 16, I18n.format("thaumicwonders.gui.structure_diviner.8")));
        this.buttonList.add(new GuiSelectorButton(5, baseX - 23, baseY + 23, 97, 143, 16, 16, I18n.format("thaumicwonders.gui.structure_diviner.5")));
        this.buttonList.add(new GuiSelectorButton(7, baseX - 23, baseY - 23, 97, 97, 16, 16, I18n.format("thaumicwonders.gui.structure_diviner.7")));
        this.buttonList.add(new GuiSelectorButton(6, baseX - 32, baseY, 88, 120, 16, 16, I18n.format("thaumicwonders.gui.structure_diviner.6")));
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Render background
        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        GL11.glEnable(GL11.GL_BLEND);
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(BG_TEXTURE);
        this.drawTexturedModalRect((this.width - 256) / 2, (this.height - 256) / 2, 0, 0, 256, 256);
        GlStateManager.disableBlend();
        GL11.glPopMatrix();
        
        // Draw everything else
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        PacketHandler.INSTANCE.sendToServer(new PacketStructureDivinerAction(button.id));
        this.mc.player.closeScreen();
    }
    
    private class GuiSelectorButton extends GuiButton {
        private final ResourceLocation TEXTURE = new ResourceLocation(ThaumicWonders.MODID, "textures/gui/gui_structure_diviner.png");
        private int texX;
        private int texY;
        
        public GuiSelectorButton(int buttonId, int x, int y, int texX, int texY, int widthIn, int heightIn, String buttonText) {
            super(buttonId, x, y, widthIn, heightIn, buttonText);
            this.texX = texX;
            this.texY = texY;
        }
        
        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (this.visible) {
                this.hovered = ((mouseX >= this.x) && (mouseY >= this.y) && (mouseX < this.x + this.width) && (mouseY < this.y + this.height));
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                mc.renderEngine.bindTexture(this.TEXTURE);
                GL11.glPushMatrix();
                if (this.hovered) {
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                } else {
                    GL11.glColor4f(0.8F, 0.8F, 0.8F, 1.0F);
                }
                this.drawTexturedModalRect(this.x, this.y, this.texX, this.texY, 16, 16);
                GL11.glPopMatrix();
                if (this.hovered) {
                    this.drawString(mc.fontRenderer, this.displayString, this.x + 19, this.y + 4, 16777215);
                }
                this.mouseDragged(mc, mouseX, mouseY);
            }
        }
    }
}
