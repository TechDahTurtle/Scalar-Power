package site.scalarstudios.scalarpower.item;

import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ToolMaterial;

public final class ScalarPowerToolMaterials {
    private static final int COBALT_DURABILITY = 8124;

    public static final ToolMaterial COBALT = new ToolMaterial(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            COBALT_DURABILITY,
            9.0F,
            4.0F,
            15,
            ItemTags.create(Identifier.fromNamespaceAndPath("c", "ingots/cobalt"))
    );

    private ScalarPowerToolMaterials() {
    }
}


