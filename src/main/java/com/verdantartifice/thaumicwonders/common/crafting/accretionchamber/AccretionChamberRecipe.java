package com.verdantartifice.thaumicwonders.common.crafting.accretionchamber;

import com.google.common.base.Preconditions;
import com.verdantartifice.thaumicwonders.common.crafting.IngredientHelper;
import com.verdantartifice.thaumicwonders.common.crafting.WeightedEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import java.time.temporal.ValueRange;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class AccretionChamberRecipe {

    protected final Ingredient input;
    protected final WeightedEntry[] outputEntries;
    protected final int aer;
    protected final int aqua;
    protected final int ignis;
    protected final int terra;
    protected final int ordo;
    protected final int perditio;

    private final int totalWeight;

    public AccretionChamberRecipe(Ingredient input, int aer, int aqua, int ignis, int terra, int ordo, int perditio, WeightedEntry... outputEntries) throws IllegalArgumentException {
        Preconditions.checkArgument(input != null && input != Ingredient.EMPTY, "Input cannot be null or empty");
        ValueRange aspectRange = ValueRange.of(0, 250);
        Preconditions.checkArgument(aspectRange.isValidIntValue(aer), "Aer aspect amount must be between 0 and 250");
        Preconditions.checkArgument(aspectRange.isValidIntValue(aqua), "Aqua aspect amount must be between 0 and 250");
        Preconditions.checkArgument(aspectRange.isValidIntValue(ignis), "Ignis aspect amount must be between 0 and 250");
        Preconditions.checkArgument(aspectRange.isValidIntValue(terra), "Terra aspect amount must be between 0 and 250");
        Preconditions.checkArgument(aspectRange.isValidIntValue(ordo), "Ordo aspect amount must be between 0 and 250");
        Preconditions.checkArgument(aspectRange.isValidIntValue(perditio), "Perditio aspect amount must be between 0 and 250");
        Preconditions.checkArgument(outputEntries.length > 0, "An output must be defined");

        this.input = input;
        this.outputEntries = outputEntries;
        this.aer = aer;
        this.aqua = aqua;
        this.ignis = ignis;
        this.terra = terra;
        this.ordo = ordo;
        this.perditio = perditio;
        this.totalWeight = Arrays.stream(outputEntries).mapToInt(WeightedEntry::getWeight).sum();
    }

    public AccretionChamberRecipe(Ingredient input, int aspectUsage, WeightedEntry... outputEntries) throws IllegalArgumentException {
        this(input, aspectUsage, aspectUsage, aspectUsage, aspectUsage, aspectUsage, aspectUsage, outputEntries);
    }

    public AccretionChamberRecipe(Ingredient input, WeightedEntry... outputEntries) throws IllegalArgumentException {
        this(input, 125, outputEntries);
    }

    public Ingredient getInput() {
        return input;
    }

    public WeightedEntry[] getOutputEntries() {
        return outputEntries;
    }

    public int getAer() {
        return aer;
    }

    public int getAqua() {
        return aqua;
    }

    public int getIgnis() {
        return ignis;
    }

    public int getTerra() {
        return terra;
    }

    public int getOrdo() {
        return ordo;
    }

    public int getPerditio() {
        return perditio;
    }

    public AspectList getAspectList() {
        return new AspectList()
                .add(Aspect.AIR, getAer())
                .add(Aspect.WATER, getAqua())
                .add(Aspect.FIRE, getIgnis())
                .add(Aspect.EARTH, getTerra())
                .add(Aspect.ORDER, getOrdo())
                .add(Aspect.ENTROPY, getPerditio());
    }

    public boolean matchesInput(ItemStack stack) {
        return this.getInput().test(stack);
    }

    public boolean matchesOutput(ItemStack stack) {
        return Arrays.stream(this.getOutputEntries()).anyMatch(entry -> ItemStack.areItemStacksEqual(entry.getStack(), stack));
    }

    public ItemStack getOutput(Random rand) {
        int weight = rand.nextInt(this.totalWeight);
        for(WeightedEntry entry : this.getOutputEntries()) {
            if(weight < entry.getWeight()) {
                return entry.getStack();
            } else {
                weight -= entry.getWeight();
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AccretionChamberRecipe)) return false;
        AccretionChamberRecipe that = (AccretionChamberRecipe) o;
        return IngredientHelper.areIngredientsEqual(getInput(), that.getInput());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(input);
    }
}
