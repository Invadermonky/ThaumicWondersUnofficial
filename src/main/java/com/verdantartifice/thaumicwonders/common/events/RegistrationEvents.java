package com.verdantartifice.thaumicwonders.common.events;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.init.*;
import com.verdantartifice.thaumicwonders.proxy.ProxyBlock;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = ThaumicWonders.MODID)
public class RegistrationEvents {
    @SubscribeEvent
    public static void regsiterBlocks(RegistryEvent.Register<Block> event) {
        InitBlocks.initBlocks(event.getRegistry());
        InitBlocks.initTileEntities();
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerBlockClient(RegistryEvent.Register<Block> event) {
        ProxyBlock.setupBlocksClient(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        InitBlocks.initItemBlocks(event.getRegistry());
        InitItems.initItems(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        InitRecipes.initRecipes(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        InitEntities.initEntities(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Potion> event) {
        InitPotions.initPotions(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        InitSounds.initSounds(event.getRegistry());
    }
}
