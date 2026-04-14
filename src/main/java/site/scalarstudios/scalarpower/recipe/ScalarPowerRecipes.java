package site.scalarstudios.scalarpower.recipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import site.scalarstudios.scalarpower.ScalarPower;

public final class ScalarPowerRecipes {
    public static final Identifier ALLOY_SMELTING_ID = Identifier.fromNamespaceAndPath(ScalarPower.MODID, "alloy_smelting");
    public static final RecipeType<AlloySmeltingRecipe> ALLOY_SMELTING_RECIPE_TYPE = RecipeType.simple(ALLOY_SMELTING_ID);
    public static final RecipeSerializer<AlloySmeltingRecipe> ALLOY_SMELTING_RECIPE_SERIALIZER = AlloySmeltingRecipe.SERIALIZER;

    public static final Identifier EXTRACTION_ID = Identifier.fromNamespaceAndPath(ScalarPower.MODID, "extracting");
    public static final RecipeType<ExtractionRecipe> EXTRACTION_RECIPE_TYPE = RecipeType.simple(EXTRACTION_ID);
    public static final RecipeSerializer<ExtractionRecipe> EXTRACTION_RECIPE_SERIALIZER = ExtractionRecipe.SERIALIZER;

    public static final Identifier GRINDING_ID = Identifier.fromNamespaceAndPath(ScalarPower.MODID, "grinding");
    public static final RecipeType<GrindingRecipe> GRINDING_RECIPE_TYPE = RecipeType.simple(GRINDING_ID);
    public static final RecipeSerializer<GrindingRecipe> GRINDING_RECIPE_SERIALIZER = GrindingRecipe.SERIALIZER;

    public static final Identifier LIQUIFYING_ID = Identifier.fromNamespaceAndPath(ScalarPower.MODID, "liquifying");
    public static final RecipeType<LiquifyingRecipe> LIQUIFYING_RECIPE_TYPE = RecipeType.simple(LIQUIFYING_ID);
    public static final RecipeSerializer<LiquifyingRecipe> LIQUIFYING_RECIPE_SERIALIZER = LiquifyingRecipe.SERIALIZER;

    public static final Identifier MACERATING_ID = Identifier.fromNamespaceAndPath(ScalarPower.MODID, "macerating");
    public static final RecipeType<MaceratingRecipe> MACERATING_RECIPE_TYPE = RecipeType.simple(MACERATING_ID);
    public static final RecipeSerializer<MaceratingRecipe> MACERATING_RECIPE_SERIALIZER = MaceratingRecipe.SERIALIZER;

    public static final Identifier SAWMILLING_ID = Identifier.fromNamespaceAndPath(ScalarPower.MODID, "sawmilling");
    public static final RecipeType<SawmillRecipe> SAWMILLING_RECIPE_TYPE = RecipeType.simple(SAWMILLING_ID);
    public static final RecipeSerializer<SawmillRecipe> SAWMILLING_RECIPE_SERIALIZER = SawmillRecipe.SERIALIZER;

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, ScalarPower.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, ScalarPower.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<AlloySmeltingRecipe>> ALLOY_SMELTING_TYPE = RECIPE_TYPES.register(
            "alloy_smelting",
            () -> ALLOY_SMELTING_RECIPE_TYPE);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AlloySmeltingRecipe>> ALLOY_SMELTING_SERIALIZER = RECIPE_SERIALIZERS.register(
            "alloy_smelting",
            () -> ALLOY_SMELTING_RECIPE_SERIALIZER);

    public static final DeferredHolder<RecipeType<?>, RecipeType<ExtractionRecipe>> EXTRACTION_TYPE = RECIPE_TYPES.register(
            "extracting",
            () -> EXTRACTION_RECIPE_TYPE);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ExtractionRecipe>> EXTRACTION_SERIALIZER = RECIPE_SERIALIZERS.register(
            "extracting",
            () -> EXTRACTION_RECIPE_SERIALIZER);

    public static final DeferredHolder<RecipeType<?>, RecipeType<GrindingRecipe>> GRINDING_TYPE = RECIPE_TYPES.register(
            "grinding",
            () -> GRINDING_RECIPE_TYPE);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<GrindingRecipe>> GRINDING_SERIALIZER = RECIPE_SERIALIZERS.register(
            "grinding",
            () -> GRINDING_RECIPE_SERIALIZER);

    public static final DeferredHolder<RecipeType<?>, RecipeType<MaceratingRecipe>> MACERATING_TYPE = RECIPE_TYPES.register(
            "macerating",
            () -> MACERATING_RECIPE_TYPE);

    public static final DeferredHolder<RecipeType<?>, RecipeType<LiquifyingRecipe>> LIQUIFYING_TYPE = RECIPE_TYPES.register(
            "liquifying",
            () -> LIQUIFYING_RECIPE_TYPE);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<LiquifyingRecipe>> LIQUIFYING_SERIALIZER = RECIPE_SERIALIZERS.register(
            "liquifying",
            () -> LIQUIFYING_RECIPE_SERIALIZER);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MaceratingRecipe>> MACERATING_SERIALIZER = RECIPE_SERIALIZERS.register(
            "macerating",
            () -> MACERATING_RECIPE_SERIALIZER);

    public static final DeferredHolder<RecipeType<?>, RecipeType<SawmillRecipe>> SAWMILLING_TYPE = RECIPE_TYPES.register(
            "sawmilling",
            () -> SAWMILLING_RECIPE_TYPE);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SawmillRecipe>> SAWMILLING_SERIALIZER = RECIPE_SERIALIZERS.register(
            "sawmilling",
            () -> SAWMILLING_RECIPE_SERIALIZER);

    public static void register(IEventBus eventBus) {
        RECIPE_TYPES.register(eventBus);
        RECIPE_SERIALIZERS.register(eventBus);
    }
}

