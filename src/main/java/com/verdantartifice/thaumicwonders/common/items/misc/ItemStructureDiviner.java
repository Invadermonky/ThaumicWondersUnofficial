package com.verdantartifice.thaumicwonders.common.items.misc;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.items.base.ItemTW;
import com.verdantartifice.thaumicwonders.common.misc.GuiIds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemStructureDiviner extends ItemTW {
    public ItemStructureDiviner() {
        super("structure_diviner");
        this.setMaxStackSize(1);
        this.setNoRepair();

        this.addPropertyOverride(new ResourceLocation("angle"), new IItemPropertyGetter() {
            @SideOnly(Side.CLIENT)
            double rotation;
            @SideOnly(Side.CLIENT)
            double rota;
            @SideOnly(Side.CLIENT)
            long lastUpdateTick;

            @SideOnly(Side.CLIENT)
            @Override
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
                if (entityIn == null && !stack.isOnItemFrame()) {
                    return 0.0F;
                } else {
                    boolean held = (entityIn != null);
                    Entity entity = (held ? entityIn : stack.getItemFrame());
                    if (worldIn == null) {
                        worldIn = entity.getEntityWorld();
                    }

                    double angle;
                    BlockPos targetPoint = this.getTargetPoint(stack);
                    if (targetPoint != null) {
                        double entityYaw = held ? (double) entity.rotationYaw : this.getFrameRotation(stack.getItemFrame());
                        entityYaw = MathHelper.positiveModulo(entityYaw / 360.0D, 1.0D);
                        angle = 0.5D - (entityYaw - 0.25D - this.getTargetPointToAngle(targetPoint, entity));
                    } else {
                        angle = Math.random();
                    }

                    if (held) {
                        angle = this.wobble(worldIn, angle);
                    }
                    return MathHelper.positiveModulo((float) angle, 1.0F);
                }
            }

            @SideOnly(Side.CLIENT)
            private double wobble(World worldIn, double angle) {
                if (worldIn.getTotalWorldTime() != this.lastUpdateTick) {
                    this.lastUpdateTick = worldIn.getTotalWorldTime();
                    double d0 = angle - this.rotation;
                    d0 = MathHelper.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
                    this.rota += d0 * 0.1D;
                    this.rota *= 0.8D;
                    this.rotation = MathHelper.positiveModulo(this.rotation + this.rota, 1.0D);
                }
                return this.rotation;
            }

            @SideOnly(Side.CLIENT)
            private double getFrameRotation(EntityItemFrame frame) {
                return MathHelper.wrapDegrees(180 + frame.facingDirection.getHorizontalIndex() * 90);
            }

            @SideOnly(Side.CLIENT)
            @Nullable
            private BlockPos getTargetPoint(ItemStack stack) {
                if (stack != null && !stack.isEmpty() && stack.hasTagCompound() && stack.getTagCompound().hasKey("targetPoint")) {
                    return BlockPos.fromLong(stack.getTagCompound().getLong("targetPoint"));
                } else {
                    return null;
                }
            }

            @SideOnly(Side.CLIENT)
            private double getTargetPointToAngle(@Nonnull BlockPos targetPoint, @Nonnull Entity entity) {
                return Math.atan2((double) targetPoint.getZ() - entity.posZ, (double) targetPoint.getX() - entity.posX) / (Math.PI * 2.0D);
            }
        });
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        playerIn.openGui(ThaumicWonders.INSTANCE, GuiIds.STRUCTURE_DIVINER, worldIn, 0, 0, 0);
        return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }

    @Override
    public IRarity getForgeRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }
}
