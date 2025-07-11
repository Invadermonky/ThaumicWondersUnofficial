package com.verdantartifice.thaumicwonders.client.renderers.tile;

import com.verdantartifice.thaumicwonders.common.tiles.devices.TilePrimordialSiphon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.client.lib.ender.ShaderCallback;
import thaumcraft.client.lib.ender.ShaderHelper;
import thaumcraft.common.lib.utils.BlockStateUtils;

public class TesrPrimordialSiphon extends TileEntitySpecialRenderer<TilePrimordialSiphon> {
    private static final ResourceLocation starsTexture = new ResourceLocation("textures/entity/end_portal.png");
    private final ShaderCallback shaderCallback = new ShaderCallback() {
        public void call(int shader) {
            Minecraft mc = Minecraft.getMinecraft();
            int x = ARBShaderObjects.glGetUniformLocationARB(shader, "yaw");
            ARBShaderObjects.glUniform1fARB(x, (float) ((double) (mc.player.rotationYaw * 2.0F) * Math.PI / (double) 360.0F));
            int z = ARBShaderObjects.glGetUniformLocationARB(shader, "pitch");
            ARBShaderObjects.glUniform1fARB(z, -((float) ((double) (mc.player.rotationPitch * 2.0F) * Math.PI / (double) 360.0F)));
        }
    };

    @Override
    public void render(TilePrimordialSiphon te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        if (BlockStateUtils.isEnabled(te.getBlockMetadata())) {
            GL11.glPushMatrix();
            this.bindTexture(starsTexture);
            ShaderHelper.useShader(ShaderHelper.endShader, this.shaderCallback);
            GL11.glPushMatrix();
            GL11.glTranslated(x + (double) 0.5F, y + (double) 0.875F, z + (double) 0.5F);
            GlStateManager.depthMask(false);

            for (EnumFacing face : EnumFacing.values()) {
                GL11.glPushMatrix();
                GL11.glRotatef(90.0F, (float) (-face.getYOffset()), (float) face.getXOffset(), (float) (-face.getZOffset()));
                if (face.getZOffset() < 0) {
                    GL11.glTranslated(0.0F, 0.0F, 0.126);
                    GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                } else {
                    GL11.glTranslated(0.0F, 0.0F, -0.126);
                }

                GL11.glRotatef(90.0F, 0.0F, 0.0F, -1.0F);
                GL11.glScaled(0.2, 0.2, 0.2);
                UtilsFX.renderQuadCentered(1, 1, 0, 1.0F, 1.0F, 1.0F, 1.0F, 200, 1, 1.0F);
                GL11.glPopMatrix();
            }

            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glTranslated(x + (double) 0.5F, y + (double) 0.3125F, z + (double) 0.5F);
            GlStateManager.depthMask(false);

            for (EnumFacing face : EnumFacing.values()) {
                GL11.glPushMatrix();
                GL11.glRotatef(90.0F, (float) (-face.getYOffset()), (float) face.getXOffset(), (float) (-face.getZOffset()));
                if (face.getZOffset() < 0) {
                    GL11.glTranslated(0.0F, 0.0F, 0.26);
                    GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                } else {
                    GL11.glTranslated(0.0F, 0.0F, -0.26);
                }

                if (face.getZOffset() != 0) {
                    GL11.glRotatef(90.0F, 0.0F, 0.0F, -1.0F);
                }

                GL11.glScaled(0.25F, 0.5F, 0.25F);
                UtilsFX.renderQuadCentered(1, 1, 0, 1.0F, 1.0F, 1.0F, 1.0F, 200, 771, 1.0F);
                GL11.glPopMatrix();
            }

            GL11.glPopMatrix();
            GlStateManager.depthMask(true);
            ShaderHelper.releaseShader();
            GL11.glPopMatrix();
        }

    }
}
