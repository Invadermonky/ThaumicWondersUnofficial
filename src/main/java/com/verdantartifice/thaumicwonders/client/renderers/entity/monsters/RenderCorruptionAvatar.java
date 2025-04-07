package com.verdantartifice.thaumicwonders.client.renderers.entity.monsters;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.entities.monsters.EntityCorruptionAvatar;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCorruptionAvatar extends RenderBiped<EntityCorruptionAvatar> {
    protected static final ResourceLocation SKIN = new ResourceLocation(ThaumicWonders.MODID, "textures/entities/monsters/corruption_avatar.png");

    public RenderCorruptionAvatar(RenderManager manager) {
        super(manager, new ModelBiped(), 0.5F);
        this.addLayer((LayerRenderer<EntityLivingBase>) new LayerHeldItem((RenderLivingBase<EntityCorruptionAvatar>) this));
        final LayerBipedArmor layerbipedarmor = new LayerBipedArmor(this) {
            protected void initArmor() {
                this.modelLeggings = new ModelBiped();
                this.modelArmor = new ModelBiped();
            }
        };
        this.addLayer((LayerRenderer<EntityLivingBase>) layerbipedarmor);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityCorruptionAvatar entity) {
        return SKIN;
    }

    @Override
    protected void preRenderCallback(EntityCorruptionAvatar entitylivingbaseIn, float partialTickTime) {
        super.preRenderCallback(entitylivingbaseIn, partialTickTime);
        GlStateManager.scale(1.15F, 1.15F, 1.15F);
    }
}
