package com.verdantartifice.thaumicwonders.common.crafting.accelerator;

import com.google.common.base.Preconditions;
import com.verdantartifice.thaumicwonders.common.crafting.IngredientHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;

import java.util.Objects;
import java.util.Random;

import static com.verdantartifice.thaumicwonders.common.tiles.devices.TilePrimordialAccelerator.MAX_TUNNELS;

public class AcceleratorRecipe {
    protected final Ingredient input;
    protected final ItemStack output;

    public AcceleratorRecipe(Ingredient input, ItemStack output) throws IllegalArgumentException {
        Preconditions.checkArgument(input != null && input != Ingredient.EMPTY, "Input cannot be null or empty");
        Preconditions.checkArgument(!output.isEmpty(), "Output cannot be empty");

        this.input = input;
        this.output = output;
    }

    public Ingredient getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output.copy();
    }

    public NonNullList<ItemStack> getOutputs(Random rand, int tunnelCount) {
        NonNullList<ItemStack> outputs = NonNullList.create();
        ItemStack output = getOutput();
        int count = output.getCount();
        output.setCount(1);
        for (int i = 0; i < count; i++) {
            outputs.add(output.copy());
            // 50-100% chance of second output
            if (rand.nextInt(2 * MAX_TUNNELS) < (MAX_TUNNELS + tunnelCount)) {
                outputs.add(output.copy());
            }
            // 0-50% chance of third output
            if (rand.nextInt(2 * MAX_TUNNELS) < tunnelCount) {
                outputs.add(output.copy());
            }
        }
        return outputs;
    }

    public boolean matches(ItemStack stack) {
        return getInput().test(stack);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getInput());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AcceleratorRecipe)) return false;
        AcceleratorRecipe that = (AcceleratorRecipe) o;
        return IngredientHelper.areIngredientsEqual(getInput(), that.getInput());
    }
}
