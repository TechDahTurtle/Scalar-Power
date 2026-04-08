package site.scalarstudios.scalarpower.machines.battery;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import java.text.NumberFormat;
import java.util.Locale;

public class BatteryScreen extends AbstractContainerScreen<BatteryMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("scalarpower", "textures/gui/battery.png");

    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;

    private static final int ENERGY_Y = 22;
    private static final int ENERGY_WIDTH = 8;
    private static final int ENERGY_HEIGHT = 42;
    private static final int ENERGY_TEXT_COLOR = 0xFF4A4A4A;
    private static final int ENERGY_TEXT_Y = 68;
    private static final float ENERGY_TEXT_SCALE = 0.75F;

    public BatteryScreen(BatteryMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        int x = this.leftPos;
        int y = this.topPos;
        int barX = x + (this.imageWidth - ENERGY_WIDTH) / 2;
        boolean infiniteEnergy = menu.hasInfiniteEnergy();

        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0.0F, 0.0F, this.imageWidth, this.imageHeight, TEXTURE_WIDTH, TEXTURE_HEIGHT);

        graphics.fill(barX, y + ENERGY_Y, barX + ENERGY_WIDTH, y + ENERGY_Y + ENERGY_HEIGHT, 0x88000000);
        int filled = infiniteEnergy
                ? ENERGY_HEIGHT
                : menu.getEnergyCapacity() > 0
                ? (int) ((float) ENERGY_HEIGHT * menu.getEnergy() / menu.getEnergyCapacity())
                : 0;
        if (filled > 0) {
            graphics.fill(
                    barX,
                    y + ENERGY_Y + (ENERGY_HEIGHT - filled),
                    barX + ENERGY_WIDTH,
                    y + ENERGY_Y + ENERGY_HEIGHT,
                    0xFF44CC44);
        }

        String energyText = infiniteEnergy
                ? "∞ FE"
                : NumberFormat.getIntegerInstance(Locale.ROOT).format(menu.getEnergy()) + " FE";
        float scaledTextWidth = this.font.width(energyText) * ENERGY_TEXT_SCALE;
        int energyTextX = Math.round(x + (this.imageWidth - scaledTextWidth) / 2.0F);
        int energyTextY = y + ENERGY_TEXT_Y;

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
}

