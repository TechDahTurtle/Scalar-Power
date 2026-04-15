package site.scalarstudios.scalarpower.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public class LiquifyingRecipe implements Recipe<SingleRecipeInput> {
    private static final Codec<FluidAmount> FLUID_OUTPUT_CODEC = RecordCodecBuilder.create(i -> i.group(
            BuiltInRegistries.FLUID.byNameCodec().fieldOf("id").forGetter(FluidAmount::fluid),
            Codec.INT.fieldOf("amount").forGetter(FluidAmount::amount)
    ).apply(i, FluidAmount::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, FluidAmount> FLUID_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(Registries.FLUID),
            FluidAmount::fluid,
            ByteBufCodecs.VAR_INT,
            FluidAmount::amount,
            FluidAmount::new);

    public static final MapCodec<LiquifyingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                            Ingredient.CODEC.fieldOf("ingredient").forGetter(LiquifyingRecipe::input),
                            FLUID_OUTPUT_CODEC.fieldOf("output_fluid").forGetter(LiquifyingRecipe::outputFluidData),
                            Codec.INT.fieldOf("spu_cost").forGetter(LiquifyingRecipe::spuCost))
                    .apply(i, LiquifyingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LiquifyingRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            LiquifyingRecipe::input,
            FLUID_STREAM_CODEC,
            LiquifyingRecipe::outputFluidData,
            ByteBufCodecs.VAR_INT,
            LiquifyingRecipe::spuCost,
            LiquifyingRecipe::new);

    public static final RecipeSerializer<LiquifyingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    private final Ingredient input;
    private final FluidAmount outputFluid;
    private final int spuCost;

    private LiquifyingRecipe(Ingredient input, FluidAmount outputFluid, int spuCost) {
        this.input = input;
        this.outputFluid = outputFluid;
        this.spuCost = spuCost;
    }

    public LiquifyingRecipe(Ingredient input, FluidStack outputFluid, int spuCost) {
        this(input, new FluidAmount(outputFluid.getFluid(), outputFluid.getAmount()), spuCost);
    }

    private FluidAmount outputFluidData() {
        return outputFluid;
    }

    public Ingredient input() {
        return input;
    }

    public FluidStack outputFluid() {
        return new FluidStack(outputFluid.fluid(), outputFluid.amount());
    }

    public int spuCost() {
        return spuCost;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return this.input.test(input.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input) {
        return ItemStack.EMPTY;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
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

    private record FluidAmount(Fluid fluid, int amount) {
    }
}

