package com.scalarpower.scalarpower.content.grinder;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class GrinderScreen extends AbstractContainerScreen<GrinderMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("scalarpower", "textures/gui/grinder.png");
    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;
    private static final int ENERGY_X = 10;
    private static final int ENERGY_Y = 20;
    private static final int ENERGY_WIDTH = 8;
    private static final int ENERGY_HEIGHT = 50;
    private static final int PROGRESS_X = 80;
    private static final int PROGRESS_Y = 32;
    private static final int PROGRESS_WIDTH = 24;
    private static final int PROGRESS_HEIGHT = 16;
    // Progress arrow sprite location in grinder.png (typically stored in the extra space to the right of the panel).
    private static final int PROGRESS_U = 176;
    private static final int PROGRESS_V = 0;

    public GrinderScreen(GrinderMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos, y = topPos;
        guiGraphics.blit(
                TEXTURE,
                x,
                y,
                x + imageWidth,
                y + imageHeight,
                0.0F,
                (float) imageWidth / TEXTURE_WIDTH,
                0.0F,
                (float) imageHeight / TEXTURE_HEIGHT);

        int progressPixels = menu.getMaxProgress() > 0
                ? (int) ((float) PROGRESS_WIDTH * menu.getProgress() / menu.getMaxProgress())
                : 0;
        if (progressPixels > 0) {
            guiGraphics.blit(
                    TEXTURE,
                    x + PROGRESS_X,
                    y + PROGRESS_Y,
                    x + PROGRESS_X + progressPixels,
                    y + PROGRESS_Y + PROGRESS_HEIGHT,
                    (float) PROGRESS_U / TEXTURE_WIDTH,
                    (float) (PROGRESS_U + progressPixels) / TEXTURE_WIDTH,
                    (float) PROGRESS_V / TEXTURE_HEIGHT,
                    (float) (PROGRESS_V + PROGRESS_HEIGHT) / TEXTURE_HEIGHT);
        }

        int energyBar = menu.getEnergyCapacity() > 0
                ? (int) ((float) ENERGY_HEIGHT * menu.getEnergy() / menu.getEnergyCapacity())
                : 0;
        guiGraphics.fill(x + ENERGY_X, y + ENERGY_Y, x + ENERGY_X + ENERGY_WIDTH, y + ENERGY_Y + ENERGY_HEIGHT, 0x66000000);
        if (energyBar > 0) {
            guiGraphics.fill(
                    x + ENERGY_X,
                    y + ENERGY_Y + (ENERGY_HEIGHT - energyBar),
                    x + ENERGY_X + ENERGY_WIDTH,
                    y + ENERGY_Y + ENERGY_HEIGHT,
                    0xFF44CC44);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, 8, 6, 0xFFFFFF, false);
        guiGraphics.drawString(this.font, menu.getEnergy() + " SP", 24, 38, 0x44CC44, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, 8, 72, 0xFFFFFF, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}

