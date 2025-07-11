package com.verdantartifice.thaumicwonders.client.renderers.entity;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.entities.EntityVoidPortal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.lib.UtilsFX;

@SideOnly(Side.CLIENT)
public class RenderVoidPortal extends Render<EntityVoidPortal> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(ThaumicWonders.MODID, "textures/misc/void_portal.png");

    public RenderVoidPortal(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = 0.0F;
        this.shadowOpaque = 0.0F;
    }

    @Override
    public void doRender(EntityVoidPortal portal, double x, double y, double z, float entityYaw, float partialTicks) {
        long nt = System.nanoTime();
        long time = nt / 50000000L;
        float scaley = 1.4F;
        int e = (int) Math.min(50.0F, portal.ticksExisted + partialTicks);
        float scale = e / 50.0F * 1.25F;

        y += portal.height / 2.0F;
        float stability = 50F;
        float m = (1.0F - MathHelper.clamp((stability + 100.0F) / 100.0F, 0.0F, 1.0F)) / 3.0F;
        float bobY = MathHelper.sin(portal.ticksExisted / (5.0F - 12.0F * m)) * m + m;
        float bobXZ = MathHelper.sin(portal.ticksExisted / (6.0F - 15.0F * m)) * m + m;
        float alpha = 1.0F - bobY;
        scaley -= bobY / 4.0F;
        scale -= bobXZ / 3.0F;

        this.bindTexture(TEXTURE);
        GlStateManager.pushMatrix();

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);

        if (Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityPlayer) {
            GlStateManager.depthMask(false);
            Tessellator tessellator = Tessellator.getInstance();
            float arX = ActiveRenderInfo.getRotationX();
            float arZ = ActiveRenderInfo.getRotationZ();
            float arYZ = ActiveRenderInfo.getRotationYZ();
            float arXY = ActiveRenderInfo.getRotationXY();
            float arXZ = ActiveRenderInfo.getRotationXZ();

            tessellator.getBuffer().begin(7, UtilsFX.VERTEXFORMAT_POS_TEX_CO_LM_NO);
            Vec3d v1 = new Vec3d(-arX - arYZ, -arXZ, -arZ - arXY);
            Vec3d v2 = new Vec3d(-arX + arYZ, arXZ, -arZ + arXY);
            Vec3d v3 = new Vec3d(arX + arYZ, arXZ, arZ + arXY);
            Vec3d v4 = new Vec3d(arX - arYZ, -arXZ, arZ - arXY);
            int frame = 15 - (int) time % 16;
            float f2 = frame / 16.0F;
            float f3 = f2 + 0.0625F;
            float f4 = 0.0F;
            float f5 = 1.0F;
            int i = 220;
            int j = i >> 16 & 0xFFFF;
            int k = i & 0xFFFF;
            tessellator.getBuffer().pos(x + v1.x * scale, y + v1.y * scaley, z + v1.z * scale).tex(f3, f4).color(1.0F, 1.0F, 1.0F, alpha).lightmap(j, k).normal(0.0F, 0.0F, -1.0F).endVertex();
            tessellator.getBuffer().pos(x + v2.x * scale, y + v2.y * scaley, z + v2.z * scale).tex(f3, f5).color(1.0F, 1.0F, 1.0F, alpha).lightmap(j, k).normal(0.0F, 0.0F, -1.0F).endVertex();
            tessellator.getBuffer().pos(x + v3.x * scale, y + v3.y * scaley, z + v3.z * scale).tex(f2, f5).color(1.0F, 1.0F, 1.0F, alpha).lightmap(j, k).normal(0.0F, 0.0F, -1.0F).endVertex();
            tessellator.getBuffer().pos(x + v4.x * scale, y + v4.y * scaley, z + v4.z * scale).tex(f2, f4).color(1.0F, 1.0F, 1.0F, alpha).lightmap(j, k).normal(0.0F, 0.0F, -1.0F).endVertex();
            tessellator.draw();
            GlStateManager.depthMask(true);
        }
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityVoidPortal entity) {
        return TEXTURE;
    }
}
