package com.verdantartifice.thaumicwonders.common.blocks.essentia;

import com.verdantartifice.thaumicwonders.common.blocks.base.BlockTileTW;
import com.verdantartifice.thaumicwonders.common.tiles.essentia.TileCreativeEssentiaJar;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
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
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.blocks.ILabelable;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.items.consumables.ItemPhial;
import thaumcraft.common.lib.SoundsTC;

public class BlockCreativeEssentiaJar extends BlockTileTW<TileCreativeEssentiaJar> implements ILabelable {
    private static final int CAPACITY = 250;

    public BlockCreativeEssentiaJar() {
        super(Material.GLASS, TileCreativeEssentiaJar.class, "creative_essentia_jar");
        setHardness(0.3F);
        setSoundType(SoundsTC.JAR);
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.75D, 0.8125D);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof TileCreativeEssentiaJar) {
            this.spawnFilledJar(worldIn, pos, state, (TileCreativeEssentiaJar) tileEntity);
        } else {
            super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileCreativeEssentiaJar) {
            TileCreativeEssentiaJar tileEntity = (TileCreativeEssentiaJar) te;
            if (!playerIn.getHeldItem(hand).isEmpty() && playerIn.getHeldItem(hand).getItem() == ItemsTC.phial) {
                ItemPhial itemPhial = (ItemPhial) ItemsTC.phial;
                if (playerIn.getHeldItem(hand).getItemDamage() == 0 && tileEntity.amount >= 10) {
                    // Fill the phial from the jar
                    if (!worldIn.isRemote && tileEntity.aspect != null && tileEntity.takeFromContainer(tileEntity.aspect, 10)) {
                        if (!playerIn.isCreative())
                            playerIn.getHeldItem(hand).shrink(1);
                        ItemStack newPhialStack = new ItemStack(itemPhial, 1, 1);
                        itemPhial.setAspects(newPhialStack, new AspectList().add(tileEntity.aspect, 10));
                        if (!playerIn.inventory.addItemStackToInventory(newPhialStack)) {
                            worldIn.spawnEntity(new EntityItem(worldIn, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, newPhialStack));
                        }
                        worldIn.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.PLAYERS, 0.5F, 1.0F);
                        playerIn.inventoryContainer.detectAndSendChanges();
                    }
                } else {
                    // Fill the jar from the phial
                    AspectList phialAspects = itemPhial.getAspects(playerIn.getHeldItem(hand));
                    if (phialAspects != null && phialAspects.size() == 1) {
                        Aspect aspect = phialAspects.getAspects()[0];
                        if (!worldIn.isRemote && playerIn.getHeldItem(hand).getItemDamage() != 0 && tileEntity.amount <= (CAPACITY - 10) && tileEntity.doesContainerAccept(aspect) && tileEntity.addToContainer(aspect, 10) == 0) {
                            worldIn.markAndNotifyBlock(pos, worldIn.getChunk(pos), state, state, 0x3);
                            tileEntity.syncTile(true);
                            if (!playerIn.isCreative()) {
                                playerIn.getHeldItem(hand).shrink(1);
                                ItemStack newPhialStack = new ItemStack(itemPhial, 1, 0);
                                if (!playerIn.inventory.addItemStackToInventory(newPhialStack)) {
                                    worldIn.spawnEntity(new EntityItem(worldIn, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, newPhialStack));
                                }
                            }
                            worldIn.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.PLAYERS, 0.5F, 1.0F);
                            playerIn.inventoryContainer.detectAndSendChanges();
                        }
                    }
                }
            } else if (!tileEntity.blocked && playerIn.getHeldItem(hand).getItem() == ItemsTC.jarBrace) {
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
            } else if (playerIn.isSneaking() && playerIn.getHeldItem(hand).isEmpty()) {
                // Dump the jar
                if (tileEntity.aspectFilter == null) {
                    tileEntity.aspect = null;
                }
                if (worldIn.isRemote) {
                    worldIn.playSound(null, pos, SoundsTC.jar, SoundCategory.BLOCKS, 0.4F, 1.0F);
                    worldIn.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 0.5F, 1.0F + (2F * worldIn.rand.nextFloat() - 1.0F) * 0.3F);
                }
                tileEntity.amount = 0;
                tileEntity.markDirty();
            }
        }
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getStateFromMeta(meta);
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {
        if (te instanceof TileCreativeEssentiaJar) {
            this.spawnFilledJar(worldIn, pos, state, (TileCreativeEssentiaJar) te);
        } else {
            super.harvestBlock(worldIn, player, pos, state, te, stack);
        }
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        int l = MathHelper.floor(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 0x3;
        TileEntity tile = worldIn.getTileEntity(pos);
        if ((tile instanceof TileCreativeEssentiaJar)) {
            switch (l) {
                case 0:
                    ((TileCreativeEssentiaJar) tile).facing = 2;
                    break;
                case 1:
                    ((TileCreativeEssentiaJar) tile).facing = 5;
                    break;
                case 2:
                    ((TileCreativeEssentiaJar) tile).facing = 3;
                    break;
                case 3:
                    ((TileCreativeEssentiaJar) tile).facing = 4;
                    break;
            }
        }
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileCreativeEssentiaJar) {
            TileCreativeEssentiaJar tileEntity = (TileCreativeEssentiaJar) te;
            float ratio = tileEntity.amount / (float) CAPACITY;
            return MathHelper.floor(ratio * 14.0F) + (tileEntity.amount > 0 ? 1 : 0);
        } else {
            return super.getComparatorInputOverride(blockState, worldIn, pos);
        }
    }

    @Override
    public SoundType getSoundType() {
        return SoundsTC.JAR;
    }

    private void spawnFilledJar(World world, BlockPos pos, IBlockState state, TileCreativeEssentiaJar te) {
        ItemStack drop = new ItemStack(this, 1, this.getMetaFromState(state));
        if (te.amount > 0) {
            ((ItemBlockCreativeEssentiaJar) drop.getItem()).setAspects(drop, new AspectList().add(te.aspect, te.amount));
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

    @Override
    public boolean applyLabel(EntityPlayer player, BlockPos pos, EnumFacing side, ItemStack labelStack) {
        TileEntity te = player.world.getTileEntity(pos);
        if (te instanceof TileCreativeEssentiaJar) {
            TileCreativeEssentiaJar tileEntity = (TileCreativeEssentiaJar) te;
            if (labelStack.getItem() instanceof IEssentiaContainerItem) {
                IEssentiaContainerItem labelItem = (IEssentiaContainerItem) labelStack.getItem();
                if (tileEntity.aspectFilter == null) {
                    if (tileEntity.amount == 0 && labelItem.getAspects(labelStack) == null) {
                        return false;
                    }
                    if (tileEntity.amount == 0 && labelItem.getAspects(labelStack) != null) {
                        tileEntity.aspect = labelItem.getAspects(labelStack).getAspects()[0];
                    }
                    this.onBlockPlacedBy(player.world, pos, player.world.getBlockState(pos), player, null);
                    tileEntity.aspectFilter = tileEntity.aspect;
                    player.world.markAndNotifyBlock(pos, player.world.getChunk(pos), player.world.getBlockState(pos), player.world.getBlockState(pos), 0x3);
                    tileEntity.markDirty();
                    player.world.playSound(null, pos, SoundsTC.jar, SoundCategory.BLOCKS, 0.4F, 1.0F);
                    return true;
                }
            }
        }
        return false;
    }
}
