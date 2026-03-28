package site.scalarstudios.scalarpower.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class ExtractionRecipe extends SingleItemRecipe {
    public static final MapCodec<ExtractionRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                            Recipe.CommonInfo.MAP_CODEC.forGetter(o -> o.commonInfo),
                            Ingredient.CODEC.fieldOf("ingredient").forGetter(SingleItemRecipe::input),
                            ItemStackTemplate.CODEC.fieldOf("result").forGetter(ExtractionRecipe::resultTemplate))
                    .apply(i, ExtractionRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ExtractionRecipe> STREAM_CODEC = StreamCodec.composite(
            Recipe.CommonInfo.STREAM_CODEC,
            o -> o.commonInfo,
            Ingredient.CONTENTS_STREAM_CODEC,
            SingleItemRecipe::input,
            ItemStackTemplate.STREAM_CODEC,
            ExtractionRecipe::resultTemplate,
            ExtractionRecipe::new);

    public static final RecipeSerializer<ExtractionRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    public ExtractionRecipe(Recipe.CommonInfo commonInfo, Ingredient ingredient, ItemStackTemplate result) {
        super(commonInfo, ingredient, result);
    }

    private ItemStackTemplate resultTemplate() {
        return result();
    }

    @Override
    public RecipeType<ExtractionRecipe> getType() {
        return ScalarPowerRecipes.EXTRACTION_RECIPE_TYPE;
    }

    @Override
    public RecipeSerializer<ExtractionRecipe> getSerializer() {
        return ScalarPowerRecipes.EXTRACTION_RECIPE_SERIALIZER;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }
}

