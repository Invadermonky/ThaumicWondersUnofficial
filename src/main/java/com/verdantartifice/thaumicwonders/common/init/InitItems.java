package com.verdantartifice.thaumicwonders.common.init;

import com.verdantartifice.thaumicwonders.common.compat.ModIds;
import com.verdantartifice.thaumicwonders.common.config.ConfigHandlerTW;
import com.verdantartifice.thaumicwonders.common.golems.seals.SealResearchAssistant;
import com.verdantartifice.thaumicwonders.common.golems.seals.SealShearing;
import com.verdantartifice.thaumicwonders.common.golems.seals.SealShearingAdvanced;
import com.verdantartifice.thaumicwonders.common.items.armor.ItemBootsVoidWalker;
import com.verdantartifice.thaumicwonders.common.items.armor.ItemNightVisionGoggles;
import com.verdantartifice.thaumicwonders.common.items.armor.ItemVoidFortressArmor;
import com.verdantartifice.thaumicwonders.common.items.armor.ItemVoidcallerArmor;
import com.verdantartifice.thaumicwonders.common.items.base.IVariantItem;
import com.verdantartifice.thaumicwonders.common.items.base.ItemTW;
import com.verdantartifice.thaumicwonders.common.items.baubles.ItemCleansingCharm;
import com.verdantartifice.thaumicwonders.common.items.baubles.ItemWarpRing;
import com.verdantartifice.thaumicwonders.common.items.catalysts.ItemAlchemistStone;
import com.verdantartifice.thaumicwonders.common.items.catalysts.ItemAlienistStone;
import com.verdantartifice.thaumicwonders.common.items.catalysts.ItemTransmuterStone;
import com.verdantartifice.thaumicwonders.common.items.consumables.ItemLetheWater;
import com.verdantartifice.thaumicwonders.common.items.consumables.ItemPanacea;
import com.verdantartifice.thaumicwonders.common.items.consumables.ItemPrimalArrow;
import com.verdantartifice.thaumicwonders.common.items.entities.ItemFlyingCarpet;
import com.verdantartifice.thaumicwonders.common.items.misc.*;
import com.verdantartifice.thaumicwonders.common.items.plants.ItemCinderpearlSeed;
import com.verdantartifice.thaumicwonders.common.items.plants.ItemShimmerleafSeed;
import com.verdantartifice.thaumicwonders.common.items.plants.ItemVishroomSpore;
import com.verdantartifice.thaumicwonders.common.items.tools.ItemBoneBow;
import com.verdantartifice.thaumicwonders.common.items.tools.ItemPrimalDestroyer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;
import thaumcraft.api.golems.GolemHelper;

import java.util.HashSet;
import java.util.Set;

public class InitItems {
    public static final Set<Item> ITEMS = new HashSet<>();
    public static final Set<IVariantItem> ITEM_VARIANT_HOLDERS = new HashSet<>();

    public static void initItems(IForgeRegistry<Item> forgeRegistry) {
        registerItem(forgeRegistry, new ItemBoneBow());
        registerItem(forgeRegistry, new ItemPrimalArrow(), !ModIds.new_crimson_revelations.isLoaded);
        registerItem(forgeRegistry, new ItemPrimalDestroyer());
        registerItem(forgeRegistry, new ItemFlyingCarpet());
        registerItem(forgeRegistry, new ItemDisjunctionCloth());
        registerItem(forgeRegistry, new ItemPortalLinker());
        registerItem(forgeRegistry, new ItemStructureDiviner());
        registerItem(forgeRegistry, new ItemTimewinder());
        registerItem(forgeRegistry, new ItemShimmerleafSeed());
        registerItem(forgeRegistry, new ItemCinderpearlSeed());
        registerItem(forgeRegistry, new ItemVishroomSpore());
        registerItem(forgeRegistry, new ItemWarpRing(), ConfigHandlerTW.warp_ring.enableRing);
        registerItem(forgeRegistry, new ItemNightVisionGoggles());
        registerItem(forgeRegistry, new ItemCleansingCharm());
        registerItem(forgeRegistry, new ItemVoidFortressArmor("void_fortress_helm", ItemVoidFortressArmor.MATERIAL, 4, EntityEquipmentSlot.HEAD));
        registerItem(forgeRegistry, new ItemVoidFortressArmor("void_fortress_chest", ItemVoidFortressArmor.MATERIAL, 4, EntityEquipmentSlot.CHEST));
        registerItem(forgeRegistry, new ItemVoidFortressArmor("void_fortress_legs", ItemVoidFortressArmor.MATERIAL, 4, EntityEquipmentSlot.LEGS));
        registerItem(forgeRegistry, new ItemVoidcallerArmor("voidcaller_helm", ItemVoidcallerArmor.MATERIAL, 4, EntityEquipmentSlot.HEAD));
        registerItem(forgeRegistry, new ItemVoidcallerArmor("voidcaller_chest", ItemVoidcallerArmor.MATERIAL, 4, EntityEquipmentSlot.CHEST));
        registerItem(forgeRegistry, new ItemVoidcallerArmor("voidcaller_legs", ItemVoidcallerArmor.MATERIAL, 4, EntityEquipmentSlot.LEGS));
        registerItem(forgeRegistry, new ItemBootsVoidWalker(), ConfigHandlerTW.void_walker_boots.enableBoots);
        registerItem(forgeRegistry, new ItemPanacea());
        registerItem(forgeRegistry, new ItemLetheWater());
        registerItem(forgeRegistry, new ItemSharingTome(), ConfigHandlerTW.sharing_tome.enableTome);
        registerItem(forgeRegistry, new ItemTW("primordial_grain"));
        registerItem(forgeRegistry, new ItemAlchemistStone());
        registerItem(forgeRegistry, new ItemTransmuterStone());
        registerItem(forgeRegistry, new ItemAlienistStone());
        registerItem(forgeRegistry, new ItemTW("eldritch_cluster", "iron", "gold", "copper", "tin", "silver", "lead", "cinnabar", "quartz", "void"));
    }

    public static void initSeals() {
        GolemHelper.registerSeal(new SealResearchAssistant());

        //TODO: Remove comment when adding new seals
        //GolemHelper.registerSeal(new SealShearing());
        //GolemHelper.registerSeal(new SealShearingAdvanced());
    }

    private static void registerItem(IForgeRegistry<Item> forgeRegistry, Item item, boolean enabled) {
        if (enabled) {
            forgeRegistry.register(item);
            if (item.getHasSubtypes() && item instanceof IVariantItem) {
                InitItems.ITEM_VARIANT_HOLDERS.add((IVariantItem) item);
            } else {
                InitItems.ITEMS.add(item);
            }
        }
    }

    private static void registerItem(IForgeRegistry<Item> forgeRegistry, Item item) {
        registerItem(forgeRegistry, item, true);
    }
}
