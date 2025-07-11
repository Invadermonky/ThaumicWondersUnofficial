package com.verdantartifice.thaumicwonders.common.blocks.misc;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.blocks.BlocksTW;
import com.verdantartifice.thaumicwonders.common.blocks.base.BlockTW;
import com.verdantartifice.thaumicwonders.common.blocks.devices.BlockCatalyzationChamber;
import com.verdantartifice.thaumicwonders.common.misc.GuiIds;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.blocks.BlocksTC;

import java.util.Random;

public class BlockTWPlaceholder extends BlockTW {
    public BlockTWPlaceholder(String name) {
        super(Material.ROCK, name);
        this.setHardness(2.5F);
        this.setSoundType(SoundType.STONE);
        this.setCreativeTab(null);
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if ((state.getBlock() == BlocksTW.PLACEHOLDER_ARCANE_STONE || state.getBlock() == BlocksTW.PLACEHOLDER_OBSIDIAN) && !BlockCatalyzationChamber.ignoreDestroy && !worldIn.isRemote) {
            this.destroyCatalyzer(worldIn, pos);
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        if (state.getBlock() == BlocksTW.PLACEHOLDER_ARCANE_STONE) {
            return Item.getItemFromBlock(BlocksTC.stoneArcane);
        } else if (state.getBlock() == BlocksTW.PLACEHOLDER_OBSIDIAN) {
            return Item.getItemFromBlock(Blocks.OBSIDIAN);
        } else {
            return Item.getItemById(0);
        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return true;
        }
        if (state.getBlock() == BlocksTW.PLACEHOLDER_ARCANE_STONE || state.getBlock() == BlocksTW.PLACEHOLDER_OBSIDIAN) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    for (int k = -1; k <= 1; k++) {
                        BlockPos targetPos = pos.add(i, j, k);
                        IBlockState targetState = worldIn.getBlockState(targetPos);
                        if (targetState.getBlock() == BlocksTW.CATALYZATION_CHAMBER) {
                            playerIn.openGui(ThaumicWonders.INSTANCE, GuiIds.CATALYZATION_CHAMBER, worldIn, targetPos.getX(), targetPos.getY(), targetPos.getZ());
                            return true;
                        }
                    }
                }
            }
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    protected boolean canSilkHarvest() {
        return false;
    }

    @Override
    public EnumPushReaction getPushReaction(IBlockState state) {
        return EnumPushReaction.BLOCK;
    }

    private void destroyCatalyzer(World worldIn, BlockPos pos) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    BlockPos targetPos = pos.add(i, j, k);
                    IBlockState targetState = worldIn.getBlockState(targetPos);
                    if (targetState.getBlock() == BlocksTW.CATALYZATION_CHAMBER) {
                        BlockCatalyzationChamber.destroyChamber(worldIn, targetPos, targetState, pos);
                        return;
                    }
                }
            }
        }
    }
}
