package com.verdantartifice.thaumicwonders.common.blocks.devices;

import com.verdantartifice.thaumicwonders.common.blocks.base.BlockDeviceTW;
import com.verdantartifice.thaumicwonders.common.tiles.devices.TileOreDiviner;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class BlockOreDiviner extends BlockDeviceTW<TileOreDiviner> {
    public static final PropertyEnum<OreDivinerPart> ORE_DIVINER_PART = PropertyEnum.create("ore_diviner_part", OreDivinerPart.class);

    public BlockOreDiviner() {
        super(Material.GLASS, TileOreDiviner.class, "ore_diviner");
        this.setSoundType(SoundType.GLASS);
        this.setDefaultState(this.getDefaultState().withProperty(ORE_DIVINER_PART, OreDivinerPart.MIDDLE));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(ORE_DIVINER_PART, meta == 0 ? OreDivinerPart.MIDDLE : OreDivinerPart.UPPER);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(ORE_DIVINER_PART) == OreDivinerPart.UPPER ? 1 : 0;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (state.getValue(ORE_DIVINER_PART) == OreDivinerPart.UPPER) {
            IBlockState down = worldIn.getBlockState(pos.down());
            if (down.getBlock() != this || down.getValue(ORE_DIVINER_PART) != OreDivinerPart.MIDDLE) {
                worldIn.setBlockToAir(pos);
            }
        } else if (state.getValue(ORE_DIVINER_PART) == OreDivinerPart.MIDDLE) {
            IBlockState up = worldIn.getBlockState(pos.up());
            if (up.getBlock() != this || up.getValue(ORE_DIVINER_PART) != OreDivinerPart.UPPER) {
                worldIn.setBlockToAir(pos);
            }
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ORE_DIVINER_PART);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (state.getValue(ORE_DIVINER_PART) == OreDivinerPart.MIDDLE) {
            IBlockState up = worldIn.getBlockState(pos.up());
            if (up.getBlock() != this || up.getValue(ORE_DIVINER_PART) == OreDivinerPart.UPPER) {
                worldIn.setBlockToAir(pos.up());
            }
        } else {
            IBlockState down = worldIn.getBlockState(pos.down());
            if (down.getBlock() != this || down.getValue(ORE_DIVINER_PART) == OreDivinerPart.MIDDLE) {
                worldIn.setBlockToAir(pos.down());
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return state.getValue(ORE_DIVINER_PART) == OreDivinerPart.MIDDLE;
    }

    @Override
    public boolean isTopSolid(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return state.getValue(ORE_DIVINER_PART) == OreDivinerPart.MIDDLE ? EnumBlockRenderType.MODEL : EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (state.getValue(ORE_DIVINER_PART)) {
            case UPPER:
                return OreDivinerPart.UPPER.getBlockAABB();
            default:
                return OreDivinerPart.MIDDLE.getBlockAABB();
        }
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return state.getValue(ORE_DIVINER_PART) == OreDivinerPart.MIDDLE ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        Arrays.stream(OreDivinerPart.values()).forEach(part -> addCollisionBoxToList(pos, entityBox, collidingBoxes, part.getBlockAABB()));
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.SOLID;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.up()).getBlock().isReplaceable(worldIn, pos.up()) && !worldIn.isOutsideBuildHeight(pos.up());
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (state.getValue(ORE_DIVINER_PART) == OreDivinerPart.UPPER) {
            pos = pos.down();
        }

        ItemStack stack = playerIn.getHeldItem(hand);
        TileEntity tile = worldIn.getTileEntity(pos);
        if (!worldIn.isRemote && tile instanceof TileOreDiviner) {
            if (playerIn.isSneaking()) {
                ((TileOreDiviner) tile).stopSearching();
            } else {
                ((TileOreDiviner) tile).onInteract(playerIn, stack);
            }
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos.up(), this.getDefaultState().withProperty(ORE_DIVINER_PART, OreDivinerPart.UPPER));
    }

    @Override
    public @Nullable TileEntity createTileEntity(World world, IBlockState state) {
        return new TileOreDiviner();
    }

    @Override
    public boolean shouldCheckWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    public enum OreDivinerPart implements IStringSerializable {
        UPPER(new AxisAlignedBB(0.25, 0, 0.25, 0.75, 0.625, 0.75)),
        MIDDLE(new AxisAlignedBB(0, 0, 0, 1.0, 1.0, 1.0)),
        BOTTOM(new AxisAlignedBB(0.25, -0.875, 0.25, 0.75, 0, 0.75));

        private final AxisAlignedBB blockAABB;

        OreDivinerPart(AxisAlignedBB blockAABB) {
            this.blockAABB = blockAABB;
        }

        public AxisAlignedBB getBlockAABB() {
            return this.blockAABB;
        }

        @Override
        public String getName() {
            return this.name().toLowerCase();
        }
    }
}
