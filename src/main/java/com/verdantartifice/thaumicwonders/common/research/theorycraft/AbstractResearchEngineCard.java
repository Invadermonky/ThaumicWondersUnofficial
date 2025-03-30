package com.verdantartifice.thaumicwonders.common.research.theorycraft;

import com.verdantartifice.thaumicwonders.common.tiles.devices.IResearchEngine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

import java.util.Random;

public abstract class AbstractResearchEngineCard extends TheorycraftCard {
    protected abstract Class<? extends IResearchEngine> getEngineTileClass();
    protected abstract int getResearchAmount(Random rng);

    @Override
    public boolean isAidOnly() {
        return true;
    }
    
    @Override
    public boolean initialize(EntityPlayer player, ResearchTableData data) {
        // Do an initial check for a fueled engine and abort if not found
        return this.findFueledEngine(data.table.getWorld(), data.table.getPos()) != null;
    }
    
    @Override
    public boolean activate(EntityPlayer player, ResearchTableData data) {
        // Re-check to make sure the engine is still there between initialize and now
        IResearchEngine engineTile = this.findFueledEngine(data.table.getWorld(), data.table.getPos());
        if (engineTile != null && engineTile.deductCost()) {
            data.addTotal(this.getResearchCategory(), this.getResearchAmount(player.getRNG()));
            data.bonusDraws += 1;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getInspirationCost() {
        return -1;
    }

    private IResearchEngine findFueledEngine(World world, BlockPos tablePos) {
        BlockPos.PooledMutableBlockPos pmbp = BlockPos.PooledMutableBlockPos.retain();
        for (int i = -4; i <= 4; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -4; k <= 4; k++) {
                    pmbp.setPos(tablePos.getX() + i, tablePos.getY() + j, tablePos.getZ() + k);
                    TileEntity tile = world.getTileEntity(pmbp);
                    if (tile != null && tile.getClass().equals(this.getEngineTileClass())) {
                        IResearchEngine engineTile = (IResearchEngine)tile;
                        if (engineTile.isFueled()) {
                            pmbp.release();
                            return engineTile;
                        }
                    }
                }
            }
        }
        pmbp.release();
        return null;
    }
}
