package site.scalarstudios.scalarpower.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import site.scalarstudios.scalarpower.content.alloysmelter.AlloySmeltingInput;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Compatibility loader for external mod recipes from datapack paths.
 *
 * This supports recipes under data/<namespace>/grinding and data/<namespace>/alloy_smelting.
 */
public final class ExternalRecipeCompat {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static @Nullable ResourceManager cachedResourceManager;
    private static List<RecipeHolder<GrindingRecipe>> cachedGrindingRecipes = List.of();
    private static List<RecipeHolder<AlloySmeltingRecipe>> cachedAlloyRecipes = List.of();

    private ExternalRecipeCompat() {
    }

    public static synchronized Optional<RecipeHolder<GrindingRecipe>> findExternalGrindingRecipe(ServerLevel level, ItemStack input) {
        ensureLoaded(level);
        return cachedGrindingRecipes.stream()
                .filter(holder -> holder.value().matches(new net.minecraft.world.item.crafting.SingleRecipeInput(input), level))
                .findFirst();
    }

    public static synchronized Optional<RecipeHolder<AlloySmeltingRecipe>> findExternalAlloyRecipe(ServerLevel level, AlloySmeltingInput input) {
        ensureLoaded(level);
        return cachedAlloyRecipes.stream()
                .filter(holder -> holder.value().matches(input, level))
                .findFirst();
    }

    public static synchronized int externalGrindingCount(ServerLevel level) {
        ensureLoaded(level);
        return cachedGrindingRecipes.size();
    }

    public static synchronized int externalAlloyCount(ServerLevel level) {
        ensureLoaded(level);
        return cachedAlloyRecipes.size();
    }

    private static void ensureLoaded(ServerLevel level) {
        ResourceManager resourceManager = level.getServer().getResourceManager();
        if (resourceManager == cachedResourceManager) {
            return;
        }

        cachedResourceManager = resourceManager;
        cachedGrindingRecipes = loadLegacyGrindingRecipes(resourceManager);
        cachedAlloyRecipes = loadLegacyAlloyRecipes(resourceManager);

        LOGGER.info(
                "[ScalarPower Compat] Loaded {} external grinding recipes and {} external alloy recipes",
                cachedGrindingRecipes.size(),
                cachedAlloyRecipes.size());
    }

