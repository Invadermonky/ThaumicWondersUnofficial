package com.verdantartifice.thaumicwonders.common.compat.groovyscript.handlers;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.crafting.voidbeacon.VoidBeaconEntry;
import com.verdantartifice.thaumicwonders.common.crafting.voidbeacon.VoidBeaconRegistry;
import com.verdantartifice.thaumicwonders.common.init.InitVoidBeacon;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.Nullable;

@RegistryDescription(linkGenerator = ThaumicWonders.MODID)
public class VoidBeacon extends VirtualizedRegistry<VoidBeaconEntry> {
    @Override
    public void onReload() {
        VoidBeaconRegistry.removeAll();
        InitVoidBeacon.registerDefaultEntries();
    }

    @MethodDescription(
            type = MethodDescription.Type.ADDITION,
            example = @Example("item('minecraft:stone')")
    )
    public void add(ItemStack stack) {
        new RecipeBuilder().setDrop(stack).register();
    }

    @MethodDescription(
            type = MethodDescription.Type.ADDITION,
            example = @Example("ore('stone')"),
            priority = 1001
    )
    public void add(OreDictIngredient ingredient) {
        new RecipeBuilder().setDrop(ingredient).register();
    }

    @MethodDescription(
            type = MethodDescription.Type.REMOVAL,
            example = @Example(commented = true),
            priority = 1002
    )
    public void remove(ItemStack stack) {
        VoidBeaconRegistry.removeEntry(stack);
    }

    @MethodDescription(
            type = MethodDescription.Type.REMOVAL,
            example = @Example(commented = true),
            priority = 1003
    )
    public void remove(OreDictIngredient ingredient) {
        VoidBeaconRegistry.removeEntry(ingredient.getOreDict());
    }

    @MethodDescription(
            type = MethodDescription.Type.REMOVAL,
            example = @Example(commented = true),
            priority = 1004
    )
    public void removeAll() {
        VoidBeaconRegistry.removeAll();
    }

    @RecipeBuilderDescription(
            example = {
                    @Example(".setDrop(item('minecraft:stone'))"),
                    @Example(".setDrop(ore('stone'))")
            }
    )
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<VoidBeaconEntry> {
        @Property
        private ItemStack stack;

        public RecipeBuilder() {
            this.stack = ItemStack.EMPTY;
        }

        public RecipeBuilder setDrop(ItemStack stack) {
            this.stack = stack;
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
        }

        @Override
        public @Nullable VoidBeaconEntry register() {
            if(this.validate()) {
                VoidBeaconEntry entry = new VoidBeaconEntry(this.stack);
                VoidBeaconRegistry.addEntry(entry);
                return entry;
            }
            return null;
        }
    }
}
