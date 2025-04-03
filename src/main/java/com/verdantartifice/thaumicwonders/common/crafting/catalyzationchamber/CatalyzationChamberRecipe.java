package com.verdantartifice.thaumicwonders.common.crafting.catalyzationchamber;

import com.google.common.base.Preconditions;
import com.verdantartifice.thaumicwonders.common.crafting.IngredientHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;

import javax.annotation.Nonnull;
import java.util.Objects;

public class CatalyzationChamberRecipe {
    private final Ingredient ingredient;
    private final Ingredient catalyst;
    private final ItemStack output;
    private final int fluxChance;
    private final int sparkleColor;

    public CatalyzationChamberRecipe(@Nonnull Ingredient ingredient, @Nonnull Ingredient catalyst, @Nonnull ItemStack output, int fluxChance, @Nullable Aspect sparkle) throws IllegalArgumentException {
        Preconditions.checkArgument(ingredient != Ingredient.EMPTY, "Ingredient cannot be empty");
        Preconditions.checkArgument(catalyst != Ingredient.EMPTY, "Catalyst cannot be empty");
        Preconditions.checkArgument(!output.isEmpty(), "Output cannot be empty");
        Preconditions.checkArgument(fluxChance >= 0, "Flux chance cannot be less than 0");

        this.ingredient = ingredient;
        this.catalyst = catalyst;
        this.output = output;
        this.fluxChance = fluxChance;
        this.sparkleColor = sparkle == null ? Aspect.ORDER.getColor() : sparkle.getColor();
    }

    public CatalyzationChamberRecipe(Ingredient ingredient, Ingredient catalyst, ItemStack output, int fluxChance) {
        this(ingredient, catalyst, output, fluxChance, Aspect.ORDER);
    }

    public CatalyzationChamberRecipe(Ingredient ingredient, Ingredient catalyst, ItemStack output, Aspect sparkle) {
        this(ingredient, catalyst, output, 10, sparkle);
    }

    public CatalyzationChamberRecipe(Ingredient ingredient, Ingredient catalyst, ItemStack output) {
        this(ingredient, catalyst, output, 10);
    }

    public Ingredient getInput() {
        return ingredient;
    }

    public Ingredient getCatalyst() {
        return catalyst;
    }

    public ItemStack getOutput() {
        return output.copy();
    }

    public int getFluxChance() {
        return fluxChance;
    }

    public int getSparkleColor() {
        return sparkleColor;
    }

    public boolean matches(ItemStack input, ItemStack catalyst) {
        return this.getInput().test(input) && this.getCatalyst().test(catalyst);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CatalyzationChamberRecipe)) return false;
        CatalyzationChamberRecipe that = (CatalyzationChamberRecipe) o;
        return IngredientHelper.areIngredientsEqual(getInput(), that.getInput()) && IngredientHelper.areIngredientsEqual(getCatalyst(), that.getCatalyst());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getInput(), getCatalyst());
    }
}
