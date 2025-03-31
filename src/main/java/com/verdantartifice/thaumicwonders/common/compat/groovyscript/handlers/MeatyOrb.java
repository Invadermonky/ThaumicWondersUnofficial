package com.verdantartifice.thaumicwonders.common.compat.groovyscript.handlers;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.verdantartifice.thaumicwonders.ThaumicWonders;
import com.verdantartifice.thaumicwonders.common.crafting.meatyorb.MeatyOrbEntry;
import com.verdantartifice.thaumicwonders.common.crafting.meatyorb.MeatyOrbRegistry;
import com.verdantartifice.thaumicwonders.common.init.InitRecipes;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription(linkGenerator = ThaumicWonders.MODID)
public class MeatyOrb extends VirtualizedRegistry<MeatyOrbEntry> {
    @GroovyBlacklist
    @Override
    public void onReload() {
        MeatyOrbRegistry.removeAll();
        InitRecipes.initMeatyOrb();
    }

    @MethodDescription(
            type = MethodDescription.Type.ADDITION,
            example = @Example("item('minecraft:beef'), 30")
    )
    public void add(ItemStack stack, int weight) {
        new RecipeBuilder().setMeat(stack).setWeight(weight).register();
    }

    @MethodDescription(
            type = MethodDescription.Type.REMOVAL,
            example = @Example("item('minecraft:beef')"),
            priority = 1001
    )
    public void remove(ItemStack stack) {
        MeatyOrbRegistry.removeEntry(stack);
    }

    @MethodDescription(
            type = MethodDescription.Type.REMOVAL,
            example = @Example(commented = true),
            priority = 1002
    )
    public void removeAll() {
        MeatyOrbRegistry.removeAll();
    }

    @RecipeBuilderDescription(
            example = @Example(".setMeat(item('minecraft:beef')).setWeight(30)")
    )
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<MeatyOrbEntry> {
        @Property
        private ItemStack meatStack;
        @Property(comp = @Comp(gt = 0))
        private int weight;

        @GroovyBlacklist
        public RecipeBuilder() {
            this.meatStack = ItemStack.EMPTY;
            this.weight = 1;
        }

        @RecipeBuilderMethodDescription(field = "meatStack")
        public RecipeBuilder setMeat(ItemStack meat) {
            this.meatStack = meat;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "weight")
        public RecipeBuilder setWeight(int weight) {
            this.weight = weight;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Meaty Orb entry";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            msg.add(this.meatStack.isEmpty(), "Meat drop cannot be empty");
            msg.add(this.weight <= 0, "Weight must be greather than 0");
        }

        @Override
        public @Nullable MeatyOrbEntry register() {
            if(this.validate()) {
                MeatyOrbEntry entry = new MeatyOrbEntry(this.meatStack, this.weight);
                MeatyOrbRegistry.addEntry(entry);
                return entry;
            }
            return null;
        }
    }
}
