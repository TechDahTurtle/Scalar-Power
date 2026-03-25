package com.scalarpower.scalarpower.content.generator;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class CoalGeneratorScreen extends AbstractContainerScreen<CoalGeneratorMenu> {
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath("scalarpower", "textures/gui/coal_generator.png");

    private static final int TEXTURE_WIDTH  = 256;
    private static final int TEXTURE_HEIGHT = 256;

    // Fuel slot is at menu pos (82,37) -> slot box: x+82 to x+98, y+37 to y+53
    private static final int FLAME_X      = 84;
    private static final int FLAME_Y      = 28;
    private static final int FLAME_WIDTH  = 7;
    private static final int FLAME_HEIGHT = 4;

    // Energy bar
    private static final int ENERGY_X      = 130;
    private static final int ENERGY_Y      = 17;
    private static final int ENERGY_WIDTH  = 8;
    private static final int ENERGY_HEIGHT = 50;


    public CoalGeneratorScreen(CoalGeneratorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth  = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos, y = topPos;

        // Background texture
        guiGraphics.blit(
                TEXTURE,
                x, y, x + imageWidth, y + imageHeight,
                0.0F, (float) imageWidth  / TEXTURE_WIDTH,
                0.0F, (float) imageHeight / TEXTURE_HEIGHT);

        // ── Flame burn indicator (grows upward as fuel is consumed) ──────────
        if (menu.getBurnTimeTotal() > 0 && menu.getBurnTime() > 0) {
            int pixels = (int) ((float) FLAME_HEIGHT * menu.getBurnTime() / menu.getBurnTimeTotal());
            int top    = y + FLAME_Y + (FLAME_HEIGHT - pixels);
            guiGraphics.fill(x + FLAME_X, top,
                             x + FLAME_X + FLAME_WIDTH, y + FLAME_Y + FLAME_HEIGHT,
                             0xFFFF6600);
            // bright tip
            guiGraphics.fill(x + FLAME_X + 2, top,
                             x + FLAME_X + FLAME_WIDTH - 2, top + 2,
                             0xFFFFCC44);
        }

        // ── Energy bar ───────────────────────────────────────────────────────
        // dark background
        guiGraphics.fill(x + ENERGY_X, y + ENERGY_Y,
                         x + ENERGY_X + ENERGY_WIDTH, y + ENERGY_Y + ENERGY_HEIGHT,
                         0x88000000);
        int filled = menu.getEnergyCapacity() > 0
                ? (int) ((float) ENERGY_HEIGHT * menu.getEnergy() / menu.getEnergyCapacity()) : 0;
        if (filled > 0) {
            guiGraphics.fill(x + ENERGY_X,
                             y + ENERGY_Y + (ENERGY_HEIGHT - filled),
                             x + ENERGY_X + ENERGY_WIDTH,
                             y + ENERGY_Y + ENERGY_HEIGHT,
                             0xFF44CC44);
        }

    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, 8, 6, 0x404040, false);
        // Display Scalar Power
        guiGraphics.drawString(this.font, menu.getEnergy() + " SP", 140, 6, 0x44CC44, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 94, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
