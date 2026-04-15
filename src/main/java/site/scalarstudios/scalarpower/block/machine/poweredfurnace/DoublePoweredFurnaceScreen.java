package site.scalarstudios.scalarpower.block.machine.poweredfurnace;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import java.util.Locale;

public class DoublePoweredFurnaceScreen extends AbstractContainerScreen<DoublePoweredFurnaceMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("scalarpower", "textures/gui/generic_2to2.png");

    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;

    private static final int ENERGY_X = 10;
    private static final int ENERGY_Y = 20;
    private static final int ENERGY_WIDTH = 8;
    private static final int ENERGY_HEIGHT = 42;
    private static final int ENERGY_TEXT_Y_OFFSET = 2;
    private static final int ENERGY_TEXT_COLOR = 0xFF4A4A4A;
    private static final float ENERGY_TEXT_SCALE = 0.85F;
    private static final int ENERGY_TEXT_X_OFFSET = -2;

    private static final int PROGRESS_X = 80;
    private static final int PROGRESS_A_Y = 17;
    private static final int PROGRESS_B_Y = 44;
    private static final int PROGRESS_WIDTH = 24;
    private static final int PROGRESS_HEIGHT = 18;
    private static final int PROGRESS_U = 176;
    private static final int PROGRESS_V = 0;
    private static final int INVENTORY_LABEL_Y = 72;

    public DoublePoweredFurnaceScreen(DoublePoweredFurnaceMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
        this.inventoryLabelY = INVENTORY_LABEL_Y;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        int x = this.leftPos;
        int y = this.topPos;

        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0.0F, 0.0F, this.imageWidth, this.imageHeight, TEXTURE_WIDTH, TEXTURE_HEIGHT);

        int progressAPixels = menu.getMaxProgressA() > 0 ? (int) ((float) PROGRESS_WIDTH * menu.getProgressA() / menu.getMaxProgressA()) : 0;
        if (progressAPixels > 0) {
            graphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    TEXTURE,
                    x + PROGRESS_X,
                    y + PROGRESS_A_Y,
                    PROGRESS_U,
                    PROGRESS_V,
                    progressAPixels,
                    PROGRESS_HEIGHT,
                    TEXTURE_WIDTH,
                    TEXTURE_HEIGHT);
        }

        int progressBPixels = menu.getMaxProgressB() > 0 ? (int) ((float) PROGRESS_WIDTH * menu.getProgressB() / menu.getMaxProgressB()) : 0;
        if (progressBPixels > 0) {
            graphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    TEXTURE,
                    x + PROGRESS_X,
                    y + PROGRESS_B_Y,
                    PROGRESS_U,
                    PROGRESS_V,
                    progressBPixels,
                    PROGRESS_HEIGHT,
                    TEXTURE_WIDTH,
                    TEXTURE_HEIGHT);
        }

        graphics.fill(x + ENERGY_X, y + ENERGY_Y, x + ENERGY_X + ENERGY_WIDTH, y + ENERGY_Y + ENERGY_HEIGHT, 0x66000000);
        int energyBar = menu.getEnergyCapacity() > 0 ? (int) ((float) ENERGY_HEIGHT * menu.getEnergy() / menu.getEnergyCapacity()) : 0;
        if (energyBar > 0) {
            graphics.fill(
                    x + ENERGY_X,
                    y + ENERGY_Y + (ENERGY_HEIGHT - energyBar),
                    x + ENERGY_X + ENERGY_WIDTH,
                    y + ENERGY_Y + ENERGY_HEIGHT,
                    0xFF44CC44);
        }

        String energyText = formatEnergy(menu.getEnergy()) + " / " + formatEnergy(menu.getEnergyCapacity());
        float scaledTextWidth = this.font.width(energyText) * ENERGY_TEXT_SCALE;
        int preferredTextX = x + ENERGY_X + ENERGY_TEXT_X_OFFSET;
        int minTextX = x + 4;
        int maxTextX = x + this.imageWidth - 4 - Math.round(scaledTextWidth);
        int energyTextX = Math.max(minTextX, Math.min(preferredTextX, maxTextX));
        int energyTextY = y + ENERGY_Y + ENERGY_HEIGHT + ENERGY_TEXT_Y_OFFSET;

        graphics.pose().pushMatrix();
        graphics.pose().scale(ENERGY_TEXT_SCALE, ENERGY_TEXT_SCALE);
        graphics.text(
                this.font,
                energyText,
                Math.round(energyTextX / ENERGY_TEXT_SCALE),
                Math.round(energyTextY / ENERGY_TEXT_SCALE),
                ENERGY_TEXT_COLOR,
                false);
        graphics.pose().popMatrix();
    }

    private static String formatEnergy(int value) {
        if (value >= 1000) {
            return String.format(Locale.ROOT, "%.1fk", value / 1000.0);
        }
        return Integer.toString(value);
    }
}

