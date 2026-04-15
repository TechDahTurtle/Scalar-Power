package site.scalarstudios.scalarpower.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
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
import site.scalarstudios.scalarpower.integration.jei.category.FreezingRecipeCategory;
import site.scalarstudios.scalarpower.integration.jei.category.LiquifyingRecipeCategory;
import site.scalarstudios.scalarpower.integration.jei.category.MaceratingRecipeCategory;
import site.scalarstudios.scalarpower.integration.jei.category.SawmillingRecipeCategory;
import site.scalarstudios.scalarpower.block.machine.alloysmelter.AlloySmelterScreen;
import site.scalarstudios.scalarpower.block.machine.extractor.ExtractorScreen;
import site.scalarstudios.scalarpower.block.machine.grinder.DoubleGrinderScreen;
import site.scalarstudios.scalarpower.block.machine.grinder.GrinderScreen;
import site.scalarstudios.scalarpower.block.machine.liquifier.LiquifierScreen;
import site.scalarstudios.scalarpower.block.machine.freezer.FreezerScreen;
import site.scalarstudios.scalarpower.block.machine.macerator.DoubleMaceratorScreen;
import site.scalarstudios.scalarpower.block.machine.macerator.MaceratorScreen;
import site.scalarstudios.scalarpower.block.machine.sawmill.SawmillScreen;
import site.scalarstudios.scalarpower.block.machine.poweredfurnace.DoublePoweredFurnaceScreen;
import site.scalarstudios.scalarpower.block.machine.poweredfurnace.PoweredFurnaceScreen;
import site.scalarstudios.scalarpower.recipe.AlloySmeltingRecipe;
import site.scalarstudios.scalarpower.recipe.ExtractionRecipe;
import site.scalarstudios.scalarpower.recipe.FreezingRecipe;
import site.scalarstudios.scalarpower.recipe.GrindingRecipe;
import site.scalarstudios.scalarpower.recipe.LiquifyingRecipe;
import site.scalarstudios.scalarpower.recipe.MaceratingRecipe;
import site.scalarstudios.scalarpower.recipe.SawmillRecipe;
import site.scalarstudios.scalarpower.recipe.ScalarPowerRecipes;
import java.util.List;

@JeiPlugin
public class ScalarPowerJeiPlugin implements IModPlugin {
    private static final Identifier PLUGIN_ID = Identifier.fromNamespaceAndPath(ScalarPower.MODID, "jei_plugin");
    private static IJeiRuntime jeiRuntime;
    private static boolean alloyInjected;
    private static boolean extractionInjected;
    private static boolean freezingInjected;
    private static boolean grindingInjected;
    private static boolean liquifyingInjected;
    private static boolean maceratingInjected;
    private static boolean sawmillingInjected;
    private static boolean tickListenerRegistered;

    @Override
    public Identifier getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new AlloySmeltingRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new ExtractionRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new FreezingRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new GrindingRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new LiquifyingRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new MaceratingRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new SawmillingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        CustomRecipes recipes = collectCustomRecipes();
        if (recipes.isEmpty()) {
            return;
        }

