package com.verdantartifice.thaumicwonders.common.sounds;

import com.verdantartifice.thaumicwonders.common.tiles.devices.TileEssentiaEnchanter;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EssentiaSoundLoop extends PositionedSound implements ITickableSound {
    protected TileEssentiaEnchanter enchanter;
    protected BlockPos position;

    public EssentiaSoundLoop(SoundEvent event, TileEssentiaEnchanter enchanter, float volume) {
        super(event.getSoundName(), SoundCategory.BLOCKS);
        this.repeat = true;
        this.enchanter = enchanter;
        this.volume = volume;
        this.position = this.enchanter.getPos();
        this.xPosF = position.getX();
        this.yPosF = position.getY();
        this.zPosF = position.getZ();
    }

    @Override
    public void update() {
        if (this.enchanter.isInvalid() || this.enchanter.recipeEssentia.visSize() <= 0) {
            this.volume -= 0.05F;
        }
    }

    @Override
    public boolean isDonePlaying() {
        return this.volume <= 0.0F;
    }
}
