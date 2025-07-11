package com.verdantartifice.thaumicwonders.common.init;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.config.ConfigHandlerTW;
import com.verdantartifice.thaumicwonders.common.entities.monsters.EntityCorruptionAvatar;
import com.verdantartifice.thaumicwonders.common.items.ItemsTW;
import com.verdantartifice.thaumicwonders.common.research.theorycraft.AidInspirationEngine;
import com.verdantartifice.thaumicwonders.common.research.theorycraft.AidMadnessEngine;
import com.verdantartifice.thaumicwonders.common.research.theorycraft.CardInducedInspiration;
import com.verdantartifice.thaumicwonders.common.research.theorycraft.CardInducedMadness;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ScanEntity;
import thaumcraft.api.research.ScanItem;
import thaumcraft.api.research.ScanningManager;
import thaumcraft.api.research.theorycraft.TheorycraftManager;

@SuppressWarnings("ConstantConditions")
public class InitResearch {
    public static void initResearch() {
        initCategories();
        initResearchLocations();
        initTheorycraft();
        initScannables();
        initOptionals();
    }

    private static void initCategories() {
        ResearchCategories.registerCategory(
                "THAUMIC_WONDERS",
                "FIRSTSTEPS",
                new AspectList(),
                new ResourceLocation(ThaumicWonders.MODID, "textures/gui/wonder_chest.png"),
                new ResourceLocation(ThaumicWonders.MODID, "textures/gui/gui_research_back.jpg"),
                new ResourceLocation("thaumcraft", "textures/gui/gui_research_back_over.png")
        );
    }

    private static void initResearchLocations() {
        ThaumcraftApi.registerResearchLocation(new ResourceLocation(ThaumicWonders.MODID, "research/misc"));
        ThaumcraftApi.registerResearchLocation(new ResourceLocation(ThaumicWonders.MODID, "research/alchemy"));
        ThaumcraftApi.registerResearchLocation(new ResourceLocation(ThaumicWonders.MODID, "research/artifice"));
        ThaumcraftApi.registerResearchLocation(new ResourceLocation(ThaumicWonders.MODID, "research/auromancy"));
        ThaumcraftApi.registerResearchLocation(new ResourceLocation(ThaumicWonders.MODID, "research/infusion"));
        ThaumcraftApi.registerResearchLocation(new ResourceLocation(ThaumicWonders.MODID, "research/eldritch"));
        ThaumcraftApi.registerResearchLocation(new ResourceLocation(ThaumicWonders.MODID, "research/scans"));
    }

    private static void initTheorycraft() {
        TheorycraftManager.registerAid(new AidInspirationEngine());
        TheorycraftManager.registerAid(new AidMadnessEngine());
        TheorycraftManager.registerCard(CardInducedInspiration.class);
        TheorycraftManager.registerCard(CardInducedMadness.class);
    }

    private static void initScannables() {
        ScanningManager.addScannableThing(new ScanItem("!TWOND_PRIMORDIAL_GRAIN", new ItemStack(ItemsTW.PRIMORDIAL_GRAIN)));
        ScanningManager.addScannableThing(new ScanEntity("!TWOND_CORRUPTION_AVATAR", EntityCorruptionAvatar.class, true));
    }

    private static void initOptionals() {
        if (ConfigHandlerTW.void_walker_boots.enableBoots) {
            ThaumcraftApi.registerResearchLocation(new ResourceLocation(ThaumicWonders.MODID, "research/optional/void_walker_boots"));
        }
        if (ConfigHandlerTW.warp_ring.enableRing) {
            ThaumcraftApi.registerResearchLocation(new ResourceLocation(ThaumicWonders.MODID, "research/optional/warp_ring"));
        }
        if (ConfigHandlerTW.sharing_tome.enableTome) {
            ThaumcraftApi.registerResearchLocation(new ResourceLocation(ThaumicWonders.MODID, "research/optional/sharing_tome"));
        }
    }
}
