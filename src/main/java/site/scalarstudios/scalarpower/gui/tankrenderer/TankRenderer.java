package site.scalarstudios.scalarpower.gui.tankrenderer;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public final class TankRenderer {
    public static final TankRenderer WATER = new TankRenderer(
            Identifier.fromNamespaceAndPath("minecraft", "textures/block/water_still.png"));
    public static final TankRenderer LAVA = new TankRenderer(
            Identifier.fromNamespaceAndPath("minecraft", "textures/block/lava_still.png"));

    private static final int TILE_SIZE = 16;
    private static final Identifier TANK_TEXTURE = Identifier.fromNamespaceAndPath("scalarpower", "textures/gui/tank.png");

    // How many px the tick marks overlap the fluid inner area on each side.
    private static final int LEFT_TICK_PIXELS = 2;
    private static final int RIGHT_TICK_PIXELS = 2;

    private static final int ROW_NORMAL = 0;
    private static final int ROW_FULL_SKIP = 1;  // full-width section lines
    private static final int ROW_LEFT_TICK = 2;  // left-side tick: skip leftmost 2px
    private static final int ROW_RIGHT_TICK = 3; // right-side tick: skip rightmost 4px

    // Lookup table indexed by row relative to the inner fluid top (max tank height = 64).
    private static final int[] ROW_TYPES = new int[64];

    static {
        for (int r : new int[]{15, 31, 47}) {
            ROW_TYPES[r] = ROW_FULL_SKIP;
        }
        for (int r : new int[]{4, 11, 20, 27, 36, 43, 52, 59}) {
            ROW_TYPES[r] = ROW_LEFT_TICK;
        }
        for (int r : new int[]{8, 24, 40, 56}) {
            ROW_TYPES[r] = ROW_RIGHT_TICK;
        }
    }

    private final Identifier fluidTexture;

    private TankRenderer(Identifier fluidTexture) {
        this.fluidTexture = fluidTexture;
    }

    public void renderFluid(
            GuiGraphicsExtractor graphics,
            int x,
            int y,
            int width,
            int height,
            int amount,
            int capacity) {
        if (width <= 0 || height <= 0 || capacity <= 0 || amount <= 0) {
            return;
        }

        int filledHeight = Math.max(1, (int) Math.ceil((double) height * amount / capacity));
        filledHeight = Math.min(height, filledHeight);
        int filledTop = y + (height - filledHeight);
        int filledBottom = y + height;

        int segmentStart = filledTop;
        for (int row = filledTop; row < filledBottom; row++) {
            int relRow = row - y;
            int rowType = (relRow >= 0 && relRow < ROW_TYPES.length) ? ROW_TYPES[relRow] : ROW_NORMAL;

            if (rowType == ROW_NORMAL) {
                continue;
            }

            // Flush accumulated normal rows as one segment.
            if (row > segmentStart) {
                renderFluidSegment(graphics, x, segmentStart, width, row - segmentStart);
            }

            // Render this special row partially or not at all.
            if (rowType == ROW_LEFT_TICK && width > LEFT_TICK_PIXELS) {
                renderFluidSegment(graphics, x + LEFT_TICK_PIXELS, row, width - LEFT_TICK_PIXELS, 1);
            } else if (rowType == ROW_RIGHT_TICK && width > RIGHT_TICK_PIXELS) {
                renderFluidSegment(graphics, x, row, width - RIGHT_TICK_PIXELS, 1);
            }

            segmentStart = row + 1;
        }

        if (segmentStart < filledBottom) {
            renderFluidSegment(graphics, x, segmentStart, width, filledBottom - segmentStart);
        }
    }

    private void renderFluidSegment(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        int bottom = y + height;
        for (int yOffset = 0; yOffset < height; yOffset += TILE_SIZE) {
            int tileHeight = Math.min(TILE_SIZE, height - yOffset);
            int drawY = bottom - yOffset - tileHeight;
            float textureV = TILE_SIZE - tileHeight;
            for (int xOffset = 0; xOffset < width; xOffset += TILE_SIZE) {
                int tileWidth = Math.min(TILE_SIZE, width - xOffset);
                graphics.blit(
                        RenderPipelines.GUI_TEXTURED,
                        fluidTexture,
                        x + xOffset, drawY,
                        0.0F, textureV,
                        tileWidth, tileHeight,
                        TILE_SIZE, TILE_SIZE);
            }
        }
    }

    public void renderOverlay(
            GuiGraphicsExtractor graphics,
            int x,
            int y,
            int width,
            int height) {
        if (width <= 0 || height <= 0) {
            return;
        }

        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                TANK_TEXTURE,
                x, y,
                0.0F, 0.0F,
                width, height,
                width, height);
    }
}
