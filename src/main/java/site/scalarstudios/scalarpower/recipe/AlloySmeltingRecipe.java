package site.scalarstudios.scalarpower.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import site.scalarstudios.scalarpower.content.alloysmelter.AlloySmeltingInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AlloySmeltingRecipe implements Recipe<AlloySmeltingInput> {
    public static final MapCodec<AlloySmeltingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                            Recipe.CommonInfo.MAP_CODEC.forGetter(o -> o.commonInfo),
                            Ingredient.CODEC.fieldOf("ingredient1").forGetter(AlloySmeltingRecipe::ingredient1),
                            Ingredient.CODEC.fieldOf("ingredient2").forGetter(AlloySmeltingRecipe::ingredient2),
                            Ingredient.CODEC.optionalFieldOf("ingredient3").forGetter(AlloySmeltingRecipe::ingredient3),
                            ItemStack.CODEC.fieldOf("result").forGetter(AlloySmeltingRecipe::result))
                    .apply(i, AlloySmeltingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AlloySmeltingRecipe> STREAM_CODEC = StreamCodec.composite(
            Recipe.CommonInfo.STREAM_CODEC,
            o -> o.commonInfo,
            Ingredient.CONTENTS_STREAM_CODEC,
            AlloySmeltingRecipe::ingredient1,
            Ingredient.CONTENTS_STREAM_CODEC,
            AlloySmeltingRecipe::ingredient2,
            ByteBufCodecs.optional(Ingredient.CONTENTS_STREAM_CODEC),
            AlloySmeltingRecipe::ingredient3,
            ItemStack.STREAM_CODEC,
            AlloySmeltingRecipe::result,
            AlloySmeltingRecipe::new);

    public static final RecipeSerializer<AlloySmeltingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    private final Recipe.CommonInfo commonInfo;
    private final Ingredient ingredient1;
    private final Ingredient ingredient2;
    private final Optional<Ingredient> ingredient3;
    private final ItemStack result;

    public AlloySmeltingRecipe(
            Recipe.CommonInfo commonInfo,
            Ingredient ingredient1,
            Ingredient ingredient2,
            Optional<Ingredient> ingredient3,
            ItemStack result) {
        this.commonInfo = commonInfo;
        this.ingredient1 = ingredient1;
        this.ingredient2 = ingredient2;
        this.ingredient3 = ingredient3;
        this.result = result;
    }

    public Ingredient ingredient1() {
        return ingredient1;
    }

    public Ingredient ingredient2() {
        return ingredient2;
    }

    public Optional<Ingredient> ingredient3() {
        return ingredient3;
    }

    private ItemStack result() {
        return result;
    }

    public boolean usesIngredient(ItemStack stack) {
        return ingredient1.test(stack) || ingredient2.test(stack) || ingredient3.map(ingredient -> ingredient.test(stack)).orElse(false);
    }

    public int[] findMatchingSlots(AlloySmeltingInput input) {
        List<Ingredient> ingredients = requiredIngredients();
        if (input.nonEmptyCount() > ingredients.size()) {
            return new int[0];
        }

        int[] remainingPerSlot = new int[input.size()];
        boolean[] consumedFromSlot = new boolean[input.size()];
        for (int slot = 0; slot < input.size(); slot++) {
            remainingPerSlot[slot] = input.getItem(slot).getCount();
        }

        int[] matchedSlots = new int[ingredients.size()];

        for (int ingredientIndex = 0; ingredientIndex < ingredients.size(); ingredientIndex++) {
            Ingredient ingredient = ingredients.get(ingredientIndex);
            boolean found = false;
            for (int slot = 0; slot < input.size(); slot++) {
                if (remainingPerSlot[slot] <= 0) {
                    continue;
                }
                if (!ingredient.test(input.getItem(slot))) {
                    continue;
                }

                remainingPerSlot[slot]--;
                consumedFromSlot[slot] = true;
                matchedSlots[ingredientIndex] = slot;
                found = true;
                break;
            }

            if (!found) {
                return new int[0];
            }
        }

        for (int slot = 0; slot < input.size(); slot++) {
            if (!input.getItem(slot).isEmpty() && !consumedFromSlot[slot]) {
                return new int[0];
            }
        }

        return matchedSlots;
    }

    @Override
    public boolean matches(AlloySmeltingInput input, Level level) {
        return findMatchingSlots(input).length > 0;
    }

    @Override
    public ItemStack assemble(AlloySmeltingInput input) {
        return result.copy();
    }

    @Override
    public boolean showNotification() {
        return true;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.FURNACE_MISC;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(requiredIngredients());
    }

    @Override
    public RecipeSerializer<AlloySmeltingRecipe> getSerializer() {
        return ScalarPowerRecipes.ALLOY_SMELTING_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<AlloySmeltingRecipe> getType() {
        return ScalarPowerRecipes.ALLOY_SMELTING_RECIPE_TYPE;
    }

    private List<Ingredient> requiredIngredients() {
        List<Ingredient> ingredients = new ArrayList<>(3);
        ingredients.add(ingredient1);
        ingredients.add(ingredient2);
        ingredient3.ifPresent(ingredients::add);
        return ingredients;
    }
}

