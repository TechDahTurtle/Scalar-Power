package site.scalarstudios.scalarpower.integration.jei.category;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import site.scalarstudios.scalarpower.ScalarPower;
import site.scalarstudios.scalarpower.block.ScalarPowerBlocks;
import site.scalarstudios.scalarpower.recipe.LiquifyingRecipe;
import site.scalarstudios.scalarpower.recipe.ScalarPowerRecipes;

public class LiquifyingRecipeCategory implements IRecipeCategory<RecipeHolder<LiquifyingRecipe>> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(ScalarPower.MODID, "textures/gui/jei_generic_1tofluid.png");
    private static final int BACKGROUND_WIDTH = 122;
    private static final int BACKGROUND_HEIGHT = 39;

    private static final int SPU_TEXT_X = 3;
    private static final int SPU_TEXT_Y = 28;

    private static final int INPUT_SLOT_X = 21;
    private static final int OUTPUT_FLUID_X = 81;
    private static final int SLOT_Y = 10;

    public static final IRecipeHolderType<LiquifyingRecipe> TYPE = IRecipeHolderType.create(ScalarPowerRecipes.LIQUIFYING_RECIPE_TYPE);

    private final IDrawableStatic background;
    private final IDrawable icon;

    public LiquifyingRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ScalarPowerBlocks.LIQUIFIER.asItem()));
    }

    @Override
    public IRecipeHolderType<LiquifyingRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.scalarpower.liquifying");
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

    @SuppressWarnings("removal")
    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<LiquifyingRecipe> recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_SLOT_X, SLOT_Y)
                .addIngredients(recipe.value().input());

        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_FLUID_X, SLOT_Y)
                .addIngredient(NeoForgeTypes.FLUID_STACK, new FluidStack(Fluids.LAVA, 1000));
    }

    @Override
    public void draw(RecipeHolder<LiquifyingRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);

        String costText = recipe.value().spuCost() + " SPU";
        guiGraphics.text(Minecraft.getInstance().font, costText, SPU_TEXT_X, SPU_TEXT_Y, 0xFF4A4A4A, false);
    }
}


