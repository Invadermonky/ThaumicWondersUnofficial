package com.verdantartifice.thaumicwonders.client.renderers.models.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelFlyingCarpet - Daedalus4096
 * Created using Tabula 7.0.0
 */
public class ModelFlyingCarpet extends ModelBase {
    public ModelRenderer CarpetBase;

    public ModelFlyingCarpet() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.CarpetBase = new ModelRenderer(this, 0, 0);
        this.CarpetBase.setRotationPoint(0.0F, 3.0F, 1.0F);
        this.CarpetBase.addBox(-14.0F, -9.0F, -3.0F, 28, 16, 1, 0.0F);
        this.setRotateAngle(CarpetBase, 1.5707963267948966F, 0.0F, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.CarpetBase.render(f5);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
