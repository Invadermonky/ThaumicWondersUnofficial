package com.verdantartifice.thaumicwonders.client.events;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.init.InitBlocks;
import com.verdantartifice.thaumicwonders.common.init.InitItems;
import com.verdantartifice.thaumicwonders.common.items.base.IVariantItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = ThaumicWonders.MODID)
public class ModelRegistrationEvents {
    @SubscribeEvent
    public static void registerAllModels(ModelRegistryEvent event) {
        registerItemBlockModels();
        registerItemModels();
        registerVariantModels();
    }

    private static void registerItemBlockModels() {
        for (ItemBlock itemBlock : InitBlocks.ITEM_BLOCKS) {
            registerItemModel(itemBlock);
        }
    }

    private static void registerItemModels() {
        for (Item item : InitItems.ITEMS) {
            registerItemModel(item);
        }
    }

    private static void registerItemModel(Item item) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    private static void registerVariantModels() {
        for (IVariantItem variantHolder : InitItems.ITEM_VARIANT_HOLDERS) {
            registerVariantModel(variantHolder);
        }
    }

    private static void registerVariantModel(IVariantItem item) {
        for (int i = 0; i < item.getVariantNames().length; i++) {
            ModelLoader.setCustomModelResourceLocation(item.getItem(), item.getVariantMeta()[i], item.getCustomModelResourceLocation(item.getVariantNames()[i]));
        }
    }
}