        registration.addRecipes(AlloySmeltingRecipeCategory.TYPE, recipes.alloy());
        registration.addRecipes(ExtractionRecipeCategory.TYPE, recipes.extraction());
        registration.addRecipes(FreezingRecipeCategory.TYPE, recipes.freezing());
        registration.addRecipes(GrindingRecipeCategory.TYPE, recipes.grinding());
        registration.addRecipes(LiquifyingRecipeCategory.TYPE, recipes.liquifying());
        registration.addRecipes(MaceratingRecipeCategory.TYPE, recipes.macerating());
        registration.addRecipes(SawmillingRecipeCategory.TYPE, recipes.sawmilling());
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
        registration.addRecipeCatalyst(new ItemStack(ScalarPowerBlocks.ALLOY_SMELTER.asItem()), AlloySmeltingRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ScalarPowerBlocks.EXTRACTOR.asItem()), ExtractionRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ScalarPowerBlocks.FREEZER.asItem()), FreezingRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ScalarPowerBlocks.GRINDER.asItem()), GrindingRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ScalarPowerBlocks.DOUBLE_GRINDER.asItem()), GrindingRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ScalarPowerBlocks.LIQUIFIER.asItem()), LiquifyingRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ScalarPowerBlocks.MACERATOR.asItem()), MaceratingRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ScalarPowerBlocks.DOUBLE_MACERATOR.asItem()), MaceratingRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ScalarPowerBlocks.POWERED_FURNACE.asItem()), RecipeTypes.SMELTING);
        registration.addRecipeCatalyst(new ItemStack(ScalarPowerBlocks.DOUBLE_POWERED_FURNACE.asItem()), RecipeTypes.SMELTING);
        registration.addRecipeCatalyst(new ItemStack(ScalarPowerBlocks.SAWMILL.asItem()), SawmillingRecipeCategory.TYPE);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(AlloySmelterScreen.class, 80, 32, 24, 18, AlloySmeltingRecipeCategory.TYPE);
        registration.addRecipeClickArea(ExtractorScreen.class, 80, 32, 24, 18, ExtractionRecipeCategory.TYPE);
        registration.addRecipeClickArea(FreezerScreen.class, 80, 32, 24, 17, FreezingRecipeCategory.TYPE);
        registration.addRecipeClickArea(GrinderScreen.class, 80, 32, 24, 18, GrindingRecipeCategory.TYPE);
        registration.addRecipeClickArea(DoubleGrinderScreen.class, 80, 17, 24, 18, GrindingRecipeCategory.TYPE);
        registration.addRecipeClickArea(DoubleGrinderScreen.class, 80, 44, 24, 18, GrindingRecipeCategory.TYPE);
        registration.addRecipeClickArea(LiquifierScreen.class, 58, 34, 24, 17, LiquifyingRecipeCategory.TYPE);
        registration.addRecipeClickArea(MaceratorScreen.class, 80, 32, 24, 18, MaceratingRecipeCategory.TYPE);
        registration.addRecipeClickArea(DoubleMaceratorScreen.class, 80, 17, 24, 18, MaceratingRecipeCategory.TYPE);
        registration.addRecipeClickArea(DoubleMaceratorScreen.class, 80, 44, 24, 18, MaceratingRecipeCategory.TYPE);
        registration.addRecipeClickArea(PoweredFurnaceScreen.class, 80, 32, 24, 18, RecipeTypes.SMELTING);
        registration.addRecipeClickArea(DoublePoweredFurnaceScreen.class, 80, 17, 24, 18, RecipeTypes.SMELTING);
        registration.addRecipeClickArea(DoublePoweredFurnaceScreen.class, 80, 44, 24, 18, RecipeTypes.SMELTING);
        registration.addRecipeClickArea(SawmillScreen.class, 80, 32, 24, 18, SawmillingRecipeCategory.TYPE);
    }

    private static <T extends net.minecraft.world.item.crafting.Recipe<?>> List<RecipeHolder<T>> findRecipes(net.minecraft.world.item.crafting.RecipeType<T> recipeType) {
        RecipeManager recipeManager = findRecipeManager();
        if (recipeManager == null) {
            return List.of();
        }

        return recipeManager.getRecipes().stream()
                .filter(holder -> holder.value().getType() == recipeType)
                .map(holder -> (RecipeHolder<T>) holder)
                .toList();
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
        List<RecipeHolder<AlloySmeltingRecipe>> alloyRecipes = findRecipes(ScalarPowerRecipes.ALLOY_SMELTING_RECIPE_TYPE);
        List<RecipeHolder<ExtractionRecipe>> extractionRecipes = findRecipes(ScalarPowerRecipes.EXTRACTION_RECIPE_TYPE);
        List<RecipeHolder<FreezingRecipe>> freezingRecipes = findRecipes(ScalarPowerRecipes.FREEZING_RECIPE_TYPE);
        List<RecipeHolder<GrindingRecipe>> grindingRecipes = findRecipes(ScalarPowerRecipes.GRINDING_RECIPE_TYPE);
        List<RecipeHolder<LiquifyingRecipe>> liquifyingRecipes = findRecipes(ScalarPowerRecipes.LIQUIFYING_RECIPE_TYPE);
        List<RecipeHolder<MaceratingRecipe>> maceratingRecipes = findRecipes(ScalarPowerRecipes.MACERATING_RECIPE_TYPE);
        List<RecipeHolder<SawmillRecipe>> sawmillingRecipes = findRecipes(ScalarPowerRecipes.SAWMILLING_RECIPE_TYPE);

        return new CustomRecipes(alloyRecipes, extractionRecipes, freezingRecipes, grindingRecipes, liquifyingRecipes, maceratingRecipes, sawmillingRecipes);
    }

    private record CustomRecipes(
            List<RecipeHolder<AlloySmeltingRecipe>> alloy,
            List<RecipeHolder<ExtractionRecipe>> extraction,
            List<RecipeHolder<FreezingRecipe>> freezing,
            List<RecipeHolder<GrindingRecipe>> grinding,
            List<RecipeHolder<LiquifyingRecipe>> liquifying,
            List<RecipeHolder<MaceratingRecipe>> macerating,
            List<RecipeHolder<SawmillRecipe>> sawmilling) {
        private boolean isEmpty() {
            return grinding.isEmpty() && extraction.isEmpty() && liquifying.isEmpty()
                    && alloy.isEmpty() && sawmilling.isEmpty() && macerating.isEmpty()
                    && freezing.isEmpty();
        }
    }

    private static void tryInjectRuntimeRecipes() {
        if (jeiRuntime == null) {
            return;
        }

        CustomRecipes recipes = collectCustomRecipes();
        if (recipes.isEmpty()) {
            return;
        }

        if (!alloyInjected && !recipes.alloy().isEmpty()) {
            jeiRuntime.getRecipeManager().addRecipes(AlloySmeltingRecipeCategory.TYPE, recipes.alloy());
            alloyInjected = true;
        }
        if (!extractionInjected && !recipes.extraction().isEmpty()) {
            jeiRuntime.getRecipeManager().addRecipes(ExtractionRecipeCategory.TYPE, recipes.extraction());
            extractionInjected = true;
        }
        if (!freezingInjected && !recipes.freezing().isEmpty()) {
            jeiRuntime.getRecipeManager().addRecipes(FreezingRecipeCategory.TYPE, recipes.freezing());
            freezingInjected = true;
        }
        if (!grindingInjected && !recipes.grinding().isEmpty()) {
            jeiRuntime.getRecipeManager().addRecipes(GrindingRecipeCategory.TYPE, recipes.grinding());
            grindingInjected = true;
        }
        if (!liquifyingInjected && !recipes.liquifying().isEmpty()) {
            jeiRuntime.getRecipeManager().addRecipes(LiquifyingRecipeCategory.TYPE, recipes.liquifying());
            liquifyingInjected = true;
        }
        if (!maceratingInjected && !recipes.macerating().isEmpty()) {
            jeiRuntime.getRecipeManager().addRecipes(MaceratingRecipeCategory.TYPE, recipes.macerating());
            maceratingInjected = true;
        }
        if (!sawmillingInjected && !recipes.sawmilling().isEmpty()) {
            jeiRuntime.getRecipeManager().addRecipes(SawmillingRecipeCategory.TYPE, recipes.sawmilling());
            sawmillingInjected = true;
        }
    }
}
