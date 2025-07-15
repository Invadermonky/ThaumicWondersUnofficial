package com.verdantartifice.thaumicwonders.common.init;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.blocks.BlocksTW;
import com.verdantartifice.thaumicwonders.common.compat.CompatHelper;
import com.verdantartifice.thaumicwonders.common.compat.ModPlugins;
import com.verdantartifice.thaumicwonders.common.config.ConfigHandlerTW;
import com.verdantartifice.thaumicwonders.common.crafting.catalyzationchamber.CatalyzationChamberRecipeRegistry;
import com.verdantartifice.thaumicwonders.common.crafting.meatyorb.MeatyOrbEntryRegistry;
import com.verdantartifice.thaumicwonders.common.crafting.recipes.RecipeDisjunctionClothUse;
import com.verdantartifice.thaumicwonders.common.crafting.recipes.RecipeFlyingCarpetDyes;
import com.verdantartifice.thaumicwonders.common.fluids.FluidQuicksilver;
import com.verdantartifice.thaumicwonders.common.items.ItemsTW;
import com.verdantartifice.thaumicwonders.common.items.consumables.ItemPrimalArrow.PrimalArrowVariant;
import com.verdantartifice.thaumicwonders.common.registry.InfusionEnchantmentsTW;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistry;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.*;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.blocks.basic.BlockPillar;
import thaumcraft.common.lib.crafting.DustTriggerMultiblock;
import thaumcraft.common.lib.crafting.InfusionEnchantmentRecipe;
import thaumcraft.common.lib.enchantment.EnumInfusionEnchantment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("ConstantConditions")
public class InitRecipes {
    private static ResourceLocation defaultGroup = new ResourceLocation("");

    public static void initRecipes(IForgeRegistry<IRecipe> forgeRegistry) {
        initNormalRecipes(forgeRegistry);
        initArcaneRecipes();
        initCrucibleRecipes();
        initInfusionRecipes();
        initInfusionEnchantmentRecipes();
        initMultiblockRecipes();
        initSmelting();
        initCatalyzationChamberRecipes();
        initMeatyOrb();
        InitVoidBeacon.init();
        ModPlugins.registerRecipes(forgeRegistry);
    }

    private static void initMultiblockRecipes() {
        initCatalyzationChamber();
        initCoalescencePlatform();
    }

    private static void initCatalyzationChamber() {
        Part AS = new Part(BlocksTC.stoneArcane, new ItemStack(BlocksTW.PLACEHOLDER_ARCANE_STONE));
        Part OB = new Part(Blocks.OBSIDIAN, new ItemStack(BlocksTW.PLACEHOLDER_OBSIDIAN));
        Part IB = new Part(Blocks.IRON_BARS, "AIR");
        Part QS = new Part(BlocksTW.FLUID_QUICKSILVER, BlocksTW.CATALYZATION_CHAMBER, true);
        Part[][][] catalyzationChamberBlueprint = {
                {
                        {AS, OB, AS},
                        {OB, null, OB},
                        {AS, OB, AS}
                },
                {
                        {AS, OB, AS},
                        {OB, QS, OB},
                        {AS, IB, AS}
                },
                {
                        {AS, OB, AS},
                        {OB, OB, OB},
                        {AS, OB, AS}
                }
        };
        IDustTrigger.registerDustTrigger(new DustTriggerMultiblock("TWOND_CATALYZATION_CHAMBER@2", catalyzationChamberBlueprint));
        ThaumcraftApi.addMultiblockRecipeToCatalog(new ResourceLocation(ThaumicWonders.MODID, "catalyzation_chamber"), new ThaumcraftApi.BluePrint(
                "TWOND_CATALYZATION_CHAMBER@2",
                catalyzationChamberBlueprint,
                new ItemStack(BlocksTC.stoneArcane, 12),
                new ItemStack(Blocks.OBSIDIAN, 12),
                new ItemStack(Blocks.IRON_BARS),
                FluidUtil.getFilledBucket(new FluidStack(FluidQuicksilver.INSTANCE, 1000))));
    }

    private static void initCoalescencePlatform() {
        Part VMET = new Part(BlocksTC.metalBlockVoid, null);
        Part ASTI = new Part(BlocksTC.stoneArcane, null);
        Part ASBR = new Part(BlocksTC.stoneArcaneBrick, null);
        Part SNTP = new Part(BlocksTC.stoneArcane, "AIR");
        Part SNB1 = new Part(BlocksTC.stoneArcane, new ItemStack(BlocksTC.pillarArcane, 1, BlockPillar.calcMeta(EnumFacing.EAST)));
        Part SNB2 = new Part(BlocksTC.stoneArcane, new ItemStack(BlocksTC.pillarArcane, 1, BlockPillar.calcMeta(EnumFacing.NORTH)));
        Part SNB3 = new Part(BlocksTC.stoneArcane, new ItemStack(BlocksTC.pillarArcane, 1, BlockPillar.calcMeta(EnumFacing.SOUTH)));
        Part SNB4 = new Part(BlocksTC.stoneArcane, new ItemStack(BlocksTC.pillarArcane, 1, BlockPillar.calcMeta(EnumFacing.WEST)));
        Part CMAT = new Part(BlocksTW.COALESCENCE_MATRIX_PRECURSOR, new ItemStack(BlocksTW.COALESCENCE_MATRIX));
        Part[][][] coalescencePlatformBlueprint = {
                {
                        {null, null, SNTP, null, null, null, SNTP, null, null},
                        {null, null, null, null, null, null, null, null, null},
                        {SNTP, null, null, null, null, null, null, null, SNTP},
                        {null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null},
                        {SNTP, null, null, null, null, null, null, null, SNTP},
                        {null, null, null, null, null, null, null, null, null},
                        {null, null, SNTP, null, null, null, SNTP, null, null},
                        },
                {
                        {null, null, SNB1, null, null, null, SNB2, null, null},
                        {null, null, null, null, null, null, null, null, null},
                        {SNB1, null, null, null, null, null, null, null, SNB2},
                        {null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, CMAT, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null},
                        {SNB3, null, null, null, null, null, null, null, SNB4},
                        {null, null, null, null, null, null, null, null, null},
                        {null, null, SNB3, null, null, null, SNB4, null, null},
                        },
                {
                        {null, null, ASBR, ASBR, ASBR, ASBR, ASBR, null, null},
                        {null, ASBR, VMET, VMET, ASTI, VMET, VMET, ASBR, null},
                        {ASBR, VMET, VMET, ASTI, ASTI, ASTI, VMET, VMET, ASBR},
                        {ASBR, VMET, ASTI, VMET, ASTI, VMET, ASTI, VMET, ASBR},
                        {ASBR, ASTI, ASTI, ASTI, ASTI, ASTI, ASTI, ASTI, ASBR},
                        {ASBR, VMET, ASTI, VMET, ASTI, VMET, ASTI, VMET, ASBR},
                        {ASBR, VMET, VMET, ASTI, ASTI, ASTI, VMET, VMET, ASBR},
                        {null, ASBR, VMET, VMET, ASTI, VMET, VMET, ASBR, null},
                        {null, null, ASBR, ASBR, ASBR, ASBR, ASBR, null, null},
                        }
        };
        IDustTrigger.registerDustTrigger(new DustTriggerMultiblock("TWOND_COALESCENCE_MATRIX", coalescencePlatformBlueprint));
        ThaumcraftApi.addMultiblockRecipeToCatalog(new ResourceLocation(ThaumicWonders.MODID, "coalescence_platform"), new ThaumcraftApi.BluePrint(
                "TWOND_COALESCENCE_MATRIX",
                coalescencePlatformBlueprint,
                new ItemStack(BlocksTC.metalBlockVoid, 24),
                new ItemStack(BlocksTC.stoneArcaneBrick, 24),
                new ItemStack(BlocksTC.stoneArcane, 37),
                new ItemStack(BlocksTW.COALESCENCE_MATRIX_PRECURSOR)));
    }

