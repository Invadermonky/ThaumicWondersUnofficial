package com.verdantartifice.thaumicwonders.common.items.armor;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CustomArmorHelper {
    @SideOnly(Side.CLIENT)
    public static ModelBiped getCustomArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped model) {
        if (model != null) {
            model.bipedHead.showModel = (armorSlot == EntityEquipmentSlot.HEAD);
            model.bipedHeadwear.showModel = (armorSlot == EntityEquipmentSlot.HEAD);
            model.bipedBody.showModel = (armorSlot == EntityEquipmentSlot.CHEST || armorSlot == EntityEquipmentSlot.LEGS);
            model.bipedRightArm.showModel = (armorSlot == EntityEquipmentSlot.CHEST);
            model.bipedLeftArm.showModel = (armorSlot == EntityEquipmentSlot.CHEST);
            model.bipedRightLeg.showModel = (armorSlot == EntityEquipmentSlot.LEGS);
            model.bipedLeftLeg.showModel = (armorSlot == EntityEquipmentSlot.LEGS);

            model.isSneak = entityLiving.isSneaking();
            model.isRiding = entityLiving.isRiding();
            model.isChild = entityLiving.isChild();

            ItemStack stackMain = entityLiving.getHeldItemMainhand();
            ModelBiped.ArmPose armPoseMain = ModelBiped.ArmPose.EMPTY;
            if (stackMain != null && !stackMain.isEmpty()) {
                armPoseMain = ModelBiped.ArmPose.ITEM;
                if (entityLiving.getItemInUseCount() > 0) {
                    EnumAction actionMain = stackMain.getItemUseAction();
                    if (actionMain == EnumAction.BLOCK) {
                        armPoseMain = ModelBiped.ArmPose.BLOCK;
                    } else if (actionMain == EnumAction.BOW) {
                        armPoseMain = ModelBiped.ArmPose.BOW_AND_ARROW;
                    }
                }
            }

            ItemStack stackOff = entityLiving.getHeldItemOffhand();
            ModelBiped.ArmPose armPoseOff = ModelBiped.ArmPose.EMPTY;
            if (stackOff != null && !stackOff.isEmpty()) {
                armPoseOff = ModelBiped.ArmPose.ITEM;
                if (entityLiving.getItemInUseCount() > 0) {
                    EnumAction actionOff = stackOff.getItemUseAction();
                    if (actionOff == EnumAction.BLOCK) {
                        armPoseOff = ModelBiped.ArmPose.BLOCK;
                    }
                }
            }

            model.rightArmPose = (entityLiving.getPrimaryHand() == EnumHandSide.RIGHT) ? armPoseMain : armPoseOff;
            model.leftArmPose = (entityLiving.getPrimaryHand() == EnumHandSide.RIGHT) ? armPoseOff : armPoseMain;
        }
        return model;
    }
}
