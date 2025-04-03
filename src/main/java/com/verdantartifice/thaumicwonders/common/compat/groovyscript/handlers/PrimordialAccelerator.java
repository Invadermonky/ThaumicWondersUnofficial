package com.verdantartifice.thaumicwonders.common.compat.groovyscript.handlers;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.verdantartifice.thaumicwonders.common.crafting.accelerator.AcceleratorRecipe;
import com.verdantartifice.thaumicwonders.common.crafting.accelerator.AcceleratorRecipeRegistry;
import com.verdantartifice.thaumicwonders.common.init.InitRecipes;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class PrimordialAccelerator extends VirtualizedRegistry<AcceleratorRecipe> {
    @GroovyBlacklist
    @Override
    public void onReload() {
        AcceleratorRecipeRegistry.removeAll();
        InitRecipes.initPrimordialAcceleratorRecipes();
    }

    @MethodDescription(
            type = MethodDescription.Type.ADDITION,
            example = @Example("item('thaumcraft:primordial_pearl:0'), item('thaumicwonders:primordial_grain') * 8")
    )
    public void addRecipe(IIngredient input, ItemStack output) {
        this.recipeBuilder().setInput(input).setOutput(output).register();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<AcceleratorRecipe> streamRecipes() {
        return new SimpleObjectStream<>(AcceleratorRecipeRegistry.getRecipes());
    }

    @MethodDescription(
            type = MethodDescription.Type.REMOVAL,
            example = @Example("item('thaumcraft:primordial_pearl')")
    )
    public void removeByInput(IIngredient input) {
        AcceleratorRecipeRegistry.removeByInput(input.toMcIngredient());
    }

    @MethodDescription(
            type = MethodDescription.Type.REMOVAL,
            example = @Example("item('thaumicwonders:primordial_grain')"),
            priority = 1001
    )
    public void removeByOutput(ItemStack output) {
        AcceleratorRecipeRegistry.removeByOutput(output);
    }

    @MethodDescription(
            type = MethodDescription.Type.REMOVAL,
            example = @Example(commented = true),
            priority = 1002
    )
    public void removeAll() {
        AcceleratorRecipeRegistry.removeAll();
    }

    @MethodDescription(
            type = MethodDescription.Type.ADDITION,
            example = @Example("setInput(item('thaumcraft:primordial_pearl:0')).setOutput(item('thaumicwonders:primordial_grain') * 8).register()")
    )
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<AcceleratorRecipe> {
        @Property
        private IIngredient input;
        @Property
        private ItemStack output;

        public RecipeBuilder() {
            this.input = IIngredient.EMPTY;
            this.output = ItemStack.EMPTY;
        }

        @RecipeBuilderMethodDescription(field = "input")
        public RecipeBuilder setInput(IIngredient input) {
            this.input = input;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "output")
        public RecipeBuilder setOutput(ItemStack output) {
            this.output = output;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Primordial Accelerator recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            msg.add(this.input == null || this.input == IIngredient.EMPTY, "");
            msg.add(this.output.isEmpty(), "");
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public @Nullable AcceleratorRecipe register() {
            if(this.validate()) {
                AcceleratorRecipe recipe = new AcceleratorRecipe(this.input.toMcIngredient(), this.output);
                AcceleratorRecipeRegistry.addRecipe(recipe);
                return recipe;
            }
            return null;
        }
    }
}
