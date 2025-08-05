package com.verdantartifice.thaumicwonders.core.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.verdantartifice.thaumicwonders.common.golems.seals.SealResearchAssistant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thaumcraft.api.research.theorycraft.TheorycraftCard;
import thaumcraft.common.container.ContainerResearchTable;
import thaumcraft.common.golems.seals.SealEntity;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.tiles.crafting.TileResearchTable;

@Mixin(value = ContainerResearchTable.class, remap = false)
public class ContainerResearchTableMixin {
    @Shadow
    public TileResearchTable tileEntity;

    @Inject(
            method = "enchantItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lthaumcraft/api/research/theorycraft/TheorycraftCard;getRequiredItems()[Lnet/minecraft/item/ItemStack;",
                    ordinal = 0,
                    shift = At.Shift.AFTER,
                    remap = false
            ),
            remap = true
    )
    private void requestResearchItems(EntityPlayer playerIn, int button, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 0) TheorycraftCard card) {
        if (card.getRequiredItems() != null && card.getRequiredItems().length > 0) {
            for (SealEntity seal : SealHandler.getSealsInRange(this.tileEntity.getWorld(), this.tileEntity.getPos(), 5)) {
                if (seal.getSeal() instanceof SealResearchAssistant) {
                    ((SealResearchAssistant) seal.getSeal()).requestResearchMaterials(seal, playerIn, NonNullList.from(ItemStack.EMPTY, card.getRequiredItems()));
                }
            }
        }
    }
}
