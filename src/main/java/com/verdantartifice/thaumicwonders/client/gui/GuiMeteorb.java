package com.verdantartifice.thaumicwonders.client.gui;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.network.PacketHandler;
import com.verdantartifice.thaumicwonders.common.network.packets.PacketMeteorbAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GuiMeteorb extends GuiScreen {
    private static final ResourceLocation BG_TEXTURE = new ResourceLocation(ThaumicWonders.MODID, "textures/gui/gui_meteorb_background.png");

    protected BlockPos pos;

    public GuiMeteorb(BlockPos pos) {
        super();
        this.pos = pos;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Render background
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(BG_TEXTURE);
        this.drawTexturedModalRect((this.width - 256) / 2, (this.height - 256) / 2, 0, 0, 256, 256);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

        // Draw everything else
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        PacketHandler.INSTANCE.sendToServer(new PacketMeteorbAction(button.id, this.pos));
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
        this.buttonList.add(new GuiSelectorButton(2, baseX + 24, baseY, 144, 120, 16, 16, I18n.format("thaumicwonders.gui.meteorb.2")));
        this.buttonList.add(new GuiSelectorButton(1, baseX, baseY, 120, 120, 16, 16, I18n.format("thaumicwonders.gui.meteorb.1")));
        this.buttonList.add(new GuiSelectorButton(0, baseX - 24, baseY, 96, 120, 16, 16, I18n.format("thaumicwonders.gui.meteorb.0")));
    }

    private class GuiSelectorButton extends GuiButton {
        private final ResourceLocation TEXTURE = new ResourceLocation(ThaumicWonders.MODID, "textures/gui/gui_meteorb.png");
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
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                mc.renderEngine.bindTexture(this.TEXTURE);
                GlStateManager.pushMatrix();
                if (this.hovered) {
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                } else {
                    GlStateManager.color(0.8F, 0.8F, 0.8F, 1.0F);
                }
                this.drawTexturedModalRect(this.x, this.y, this.texX, this.texY, 16, 16);
                GlStateManager.popMatrix();
                if (this.hovered) {
                    this.drawString(mc.fontRenderer, this.displayString, this.x + 19, this.y + 4, 0xFFFFFF);
                }
                this.mouseDragged(mc, mouseX, mouseY);
            }
        }
    }
}
