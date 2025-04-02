package com.verdantartifice.thaumicwonders.common.compat.groovyscript.handlers;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.verdantartifice.thaumicwonders.common.crafting.WeightedEntry;
import com.verdantartifice.thaumicwonders.common.crafting.accretionchamber.PrimordialAccretionChamberRecipe;
import com.verdantartifice.thaumicwonders.common.crafting.accretionchamber.PrimordialAccretionChamberRegistry;
import com.verdantartifice.thaumicwonders.common.init.InitRecipes;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.List;

public class PrimordialAccretionChamber extends VirtualizedRegistry<PrimordialAccretionChamberRecipe> {
    @GroovyBlacklist
    @Override
    public void onReload() {
        PrimordialAccretionChamberRegistry.removeAll();
        InitRecipes.initPrimordialAccretionChamberRecipes();
    }

    @MethodDescription(
            type = MethodDescription.Type.ADDITION,
            example = @Example("item('thaumicwonders:primordial_grain'), item('thaumcraft:primordial_pearl:7'), 125, 125, 125, 125, 125, 125")
    )
    public void addRecipe(IIngredient input, ItemStack output, int aer, int aqua, int ignis, int terra, int ordo, int perditio) {
        this.recipeBuilder().setInput(input)
                .setAer(aer).setAqua(aqua).setIgnis(ignis).setTerra(terra).setOrdo(ordo).setPerditio(perditio)
                .addOutput(output, 1)
                .register();
    }

    @MethodDescription(
            type = MethodDescription.Type.ADDITION,
            example = @Example("item('thaumicwonders:primordial_grain'), item('thaumcraft:primordial_pearl:7'), 125"),
            priority = 1001
    )
    public void addRecipe(IIngredient input, ItemStack output, int aspectCost) {
        addRecipe(input, output, aspectCost, aspectCost, aspectCost, aspectCost, aspectCost, aspectCost);
    }



    @MethodDescription(
            type = MethodDescription.Type.REMOVAL,
            example = @Example("item('thaumicwonders:primordial_grain')")
    )
    public void removeByInput(IIngredient input) {
        PrimordialAccretionChamberRegistry.removeByInput(input.toMcIngredient());
    }

    @MethodDescription(
            type = MethodDescription.Type.REMOVAL,
            example = @Example("item('thaumcraft:primordial_pearl:*')"),
            priority = 1001
    )
    public void removeByOutput(ItemStack output) {
        PrimordialAccretionChamberRegistry.removeByOutput(output);
    }

    @MethodDescription(
            type = MethodDescription.Type.REMOVAL,
            example = @Example(commented = true),
            priority = 1002
    )
    public void removeAll() {
        PrimordialAccretionChamberRegistry.removeAll();
    }

    @RecipeBuilderDescription(
            example = @Example(".setInput().setOutput(item('thaumcraft:primordial_pearl:5'), 1).setOutput(item('thaumcraft:primordial_pearl:6'), 2).setOutput(item('thaumcraft:primordial_pearl:7'), 7).setAer(125).setAqua(125).setIgnis(125).setTerra(125).setOrdo(125).setPerditio(125).register()")
    )
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<PrimordialAccretionChamberRecipe> {
        @Property
        private IIngredient input;
        @Property
        private final List<WeightedEntry> outputs;
        @Property(comp = @Comp(gte = 0, lte = 250))
        private int aer;
        @Property(comp = @Comp(gte = 0, lte = 250))
        private int aqua;
        @Property(comp = @Comp(gte = 0, lte = 250))
        private int ignis;
        @Property(comp = @Comp(gte = 0, lte = 250))
        private int terra;
        @Property(comp = @Comp(gte = 0, lte = 250))
        private int ordo;
        @Property(comp = @Comp(gte = 0, lte = 250))
        private int perditio;

        public RecipeBuilder() {
            this.input = IIngredient.EMPTY;
            this.outputs = new ArrayList<>();
            this.aer = 0;
            this.aqua = 0;
            this.ignis = 0;
            this.terra = 0;
            this.ordo = 0;
            this.perditio = 0;
        }

        @RecipeBuilderMethodDescription(field = "input")
        public RecipeBuilder setInput(IIngredient input) {
            this.input = input;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "outputs")
        public RecipeBuilder addOutput(ItemStack output, int weight) {
            this.outputs.add(new WeightedEntry(output, Math.max(weight, 1)));
            return this;
        }

        @RecipeBuilderMethodDescription(field = "aer")
        public RecipeBuilder setAer(int aer) {
            this.aer = aer;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "aqua")
        public RecipeBuilder setAqua(int aqua) {
            this.aqua = aqua;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "ignis")
        public RecipeBuilder setIgnis(int ignis) {
            this.ignis = ignis;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "terra")
        public RecipeBuilder setTerra(int terra) {
            this.terra = terra;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "ordo")
        public RecipeBuilder setOrdo(int ordo) {
            this.ordo = ordo;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "perditio")
        public RecipeBuilder setPerditio(int perditio) {
            this.perditio = perditio;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Primordial Accretion Chamber recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            msg.add(this.input == null || this.input == IIngredient.EMPTY, "Input cannot be empty or null");
            msg.add(this.output.isEmpty(), "Output cannot be empty");
            msg.add(this.outputs.stream().anyMatch(output -> output.getStack().isEmpty()), "Invalid output found. Outputs cannot be empty.");
            ValueRange aspectRange = ValueRange.of(0, 250);
            msg.add(aspectRange.isValidIntValue(this.aer), "Aer aspect amount must be between 0 and 250");
            msg.add(aspectRange.isValidIntValue(this.aqua), "Aqua aspect amount must be between 0 and 250");
            msg.add(aspectRange.isValidIntValue(this.ignis), "Ignis aspect amount must be between 0 and 250");
            msg.add(aspectRange.isValidIntValue(this.terra), "Terra aspect amount must be between 0 and 250");
            msg.add(aspectRange.isValidIntValue(this.ordo), "Ordo aspect amount must be between 0 and 250");
            msg.add(aspectRange.isValidIntValue(this.perditio), "Perditio aspect amount must be between 0 and 250");
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public @Nullable PrimordialAccretionChamberRecipe register() {
            if(this.validate()) {
                PrimordialAccretionChamberRecipe recipe = new PrimordialAccretionChamberRecipe(
                        this.input.toMcIngredient(),
                        this.aer, this.aqua, this.ignis, this.terra, this.ordo, this.perditio,
                        this.outputs.toArray(new WeightedEntry[0])
                );
                PrimordialAccretionChamberRegistry.addRecipe(recipe);
                return recipe;
            }
            return null;
        }
    }
}
