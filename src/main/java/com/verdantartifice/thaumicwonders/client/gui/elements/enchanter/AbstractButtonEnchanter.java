package com.verdantartifice.thaumicwonders.client.gui.elements.enchanter;

import com.verdantartifice.thaumicwonders.client.gui.GuiEssentiaEnchanter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class AbstractButtonEnchanter extends GuiButton {
    private ResourceLocation textureLocation;
    protected GuiEssentiaEnchanter guiEnchanter;
    private final int textureX;
    private final int textureY;

    public AbstractButtonEnchanter(GuiEssentiaEnchanter guiEnchanter, int buttonId, int x, int y, int textureX, int textureY, int widthIn, int heightIn, String buttonText) {
        super(buttonId, guiEnchanter.getGuiLeft() + x, guiEnchanter.getGuiTop() + y, widthIn, heightIn, buttonText);
        this.setTextureLocation(GuiEssentiaEnchanter.getTextureLocation());
        this.guiEnchanter = guiEnchanter;
        this.textureX = textureX;
        this.textureY = textureY;
    }

    public ResourceLocation getTextureLocation() {
        return this.textureLocation;
    }

    public void setTextureLocation(ResourceLocation textureLocation) {
        this.textureLocation = textureLocation;
    }

    public abstract void updateEnabled();

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        this.updateEnabled();
        if (this.visible) {
            FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(this.getTextureLocation());
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int state = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            this.drawTexturedModalRect(this.x, this.y, textureX, textureY + state * this.height, this.width, this.height);
            this.mouseDragged(mc, mouseX, mouseY);
            int j = 14737632;

            if (packedFGColour != 0) {
                j = packedFGColour;
            } else if (!this.enabled) {
                j = 10526880;
            } else if (this.hovered) {
                j = 16777120;
            }

            this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
        }
    }
}
