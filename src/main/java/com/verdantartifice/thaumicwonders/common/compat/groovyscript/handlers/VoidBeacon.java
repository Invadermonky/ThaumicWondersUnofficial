package com.verdantartifice.thaumicwonders.common.compat.groovyscript.handlers;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.crafting.voidbeacon.VoidBeaconEntry;
import com.verdantartifice.thaumicwonders.common.crafting.voidbeacon.VoidBeaconEntryRegistry;
import com.verdantartifice.thaumicwonders.common.init.InitVoidBeacon;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.Nullable;

@RegistryDescription(linkGenerator = ThaumicWonders.MODID)
public class VoidBeacon extends VirtualizedRegistry<VoidBeaconEntry> {
    @Override
    public void onReload() {
        VoidBeaconEntryRegistry.removeAll();
        InitVoidBeacon.registerDefaultEntries();
    }

    @MethodDescription(
            type = MethodDescription.Type.ADDITION,
            example = @Example("item('minecraft:stone'), 10")
    )
    public void add(ItemStack stack, int weight) {
        new RecipeBuilder().setDrop(stack).setWeight(weight).register();
    }

    @MethodDescription(
            type = MethodDescription.Type.ADDITION,
            example = @Example("ore('stone'), 10"),
            priority = 1001
    )
    public void add(OreDictIngredient ingredient, int weight) {
        new RecipeBuilder().setDrop(ingredient).setWeight(weight).register();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<VoidBeaconEntry> getRecipes() {
        return new SimpleObjectStream<>(VoidBeaconEntryRegistry.getVoidBeaconEntries());
    }

    @MethodDescription(
            type = MethodDescription.Type.REMOVAL,
            example = @Example(commented = true),
            priority = 1002
    )
    public void remove(ItemStack stack) {
        VoidBeaconEntryRegistry.removeEntry(stack);
    }

    @MethodDescription(
            type = MethodDescription.Type.REMOVAL,
            example = @Example(commented = true),
            priority = 1003
    )
    public void remove(OreDictIngredient ingredient) {
        VoidBeaconEntryRegistry.removeEntry(ingredient.getOreDict());
    }

    @MethodDescription(
            type = MethodDescription.Type.REMOVAL,
            example = @Example(commented = true),
            priority = 1004
    )
    public void removeAll() {
        VoidBeaconEntryRegistry.removeAll();
    }

    @RecipeBuilderDescription(
            example = {
                    @Example(".setDrop(item('minecraft:stone')).setWeight(10)"),
                    @Example(".setDrop(ore('stone')).setWeight(10)")
            }
    )
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<VoidBeaconEntry> {
        @Property
        private ItemStack stack;
        @Property(comp = @Comp(gt = 0))
        private int weight;

        public RecipeBuilder() {
            this.stack = ItemStack.EMPTY;
            weight = 0;
        }

        public RecipeBuilder setDrop(ItemStack stack) {
            this.stack = stack;
            return this;
        }

        public RecipeBuilder setWeight(int weight) {
            this.weight = weight;
            return this;
        }

        public RecipeBuilder setDrop(OreDictIngredient oreDictIngredient) {
            NonNullList<ItemStack> stacks = OreDictionary.getOres(oreDictIngredient.getOreDict());
            if(!stacks.isEmpty()) {
                this.stack = stacks.get(0);
            }
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Void Beacon entry";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            msg.add(this.stack.isEmpty(), "Void Beacon entry cannot be empty");
            msg.add(this.weight <= 0, "Void Beacon entry weight must be greater than 0");
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public @Nullable VoidBeaconEntry register() {
            if(this.validate()) {
                VoidBeaconEntry entry = new VoidBeaconEntry(this.stack, this.weight);
                VoidBeaconEntryRegistry.addEntry(entry);
                return entry;
            }
            return null;
        }
    }
}