    private static List<RecipeHolder<GrindingRecipe>> loadLegacyGrindingRecipes(ResourceManager resourceManager) {
        Map<Identifier, Resource> resources = resourceManager.listResources("grinding", id -> id.getPath().endsWith(".json"));
        List<RecipeHolder<GrindingRecipe>> recipes = new ArrayList<>();

        resources.entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().toString()))
                .forEach(entry -> parseLegacyGrinding(entry.getKey(), entry.getValue()).ifPresent(recipes::add));

        return List.copyOf(recipes);
    }

    private static List<RecipeHolder<AlloySmeltingRecipe>> loadLegacyAlloyRecipes(ResourceManager resourceManager) {
        Map<Identifier, Resource> resources = resourceManager.listResources("alloy_smelting", id -> id.getPath().endsWith(".json"));
        List<RecipeHolder<AlloySmeltingRecipe>> recipes = new ArrayList<>();

        resources.entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().toString()))
                .forEach(entry -> parseLegacyAlloy(entry.getKey(), entry.getValue()).ifPresent(recipes::add));

        return List.copyOf(recipes);
    }

    private static Optional<RecipeHolder<GrindingRecipe>> parseLegacyGrinding(Identifier fileId, Resource resource) {
        JsonObject json = readJson(fileId, resource);
        if (json == null || !isType(json, ScalarPowerRecipes.GRINDING_ID)) {
            return Optional.empty();
        }

        Ingredient ingredient = parseIngredient(json.get("ingredient"));
        ItemStackTemplate result = parseResult(json.get("result"));
        if (ingredient == null || result == null) {
            return Optional.empty();
        }

        float bonusChance = json.has("bonus_chance") ? json.get("bonus_chance").getAsFloat() : 0.0F;
        int bonusCount = json.has("bonus_count") ? Math.max(1, json.get("bonus_count").getAsInt()) : 1;

        GrindingRecipe recipe = new GrindingRecipe(new Recipe.CommonInfo(true), ingredient, result, bonusChance, bonusCount);
        ResourceKey<Recipe<?>> recipeKey = ResourceKey.create(Registries.RECIPE, toRecipeId(fileId));
        return Optional.of(new RecipeHolder<>(recipeKey, recipe));
    }

    private static Optional<RecipeHolder<AlloySmeltingRecipe>> parseLegacyAlloy(Identifier fileId, Resource resource) {
        JsonObject json = readJson(fileId, resource);
        if (json == null || !isType(json, ScalarPowerRecipes.ALLOY_SMELTING_ID)) {
            return Optional.empty();
        }

        Ingredient ingredient1 = parseIngredient(json.get("ingredient1"));
        Ingredient ingredient2 = parseIngredient(json.get("ingredient2"));
        Ingredient ingredient3 = json.has("ingredient3") ? parseIngredient(json.get("ingredient3")) : null;
        ItemStackTemplate result = parseResult(json.get("result"));

        if (ingredient1 == null || ingredient2 == null || result == null) {
            return Optional.empty();
        }

        AlloySmeltingRecipe recipe = new AlloySmeltingRecipe(
                new Recipe.CommonInfo(true),
                ingredient1,
                ingredient2,
                Optional.ofNullable(ingredient3),
                result);

        ResourceKey<Recipe<?>> recipeKey = ResourceKey.create(Registries.RECIPE, toRecipeId(fileId));
        return Optional.of(new RecipeHolder<>(recipeKey, recipe));
    }

    private static boolean isType(JsonObject json, Identifier expectedType) {
        if (!json.has("type")) {
            return false;
        }

        Identifier found = Identifier.tryParse(json.get("type").getAsString());
        return expectedType.equals(found);
    }

    private static @Nullable JsonObject readJson(Identifier fileId, Resource resource) {
        try (Reader reader = resource.openAsReader()) {
            JsonElement root = JsonParser.parseReader(reader);
            if (root == null || !root.isJsonObject()) {
                return null;
            }
            return root.getAsJsonObject();
        } catch (IOException | RuntimeException ex) {
            LOGGER.warn("[ScalarPower Compat] Failed to read legacy recipe {}", fileId, ex);
            return null;
        }
    }

    private static @Nullable Ingredient parseIngredient(@Nullable JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return null;
        }

        String rawId;
        if (element.isJsonPrimitive()) {
            rawId = element.getAsString();
        } else if (element.isJsonObject() && element.getAsJsonObject().has("item")) {
            rawId = element.getAsJsonObject().get("item").getAsString();
        } else {
            return null;
        }

        if (rawId.startsWith("#")) {
            return null;
        }

        Identifier id = Identifier.tryParse(rawId);
        if (id == null || !BuiltInRegistries.ITEM.containsKey(id)) {
            return null;
        }

        Item item = BuiltInRegistries.ITEM.getValue(id);
        if (item == null || item == Items.AIR) {
            return null;
        }

        return Ingredient.of(item);
    }

    private static @Nullable ItemStackTemplate parseResult(@Nullable JsonElement element) {
        if (element == null || !element.isJsonObject()) {
            return null;
        }

        JsonObject obj = element.getAsJsonObject();
        if (!obj.has("id")) {
            return null;
        }

        Identifier id = Identifier.tryParse(obj.get("id").getAsString());
        if (id == null || !BuiltInRegistries.ITEM.containsKey(id)) {
            return null;
        }

        Item item = BuiltInRegistries.ITEM.getValue(id);
        if (item == null || item == Items.AIR) {
            return null;
        }

        int count = obj.has("count") ? Math.max(1, obj.get("count").getAsInt()) : 1;
        return new ItemStackTemplate(item, count);
    }

    private static Identifier toRecipeId(Identifier fileId) {
        String path = fileId.getPath();
        if (path.endsWith(".json")) {
            path = path.substring(0, path.length() - 5);
        }
        return Identifier.fromNamespaceAndPath(fileId.getNamespace(), path);
    }
}

