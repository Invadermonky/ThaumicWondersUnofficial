package com.verdantartifice.thaumicwonders.common.compat.groovyscript.handlers;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect.AspectStack;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.verdantartifice.thaumicwonders.common.config.ConfigHandlerTW;
import com.verdantartifice.thaumicwonders.common.crafting.catalyzationchamber.CatalyzationChamberRecipe;
import com.verdantartifice.thaumicwonders.common.crafting.catalyzationchamber.CatalyzationChamberRegistry;
import com.verdantartifice.thaumicwonders.common.init.InitRecipes;
import com.verdantartifice.thaumicwonders.common.items.ItemsTW;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;

public class CatalyzationChamber extends VirtualizedRegistry<CatalyzationChamberRecipe> {
    @GroovyBlacklist
    @Override
    public void onReload() {
        CatalyzationChamberRegistry.removeAll();
        InitRecipes.initCatalyzationChamberRecipes();
    }

    @MethodDescription(
            type = MethodDescription.Type.ADDITION,
            example = {
                    @Example("item('minecraft:iron_ore'), item('thaumicwonders:alchemist_stone'), item('thaumcraft:cluster:0'), 50, aspect('ordo')"),
                    @Example("ore('oreIron'), item('thaumicwonders:alchemist_stone'), item('thaumcraft:cluster:0', 50), aspect('ordo')")
            }
    )
    public void addRecipe(IIngredient input, IIngredient catalyst, ItemStack output, int fluxChance, @Nullable AspectStack color) {
        this.recipeBuilder()
                .setInput(input)
                .setCatalyst(catalyst)
                .setOutput(output)
                .setFluxChance(fluxChance)
                .setParticleColor(color)
                .register();
    }

    @MethodDescription(
            type = MethodDescription.Type.ADDITION,
            example = {
                    @Example("item('minecraft:iron_ore'), item('thaumicwonders:alchemist_stone'), item('thaumcraft:cluster:0'), 50"),
                    @Example("ore('oreIron'), item('thaumicwonders:alchemist_stone'), item('thaumcraft:cluster:0', 50)")
            },
            priority = 1001
    )
    public void addRecipe(IIngredient input, IIngredient catalyst, ItemStack output, int fluxChance) {
        addRecipe(input, catalyst, output, fluxChance, null);
    }

    @MethodDescription(
            type = MethodDescription.Type.ADDITION,
            example = {
                    @Example("item('minecraft:iron_ore'), item('thaumcraft:cluster:0')"),
                    @Example("ore('oreIron'), item('thaumcraft:cluster:0')")
            },
            priority = 1002
    )
    public void addAlchemistRecipe(IIngredient input, ItemStack output) {
        addRecipe(input, IngredientHelper.toIIngredient(new ItemStack(ItemsTW.ALCHEMIST_STONE, 1, Short.MAX_VALUE)), output, ConfigHandlerTW.alchemist_stone.defaultFluxChance, new AspectStack(Aspect.ORDER));
    }

    @MethodDescription(
            type = MethodDescription.Type.ADDITION,
            example = {
                    @Example("item('minecraft:iron_ore'), item('thaumicwonders:eldritch_cluster:0')"),
                    @Example("ore('oreIron'), item('thaumicwonders:eldritch_cluster:0')")
            },
            priority = 1002
    )
    public void addAlienistRecipe(IIngredient input, ItemStack output) {
        addRecipe(input, IngredientHelper.toIIngredient(new ItemStack(ItemsTW.ALIENIST_STONE, 1, Short.MAX_VALUE)), output, ConfigHandlerTW.alienist_stone.defaultFluxChance, new AspectStack(Aspect.FLUX));
    }

    @MethodDescription(
            type = MethodDescription.Type.ADDITION,
            example = {
                    @Example("item('minecraft:iron_ingot'), item('minecraft:gold_ingot')"),
                    @Example("ore('ingotIron'), item('minecraft:gold_ingot')")
            },
            priority = 1002
    )
    public void addTransmuterRecipe(IIngredient input, ItemStack output) {
        addRecipe(input, IngredientHelper.toIIngredient(new ItemStack(ItemsTW.TRANSMUTER_STONE, 1, Short.MAX_VALUE)), output, ConfigHandlerTW.transmuter_stone.defaultFluxChance, new AspectStack(Aspect.EXCHANGE));
    }

