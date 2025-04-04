package com.verdantartifice.thaumicwonders.client.renderers.entity.monsters;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.entities.monsters.EntityCorruptionAvatar;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderCorruptionAvatar extends RenderBiped<EntityCorruptionAvatar> {
    protected static final ResourceLocation SKIN = new ResourceLocation(ThaumicWonders.MODID, "textures/entities/monsters/corruption_avatar.png");

    public RenderCorruptionAvatar(RenderManager manager) {
        super(manager, new ModelBiped(), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityCorruptionAvatar entity) {
        return SKIN;
    }

    @Override
    protected void preRenderCallback(EntityCorruptionAvatar entitylivingbaseIn, float partialTickTime) {
        super.preRenderCallback(entitylivingbaseIn, partialTickTime);
        GL11.glScalef(1.15F, 1.15F, 1.15F);
    }
}
