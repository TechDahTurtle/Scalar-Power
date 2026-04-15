package site.scalarstudios.scalarpower.block.machine.freezer;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import site.scalarstudios.scalarpower.gui.tankrenderer.TankRenderer;

import java.util.Locale;

public class FreezerScreen extends AbstractContainerScreen<FreezerMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("scalarpower", "textures/gui/freezer.png");

    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;

    // Progress arrow — centred between tank right edge (51) and output slot (116)
    private static final int PROGRESS_X = 80;
    private static final int PROGRESS_Y = 32;
    private static final int PROGRESS_WIDTH = 24;
    private static final int PROGRESS_HEIGHT = 17;
    private static final int PROGRESS_U = 176;
    private static final int PROGRESS_V = 0;

    private static final int ENERGY_X = 10;
    private static final int ENERGY_Y = 20;
    private static final int ENERGY_WIDTH = 8;
    private static final int ENERGY_HEIGHT = 42;
    private static final int ENERGY_TEXT_Y_OFFSET = 2;
    private static final float ENERGY_TEXT_SCALE = 0.85F;

    // Tank sits between the energy bar and arrow in the freezer layout.
    private static final int TANK_X = 55;
    private static final int TANK_Y = 10;
    private static final int TANK_WIDTH = 17;
    private static final int TANK_HEIGHT = 65;
    private static final int TANK_INNER_X = 56;
    private static final int TANK_INNER_Y = 11;
    private static final int TANK_INNER_WIDTH = 15;
    private static final int TANK_INNER_HEIGHT = 63;

    private static final int FLUID_TEXT_Y = 76;
    private static final int INFO_COLOR = 0xFF4A4A4A;

    public FreezerScreen(FreezerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        int x = this.leftPos;
        int y = this.topPos;

        // Layer 1: GUI background
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0.0F, 0.0F, this.imageWidth, this.imageHeight,
                TEXTURE_WIDTH, TEXTURE_HEIGHT);

        // Layer 2: Fluid
        drawTankFluid(graphics, x, y);

        drawProgress(graphics, x, y);
        drawEnergy(graphics, x, y);
        drawInfoText(graphics, x, y);

        // Draw overlay last so the tank frame is always above the fluid.
        drawTankOverlay(graphics, x, y);
    }

    // This is a little screwy as I never originally intended lava to be in this machine
    private void drawTankFluid(GuiGraphicsExtractor graphics, int x, int y) {
        int fluidAmount = menu.getFluidAmount();
        int capacity = menu.getFluidCapacity();
        int fluidType = menu.getFluidType(); // 0=empty, 1=water, 2=lava
        if (fluidType == 2) {
            TankRenderer.LAVA.renderFluid(
                    graphics,
                    x + TANK_INNER_X,
                    y + TANK_INNER_Y,
                    TANK_INNER_WIDTH,
                    TANK_INNER_HEIGHT,
                    fluidAmount,
                    capacity);
            return;
        }

        TankRenderer.WATER.renderFluid(
                graphics,
                x + TANK_INNER_X,
                y + TANK_INNER_Y,
                TANK_INNER_WIDTH,
                TANK_INNER_HEIGHT,
                fluidAmount,
                capacity);
    }

    private void drawTankOverlay(GuiGraphicsExtractor graphics, int x, int y) {
        // Overlay is identical for all fluids; renderer instance doesn't matter.
        TankRenderer.WATER.renderOverlay(graphics, x + TANK_X, y + TANK_Y, TANK_WIDTH, TANK_HEIGHT);
    }

    private void drawEnergy(GuiGraphicsExtractor graphics, int x, int y) {
        graphics.fill(x + ENERGY_X, y + ENERGY_Y, x + ENERGY_X + ENERGY_WIDTH, y + ENERGY_Y + ENERGY_HEIGHT, 0x88000000);
        int energyBar = menu.getEnergyCapacity() > 0
                ? (int) ((float) ENERGY_HEIGHT * menu.getEnergy() / menu.getEnergyCapacity())
                : 0;
        if (energyBar > 0) {
            graphics.fill(x + ENERGY_X, y + ENERGY_Y + (ENERGY_HEIGHT - energyBar),
                    x + ENERGY_X + ENERGY_WIDTH, y + ENERGY_Y + ENERGY_HEIGHT, 0xFF44CC44);
        }

        String energyText = formatEnergy(menu.getEnergy()) + " / " + formatEnergy(menu.getEnergyCapacity());
        float scaledTextWidth = this.font.width(energyText) * ENERGY_TEXT_SCALE;
        int barCenterX = x + ENERGY_X + (ENERGY_WIDTH / 2);
        int preferredTextX = Math.round(barCenterX - (scaledTextWidth / 2.0F));
        int energyTextX = Math.clamp(preferredTextX, x + 4, x + this.imageWidth - 4 - Math.round(scaledTextWidth));
        int energyTextY = y + ENERGY_Y + ENERGY_HEIGHT + ENERGY_TEXT_Y_OFFSET;

        graphics.pose().pushMatrix();
        graphics.pose().scale(ENERGY_TEXT_SCALE, ENERGY_TEXT_SCALE);
        graphics.text(this.font, energyText,
                Math.round(energyTextX / ENERGY_TEXT_SCALE),
                Math.round(energyTextY / ENERGY_TEXT_SCALE),
                INFO_COLOR, false);
        graphics.pose().popMatrix();
    }

    private void drawProgress(GuiGraphicsExtractor graphics, int x, int y) {
        int cost = menu.getRecipeSpuCost();
        int progress = menu.getProgressSpu();
        if (cost <= 0 || progress <= 0) return;

        int progressPixels = Math.clamp((int) ((float) PROGRESS_WIDTH * progress / cost), 1, PROGRESS_WIDTH);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x + PROGRESS_X, y + PROGRESS_Y,
                PROGRESS_U, PROGRESS_V, progressPixels, PROGRESS_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    private void drawInfoText(GuiGraphicsExtractor graphics, int x, int y) {
        String fluidText = formatBuckets(menu.getFluidAmount()) + " / " + formatBuckets(menu.getFluidCapacity());
        int tankCenterX = x + TANK_X + (TANK_WIDTH / 2);
        int fluidTextX = Math.round(tankCenterX - (this.font.width(fluidText) / 2.0F));
        graphics.text(this.font, fluidText, fluidTextX, y + FLUID_TEXT_Y, INFO_COLOR, false);
    }

    private static String formatEnergy(int value) {
        if (value >= 1000) return String.format(Locale.ROOT, "%.1fk", value / 1000.0);
        return Integer.toString(value);
    }

    private static String formatBuckets(int value) {
        return String.format(Locale.ROOT, "%.1fB", value / 1000.0);
    }
}
