package com.verdantartifice.thaumicwonders.client.renderers.tile;

import com.verdantartifice.thaumicwonders.common.tiles.devices.TileDimensionalRipper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.ARBShaderObjects;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.client.lib.ender.ShaderCallback;
import thaumcraft.client.lib.ender.ShaderHelper;
import thaumcraft.common.lib.utils.BlockStateUtils;

public class TesrDimensionalRipper extends TileEntitySpecialRenderer<TileDimensionalRipper> {
    private static final ResourceLocation STARS_TEXTURE = new ResourceLocation("textures/entity/end_portal.png");

    private final ShaderCallback shaderCallback;

    public TesrDimensionalRipper() {
        this.shaderCallback = new ShaderCallback() {
            @Override
            public void call(int shader) {
                Minecraft mc = Minecraft.getMinecraft();

                int x = ARBShaderObjects.glGetUniformLocationARB(shader, "yaw");
                ARBShaderObjects.glUniform1fARB(x, (float) (mc.player.rotationYaw * Math.PI / 180.0D));

                int z = ARBShaderObjects.glGetUniformLocationARB(shader, "pitch");
                ARBShaderObjects.glUniform1fARB(z, -(float) (mc.player.rotationPitch * Math.PI / 180.0D));
            }
        };
    }

    @Override
    public void render(TileDimensionalRipper te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        if (BlockStateUtils.isEnabled(te.getBlockMetadata())) {
            GlStateManager.pushMatrix();
            bindTexture(STARS_TEXTURE);
            ShaderHelper.useShader(ShaderHelper.endShader, this.shaderCallback);

            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
            switch (BlockStateUtils.getFacing(te.getBlockMetadata())) {
                case DOWN:
                	GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
                    break;
                case NORTH:
                	GlStateManager.rotate(90.0F, -1.0F, 0.0F, 0.0F);
                    break;
                case SOUTH:
                	GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                    break;
                case WEST:
                	GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
                    break;
                case EAST:
                	GlStateManager.rotate(90.0F, 0.0F, 0.0F, -1.0F);
                    break;
                case UP:
                default:
                    // Don't rotate
                    break;
            }

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0D, 0.375D, 0.0D);
            GlStateManager.depthMask(false);
            for (EnumFacing face : EnumFacing.values()) {
                GlStateManager.pushMatrix();
                GlStateManager.rotate(90.0F, -face.getYOffset(), face.getXOffset(), -face.getZOffset());
                if (face.getZOffset() < 0) {
                    GlStateManager.translate(0.0D, 0.0D, 0.126D);
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                } else {
                    GlStateManager.translate(0.0D, 0.0D, -0.126D);
                }
                GlStateManager.rotate(90.0F, 0.0F, 0.0F, -1.0F);
                GlStateManager.scale(0.25D, 0.25D, 0.25D);
                UtilsFX.renderQuadCentered(1, 1, 0, 1.0F, 1.0F, 1.0F, 1.0F, 200, 1, 1.0F);
                GlStateManager.popMatrix();
            }
            GlStateManager.popMatrix();

            GlStateManager.popMatrix();

            GlStateManager.depthMask(true);
            ShaderHelper.releaseShader();
            GlStateManager.popMatrix();
        }
    }
}
