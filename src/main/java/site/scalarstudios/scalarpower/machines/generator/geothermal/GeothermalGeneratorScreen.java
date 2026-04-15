package site.scalarstudios.scalarpower.machines.generator.geothermal;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import site.scalarstudios.scalarpower.gui.tankrenderer.LavaTankRenderer;

import java.util.Locale;

public class GeothermalGeneratorScreen extends AbstractContainerScreen<GeothermalGeneratorMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("scalarpower", "textures/gui/geothermal_generator.png");

    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;

    private static final int ENERGY_X = 130;
    private static final int ENERGY_Y = 17;
    private static final int ENERGY_WIDTH = 8;
    private static final int ENERGY_HEIGHT = 42;
    private static final int ENERGY_TEXT_Y_OFFSET = 2;
    private static final int ENERGY_TEXT_COLOR = 0xFF4A4A4A;
    private static final float ENERGY_TEXT_SCALE = 0.85F;

    private static final int TANK_X = 81;
    private static final int TANK_Y = 10;
    private static final int TANK_WIDTH = 17;
    private static final int TANK_HEIGHT = 65;
    private static final int TANK_INNER_X = 82;
    private static final int TANK_INNER_Y = 11;
    private static final int TANK_INNER_WIDTH = 15;
    private static final int TANK_INNER_HEIGHT = 63;
    private static final int FLUID_TEXT_Y = 76;
    private static final float FLUID_TEXT_SCALE = 0.75F;

    private static final int INFO_WIDTH = 74;
    private static final int INFO_X = 8;
    private static final int INFO_RATE_Y = 34;
    private static final int INFO_EFFICIENCY_Y = 47;

    private static final int TANK_BORDER_RED = 0xFFD50000;
    private static final int TANK_BORDER_WHITE = 0xFFFFFFFF;
    private static final int[] SECTION_LINES = {10, 26, 42, 58, 74};
    private static final int[] LEFT_TICKS = {15, 22, 31, 38, 47, 54, 63, 70};
    private static final int[] RIGHT_TICKS = {19, 35, 51, 67};

    public GeothermalGeneratorScreen(GeothermalGeneratorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        int x = this.leftPos;
        int y = this.topPos;

        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0.0F, 0.0F, this.imageWidth, this.imageHeight, TEXTURE_WIDTH, TEXTURE_HEIGHT);

        drawTankFluid(graphics, x, y);
        drawTankOverlay(graphics, x, y);
        drawEnergy(graphics, x, y);
        drawInfoText(graphics, x, y);
    }

    private void drawTankFluid(GuiGraphicsExtractor graphics, int x, int y) {
        LavaTankRenderer.INSTANCE.render(
                graphics,
                x + TANK_INNER_X,
                y + TANK_INNER_Y,
                TANK_INNER_WIDTH,
                TANK_INNER_HEIGHT,
                menu.getFluidAmount(),
                menu.getFluidCapacity());
    }

    private void drawTankOverlay(GuiGraphicsExtractor graphics, int x, int y) {
        graphics.fill(x + TANK_X, y + TANK_Y, x + TANK_X + 1, y + TANK_Y + TANK_HEIGHT, TANK_BORDER_RED);
        graphics.fill(x + TANK_X + TANK_WIDTH - 1, y + TANK_Y, x + TANK_X + TANK_WIDTH, y + TANK_Y + TANK_HEIGHT,
                TANK_BORDER_WHITE);

        for (int lineY : SECTION_LINES) {
            graphics.fill(x + TANK_X, y + lineY, x + TANK_X + TANK_WIDTH, y + lineY + 1, TANK_BORDER_RED);
        }

        for (int lineY : LEFT_TICKS) {
            graphics.fill(x + TANK_X, y + lineY, x + TANK_X + 3, y + lineY + 1, TANK_BORDER_RED);
        }

        for (int lineY : RIGHT_TICKS) {
            graphics.fill(x + TANK_X + 12, y + lineY, x + TANK_X + TANK_WIDTH, y + lineY + 1, TANK_BORDER_WHITE);
        }
    }

    private void drawEnergy(GuiGraphicsExtractor graphics, int x, int y) {
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

    private void drawInfoText(GuiGraphicsExtractor graphics, int x, int y) {
        String rateText = menu.getSpuPerTick() + " SPU/t";
        String efficiencyText = "40 SPU/mB";
        String fluidText = formatBuckets(menu.getFluidAmount()) + " / " + formatBuckets(menu.getFluidCapacity());

        int rateX = x + INFO_X + Math.max(0, (INFO_WIDTH - this.font.width(rateText)) / 2);
        int efficiencyX = x + INFO_X + Math.max(0, (INFO_WIDTH - this.font.width(efficiencyText)) / 2);
        graphics.text(this.font, rateText, rateX, y + INFO_RATE_Y, ENERGY_TEXT_COLOR, false);
        graphics.text(this.font, efficiencyText, efficiencyX, y + INFO_EFFICIENCY_Y, ENERGY_TEXT_COLOR, false);

        int tankCenterX = x + TANK_X + (TANK_WIDTH / 2);
        float scaledTextWidth = this.font.width(fluidText) * FLUID_TEXT_SCALE;
        int fluidTextX = Math.round(tankCenterX - (scaledTextWidth / 2.0F));

        graphics.pose().pushMatrix();
        graphics.pose().scale(FLUID_TEXT_SCALE, FLUID_TEXT_SCALE);
        graphics.text(
                this.font,
                fluidText,
                Math.round(fluidTextX / FLUID_TEXT_SCALE),
                Math.round((y + FLUID_TEXT_Y) / FLUID_TEXT_SCALE),
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

    private static String formatBuckets(int value) {
        return String.format(Locale.ROOT, "%.1fB", value / 1000.0);
    }
}

