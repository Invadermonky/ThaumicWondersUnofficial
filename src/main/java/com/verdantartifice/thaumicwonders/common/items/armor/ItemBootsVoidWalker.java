package com.verdantartifice.thaumicwonders.common.items.armor;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.config.ConfigHandlerTW;
import com.verdantartifice.thaumicwonders.common.items.ItemsTW;
import com.verdantartifice.thaumicwonders.common.misc.PlayerMovementAbilityManager;
import com.verdantartifice.thaumicwonders.common.misc.PlayerMovementAbilityManager.MovementType;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.common.crafting.CraftingHelper;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.IWarpingGear;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.api.potions.PotionFluxTaint;

import java.util.function.BiFunction;
import java.util.function.Predicate;


public class ItemBootsVoidWalker extends ItemArmor implements IWarpingGear, IRechargable {
    protected static final BiFunction<EntityPlayer, MovementType, Float> MOVEMENT_FUNC = (player, type) -> {
        float boost = 0;
        switch (type) {
            case DRY_GROUND:
                boost = (float) ConfigHandlerTW.void_walker_boots.landSpeedBoost;
                return player.isSneaking() ? boost / 4.0f : boost;
            case WATER_GROUND:
                boost = (float) Math.max(ConfigHandlerTW.void_walker_boots.landSpeedBoost / 4.0f, ConfigHandlerTW.void_walker_boots.waterSpeedBoost);
                return player.isSneaking() ? boost / 4.0f : boost;
            case WATER_SWIM:
                boost = (float) ConfigHandlerTW.void_walker_boots.waterSpeedBoost;
                return player.isSneaking() ? boost / 4.0f : boost;
            case JUMP_BEGIN:
                return (float) ConfigHandlerTW.void_walker_boots.jumpBoost;
            case JUMP_FACTOR:
                return (float) ConfigHandlerTW.void_walker_boots.jumpFactor;
            case STEP_HEIGHT:
                return !player.isSneaking() ? (float) ConfigHandlerTW.void_walker_boots.stepHeight : 0;
            default:
                return boost;
        }
    };

    protected static final Predicate<EntityPlayer> CONTINUE_FUNC = player ->
            player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() instanceof ItemBootsVoidWalker;

    public ItemBootsVoidWalker() {
        super(ItemVoidcallerArmor.MATERIAL, 4, EntityEquipmentSlot.FEET);
        this.setRegistryName(new ResourceLocation(ThaumicWonders.MODID, "void_walker_boots"));
        this.setTranslationKey(this.getRegistryName().toString());
        this.setCreativeTab(ThaumicWonders.CREATIVE_TAB);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab == this.getCreativeTab()) {
            ItemStack stack = new ItemStack(this);
            items.add(stack);
            ItemStack charged = stack.copy();
            RechargeHelper.rechargeItemBlindly(charged, null, this.getMaxCharge(charged, null));
            items.add(charged);
        }
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        if (!world.isRemote && player.ticksExisted % 20 == 0) {
            if (stack.getItemDamage() > 0)
                stack.attemptDamageItem(-1, world.rand, player instanceof EntityPlayerMP ? (EntityPlayerMP) player : null);

            int energy = this.getEnergy(stack);
            if (energy > 0) {
                --energy;
            } else if (RechargeHelper.consumeCharge(stack, player, 1)) {
                energy = ConfigHandlerTW.void_walker_boots.energyPerVis;
            }
            this.setEnergy(stack, energy);
        }
        this.handleMovement(world, player, stack);
        this.handleVoidWalk(world, player, stack);
    }

    @Override
    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return "thaumicwonders:textures/entities/armor/void_walker_armor.png";
    }

    @Override
    public IRarity getForgeRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    public void handleMovement(World world, EntityPlayer player, ItemStack stack) {
        if (player.world.isRemote) {
            boolean apply = !player.capabilities.isFlying && !player.isElytraFlying() && this.getEnergy(stack) > 0;
            if (apply && !PlayerMovementAbilityManager.playerHasAbility(player, MOVEMENT_FUNC, CONTINUE_FUNC)) {
                PlayerMovementAbilityManager.put(player, MOVEMENT_FUNC, CONTINUE_FUNC);
            } else if (!apply && PlayerMovementAbilityManager.playerHasAbility(player, MOVEMENT_FUNC, CONTINUE_FUNC)) {
                PlayerMovementAbilityManager.remove(player, MOVEMENT_FUNC, CONTINUE_FUNC);
            }
        }
    }

    public boolean handleVoidWalk(World world, EntityLivingBase player, ItemStack stack) {
        int energy = this.getEnergy(stack);
        if (energy > 0) {
            //Soul Sand speedup
            if (world.getBlockState(new BlockPos(player.posX, player.getEntityBoundingBox().minY, player.posZ)).getBlock() instanceof BlockSoulSand) {
                player.motionX /= 0.4f;
                player.motionZ /= 0.4f;
            }
        }
        return false;
    }

    public float getAdjustedFallDamage(ItemStack bootStack, float damage) {
        if (bootStack.getItem() == ItemsTW.VOID_WALKER_BOOTS && RechargeHelper.getCharge(bootStack) > 0) {
            damage = Math.max(0, damage / 2.0f - 1.0f);
        }
        return damage;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        Ingredient plate = CraftingHelper.getIngredient("plateVoid");
        Ingredient ingot = CraftingHelper.getIngredient("ingotVoid");
        return (plate != null && plate.test(repair)) || (ingot != null && ingot.test(repair)) || super.getIsRepairable(toRepair, repair);
    }

    public int getEnergy(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound().getShort("energy");
    }

    public void setEnergy(ItemStack stack, int energy) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setShort("energy", (short) energy);
    }

    @Override
    public int getWarp(ItemStack itemStack, EntityPlayer entityPlayer) {
        return 3;
    }

    @Override
    public int getMaxCharge(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return ConfigHandlerTW.void_walker_boots.visCapacity;
    }

    @Override
    public EnumChargeDisplay showInHud(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return EnumChargeDisplay.PERIODIC;
    }
}
