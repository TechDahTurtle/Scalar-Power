package site.scalarstudios.scalarpower.gui.tankrenderer;

import net.minecraft.resources.Identifier;

public final class LavaTankRenderer extends TankRenderer {
    public static final LavaTankRenderer INSTANCE = new LavaTankRenderer();
    private static final Identifier LAVA_TEXTURE = Identifier.fromNamespaceAndPath("minecraft", "textures/block/lava_still.png");

    private LavaTankRenderer() {
    }

    @Override
    protected Identifier getTexture() {
        return LAVA_TEXTURE;
    }
}

