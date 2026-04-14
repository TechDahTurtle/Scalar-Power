package site.scalarstudios.scalarpower.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

public class LiquifyingRecipe extends SingleItemRecipe {
    public static final MapCodec<LiquifyingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                            Recipe.CommonInfo.MAP_CODEC.forGetter(o -> o.commonInfo),
                            Ingredient.CODEC.fieldOf("ingredient").forGetter(SingleItemRecipe::input),
                            ItemStackTemplate.CODEC.fieldOf("result").forGetter(LiquifyingRecipe::resultTemplate),
                            Codec.INT.fieldOf("spu_cost").forGetter(LiquifyingRecipe::spuCost))
                    .apply(i, LiquifyingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LiquifyingRecipe> STREAM_CODEC = StreamCodec.composite(
            Recipe.CommonInfo.STREAM_CODEC,
            o -> o.commonInfo,
            Ingredient.CONTENTS_STREAM_CODEC,
            SingleItemRecipe::input,
            ItemStackTemplate.STREAM_CODEC,
            LiquifyingRecipe::resultTemplate,
            ByteBufCodecs.VAR_INT,
            LiquifyingRecipe::spuCost,
            LiquifyingRecipe::new);

    public static final RecipeSerializer<LiquifyingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    private final int spuCost;

    public LiquifyingRecipe(Recipe.CommonInfo commonInfo, Ingredient ingredient, ItemStackTemplate result, int spuCost) {
        super(commonInfo, ingredient, result);
        this.spuCost = spuCost;
    }

    public int spuCost() {
        return spuCost;
    }

    private ItemStackTemplate resultTemplate() {
        return result();
    }

    @Override
    public RecipeType<LiquifyingRecipe> getType() {
        return ScalarPowerRecipes.LIQUIFYING_RECIPE_TYPE;
    }

    @Override
    public RecipeSerializer<LiquifyingRecipe> getSerializer() {
        return ScalarPowerRecipes.LIQUIFYING_RECIPE_SERIALIZER;
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

