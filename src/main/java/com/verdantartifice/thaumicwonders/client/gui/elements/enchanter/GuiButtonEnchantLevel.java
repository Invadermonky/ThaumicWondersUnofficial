package com.verdantartifice.thaumicwonders.client.gui.elements.enchanter;

import com.verdantartifice.thaumicwonders.client.gui.GuiEssentiaEnchanter;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonEnchantLevel extends AbstractButtonEnchanter {
    public final boolean isIncrease;

    public GuiButtonEnchantLevel(GuiEssentiaEnchanter guiEnchanter, int buttonId, int x, int y, boolean isIncrease) {
        super(guiEnchanter, buttonId, x, y, isIncrease ? 176 : 194, 32, 18, 18, "");
        this.isIncrease = isIncrease;
    }

    @Override
    public void updateEnabled() {
        boolean flag = false;
        if(this.guiEnchanter.getCurrentRecipe() != null) {
            if(this.isIncrease) {
                flag = this.guiEnchanter.getCurrentRecipeLevel() < this.guiEnchanter.getCurrentRecipe().getMaxEnchantmentLevel();
            } else {
                flag = this.guiEnchanter.getCurrentRecipeLevel() > 0;
            }
        }
        this.enabled = flag;
    }
}