    @MethodDescription(
            type = MethodDescription.Type.REMOVAL,
            example = {
                    @Example("item('minecraft:iron_ingot')"),
                    @Example("ore('ingotIron')")
            }
    )
    public void removeByInput(IIngredient input) {
        CatalyzationChamberRegistry.removeByInput(input.toMcIngredient());
    }

    @MethodDescription(
            type = MethodDescription.Type.REMOVAL,
            example = {
                    @Example("item('minecraft:iron_ingot'), item('thaumicwonders:alchemist_stone')"),
                    @Example("ore('ingotIron'), item('thaumicwonders:alchemist_stone')")
            }
    )
    public void removeByInput(IIngredient input, IIngredient catalyst) {
        CatalyzationChamberRegistry.removeByInput(input.toMcIngredient(), catalyst.toMcIngredient());
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("item('minecraft:iron_ingot')"), priority = 1001)
    public void removeByOutput(ItemStack output) {
        CatalyzationChamberRegistry.removeByOutput(output);
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("item('minecraft:iron_ingot'), item('thaumicwonders:alchemist_stone')"), priority = 1001)
    public void removeByOutput(ItemStack output, IIngredient catalyst) {
        CatalyzationChamberRegistry.removeByOutput(output, catalyst.toMcIngredient());
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example(commented = true), priority = 1002)
    public void removeAll() {
        CatalyzationChamberRegistry.removeAll();
    }

    @RecipeBuilderDescription(
            example = {
                    @Example(".setInput(item('minecraft:iron_ore')).setCatalyst(item('thaumicwonders:alchemist_stone')).setOutput(item('thaumcraft:cluster:0')).setFluxChance(50).setParticleColor(aspect('ordo'))"),
                    @Example(".setInput(ore('oreIron')).setCatalyst(item('thaumicwonders:alchemist_stone')).setOutput(item('thaumcraft:cluster:0')).setFluxChance(50).setParticleColor(aspect('ordo'))")
            }
    )
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<CatalyzationChamberRecipe> {
        @Property
        private IIngredient input;
        @Property
        private IIngredient catalyst;
        @Property
        private ItemStack output;
        @Property(comp = @Comp(gte = 0))
        private int fluxChance;
        @Property
        private AspectStack particleColor;

        @GroovyBlacklist
        public RecipeBuilder() {
            this.input = IIngredient.EMPTY;
            this.catalyst = IIngredient.EMPTY;
            this.output = ItemStack.EMPTY;
            this.fluxChance = 0;
            this.particleColor = new AspectStack(Aspect.ORDER);
        }

        @RecipeBuilderMethodDescription(field = "input")
        public RecipeBuilder setInput(IIngredient input) {
            this.input = input;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "catalyst")
        public RecipeBuilder setCatalyst(IIngredient catalyst) {
            this.catalyst = catalyst;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "output")
        public RecipeBuilder setOutput(ItemStack output) {
            this.output = output;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "fluxChance")
        public RecipeBuilder setFluxChance(int fluxChance) {
            this.fluxChance = fluxChance;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "sparkleColor")
        public RecipeBuilder setParticleColor(AspectStack aspect) {
            this.particleColor = aspect;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Catalyzation Chamber recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            msg.add(this.input == IIngredient.EMPTY, "Ingredient cannot be empty");
            msg.add(this.catalyst == IIngredient.EMPTY, "Catalyst cannot be empty");
            msg.add(this.output.isEmpty(), "Output cannot be empty");
            msg.add(fluxChance < 0, "Flux chance cannot be less than 0");
        }

        @Override
        public @Nullable CatalyzationChamberRecipe register() {
            if(this.validate()) {
                CatalyzationChamberRecipe recipe = new CatalyzationChamberRecipe(
                        this.input.toMcIngredient(),
                        this.catalyst.toMcIngredient(),
                        this.output,
                        this.fluxChance,
                        this.particleColor != null ? this.particleColor.getAspect() : null
                );
                CatalyzationChamberRegistry.addRecipe(recipe);
                return recipe;
            }
            return null;
        }
    }
}
