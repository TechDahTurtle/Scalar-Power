package site.scalarstudios.scalarpower.content.generator;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import java.util.Locale;

public class CoalGeneratorScreen extends AbstractContainerScreen<CoalGeneratorMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("scalarpower", "textures/gui/coal_generator.png");

    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;

    private static final int ENERGY_X = 130;
    private static final int ENERGY_Y = 17;
    private static final int ENERGY_WIDTH = 8;
    private static final int ENERGY_HEIGHT = 42;
    private static final int ENERGY_TEXT_Y_OFFSET = 2;
    private static final int ENERGY_TEXT_COLOR = 0xFF4A4A4A;
    private static final float ENERGY_TEXT_SCALE = 0.85F;

    public CoalGeneratorScreen(CoalGeneratorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        int x = this.leftPos;
        int y = this.topPos;

        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0.0F, 0.0F, this.imageWidth, this.imageHeight, TEXTURE_WIDTH, TEXTURE_HEIGHT);

        graphics.fill(x + ENERGY_X, y + ENERGY_Y, x + ENERGY_X + ENERGY_WIDTH, y + ENERGY_Y + ENERGY_HEIGHT, 0x88000000);
        int filled = menu.getEnergyCapacity() > 0 ? (int) ((float) ENERGY_HEIGHT * menu.getEnergy() / menu.getEnergyCapacity()) : 0;
        if (filled > 0) {
            graphics.fill(x + ENERGY_X, y + ENERGY_Y + (ENERGY_HEIGHT - filled), x + ENERGY_X + ENERGY_WIDTH, y + ENERGY_Y + ENERGY_HEIGHT, 0xFF44CC44);
        }

        String energyText = formatEnergy(menu.getEnergy()) + " / " + formatEnergy(menu.getEnergyCapacity());
        int barCenterX = x + ENERGY_X + (ENERGY_WIDTH / 2);
        float scaledTextWidth = this.font.width(energyText) * ENERGY_TEXT_SCALE;
        int energyTextX = Math.round(barCenterX - (scaledTextWidth / 2.0F));
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

