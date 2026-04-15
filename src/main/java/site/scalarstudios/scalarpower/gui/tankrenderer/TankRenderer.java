package site.scalarstudios.scalarpower.gui.tankrenderer;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public abstract class TankRenderer {
    private static final int DEFAULT_TILE_SIZE = 16;

    protected TankRenderer() {
    }

    protected abstract Identifier getTexture();

    protected int getTileSize() {
        return DEFAULT_TILE_SIZE;
    }

    public final void render(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int amount, int capacity) {
        if (width <= 0 || height <= 0 || capacity <= 0 || amount <= 0) {
            return;
        }

        int tileSize = getTileSize();
        int filledHeight = Math.max(1, (int) Math.ceil((double) height * amount / capacity));
        filledHeight = Math.min(height, filledHeight);
        int bottom = y + height;

        for (int yOffset = 0; yOffset < filledHeight; yOffset += tileSize) {
            int tileHeight = Math.min(tileSize, filledHeight - yOffset);
            int drawY = bottom - yOffset - tileHeight;
            float textureV = tileSize - tileHeight;

            for (int xOffset = 0; xOffset < width; xOffset += tileSize) {
                int tileWidth = Math.min(tileSize, width - xOffset);
                graphics.blit(
                        RenderPipelines.GUI_TEXTURED,
                        getTexture(),
                        x + xOffset,
                        drawY,
                        0.0F,
                        textureV,
                        tileWidth,
                        tileHeight,
                        tileSize,
                        tileSize);
            }
        }
    }
}

