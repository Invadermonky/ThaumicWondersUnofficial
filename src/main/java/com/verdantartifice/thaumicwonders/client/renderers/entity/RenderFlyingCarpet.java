package com.verdantartifice.thaumicwonders.client.renderers.entity;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.client.renderers.models.entity.ModelFlyingCarpet;
import com.verdantartifice.thaumicwonders.common.entities.EntityFlyingCarpet;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderFlyingCarpet extends Render<EntityFlyingCarpet> {
    private static final ResourceLocation TEXTURE_WHITE = new ResourceLocation(ThaumicWonders.MODID, "textures/entities/flying_carpet_white.png");
    private static final ResourceLocation TEXTURE_ORANGE = new ResourceLocation(ThaumicWonders.MODID, "textures/entities/flying_carpet_orange.png");
    private static final ResourceLocation TEXTURE_MAGENTA = new ResourceLocation(ThaumicWonders.MODID, "textures/entities/flying_carpet_magenta.png");
    private static final ResourceLocation TEXTURE_LIGHT_BLUE = new ResourceLocation(ThaumicWonders.MODID, "textures/entities/flying_carpet_light_blue.png");
    private static final ResourceLocation TEXTURE_YELLOW = new ResourceLocation(ThaumicWonders.MODID, "textures/entities/flying_carpet_yellow.png");
    private static final ResourceLocation TEXTURE_LIME = new ResourceLocation(ThaumicWonders.MODID, "textures/entities/flying_carpet_lime.png");
    private static final ResourceLocation TEXTURE_PINK = new ResourceLocation(ThaumicWonders.MODID, "textures/entities/flying_carpet_pink.png");
    private static final ResourceLocation TEXTURE_GRAY = new ResourceLocation(ThaumicWonders.MODID, "textures/entities/flying_carpet_gray.png");
    private static final ResourceLocation TEXTURE_SILVER = new ResourceLocation(ThaumicWonders.MODID, "textures/entities/flying_carpet_silver.png");
    private static final ResourceLocation TEXTURE_CYAN = new ResourceLocation(ThaumicWonders.MODID, "textures/entities/flying_carpet_cyan.png");
    private static final ResourceLocation TEXTURE_PURPLE = new ResourceLocation(ThaumicWonders.MODID, "textures/entities/flying_carpet_purple.png");
    private static final ResourceLocation TEXTURE_BLUE = new ResourceLocation(ThaumicWonders.MODID, "textures/entities/flying_carpet_blue.png");
    private static final ResourceLocation TEXTURE_BROWN = new ResourceLocation(ThaumicWonders.MODID, "textures/entities/flying_carpet_brown.png");
    private static final ResourceLocation TEXTURE_GREEN = new ResourceLocation(ThaumicWonders.MODID, "textures/entities/flying_carpet_green.png");
    private static final ResourceLocation TEXTURE_RED = new ResourceLocation(ThaumicWonders.MODID, "textures/entities/flying_carpet_red.png");
    private static final ResourceLocation TEXTURE_BLACK = new ResourceLocation(ThaumicWonders.MODID, "textures/entities/flying_carpet_black.png");

    protected ModelBase modelFlyingCarpet = new ModelFlyingCarpet();

    public RenderFlyingCarpet(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = 0.5F;
    }

    @Override
    public void doRender(EntityFlyingCarpet entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        this.setupTranslation(x, y, z);
        this.setupRotation(entity, entityYaw, partialTicks);
        this.bindEntityTexture(entity);

        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        this.modelFlyingCarpet.render(entity, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

        if (this.renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityFlyingCarpet entity) {
        EnumDyeColor color = entity.getDyeColor();
        if (color == null) {
            return TEXTURE_RED;
        } else {
            switch (color) {
                case WHITE:
                    return TEXTURE_WHITE;
                case ORANGE:
                    return TEXTURE_ORANGE;
                case MAGENTA:
                    return TEXTURE_MAGENTA;
                case LIGHT_BLUE:
                    return TEXTURE_LIGHT_BLUE;
                case YELLOW:
                    return TEXTURE_YELLOW;
                case LIME:
                    return TEXTURE_LIME;
                case PINK:
                    return TEXTURE_PINK;
                case GRAY:
                    return TEXTURE_GRAY;
                case SILVER:
                    return TEXTURE_SILVER;
                case CYAN:
                    return TEXTURE_CYAN;
                case PURPLE:
                    return TEXTURE_PURPLE;
                case BLUE:
                    return TEXTURE_BLUE;
                case BROWN:
                    return TEXTURE_BROWN;
                case GREEN:
                    return TEXTURE_GREEN;
                case BLACK:
                    return TEXTURE_BLACK;
                case RED:
                default:
                    return TEXTURE_RED;
            }
        }
    }

    public void setupRotation(EntityFlyingCarpet entity, float entityYaw, float partialTicks) {
        GlStateManager.rotate(90.0F - entityYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
    }

    public void setupTranslation(double x, double y, double z) {
        GlStateManager.translate((float) x, (float) y + 0.375F, (float) z);
    }
}
