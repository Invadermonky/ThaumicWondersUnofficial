package com.verdantartifice.thaumicwonders.common.items.consumables;

import com.verdantartifice.thaumicwonders.common.items.base.ItemTW;
import com.verdantartifice.thaumicwonders.common.network.PacketHandler;
import com.verdantartifice.thaumicwonders.common.network.packets.PacketLocalizedMessage;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.common.lib.utils.RandomItemChooser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ItemLetheWater extends ItemTW {
    public ItemLetheWater() {
        super("lethe_water");
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        playerIn.setActiveHand(handIn);
        return ActionResult.newResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }

    @Override
    @Nonnull
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        if (!worldIn.isRemote && entityLiving instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) entityLiving;
            IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);

            ResearchCategory category = this.selectCategory(player, knowledge);
            if (category == null) {
                PacketHandler.INSTANCE.sendTo(new PacketLocalizedMessage("event.lethe_water.not_found"), player);
            } else {
                boolean success = knowledge.addKnowledge(IPlayerKnowledge.EnumKnowledgeType.THEORY, category, -IPlayerKnowledge.EnumKnowledgeType.THEORY.getProgression());
                if (success) {
                    player.addExperience(25);
                    PacketHandler.INSTANCE.sendTo(new PacketLocalizedMessage("event.lethe_water.forgot"), player);
                }
            }

            if (worldIn.rand.nextBoolean()) {
                worldIn.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 1F, 1F);
            }

            player.getCooldownTracker().setCooldown(this, 20);

            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
                ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
                if (!player.inventory.addItemStackToInventory(bottle)) {
                    worldIn.spawnEntity(new EntityItem(worldIn, player.posX, player.posY, player.posZ, bottle));
                }
            }
        }
        return stack;
    }

    @Override
    @Nonnull
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.DRINK;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public IRarity getForgeRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

    @Nullable
    protected ResearchCategory selectCategory(EntityPlayer player, IPlayerKnowledge knowledge) {
        List<RandomItemChooser.Item> selectionList = new ArrayList<>();
        for (ResearchCategory category : ResearchCategories.researchCategories.values()) {
            int count = knowledge.getKnowledge(IPlayerKnowledge.EnumKnowledgeType.THEORY, category);
            if (count > 0) {
                selectionList.add(new CategoryEntry(category, count));
            }
        }
        if (selectionList.isEmpty()) {
            return null;
        } else {
            RandomItemChooser ric = new RandomItemChooser();
            CategoryEntry selected = (CategoryEntry) ric.chooseOnWeight(selectionList);
            if (selected == null) {
                return null;
            } else {
                return selected.category;
            }
        }
    }

    protected static class CategoryEntry implements RandomItemChooser.Item {
        public ResearchCategory category;
        public int weight;

        protected CategoryEntry(ResearchCategory category, int weight) {
            this.category = category;
            this.weight = weight;
        }

        @Override
        public double getWeight() {
            return this.weight;
        }
    }
}
