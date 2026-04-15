package site.scalarstudios.scalarpower.block.machine.alloysmelter;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record AlloySmeltingInput(ItemStack first, ItemStack second, ItemStack third) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> first;
            case 1 -> second;
            case 2 -> third;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int size() {
        return 3;
    }

    public int nonEmptyCount() {
        int count = 0;
        if (!first.isEmpty()) {
            count++;
        }
        if (!second.isEmpty()) {
            count++;
        }
        if (!third.isEmpty()) {
            count++;
        }
        return count;
    }
}

