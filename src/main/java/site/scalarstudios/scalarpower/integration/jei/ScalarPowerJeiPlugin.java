package site.scalarstudios.scalarpower.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import site.scalarstudios.scalarpower.ScalarPower;
import site.scalarstudios.scalarpower.block.ScalarPowerBlocks;
import site.scalarstudios.scalarpower.integration.jei.category.AlloySmeltingRecipeCategory;
import site.scalarstudios.scalarpower.integration.jei.category.ExtractionRecipeCategory;
import site.scalarstudios.scalarpower.integration.jei.category.GrindingRecipeCategory;
import site.scalarstudios.scalarpower.machines.alloysmelter.AlloySmelterScreen;
import site.scalarstudios.scalarpower.machines.extractor.ExtractorScreen;
import site.scalarstudios.scalarpower.machines.grinder.DoubleGrinderScreen;
import site.scalarstudios.scalarpower.machines.grinder.GrinderScreen;
import site.scalarstudios.scalarpower.recipe.AlloySmeltingRecipe;
import site.scalarstudios.scalarpower.recipe.ExtractionRecipe;
import site.scalarstudios.scalarpower.recipe.GrindingRecipe;
import java.util.List;

@JeiPlugin
public class ScalarPowerJeiPlugin implements IModPlugin {
    private static final Identifier PLUGIN_ID = Identifier.fromNamespaceAndPath(ScalarPower.MODID, "jei_plugin");
    private static IJeiRuntime jeiRuntime;
    private static boolean runtimeRecipesInjected;
    private static boolean tickListenerRegistered;

    @Override
    public Identifier getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new GrindingRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new ExtractionRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new AlloySmeltingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        CustomRecipes recipes = collectCustomRecipes();
        if (recipes.isEmpty()) {
            return;
        }

        registration.addRecipes(GrindingRecipeCategory.TYPE, recipes.grinding());
        registration.addRecipes(ExtractionRecipeCategory.TYPE, recipes.extraction());
        registration.addRecipes(AlloySmeltingRecipeCategory.TYPE, recipes.alloy());
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        ScalarPowerJeiPlugin.jeiRuntime = jeiRuntime;
        tryInjectRuntimeRecipes();

        if (!tickListenerRegistered) {
            NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post event) -> tryInjectRuntimeRecipes());
            tickListenerRegistered = true;
        }
    }

    @SuppressWarnings("removal")
    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ScalarPowerBlocks.GRINDER.asItem()), GrindingRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ScalarPowerBlocks.DOUBLE_GRINDER.asItem()), GrindingRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ScalarPowerBlocks.EXTRACTOR.asItem()), ExtractionRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ScalarPowerBlocks.ALLOY_SMELTER.asItem()), AlloySmeltingRecipeCategory.TYPE);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(GrinderScreen.class, 80, 32, 24, 18, GrindingRecipeCategory.TYPE);
        registration.addRecipeClickArea(DoubleGrinderScreen.class, 80, 17, 24, 18, GrindingRecipeCategory.TYPE);
        registration.addRecipeClickArea(DoubleGrinderScreen.class, 80, 44, 24, 18, GrindingRecipeCategory.TYPE);
        registration.addRecipeClickArea(ExtractorScreen.class, 80, 32, 24, 18, ExtractionRecipeCategory.TYPE);
        registration.addRecipeClickArea(AlloySmelterScreen.class, 80, 32, 24, 18, AlloySmeltingRecipeCategory.TYPE);
    }

    private static List<RecipeHolder<?>> findAllRecipes() {
        RecipeManager recipeManager = findRecipeManager();
        if (recipeManager == null) {
            return List.of();
        }

        return List.copyOf(recipeManager.getRecipes());
    }

    private static RecipeManager findRecipeManager() {
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.hasSingleplayerServer()) {
            return null;
        }

        IntegratedServer server = minecraft.getSingleplayerServer();
        if (server == null) {
            return null;
        }

        return server.getRecipeManager();
    }

    private static CustomRecipes collectCustomRecipes() {
        List<RecipeHolder<?>> allRecipes = findAllRecipes();

        List<RecipeHolder<GrindingRecipe>> grindingRecipes = allRecipes.stream()
                .filter(holder -> holder.value() instanceof GrindingRecipe)
                .map(holder -> (RecipeHolder<GrindingRecipe>) holder)
                .toList();

        List<RecipeHolder<ExtractionRecipe>> extractionRecipes = allRecipes.stream()
                .filter(holder -> holder.value() instanceof ExtractionRecipe)
                .map(holder -> (RecipeHolder<ExtractionRecipe>) holder)
                .toList();

        List<RecipeHolder<AlloySmeltingRecipe>> alloyRecipes = allRecipes.stream()
                .filter(holder -> holder.value() instanceof AlloySmeltingRecipe)
                .map(holder -> (RecipeHolder<AlloySmeltingRecipe>) holder)
                .toList();

        return new CustomRecipes(grindingRecipes, extractionRecipes, alloyRecipes);
    }

    private record CustomRecipes(
            List<RecipeHolder<GrindingRecipe>> grinding,
            List<RecipeHolder<ExtractionRecipe>> extraction,
            List<RecipeHolder<AlloySmeltingRecipe>> alloy) {
        private boolean isEmpty() {
            return grinding.isEmpty() && extraction.isEmpty() && alloy.isEmpty();
        }
    }

    private static void tryInjectRuntimeRecipes() {
        if (runtimeRecipesInjected || jeiRuntime == null) {
            return;
        }

        CustomRecipes recipes = collectCustomRecipes();
        if (recipes.isEmpty()) {
            return;
        }

        jeiRuntime.getRecipeManager().addRecipes(GrindingRecipeCategory.TYPE, recipes.grinding());
        jeiRuntime.getRecipeManager().addRecipes(ExtractionRecipeCategory.TYPE, recipes.extraction());
        jeiRuntime.getRecipeManager().addRecipes(AlloySmeltingRecipeCategory.TYPE, recipes.alloy());
        runtimeRecipesInjected = true;
    }
}
