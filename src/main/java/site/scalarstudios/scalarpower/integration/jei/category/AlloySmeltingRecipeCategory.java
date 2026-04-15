package site.scalarstudios.scalarpower.integration.jei.category;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import site.scalarstudios.scalarpower.ScalarPower;
import site.scalarstudios.scalarpower.block.ScalarPowerBlocks;
import site.scalarstudios.scalarpower.block.machine.alloysmelter.AlloySmeltingInput;
import site.scalarstudios.scalarpower.recipe.AlloySmeltingRecipe;
import site.scalarstudios.scalarpower.recipe.ScalarPowerRecipes;

public class AlloySmeltingRecipeCategory implements IRecipeCategory<RecipeHolder<AlloySmeltingRecipe>> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(ScalarPower.MODID, "textures/gui/jei_alloy_smelter.png");
    private static final int BACKGROUND_WIDTH = 122;
    private static final int BACKGROUND_HEIGHT = 39;

    public static final IRecipeHolderType<AlloySmeltingRecipe> TYPE = IRecipeHolderType.create(ScalarPowerRecipes.ALLOY_SMELTING_RECIPE_TYPE);

    private final IDrawableStatic background;
    private final IDrawable icon;

    public AlloySmeltingRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ScalarPowerBlocks.ALLOY_SMELTER.asItem()));
    }

    @Override
    public IRecipeHolderType<AlloySmeltingRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.scalarpower.alloy_smelting");
    }

    @Override
    public int getWidth() {
        return BACKGROUND_WIDTH;
    }

    @Override
    public int getHeight() {
        return BACKGROUND_HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @SuppressWarnings("removal") // This will need to be addressed at some point
    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<AlloySmeltingRecipe> recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 2, 10)
                .addIngredients(recipe.value().ingredient1());
        builder.addSlot(RecipeIngredientRole.INPUT, 20, 10)
                .addIngredients(recipe.value().ingredient2());
        recipe.value().ingredient3().ifPresent(ingredient -> builder.addSlot(RecipeIngredientRole.INPUT, 38, 10)
                .addIngredients(ingredient));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 101, 10)
                .addItemStack(recipe.value().assemble(new AlloySmeltingInput(ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY)));
    }

    @Override
    public void draw(RecipeHolder<AlloySmeltingRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);
    }
}
