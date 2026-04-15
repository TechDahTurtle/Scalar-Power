package site.scalarstudios.scalarpower.gui.tankrenderer;

import net.minecraft.resources.Identifier;

public final class WaterTankRenderer extends TankRenderer {
    public static final WaterTankRenderer INSTANCE = new WaterTankRenderer();
    private static final Identifier WATER_TEXTURE = Identifier.fromNamespaceAndPath("minecraft", "textures/block/water_still.png");

    private WaterTankRenderer() {
    }

    @Override
    protected Identifier getTexture() {
        return WATER_TEXTURE;
    }
}