    private static void initNormalRecipes(IForgeRegistry<IRecipe> forgeRegistry) {
        forgeRegistry.register(new RecipeDisjunctionClothUse());
        forgeRegistry.register(new RecipeFlyingCarpetDyes());

        ResourceLocation qsGroup = new ResourceLocation(ThaumicWonders.MODID, "quicksilver_bucket_group");
        shapelessOreDictRecipe("quicksilver_bucket", qsGroup, FluidUtil.getFilledBucket(new FluidStack(FluidQuicksilver.INSTANCE, 1000)), new Object[]{
                Items.BUCKET, new ItemStack(ItemsTC.quicksilver), new ItemStack(ItemsTC.quicksilver), new ItemStack(ItemsTC.quicksilver),
                new ItemStack(ItemsTC.quicksilver), new ItemStack(ItemsTC.quicksilver), new ItemStack(ItemsTC.quicksilver),
                new ItemStack(ItemsTC.quicksilver), new ItemStack(ItemsTC.quicksilver)
        });
        shapelessOreDictRecipe("quicksilver_bucket_deconstruct", qsGroup, new ItemStack(ItemsTC.quicksilver, 8),
                new Object[]{new IngredientNBTTC(FluidUtil.getFilledBucket(new FluidStack(FluidQuicksilver.INSTANCE, 1000)))});
    }

    private static IRecipe shapelessOreDictRecipe(@Nonnull String name, @Nullable ResourceLocation group, @Nonnull ItemStack result, Object[] inputs) {
        IRecipe recipe = new ShapelessOreRecipe(group, result, inputs);
        recipe.setRegistryName(new ResourceLocation(ThaumicWonders.MODID, name));
        GameData.register_impl(recipe);
        return recipe;
    }

