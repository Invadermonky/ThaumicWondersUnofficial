package com.verdantartifice.thaumicwonders.client.renderers.entity;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.entities.EntityPrimalArrow;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderPrimalArrow extends RenderArrow<EntityPrimalArrow> {
    private static final ResourceLocation RES_AIR = new ResourceLocation(ThaumicWonders.MODID, "textures/entities/primal_arrow_air.png");
    private static final ResourceLocation RES_EARTH = new ResourceLocation(ThaumicWonders.MODID, "textures/entities/primal_arrow_earth.png");
    private static final ResourceLocation RES_FIRE = new ResourceLocation(ThaumicWonders.MODID, "textures/entities/primal_arrow_fire.png");
    private static final ResourceLocation RES_WATER = new ResourceLocation(ThaumicWonders.MODID, "textures/entities/primal_arrow_water.png");
    private static final ResourceLocation RES_ORDER = new ResourceLocation(ThaumicWonders.MODID, "textures/entities/primal_arrow_order.png");
    private static final ResourceLocation RES_ENTROPY = new ResourceLocation(ThaumicWonders.MODID, "textures/entities/primal_arrow_entropy.png");

    public RenderPrimalArrow(RenderManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityPrimalArrow entity) {
        switch (entity.getArrowType()) {
            case 5:
                return RES_ENTROPY;
            case 4:
                return RES_ORDER;
            case 3:
                return RES_WATER;
            case 2:
                return RES_FIRE;
            case 1:
                return RES_EARTH;
            case 0:
            default:
                return RES_AIR;
        }
    }

}
