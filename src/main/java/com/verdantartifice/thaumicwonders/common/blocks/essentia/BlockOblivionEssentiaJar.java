package com.verdantartifice.thaumicwonders.common.blocks.essentia;

import com.verdantartifice.thaumicwonders.common.blocks.base.BlockTileTW;
import com.verdantartifice.thaumicwonders.common.tiles.essentia.TileOblivionEssentiaJar;
import com.verdantartifice.thaumicwonders.common.utils.StringHelper;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.blocks.ILabelable;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.blocks.essentia.BlockJarItem;
import thaumcraft.common.lib.SoundsTC;

import java.util.List;

public class BlockOblivionEssentiaJar extends BlockTileTW<TileOblivionEssentiaJar> implements ILabelable {
    private static final int CAPACITY = 250;

    public BlockOblivionEssentiaJar() {
        super(Material.GLASS, TileOblivionEssentiaJar.class, "oblivion_essentia_jar");
        this.setHardness(0.3f);
        this.setSoundType(SoundsTC.JAR);
    }

    @SuppressWarnings("deprecation")
    @Override
    public SoundType getSoundType() {
        return SoundsTC.JAR;
    }

    @SuppressWarnings("deprecation")
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.75D, 0.8125D);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileOblivionEssentiaJar) {
            TileOblivionEssentiaJar tileEntity = (TileOblivionEssentiaJar) te;
            if (!tileEntity.blocked && playerIn.getHeldItem(hand).getItem() == ItemsTC.jarBrace) {
                // Apply the jar brace
                tileEntity.blocked = true;
                playerIn.getHeldItem(hand).shrink(1);
                if (worldIn.isRemote) {
                    worldIn.playSound(null, pos, SoundsTC.key, SoundCategory.BLOCKS, 1.0F, 1.0F);
                } else {
                    tileEntity.markDirty();
                }
            } else if (playerIn.isSneaking() && tileEntity.aspectFilter != null && facing.ordinal() == tileEntity.facing) {
                // Remove the jar label
                tileEntity.aspectFilter = null;
                if (worldIn.isRemote) {
                    worldIn.playSound(null, pos, SoundsTC.page, SoundCategory.BLOCKS, 1.0F, 1.0F);
                } else {
                    worldIn.spawnEntity(new EntityItem(worldIn, pos.getX() + 0.5F + facing.getXOffset() / 3.0F, pos.getY() + 0.5F, pos.getZ() + 0.5F + facing.getZOffset() / 3.0F, new ItemStack(ItemsTC.label)));
                }
            }
        }
        return true;
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof TileOblivionEssentiaJar) {
            this.spawnFilledJar(worldIn, pos, state, (TileOblivionEssentiaJar) tileEntity);
        } else {
            super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
        }
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {
        if (te instanceof TileOblivionEssentiaJar) {
            this.spawnFilledJar(worldIn, pos, state, (TileOblivionEssentiaJar) te);
        } else {
            super.harvestBlock(worldIn, player, pos, state, te, stack);
        }
    }

    private void spawnFilledJar(World world, BlockPos pos, IBlockState state, TileOblivionEssentiaJar te) {
        ItemStack drop = new ItemStack(this, 1, this.getMetaFromState(state));
        if (te.amount > 0) {
            ((BlockJarItem) drop.getItem()).setAspects(drop, new AspectList().add(te.aspect, te.amount));
        }
        if (te.aspectFilter != null) {
            if (!drop.hasTagCompound()) {
                drop.setTagCompound(new NBTTagCompound());
            }
            drop.getTagCompound().setString("AspectFilter", te.aspectFilter.getTag());
        }
        if (te.blocked) {
            Block.spawnAsEntity(world, pos, new ItemStack(ItemsTC.jarBrace));
        }
        Block.spawnAsEntity(world, pos, drop);
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getStateFromMeta(meta);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        int l = MathHelper.floor(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 0x3;
        TileEntity tile = worldIn.getTileEntity(pos);
        if ((tile instanceof TileOblivionEssentiaJar)) {
            switch (l) {
                case 0:
                    ((TileOblivionEssentiaJar) tile).facing = 2;
                    break;
                case 1:
                    ((TileOblivionEssentiaJar) tile).facing = 5;
                    break;
                case 2:
                    ((TileOblivionEssentiaJar) tile).facing = 3;
                    break;
                case 3:
                    ((TileOblivionEssentiaJar) tile).facing = 4;
                    break;
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(StringHelper.getLocalizedString("oblivion_essentia_jar", "tooltip", "desc"));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean applyLabel(EntityPlayer player, BlockPos pos, EnumFacing side, ItemStack labelStack) {
        ItemStack ohStack = player.getHeldItemOffhand();
        TileEntity tile = player.world.getTileEntity(pos);
        if (tile instanceof TileOblivionEssentiaJar && !ohStack.isEmpty() && ohStack != labelStack) {
            TileOblivionEssentiaJar jar = (TileOblivionEssentiaJar) tile;
            if (labelStack.getItem() instanceof IEssentiaContainerItem) {
                IEssentiaContainerItem labelItem = (IEssentiaContainerItem) labelStack.getItem();
                Aspect ohAspect = this.getEssentiaContainerAspect(ohStack);
                if(ohAspect != null) {
                    jar.aspectFilter = ohAspect;
                    player.world.markAndNotifyBlock(pos, player.world.getChunk(pos), player.world.getBlockState(pos), player.world.getBlockState(pos), 0x3);
                    jar.markDirty();
                    player.world.playSound(null, pos, SoundsTC.jar, SoundCategory.BLOCKS, 0.4F, 1.0F);
                    return true;
                }
            }
        }
        return false;
    }

    @Nullable
    private Aspect getEssentiaContainerAspect(ItemStack stack) {
        if(stack.getItem() instanceof IEssentiaContainerItem) {
            AspectList aspectList = ((IEssentiaContainerItem) stack.getItem()).getAspects(stack);
            if(aspectList != null && aspectList.size() > 0) {
                return aspectList.getAspects()[0];
            }
        }
        return null;
    }
}
