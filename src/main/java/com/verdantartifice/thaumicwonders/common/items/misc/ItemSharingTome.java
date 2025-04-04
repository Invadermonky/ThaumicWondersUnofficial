package com.verdantartifice.thaumicwonders.common.items.misc;

import com.verdantartifice.thaumicwonders.common.config.ConfigHandlerTW;
import com.verdantartifice.thaumicwonders.common.items.base.ItemTW;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.common.lib.CommandThaumcraft;
import thaumcraft.common.lib.SoundsTC;

import java.util.*;

import static thaumcraft.api.capabilities.IPlayerKnowledge.EnumKnowledgeType;

public class ItemSharingTome extends ItemTW {
    private static final String TAG_PLAYER = "player";
    private static final String TAG_RESEARCH = "research";
    private static final String TAG_OBSERVATION = "observations";
    private static final String NON_ASSIGNED = "[none]";

    public ItemSharingTome() {
        super("sharing_tome");
        setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        String message = "item.thaumicwonders.sharing_tome.chat.";
        knowledge:
        if (!player.isSneaking()) {
            String name = this.getPlayerName(stack);
            if (name.equals(NON_ASSIGNED)) {
                worldIn.playSound(player, player.getPosition(), SoundsTC.write, SoundCategory.PLAYERS, 1.0f, 1.0f);
                message += "write";
                this.setPlayerName(stack, player.getDisplayNameString());
                this.setPlayerObservations(stack, player);
                this.setPlayerResearch(stack, player);
            } else if (name.equals(player.getDisplayNameString())) {
                message += "owned";
            } else {
                IPlayerKnowledge known = ThaumcraftCapabilities.getKnowledge(player);
                boolean learnedAnything = false;

                if (player.experienceLevel < ConfigHandlerTW.sharing_tome.requiredExp) {
                    message += "fail";
                    break knowledge;
                }

                for (String key : this.getPlayerResearch(stack)) {
                    ResearchEntry re = ResearchCategories.getResearch(key);
                    if (re != null && re.getStages() != null) {
                        if (!known.isResearchComplete(key))
                            learnedAnything = true;
                        CommandThaumcraft.giveRecursiveResearch(player, key);
                    }
                }
                if (!worldIn.isRemote) {
                    ThaumcraftCapabilities.getKnowledge(player).sync((EntityPlayerMP) player);
                }

                this.getPlayerObservations(stack).forEach((category, amount) -> {
                    int current = ThaumcraftCapabilities.getKnowledge(player).getKnowledgeRaw(EnumKnowledgeType.OBSERVATION, ResearchCategories.getResearchCategory(category));
                    int toAdd = Math.abs(amount - current);
                    ThaumcraftCapabilities.getKnowledge(player).addKnowledge(EnumKnowledgeType.OBSERVATION, ResearchCategories.getResearchCategory(category), toAdd);
                });

                if (!learnedAnything) {
                    message += "owned";
                } else {
                    player.addExperience(-this.getRequiredExperience());
                    worldIn.playSound(player, player.getPosition(), SoundsTC.write, SoundCategory.PLAYERS, 1.0f, 1.0f);
                    if (ConfigHandlerTW.sharing_tome.consumeTome) {
                        message += "burn";
                        stack.shrink(1);
                        for (int i = 0; i < 2; i++) {
                            Vec3d vec3d = new Vec3d(((double) worldIn.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
                            vec3d = vec3d.rotatePitch(-player.rotationPitch * 0.017453292F);
                            vec3d = vec3d.rotateYaw(-player.rotationYaw * 0.017453292F);
                            double d0 = (double) (-worldIn.rand.nextFloat()) * 0.6D - 0.3D;
                            Vec3d vec3d1 = new Vec3d(((double) worldIn.rand.nextFloat() - 0.5D) * 0.3D, d0, 0.6D);
                            vec3d1 = vec3d1.rotatePitch(-player.rotationPitch * 0.017453292F);
                            vec3d1 = vec3d1.rotateYaw(-player.rotationYaw * 0.017453292F);
                            vec3d1 = vec3d1.add(player.posX, player.posY + (double) player.getEyeHeight(), player.posZ);
                            if (worldIn instanceof WorldServer)
                                ((WorldServer) worldIn).spawnParticle(EnumParticleTypes.FLAME, vec3d1.x, vec3d1.y, vec3d1.z, 0, vec3d.x, vec3d.y + 0.05, vec3d.z, Item.getIdFromItem(stack.getItem()), stack.getMetadata());
                            else
                                worldIn.spawnParticle(EnumParticleTypes.FLAME, vec3d1.x, vec3d1.y, vec3d1.z, 0, 0, 0, Item.getIdFromItem(stack.getItem()), stack.getMetadata());
                        }
                    } else {
                        message += "read";
                    }
                }
            }
        } else {
            worldIn.playSound(player, player.getPosition(), SoundsTC.write, SoundCategory.PLAYERS, 1.0f, 1.0f);
            message += "erase";
            this.clearPlayerResearch(stack);
            this.setPlayerName(stack, NON_ASSIGNED);
        }

        if (!worldIn.isRemote)
            player.sendMessage(new TextComponentTranslation(message));

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        String name = this.getPlayerName(stack);
        if (name.equals(NON_ASSIGNED)) {
            tooltip.add(I18n.format("item.thaumicwonders.sharing_tome.tooltip.blank"));
        } else {
            tooltip.add(I18n.format("item.thaumicwonders.sharing_tome.tooltip.player", name));
        }
    }

    protected String getPlayerName(ItemStack stack) {
        return this.getItemTag(stack).hasKey(TAG_PLAYER) ? stack.getTagCompound().getString(TAG_PLAYER) : NON_ASSIGNED;
    }

    protected void setPlayerName(ItemStack stack, String playerName) {
        this.getItemTag(stack).setString(TAG_PLAYER, playerName);
    }

    protected Map<String, Integer> getPlayerObservations(ItemStack stack) {
        Map<String, Integer> observations = new HashMap<>();
        NBTTagCompound tag = this.getItemTag(stack);
        if (!tag.hasKey(TAG_OBSERVATION) || !ConfigHandlerTW.sharing_tome.shareObservations)
            return observations;
        NBTTagList list = tag.getTagList(TAG_OBSERVATION, Constants.NBT.TAG_STRING);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound listTag = list.getCompoundTagAt(i);
            observations.put(listTag.getString("category"), listTag.getInteger("amount"));
        }
        return observations;
    }

    protected void setPlayerObservations(ItemStack stack, EntityPlayer player) {
        NBTTagCompound tag = this.getItemTag(stack);
        NBTTagList list = new NBTTagList();
        ResearchCategories.researchCategories.values().forEach(category -> {
            int observation = ThaumcraftCapabilities.getKnowledge(player).getKnowledgeRaw(EnumKnowledgeType.OBSERVATION, category);
            NBTTagCompound observationTag = new NBTTagCompound();
            observationTag.setString("category", category.key);
            observationTag.setInteger("amount", observation);
            list.appendTag(observationTag);
        });
        tag.setTag(TAG_OBSERVATION, list);
    }

    protected Set<String> getPlayerResearch(ItemStack stack) {
        Set<String> research = new HashSet<>();
        NBTTagCompound tag = this.getItemTag(stack);
        if (!tag.hasKey(TAG_RESEARCH))
            return research;
        NBTTagList list = tag.getTagList(TAG_RESEARCH, Constants.NBT.TAG_STRING);
        for (int i = 0; i < list.tagCount(); i++) {
            research.add(list.getStringTagAt(i));
        }
        return research;
    }

    protected void setPlayerResearch(ItemStack stack, EntityPlayer player) {
        Set<String> researchDone = ThaumcraftCapabilities.getKnowledge(player).getResearchList();
        NBTTagCompound itemTag = this.getItemTag(stack);
        NBTTagList list = new NBTTagList();
        for (String research : researchDone) {
            if (ThaumcraftCapabilities.getKnowledge(player).isResearchComplete(research))
                list.appendTag(new NBTTagString(research));
        }
        itemTag.setTag(TAG_RESEARCH, list);
    }

    protected void clearPlayerResearch(ItemStack stack) {
        NBTTagCompound itemTag = this.getItemTag(stack);
        itemTag.removeTag(TAG_OBSERVATION);
        itemTag.removeTag(TAG_RESEARCH);
        stack.setTagCompound(itemTag);
    }

    protected NBTTagCompound getItemTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }

    protected int getRequiredExperience() {
        int levels = ConfigHandlerTW.sharing_tome.requiredExp;
        int xp = 0;
        for (int i = 0; i < levels; i++) {
            if (i < 16) {
                xp += (int) Math.pow(i, 2) + 6 * i;
            } else if (i < 32) {
                xp += (int) (2.5 * Math.pow(i, 2) - 40.5 * i + 360);
            } else {
                xp += (int) (4.5 * Math.pow(i, 2) - 162.5 * i + 2220);
            }
        }
        return xp;
    }
}