    private static void initArcaneRecipes() {
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "dimensional_ripper"), new ShapedArcaneRecipe(
                defaultGroup,
                "TWOND_DIMENSIONAL_RIPPER",
                100,
                new AspectList().add(Aspect.AIR, 1).add(Aspect.ENTROPY, 1),
                new ItemStack(BlocksTW.DIMENSIONAL_RIPPER, 2),
                "BPB",
                "VAV",
                "VMV",
                'B', "plateBrass",
                'P', Ingredient.fromItem(ItemsTC.visResonator),
                'V', "plateVoid",
                'A', new ItemStack(ItemsTC.morphicResonator),
                'M', new ItemStack(ItemsTC.mechanismComplex)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "inspiration_engine"), new ShapedArcaneRecipe(
                defaultGroup,
                "TWOND_INSPIRATION_ENGINE",
                50,
                new AspectList().add(Aspect.AIR, 1).add(Aspect.WATER, 1).add(Aspect.ORDER, 1),
                BlocksTW.INSPIRATION_ENGINE,
                "BRB",
                "VMV",
                "SZS",
                'B', "plateBrass",
                'R', new ItemStack(ItemsTC.morphicResonator),
                'V', new ItemStack(ItemsTC.visResonator),
                'M', new ItemStack(ItemsTC.mechanismSimple),
                'S', new ItemStack(BlocksTC.stoneArcane),
                'Z', new ItemStack(ItemsTC.brain)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "portal_anchor"), new ShapedArcaneRecipe(
                defaultGroup,
                "TWOND_VOID_PORTAL@2",
                50,
                new AspectList().add(Aspect.AIR, 1).add(Aspect.ORDER, 1).add(Aspect.ENTROPY, 1),
                BlocksTW.PORTAL_ANCHOR,
                " E ",
                "SMS",
                'E', new ItemStack(Items.ENDER_PEARL),
                'S', new ItemStack(BlocksTC.stoneArcane),
                'M', new ItemStack(ItemsTC.mechanismComplex)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "portal_linker"), new ShapedArcaneRecipe(
                defaultGroup,
                "TWOND_VOID_PORTAL@2",
                20,
                new AspectList().add(Aspect.ORDER, 1).add(Aspect.ENTROPY, 1),
                new ItemStack(ItemsTW.PORTAL_LINKER),
                " E ",
                "PMP",
                " P ",
                'E', new ItemStack(Items.ENDER_PEARL),
                'P', "plateBrass",
                'M', new ItemStack(ItemsTC.mirroredGlass)
        ));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "hexamite"), new ShapedArcaneRecipe(
                defaultGroup,
                "TWOND_HEXAMITE",
                50,
                new AspectList().add(Aspect.FIRE, 1).add(Aspect.ENTROPY, 1),
                BlocksTW.HEXAMITE,
                "AVA",
                "VGV",
                "AVA",
                'A', new ItemStack(ItemsTC.alumentum),
                'V', new IngredientNBTTC(ThaumcraftApiHelper.makeCrystal(Aspect.FLUX)),
                'G', new ItemStack(Items.GUNPOWDER)));

        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "primal_arrow_air"), new ShapedArcaneRecipe(
                defaultGroup,
                "TWOND_PRIMAL_ARROWS",
                10,
                new AspectList(),
                CompatHelper.getPrimalArrow(PrimalArrowVariant.AIR, 8),
                "AAA",
                "AVA",
                "AAA",
                'A', new ItemStack(Items.ARROW),
                'V', new IngredientNBTTC(ThaumcraftApiHelper.makeCrystal(Aspect.AIR))));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "primal_arrow_earth"), new ShapedArcaneRecipe(
                defaultGroup,
                "TWOND_PRIMAL_ARROWS",
                10,
                new AspectList(),
                CompatHelper.getPrimalArrow(PrimalArrowVariant.EARTH, 8),
                "AAA",
                "AVA",
                "AAA",
                'A', new ItemStack(Items.ARROW),
                'V', new IngredientNBTTC(ThaumcraftApiHelper.makeCrystal(Aspect.EARTH))));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "primal_arrow_fire"), new ShapedArcaneRecipe(
                defaultGroup,
                "TWOND_PRIMAL_ARROWS",
                10,
                new AspectList(),
                CompatHelper.getPrimalArrow(PrimalArrowVariant.FIRE, 8),
                "AAA",
                "AVA",
                "AAA",
                'A', new ItemStack(Items.ARROW),
                'V', new IngredientNBTTC(ThaumcraftApiHelper.makeCrystal(Aspect.FIRE))));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "primal_arrow_water"), new ShapedArcaneRecipe(
                defaultGroup,
                "TWOND_PRIMAL_ARROWS",
                10,
                new AspectList(),
                CompatHelper.getPrimalArrow(PrimalArrowVariant.WATER, 8),
                "AAA",
                "AVA",
                "AAA",
                'A', new ItemStack(Items.ARROW),
                'V', new IngredientNBTTC(ThaumcraftApiHelper.makeCrystal(Aspect.WATER))));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "primal_arrow_order"), new ShapedArcaneRecipe(
                defaultGroup,
                "TWOND_PRIMAL_ARROWS",
                10,
                new AspectList(),
                CompatHelper.getPrimalArrow(PrimalArrowVariant.ORDER, 8),
                "AAA",
                "AVA",
                "AAA",
                'A', new ItemStack(Items.ARROW),
                'V', new IngredientNBTTC(ThaumcraftApiHelper.makeCrystal(Aspect.ORDER))));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "primal_arrow_entropy"), new ShapedArcaneRecipe(
                defaultGroup,
                "TWOND_PRIMAL_ARROWS",
                10,
                new AspectList(),
                CompatHelper.getPrimalArrow(PrimalArrowVariant.ENTROPY, 8),
                "AAA",
                "AVA",
                "AAA",
                'A', new ItemStack(Items.ARROW),
                'V', new IngredientNBTTC(ThaumcraftApiHelper.makeCrystal(Aspect.ENTROPY))));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "flux_distiller"), new ShapedArcaneRecipe(
                defaultGroup,
                "TWOND_FLUX_DISTILLER",
                200,
                new AspectList().add(Aspect.AIR, 2).add(Aspect.WATER, 2).add(Aspect.FIRE, 2).add(Aspect.EARTH, 2).add(Aspect.ORDER, 2).add(Aspect.ENTROPY, 2),
                BlocksTW.FLUX_DISTILLER,
                "VLV",
                "MCM",
                "VAV",
                'V', "plateVoid",
                'M', ItemsTC.mechanismComplex,
                'L', BlocksTC.condenserlattice,
                'C', BlocksTC.condenser,
                'A', BlocksTC.metalAlchemicalAdvanced));
    }

    private static void initCrucibleRecipes() {
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation(ThaumicWonders.MODID, "hedge_soul_sand"), new CrucibleRecipe(
                "TWOND_NETHER_HEDGE",
                new ItemStack(Blocks.SOUL_SAND),
                new ItemStack(Blocks.SAND),
                new AspectList().add(Aspect.SOUL, 3).add(Aspect.EARTH, 3).add(Aspect.TRAP, 1)
        ));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation(ThaumicWonders.MODID, "hedge_ghast_tear"), new CrucibleRecipe(
                "TWOND_NETHER_HEDGE",
                new ItemStack(Items.GHAST_TEAR, 2),
                new ItemStack(Items.GHAST_TEAR),
                new AspectList().add(Aspect.SOUL, 10).add(Aspect.ALCHEMY, 10).add(Aspect.UNDEAD, 5)
        ));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation(ThaumicWonders.MODID, "hedge_wither_skull"), new CrucibleRecipe(
                "TWOND_NETHER_HEDGE",
                new ItemStack(Items.SKULL, 1, 1),
                new ItemStack(Items.SKULL, 1, 0),
                new AspectList().add(Aspect.UNDEAD, 10).add(Aspect.DEATH, 10).add(Aspect.SOUL, 10).add(Aspect.DARKNESS, 5)
        ));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation(ThaumicWonders.MODID, "hedge_end_stone"), new CrucibleRecipe(
                "TWOND_END_HEDGE",
                new ItemStack(Blocks.END_STONE),
                new ItemStack(Blocks.STONE),
                new AspectList().add(Aspect.EARTH, 5).add(Aspect.DARKNESS, 5)
        ));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation(ThaumicWonders.MODID, "hedge_chorus_fruit"), new CrucibleRecipe(
                "TWOND_END_HEDGE",
                new ItemStack(Items.CHORUS_FRUIT),
                new ItemStack(Items.APPLE),
                new AspectList().add(Aspect.ELDRITCH, 5).add(Aspect.SENSES, 5).add(Aspect.PLANT, 5)
        ));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation(ThaumicWonders.MODID, "hedge_ender_pearl"), new CrucibleRecipe(
                "TWOND_END_HEDGE",
                new ItemStack(Items.ENDER_PEARL, 2),
                new ItemStack(Items.ENDER_PEARL),
                new AspectList().add(Aspect.MOTION, 15).add(Aspect.ELDRITCH, 10)
        ));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation(ThaumicWonders.MODID, "disjunction_cloth"), new CrucibleRecipe(
                "TWOND_DISJUNCTION_CLOTH",
                new ItemStack(ItemsTW.DISJUNCTION_CLOTH),
                new ItemStack(ItemsTC.fabric),
                new AspectList().add(Aspect.MAGIC, 40).add(Aspect.VOID, 40).add(Aspect.AURA, 20)
        ));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation(ThaumicWonders.MODID, "alchemist_stone"), new CrucibleRecipe(
                "TWOND_CATALYZATION_CHAMBER",
                new ItemStack(ItemsTW.ALCHEMIST_STONE),
                new ItemStack(Items.DIAMOND),
                new AspectList().add(Aspect.METAL, 40).add(Aspect.ORDER, 40).add(Aspect.ALCHEMY, 10)
        ));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation(ThaumicWonders.MODID, "transmuter_stone"), new CrucibleRecipe(
                "TWOND_TRANSMUTER_STONE",
                new ItemStack(ItemsTW.TRANSMUTER_STONE),
                new ItemStack(ItemsTW.ALCHEMIST_STONE),
                new AspectList().add(Aspect.EXCHANGE, 40).add(Aspect.ALCHEMY, 10)
        ));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation(ThaumicWonders.MODID, "primordial_pearl_reconstitution"), new CrucibleRecipe(
                "TWOND_PRIMORDIAL_ACCELERATOR&&!TWOND_PRIMORDIAL_GRAIN",
                new ItemStack(ItemsTC.primordialPearl, 1, 7),
                new ItemStack(ItemsTW.PRIMORDIAL_GRAIN),
                new AspectList().add(Aspect.AIR, 250).add(Aspect.EARTH, 250).add(Aspect.FIRE, 250).add(Aspect.WATER, 250).add(Aspect.ORDER, 250).add(Aspect.ENTROPY, 250)
        ));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation(ThaumicWonders.MODID, "panacea"), new CrucibleRecipe(
                "TWOND_PANACEA",
                new ItemStack(ItemsTW.PANACEA),
                new ItemStack(Items.GOLDEN_APPLE),
                new AspectList().add(Aspect.LIFE, 20).add(Aspect.ORDER, 20).add(Aspect.PROTECT, 20)
        ));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation(ThaumicWonders.MODID, "enchanted_panacea"), new CrucibleRecipe(
                "TWOND_PANACEA",
                new ItemStack(ItemsTW.PANACEA, 1, 1),
                new ItemStack(Items.GOLDEN_APPLE, 1, 1),
                new AspectList().add(Aspect.LIFE, 20).add(Aspect.ORDER, 20).add(Aspect.PROTECT, 20)
        ));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation(ThaumicWonders.MODID, "alkahest_vat"), new CrucibleRecipe(
                "TWOND_ALKAHEST",
                new ItemStack(BlocksTW.ALKAHEST_VAT),
                new ItemStack(BlocksTC.crucible),
                new AspectList().add(Aspect.ALCHEMY, 25).add(Aspect.WATER, 25).add(Aspect.ENTROPY, 50).add(Aspect.PROTECT, 25)
        ));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation(ThaumicWonders.MODID, "lethe_water"), new CrucibleRecipe(
                "TWOND_LETHE_WATER",
                new ItemStack(ItemsTW.LETHE_WATER),
                PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER),
                new AspectList().add(Aspect.MAGIC, 20).add(Aspect.MIND, 20).add(Aspect.VOID, 20)
        ));
    }

    private static void initInfusionRecipes() {
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "bone_bow"), new InfusionRecipe(
                "TWOND_BONE_BOW",
                new ItemStack(ItemsTW.BONE_BOW),
                3,
                new AspectList().add(Aspect.AURA, 40).add(Aspect.FLIGHT, 40).add(Aspect.ENTROPY, 40),
                new ItemStack(Items.BOW),
                ThaumcraftApiHelper.makeCrystal(Aspect.ENTROPY),
                ThaumcraftApiHelper.makeCrystal(Aspect.ENTROPY),
                new ItemStack(ItemsTC.visResonator),
                new ItemStack(Blocks.BONE_BLOCK)
        ));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "everburning_urn"), new InfusionRecipe(
                "TWOND_EVERBURNING_URN",
                new ItemStack(BlocksTW.EVERBURNING_URN),
                4,
                new AspectList().add(Aspect.FIRE, 40).add(Aspect.EARTH, 20).add(Aspect.ENERGY, 10).add(Aspect.CRAFT, 10),
                new ItemStack(BlocksTC.everfullUrn),
                new ItemStack(Items.NETHERBRICK),
                new ItemStack(Items.NETHERBRICK),
                new ItemStack(Items.LAVA_BUCKET),
                ThaumcraftApiHelper.makeCrystal(Aspect.FIRE),
                new ItemStack(Blocks.OBSIDIAN)
        ));

        ItemStack destroyer = new ItemStack(ItemsTW.PRIMAL_DESTROYER);
        EnumInfusionEnchantment.addInfusionEnchantment(destroyer, EnumInfusionEnchantment.ESSENCE, 3);
        EnumInfusionEnchantment.addInfusionEnchantment(destroyer, InfusionEnchantmentsTW.VOIDFLAME, 2);
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "primal_destroyer"), new InfusionRecipe(
                "TWOND_PRIMAL_DESTROYER@2",
                destroyer,
                8,
                new AspectList().add(Aspect.FIRE, 100).add(Aspect.ENTROPY, 50).add(Aspect.VOID, 50).add(Aspect.AVERSION, 100).add(Aspect.ELDRITCH, 75).add(Aspect.DARKNESS, 75).add(Aspect.DEATH, 100),
                Ingredient.fromItem(ItemsTC.voidSword),
                Ingredient.fromItem(ItemsTC.primordialPearl),
                new ItemStack(Items.NETHER_STAR),
                "plateVoid",
                "plateVoid",
                ThaumcraftApiHelper.makeCrystal(Aspect.FIRE),
                ThaumcraftApiHelper.makeCrystal(Aspect.FIRE)
        ));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "flying_carpet"), new InfusionRecipe(
                "TWOND_FLYING_CARPET",
                new ItemStack(ItemsTW.FLYING_CARPET),
                6,
                new AspectList().add(Aspect.FLIGHT, 150).add(Aspect.MOTION, 100).add(Aspect.AIR, 100).add(Aspect.MAGIC, 50).add(Aspect.ENERGY, 50),
                new ItemStack(Blocks.CARPET, 1, 32767),
                new ItemStack(BlocksTC.levitator),
                new ItemStack(Items.SADDLE),
                new ItemStack(ItemsTC.visResonator),
                ThaumcraftApiHelper.makeCrystal(Aspect.AIR),
                ThaumcraftApiHelper.makeCrystal(Aspect.AIR)
        ));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "timewinder"), new InfusionRecipe(
                "TWOND_TIMEWINDER",
                new ItemStack(ItemsTW.TIMEWINDER),
                7,
                new AspectList().add(Aspect.ELDRITCH, 100).add(Aspect.DARKNESS, 100).add(Aspect.LIGHT, 100),
                new ItemStack(Items.CLOCK),
                new ItemStack(Items.DIAMOND),
                new ItemStack(Items.ENDER_PEARL),
                new ItemStack(ItemsTC.quicksilver),
                new ItemStack(ItemsTC.celestialNotes, 1, 5),
                new ItemStack(ItemsTC.celestialNotes, 1, 6),
                new ItemStack(ItemsTC.celestialNotes, 1, 7),
                new ItemStack(ItemsTC.celestialNotes, 1, 8),
                new ItemStack(ItemsTC.celestialNotes, 1, 9),
                new ItemStack(ItemsTC.celestialNotes, 1, 10),
                new ItemStack(ItemsTC.celestialNotes, 1, 11),
                new ItemStack(ItemsTC.celestialNotes, 1, 12),
                new ItemStack(ItemsTC.celestialNotes, 1, 0)
        ));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "madness_engine"), new InfusionRecipe(
                "TWOND_MADNESS_ENGINE",
                new ItemStack(BlocksTW.MADNESS_ENGINE),
                6,
                new AspectList().add(Aspect.ELDRITCH, 150).add(Aspect.MIND, 100).add(Aspect.MECHANISM, 100).add(Aspect.AURA, 50),
                new ItemStack(BlocksTW.INSPIRATION_ENGINE),
                "plateVoid",
                "plateVoid",
                "plateThaumium",
                "plateThaumium",
                new ItemStack(ItemsTC.mind, 1, 1),
                new ItemStack(Items.ENDER_PEARL)
        ));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "portal_generator"), new InfusionRecipe(
                "TWOND_VOID_PORTAL",
                new ItemStack(BlocksTW.PORTAL_GENERATOR),
                8,
                new AspectList().add(Aspect.ELDRITCH, 150).add(Aspect.MOTION, 150).add(Aspect.EXCHANGE, 100),
                new ItemStack(BlocksTW.PORTAL_ANCHOR),
                Ingredient.fromItem(ItemsTC.primordialPearl),
                new ItemStack(BlocksTC.mirror),
                new ItemStack(Items.ENDER_PEARL)
        ));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "flux_capacitor"), new InfusionRecipe(
                "TWOND_FLUX_CAPACITOR",
                new ItemStack(BlocksTW.FLUX_CAPACITOR),
                6,
                new AspectList().add(Aspect.FLUX, 50).add(Aspect.AURA, 50).add(Aspect.VOID, 50),
                new ItemStack(BlocksTC.visBattery),
                Ingredient.fromItem(ItemsTC.primordialPearl),
                new ItemStack(BlocksTC.crystalTaint),
                new ItemStack(ItemsTC.visResonator),
                new ItemStack(BlocksTC.condenserlattice)
        ));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "alienist_stone"), new InfusionRecipe(
                "TWOND_ALIENIST_STONE",
                new ItemStack(ItemsTW.ALIENIST_STONE),
                5,
                new AspectList().add(Aspect.METAL, 60).add(Aspect.ELDRITCH, 40).add(Aspect.ALCHEMY, 25),
                new ItemStack(ItemsTW.ALCHEMIST_STONE),
                new ItemStack(ItemsTC.nuggets, 1, 10),
                new ItemStack(ItemsTC.clusters, 1, 0),
                new ItemStack(ItemsTC.clusters, 1, 1),
                new ItemStack(ItemsTC.clusters, 1, 6),
                "gemAmber"
        ));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "meteorb"), new InfusionRecipe(
                "TWOND_METEORB",
                new ItemStack(BlocksTW.METEORB),
                6,
                new AspectList().add(Aspect.AIR, 100).add(Aspect.WATER, 100).add(Aspect.ENERGY, 100).add(Aspect.ELDRITCH, 50),
                new ItemStack(Items.ENDER_PEARL),
                new ItemStack(BlocksTC.stoneArcane),
                new ItemStack(BlocksTC.slabArcaneStone),
                new ItemStack(BlocksTC.tube),
                new ItemStack(BlocksTC.stoneArcane),
                new ItemStack(BlocksTC.slabArcaneStone),
                new ItemStack(BlocksTC.tube),
                new ItemStack(BlocksTC.stoneArcane),
                new ItemStack(BlocksTC.slabArcaneStone),
                new ItemStack(BlocksTC.tube),
                new ItemStack(BlocksTC.stoneArcane),
                new ItemStack(BlocksTC.slabArcaneStone),
                new ItemStack(Blocks.STONE_BUTTON)
        ));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "ore_diviner"), new InfusionRecipe(
                "TWOND_ORE_DIVINER",
                new ItemStack(BlocksTW.ORE_DIVINER),
                6,
                new AspectList().add(Aspect.SENSES, 75).add(Aspect.EARTH, 50).add(Aspect.METAL, 50).add(Aspect.MAGIC, 50),
                new ItemStack(Blocks.QUARTZ_BLOCK),
                "oreIron",
                "oreGold",
                "blockCoal",
                "blockRedstone",
                "blockLapis",
                "gemDiamond",
                "gemEmerald",
                "oreCinnabar"
        ));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "void_fortress_helm"), new InfusionRecipe(
                "TWOND_VOID_FORTRESS_ARMOR",
                new ItemStack(ItemsTW.VOID_FORTRESS_HELM),
                8,
                new AspectList().add(Aspect.PROTECT, 45).add(Aspect.METAL, 45).add(Aspect.ELDRITCH, 50).add(Aspect.ENERGY, 25).add(Aspect.VOID, 25).add(Aspect.MAGIC, 25).add(Aspect.SENSES, 25).add(Aspect.UNDEAD, 40).add(Aspect.LIFE, 40),
                new ItemStack(ItemsTC.voidHelm),
                Ingredient.fromItem(ItemsTC.primordialPearl),
                "plateVoid",
                new ItemStack(ItemsTC.crimsonPlateHelm, 1, Short.MAX_VALUE),
                new ItemStack(ItemsTC.goggles, 1, 32767),
                new ItemStack(Items.GHAST_TEAR),
                new ItemStack(ItemsTC.salisMundus),
                "leather"
        ));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "void_fortress_chest"), new InfusionRecipe(
                "TWOND_VOID_FORTRESS_ARMOR",
                new ItemStack(ItemsTW.VOID_FORTRESS_CHEST),
                8,
                new AspectList().add(Aspect.PROTECT, 55).add(Aspect.METAL, 55).add(Aspect.ELDRITCH, 50).add(Aspect.ENERGY, 25).add(Aspect.VOID, 35).add(Aspect.MAGIC, 25),
                new ItemStack(ItemsTC.voidChest),
                Ingredient.fromItem(ItemsTC.primordialPearl),
                "plateVoid",
                "plateVoid",
                "plateVoid",
                new ItemStack(ItemsTC.crimsonPlateChest, 1, Short.MAX_VALUE),
                new ItemStack(ItemsTC.salisMundus),
                "leather"
        ));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "void_fortress_legs"), new InfusionRecipe(
                "TWOND_VOID_FORTRESS_ARMOR",
                new ItemStack(ItemsTW.VOID_FORTRESS_LEGS),
                8,
                new AspectList().add(Aspect.PROTECT, 50).add(Aspect.METAL, 50).add(Aspect.ELDRITCH, 50).add(Aspect.ENERGY, 25).add(Aspect.VOID, 30).add(Aspect.MAGIC, 25),
                new ItemStack(ItemsTC.voidLegs),
                Ingredient.fromItem(ItemsTC.primordialPearl),
                "plateVoid",
                "plateVoid",
                new ItemStack(ItemsTC.crimsonPlateLegs, 1, Short.MAX_VALUE),
                new ItemStack(ItemsTC.salisMundus),
                "leather"
        ));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "meaty_orb"), new InfusionRecipe(
                "TWOND_MEATY_ORB",
                new ItemStack(BlocksTW.MEATY_ORB),
                8,
                new AspectList().add(Aspect.WATER, 250).add(Aspect.LIFE, 250).add(Aspect.ELDRITCH, 250),
                new ItemStack(BlocksTW.METEORB),
                Ingredient.fromItem(ItemsTC.primordialPearl),
                new ItemStack(Items.BEEF),
                new ItemStack(Items.PORKCHOP),
                new ItemStack(Items.CHICKEN),
                new ItemStack(Items.MUTTON),
                new ItemStack(Items.RABBIT)
        ));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "structure_diviner"), new InfusionRecipe(
                "TWOND_STRUCTURE_DIVINER",
                new ItemStack(ItemsTW.STRUCTURE_DIVINER),
                4,
                new AspectList().add(Aspect.SENSES, 100).add(Aspect.MECHANISM, 50).add(Aspect.MAGIC, 50).add(Aspect.EARTH, 50),
                new ItemStack(Items.COMPASS),
                new ItemStack(Items.ENDER_EYE),
                new ItemStack(Items.EMERALD),
                new ItemStack(Items.ENDER_EYE),
                new ItemStack(Blocks.NETHER_BRICK),
                new ItemStack(Items.ENDER_EYE),
                new ItemStack(Items.PRISMARINE_CRYSTALS)
        ));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "night_vision_goggles"), new InfusionRecipe(
                "TWOND_NV_GOGGLES",
                new ItemStack(ItemsTW.NIGHT_VISION_GOGGLES),
                2,
                new AspectList().add(Aspect.SENSES, 50).add(Aspect.LIGHT, 50).add(Aspect.MAGIC, 25).add(Aspect.ENERGY, 25),
                new ItemStack(ItemsTC.goggles),
                new ItemStack(Items.GOLDEN_CARROT),
                "nitor",
                ThaumcraftApiHelper.makeCrystal(Aspect.SENSES),
                ThaumcraftApiHelper.makeCrystal(Aspect.SENSES)
        ));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "void_beacon"), new InfusionRecipe(
                "TWOND_VOID_BEACON",
                new ItemStack(BlocksTW.VOID_BEACON),
                10,
                new AspectList().add(Aspect.ELDRITCH, 100).add(Aspect.VOID, 100).add(Aspect.MAGIC, 100).add(Aspect.FLUX, 100)
                        .add(Aspect.AIR, 50).add(Aspect.EARTH, 50).add(Aspect.FIRE, 50).add(Aspect.WATER, 50).add(Aspect.ORDER, 50).add(Aspect.ENTROPY, 50),
                new ItemStack(Blocks.BEACON),
                Ingredient.fromItem(ItemsTC.primordialPearl),
                "plateVoid",
                Ingredient.fromItem(ItemsTC.primordialPearl),
                "plateVoid",
                Ingredient.fromItem(ItemsTC.primordialPearl),
                "plateVoid",
                Ingredient.fromItem(ItemsTC.primordialPearl),
                "plateVoid"
        ));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "cleansing_charm"), new InfusionRecipe(
                "TWOND_CLEANSING_CHARM",
                new ItemStack(ItemsTW.CLEANSING_CHARM),
                4,
                new AspectList().add(Aspect.MIND, 75).add(Aspect.ORDER, 75).add(Aspect.ELDRITCH, 50).add(Aspect.LIFE, 50),
                new ItemStack(Items.ENDER_PEARL),
                Ingredient.fromItem(ItemsTC.primordialPearl),
                new ItemStack(Items.GOLD_INGOT),
                new ItemStack(Items.GOLD_INGOT),
                new ItemStack(Items.GOLD_INGOT)
        ));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "shimmerleaf_seed"), new InfusionRecipe(
                "TWOND_MYSTIC_GARDENING",
                new ItemStack(ItemsTW.SHIMMERLEAF_SEED),
                2,
                new AspectList().add(Aspect.PLANT, 10).add(Aspect.LIFE, 10).add(Aspect.AURA, 5),
                new ItemStack(Items.WHEAT_SEEDS),
                new ItemStack(BlocksTC.shimmerleaf),
                new ItemStack(ItemsTC.salisMundus)
        ));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "cinderpearl_seed"), new InfusionRecipe(
                "TWOND_MYSTIC_GARDENING",
                new ItemStack(ItemsTW.CINDERPEARL_SEED),
                2,
                new AspectList().add(Aspect.PLANT, 10).add(Aspect.LIFE, 10).add(Aspect.FIRE, 5),
                new ItemStack(Items.WHEAT_SEEDS),
                new ItemStack(BlocksTC.cinderpearl),
                new ItemStack(ItemsTC.salisMundus)
        ));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "vishroom_spore"), new InfusionRecipe(
                "TWOND_MYSTIC_GARDENING",
                new ItemStack(ItemsTW.VISHROOM_SPORE),
                2,
                new AspectList().add(Aspect.PLANT, 10).add(Aspect.LIFE, 10).add(Aspect.MAGIC, 5),
                new ItemStack(Items.WHEAT_SEEDS),
                new ItemStack(BlocksTC.vishroom),
                new ItemStack(ItemsTC.salisMundus)
        ));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "coalescence_matrix"), new InfusionRecipe(
                "TWOND_COALESCENCE_MATRIX",
                new ItemStack(BlocksTW.COALESCENCE_MATRIX_PRECURSOR),
                10,
                new AspectList().add(Aspect.FLUX, 200).add(Aspect.MAN, 100).add(Aspect.MAGIC, 200).add(Aspect.ENERGY, 100),
                new ItemStack(Blocks.GOLD_BLOCK),
                Ingredient.fromItem(ItemsTC.primordialPearl),
                new ItemStack(BlocksTC.inlay),
                new ItemStack(BlocksTC.inlay),
                new ItemStack(ItemsTC.voidSeed),
                new ItemStack(BlocksTC.inlay),
                new ItemStack(BlocksTC.inlay)
        ));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "vis_capacitor"), new InfusionRecipe(
                "TWOND_VIS_CAPACITOR",
                new ItemStack(BlocksTW.VIS_CAPACITOR),
                6,
                new AspectList().add(Aspect.VOID, 50).add(Aspect.AURA, 50).add(Aspect.MAGIC, 50),
                new ItemStack(BlocksTC.visBattery),
                new ItemStack(ItemsTC.primordialPearl),
                "plateThaumium",
                new ItemStack(ItemsTC.visResonator),
                "plateThaumium"
        ));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "primordial_pearl_growth"), new InfusionRecipe(
                "TWOND_PRIMORDIAL_SIPHON",
                new ItemStack(ItemsTC.primordialPearl, 1, 6),
                10,
                new AspectList().add(Aspect.AIR, 150).add(Aspect.FIRE, 150).add(Aspect.WATER, 150).add(Aspect.EARTH, 150).add(Aspect.ORDER, 150).add(Aspect.ENTROPY, 150),
                new ItemStack(ItemsTC.primordialPearl, 1, 7),
                new ItemStack(ItemsTW.PRIMORDIAL_GRAIN),
                new ItemStack(ItemsTC.salisMundus),
                ThaumcraftApiHelper.makeCrystal(Aspect.AIR),
                ThaumcraftApiHelper.makeCrystal(Aspect.EARTH),
                ThaumcraftApiHelper.makeCrystal(Aspect.FIRE),
                ThaumcraftApiHelper.makeCrystal(Aspect.WATER),
                ThaumcraftApiHelper.makeCrystal(Aspect.ORDER),
                ThaumcraftApiHelper.makeCrystal(Aspect.ENTROPY)
        ));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "primordial_siphon"), new InfusionRecipe(
                "TWOND_PRIMORDIAL_SIPHON@2",
                new ItemStack(BlocksTW.PRIMORDIAL_SIPHON),
                7,
                new AspectList().add(Aspect.AIR, 75).add(Aspect.FIRE, 75).add(Aspect.WATER, 75).add(Aspect.EARTH, 75).add(Aspect.ORDER, 75).add(Aspect.ENTROPY, 75),
                new ItemStack(BlocksTC.voidSiphon),
                new ItemStack(BlocksTC.metalAlchemical),
                new ItemStack(ItemsTC.mechanismComplex),
                new ItemStack(BlocksTC.metalAlchemical),
                new ItemStack(ItemsTC.nuggets, 1, 10)
        ));

        if (ConfigHandlerTW.sharing_tome.enableTome) {
            ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "sharing_tome"), new InfusionRecipe(
                    "TWOND_SHARING_TOME",
                    new ItemStack(ItemsTW.SHARING_TOME),
                    3,
                    new AspectList().add(Aspect.AIR, 20).add(Aspect.WATER, 20).add(Aspect.FIRE, 20).add(Aspect.EARTH, 20).add(Aspect.ORDER, 20).add(Aspect.ENTROPY, 20),
                    CraftingHelper.getIngredient(new ItemStack(ItemsTC.thaumonomicon)),
                    CraftingHelper.getIngredient(new ItemStack(ItemsTC.scribingTools)),
                    CraftingHelper.getIngredient(ItemsTC.amber),
                    CraftingHelper.getIngredient(ItemsTW.LETHE_WATER),
                    CraftingHelper.getIngredient(ItemsTC.amber)
            ));
        }

        if (ConfigHandlerTW.void_walker_boots.enableBoots) {
            ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "void_walker_boots"), new InfusionRecipe(
                    "TWOND_VOID_WALKER_BOOTS",
                    new ItemStack(ItemsTW.VOID_WALKER_BOOTS),
                    8,
                    new AspectList().add(Aspect.MOTION, 150).add(Aspect.FLIGHT, 150).add(Aspect.VOID, 50).add(Aspect.ELDRITCH, 50).add(Aspect.FLUX, 50),
                    CraftingHelper.getIngredient(new ItemStack(ItemsTC.travellerBoots)),
                    Ingredient.fromItem(ItemsTC.primordialPearl),
                    "plateVoid",
                    "plateVoid",
                    "plateVoid",
                    new ItemStack(ItemsTC.salisMundus),
                    "leather"));
        }

        if (ConfigHandlerTW.warp_ring.enableRing) {
            ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "warp_ring"), new InfusionRecipe(
                    "TWOND_WARP_RING",
                    new ItemStack(ItemsTW.WARP_RING),
                    6,
                    new AspectList().add(Aspect.LIFE, 50).add(Aspect.DARKNESS, 50).add(Aspect.VOID, 50).add(Aspect.FLUX, 10),
                    CraftingHelper.getIngredient(new ItemStack(ItemsTC.baubles, 1, 1)),
                    CraftingHelper.getIngredient(Items.SPIDER_EYE),
                    CraftingHelper.getIngredient(ThaumcraftApiHelper.makeCrystal(Aspect.FLUX)),
                    CraftingHelper.getIngredient(Items.MILK_BUCKET),
                    CraftingHelper.getIngredient(ThaumcraftApiHelper.makeCrystal(Aspect.VOID))
            ));
        }
    }

    private static void initInfusionEnchantmentRecipes() {
        InfusionEnchantmentRecipe voidflameInfusion = new InfusionEnchantmentRecipe(InfusionEnchantmentsTW.VOIDFLAME,
                new AspectList().add(Aspect.FIRE, 60).add(Aspect.VOID, 40),
                new IngredientNBTTC(new ItemStack(Items.ENCHANTED_BOOK)), new ItemStack(ItemsTW.PRIMORDIAL_GRAIN)
        );
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "voidflame_infusion"), voidflameInfusion);
        ThaumcraftApi.addFakeCraftingRecipe(new ResourceLocation(ThaumicWonders.MODID, "voidflame_infusion_fake"), new InfusionEnchantmentRecipe(voidflameInfusion, new ItemStack(Items.WOODEN_SWORD)));
    }

    private static void initSmelting() {
        GameRegistry.addSmelting(new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 0), new ItemStack(Items.IRON_INGOT, 3, 0), 1.0F);
        GameRegistry.addSmelting(new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 1), new ItemStack(Items.GOLD_INGOT, 3, 0), 1.0F);
        GameRegistry.addSmelting(new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 6), new ItemStack(ItemsTC.quicksilver, 3, 0), 1.0F);
        GameRegistry.addSmelting(new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 7), new ItemStack(Items.QUARTZ, 3, 0), 1.0F);
        GameRegistry.addSmelting(new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 8), new ItemStack(ItemsTC.ingots, 2, 1), 1.0F);

        ThaumcraftApi.addSmeltingBonus(new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 0), new ItemStack(Items.IRON_NUGGET, 1, 0));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 1), new ItemStack(Items.GOLD_NUGGET, 1, 0));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 6), new ItemStack(ItemsTC.nuggets, 1, 5));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 7), new ItemStack(ItemsTC.nuggets, 1, 9));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 8), new ItemStack(ItemsTC.nuggets, 1, 7));

        ThaumcraftApi.addSmeltingBonus(new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 32767), new ItemStack(ItemsTC.nuggets, 1, 10), 0.025F);
        ThaumcraftApi.addSmeltingBonus(new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 32767), ThaumcraftApiHelper.makeCrystal(Aspect.FLUX), 0.1F);

        if (OreDictionary.doesOreNameExist("ingotCopper") && !OreDictionary.getOres("ingotCopper", false).isEmpty()) {
            ItemStack stack = OreDictionary.getOres("ingotCopper", false).get(0);
            GameRegistry.addSmelting(new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 2), new ItemStack(stack.getItem(), 3, stack.getItemDamage()), 1.0F);
            ThaumcraftApi.addSmeltingBonus(new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 2), new ItemStack(ItemsTC.nuggets, 1, 1));
        }
        if (OreDictionary.doesOreNameExist("ingotTin") && !OreDictionary.getOres("ingotTin", false).isEmpty()) {
            ItemStack stack = OreDictionary.getOres("ingotTin", false).get(0);
            GameRegistry.addSmelting(new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 3), new ItemStack(stack.getItem(), 3, stack.getItemDamage()), 1.0F);
            ThaumcraftApi.addSmeltingBonus(new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 3), new ItemStack(ItemsTC.nuggets, 1, 2));
        }
        if (OreDictionary.doesOreNameExist("ingotSilver") && !OreDictionary.getOres("ingotSilver", false).isEmpty()) {
            ItemStack stack = OreDictionary.getOres("ingotSilver", false).get(0);
            GameRegistry.addSmelting(new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 4), new ItemStack(stack.getItem(), 3, stack.getItemDamage()), 1.0F);
            ThaumcraftApi.addSmeltingBonus(new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 4), new ItemStack(ItemsTC.nuggets, 1, 3));
        }
        if (OreDictionary.doesOreNameExist("ingotLead") && !OreDictionary.getOres("ingotLead", false).isEmpty()) {
            ItemStack stack = OreDictionary.getOres("ingotLead", false).get(0);
            GameRegistry.addSmelting(new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 5), new ItemStack(stack.getItem(), 3, stack.getItemDamage()), 1.0F);
            ThaumcraftApi.addSmeltingBonus(new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 5), new ItemStack(ItemsTC.nuggets, 1, 4));
        }
    }

    public static void initCatalyzationChamberRecipes() {
        //Alchemist Stone
        CatalyzationChamberRecipeRegistry.addAlchemistRecipe("oreIron", new ItemStack(ItemsTC.clusters, 1, 0));
        CatalyzationChamberRecipeRegistry.addAlchemistRecipe("oreGold", new ItemStack(ItemsTC.clusters, 1, 1));
        CatalyzationChamberRecipeRegistry.addAlchemistRecipe("oreCopper", new ItemStack(ItemsTC.clusters, 1, 2));
        CatalyzationChamberRecipeRegistry.addAlchemistRecipe("oreTin", new ItemStack(ItemsTC.clusters, 1, 3));
        CatalyzationChamberRecipeRegistry.addAlchemistRecipe("oreSilver", new ItemStack(ItemsTC.clusters, 1, 4));
        CatalyzationChamberRecipeRegistry.addAlchemistRecipe("oreLead", new ItemStack(ItemsTC.clusters, 1, 5));
        CatalyzationChamberRecipeRegistry.addAlchemistRecipe("oreCinnabar", new ItemStack(ItemsTC.clusters, 1, 6));

        //Alienist Stone
        CatalyzationChamberRecipeRegistry.addAlienistRecipe("oreIron", new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 0));
        CatalyzationChamberRecipeRegistry.addAlienistRecipe("oreGold", new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 1));
        CatalyzationChamberRecipeRegistry.addAlienistRecipe("oreCopper", new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 2));
        CatalyzationChamberRecipeRegistry.addAlienistRecipe("oreTin", new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 3));
        CatalyzationChamberRecipeRegistry.addAlienistRecipe("oreSilver", new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 4));
        CatalyzationChamberRecipeRegistry.addAlienistRecipe("oreLead", new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 5));
        CatalyzationChamberRecipeRegistry.addAlienistRecipe("oreCinnabar", new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 6));
        CatalyzationChamberRecipeRegistry.addAlienistRecipe("oreQuartz", new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 7));
        CatalyzationChamberRecipeRegistry.addAlienistRecipe(Ingredient.fromItem(ItemsTC.voidSeed), new ItemStack(ItemsTW.ELDRITCH_CLUSTER, 1, 8));

        //Transmuter Stone
        CatalyzationChamberRecipeRegistry.addTransmuterRecipe("oreIron", new ItemStack(Blocks.GOLD_ORE));
        CatalyzationChamberRecipeRegistry.addTransmuterRecipe("ingotIron", new ItemStack(Items.GOLD_INGOT));
        CatalyzationChamberRecipeRegistry.addTransmuterRecipe("blockIron", new ItemStack(Blocks.GOLD_BLOCK));
        CatalyzationChamberRecipeRegistry.addTransmuterRecipe("nuggetIron", new ItemStack(Items.GOLD_NUGGET));
        CatalyzationChamberRecipeRegistry.addTransmuterRecipe("oreGold", new ItemStack(Blocks.IRON_ORE));
        CatalyzationChamberRecipeRegistry.addTransmuterRecipe("ingotGold", new ItemStack(Items.IRON_INGOT));
        CatalyzationChamberRecipeRegistry.addTransmuterRecipe("blockGold", new ItemStack(Blocks.IRON_BLOCK));
        CatalyzationChamberRecipeRegistry.addTransmuterRecipe("nuggetGold", new ItemStack(Items.IRON_NUGGET));
        CatalyzationChamberRecipeRegistry.addTransmuterRecipe("oreTin", "oreCopper");
        CatalyzationChamberRecipeRegistry.addTransmuterRecipe("ingotTin", "ingotCopper");
        CatalyzationChamberRecipeRegistry.addTransmuterRecipe("blockTin", "blockCopper");
        CatalyzationChamberRecipeRegistry.addTransmuterRecipe("nuggetTin", "nuggetCopper");
        CatalyzationChamberRecipeRegistry.addTransmuterRecipe("oreCopper", "oreTin");
        CatalyzationChamberRecipeRegistry.addTransmuterRecipe("ingotCopper", "ingotTin");
        CatalyzationChamberRecipeRegistry.addTransmuterRecipe("blockCopper", "blockTin");
        CatalyzationChamberRecipeRegistry.addTransmuterRecipe("nuggetCopper", "nuggetTin");
        CatalyzationChamberRecipeRegistry.addTransmuterRecipe("oreSilver", "oreLead");
        CatalyzationChamberRecipeRegistry.addTransmuterRecipe("ingotSilver", "ingotLead");
        CatalyzationChamberRecipeRegistry.addTransmuterRecipe("blockSilver", "blockLead");
        CatalyzationChamberRecipeRegistry.addTransmuterRecipe("nuggetSilver", "nuggetLead");
        CatalyzationChamberRecipeRegistry.addTransmuterRecipe("oreLead", "oreSilver");
        CatalyzationChamberRecipeRegistry.addTransmuterRecipe("ingotLead", "ingotSilver");
        CatalyzationChamberRecipeRegistry.addTransmuterRecipe("blockLead", "blockSilver");
        CatalyzationChamberRecipeRegistry.addTransmuterRecipe("nuggetLead", "nuggetSilver");
    }

    public static void initMeatyOrb() {
        MeatyOrbEntryRegistry.addEntry(new ItemStack(Items.BEEF), 30);
        MeatyOrbEntryRegistry.addEntry(new ItemStack(Items.PORKCHOP), 25);
        MeatyOrbEntryRegistry.addEntry(new ItemStack(Items.CHICKEN), 20);
        MeatyOrbEntryRegistry.addEntry(new ItemStack(Items.MUTTON), 15);
        MeatyOrbEntryRegistry.addEntry(new ItemStack(Items.RABBIT), 10);
    }

}
