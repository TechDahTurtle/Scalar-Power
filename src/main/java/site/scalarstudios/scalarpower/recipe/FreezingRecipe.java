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
import net.minecraft.world.item.ItemStackTemplate;
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

public class FreezingRecipe implements Recipe<SingleRecipeInput> {

    private static final Codec<FluidAmount> FLUID_INPUT_CODEC = RecordCodecBuilder.create(i -> i.group(
            BuiltInRegistries.FLUID.byNameCodec().fieldOf("id").forGetter(FluidAmount::fluid),
            Codec.INT.fieldOf("amount").forGetter(FluidAmount::amount)
    ).apply(i, FluidAmount::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, FluidAmount> FLUID_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(Registries.FLUID),
            FluidAmount::fluid,
            ByteBufCodecs.VAR_INT,
            FluidAmount::amount,
            FluidAmount::new);

    public static final MapCodec<FreezingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            FLUID_INPUT_CODEC.fieldOf("input_fluid").forGetter(FreezingRecipe::inputFluidData),
            ItemStackTemplate.CODEC.fieldOf("output").forGetter(FreezingRecipe::outputTemplate),
            Codec.INT.fieldOf("spu_cost").forGetter(FreezingRecipe::spuCost)
    ).apply(i, FreezingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FreezingRecipe> STREAM_CODEC = StreamCodec.composite(
            FLUID_STREAM_CODEC,
            FreezingRecipe::inputFluidData,
            ItemStackTemplate.STREAM_CODEC,
            FreezingRecipe::outputTemplate,
            ByteBufCodecs.VAR_INT,
            FreezingRecipe::spuCost,
            (inputFluid, output, spuCost) -> new FreezingRecipe(inputFluid, output, spuCost));

    public static final RecipeSerializer<FreezingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    private final FluidAmount inputFluid;
    private final ItemStackTemplate output;
    private final int spuCost;

    private FreezingRecipe(FluidAmount inputFluid, ItemStackTemplate output, int spuCost) {
        this.inputFluid = inputFluid;
        this.output = output;
        this.spuCost = spuCost;
    }

    public FreezingRecipe(FluidStack inputFluid, ItemStackTemplate output, int spuCost) {
        this(new FluidAmount(inputFluid.getFluid(), inputFluid.getAmount()), output, spuCost);
    }

    private FluidAmount inputFluidData() {
        return inputFluid;
    }

    public FluidStack inputFluid() {
        return new FluidStack(inputFluid.fluid(), inputFluid.amount());
    }

    private ItemStackTemplate outputTemplate() {
        return output;
    }

    public int spuCost() {
        return spuCost;
    }

    public ItemStack resultItem() {
        return output.create();
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input) {
        return output.create();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public RecipeSerializer<FreezingRecipe> getSerializer() {
        return ScalarPowerRecipes.FREEZING_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<FreezingRecipe> getType() {
        return ScalarPowerRecipes.FREEZING_RECIPE_TYPE;
    }

    private record FluidAmount(Fluid fluid, int amount) {
    }
}

