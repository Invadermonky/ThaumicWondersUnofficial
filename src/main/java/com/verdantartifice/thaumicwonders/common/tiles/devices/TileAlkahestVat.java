package com.verdantartifice.thaumicwonders.common.tiles.devices;

import com.verdantartifice.thaumicwonders.common.tiles.base.TileTW;
import net.minecraft.util.ITickable;
import thaumcraft.client.fx.FXDispatcher;

public class TileAlkahestVat extends TileTW implements ITickable {
    @Override
    public void update() {
        if (this.world.isRemote) {
            this.drawEffects();
        }
    }

    protected void drawEffects() {
        FXDispatcher.INSTANCE.crucibleFroth(
                this.pos.getX() + 0.2F + (this.world.rand.nextFloat() * 0.6F),
                this.pos.getY() + this.getFluidHeight(),
                this.pos.getZ() + 0.2F + (this.world.rand.nextFloat() * 0.6F)
        );
    }

    protected float getFluidHeight() {
        return 0.8F;
    }
}
