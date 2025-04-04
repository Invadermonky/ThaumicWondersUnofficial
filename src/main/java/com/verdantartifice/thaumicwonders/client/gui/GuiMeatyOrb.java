package com.verdantartifice.thaumicwonders.client.gui;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.network.PacketHandler;
import com.verdantartifice.thaumicwonders.common.network.packets.PacketMeatyOrbAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

public class GuiMeatyOrb extends GuiScreen {
    private static final ResourceLocation BG_TEXTURE = new ResourceLocation(ThaumicWonders.MODID, "textures/gui/gui_meaty_orb_background.png");

    protected BlockPos pos;

    public GuiMeatyOrb(BlockPos pos) {
        super();
        this.pos = pos;
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
        PacketHandler.INSTANCE.sendToServer(new PacketMeatyOrbAction(this.pos));
        this.mc.player.closeScreen();
    }

    @Override
    public void initGui() {
        if (this.mc == null) {
            this.mc = Minecraft.getMinecraft();
        }
        this.buttonList.clear();

        int baseX = (this.width - 16) / 2;
        int baseY = (this.height - 16) / 2;
        this.buttonList.add(new GuiSelectorButton(0, baseX, baseY, 120, 120, 16, 16, I18n.format("thaumicwonders.gui.meaty_orb.0")));
    }

    private class GuiSelectorButton extends GuiButton {
        private final ResourceLocation TEXTURE = new ResourceLocation(ThaumicWonders.MODID, "textures/gui/gui_meaty_orb.png");
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
                    this.drawString(mc.fontRenderer, this.displayString, this.x + 19, this.y + 4, 0xFFFFFF);
                }
                this.mouseDragged(mc, mouseX, mouseY);
            }
        }
    }
}
