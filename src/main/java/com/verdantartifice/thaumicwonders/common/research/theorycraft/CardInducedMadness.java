package com.verdantartifice.thaumicwonders.common.research.theorycraft;

import com.verdantartifice.thaumicwonders.common.tiles.devices.IResearchEngine;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TileMadnessEngine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.research.theorycraft.ResearchTableData;

import java.util.Random;

public class CardInducedMadness extends AbstractResearchEngineCard {
    @Override
    protected Class<? extends IResearchEngine> getEngineTileClass() {
        return TileMadnessEngine.class;
    }
    
    @Override
    public String getResearchCategory() {
        return "ELDRITCH";
    }

    @Override
    protected int getResearchAmount(Random rng) {
        return MathHelper.getInt(rng, 20, 25);
    }
    
    @Override
    public boolean activate(EntityPlayer player, ResearchTableData data) {
        if (super.activate(player, data)) {
            ThaumcraftApi.internalMethods.addWarpToPlayer(player, 1, IPlayerWarp.EnumWarpType.NORMAL);
            ThaumcraftApi.internalMethods.addWarpToPlayer(player, 5, IPlayerWarp.EnumWarpType.TEMPORARY);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getLocalizedName() {
        return new TextComponentTranslation("card.induced_madness.name").getUnformattedText();
    }

    @Override
    public String getLocalizedText() {
        return new TextComponentTranslation("card.induced_madness.text").getUnformattedText();
    }
}
