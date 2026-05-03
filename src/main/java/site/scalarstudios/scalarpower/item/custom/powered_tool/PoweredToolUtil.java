package site.scalarstudios.scalarpower.item.custom.powered_tool;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public final class PoweredToolUtil {
    private PoweredToolUtil() {
    }

    public static int getPower(ItemStack stack) {
        return Math.max(0, stack.getMaxDamage() - stack.getDamageValue());
    }

    public static boolean hasPower(ItemStack stack) {
        return getPower(stack) > 0;
    }

    public static void drainPower(ItemStack stack) {
        if (!hasPower(stack)) {
            return;
        }

        stack.setDamageValue(Math.min(stack.getMaxDamage(), stack.getDamageValue() + 1));
    }

    public static void syncPoweredState(ItemStack stack, Item item) {
        if (hasPower(stack)) {
            restoreComponent(stack, item, DataComponents.TOOL);
            restoreComponent(stack, item, DataComponents.WEAPON);
            restoreComponent(stack, item, DataComponents.ATTRIBUTE_MODIFIERS);
            restoreComponent(stack, item, DataComponents.KINETIC_WEAPON);
            restoreComponent(stack, item, DataComponents.PIERCING_WEAPON);
            restoreComponent(stack, item, DataComponents.ATTACK_RANGE);
            restoreComponent(stack, item, DataComponents.MINIMUM_ATTACK_CHARGE);
            restoreComponent(stack, item, DataComponents.SWING_ANIMATION);
            restoreComponent(stack, item, DataComponents.USE_EFFECTS);
            restoreComponent(stack, item, DataComponents.DAMAGE_TYPE);
            return;
        }

        stack.remove(DataComponents.TOOL);
        stack.remove(DataComponents.WEAPON);
        stack.remove(DataComponents.ATTRIBUTE_MODIFIERS);
        stack.remove(DataComponents.KINETIC_WEAPON);
        stack.remove(DataComponents.PIERCING_WEAPON);
        stack.remove(DataComponents.ATTACK_RANGE);
        stack.remove(DataComponents.MINIMUM_ATTACK_CHARGE);
        stack.remove(DataComponents.SWING_ANIMATION);
        stack.remove(DataComponents.USE_EFFECTS);
        stack.remove(DataComponents.DAMAGE_TYPE);
    }

    public static void initializeCraftedUncharged(ItemStack stack, Item item) {
        if (stack.isDamageableItem()) {
            stack.setDamageValue(stack.getMaxDamage());
        }

        syncPoweredState(stack, item);
    }

    private static <T> void restoreComponent(ItemStack stack, Item item, DataComponentType<T> componentType) {
        T component = item.components().get(componentType);
        if (component == null) {
            stack.remove(componentType);
            return;
        }

        stack.set(componentType, component);
    }

    @SuppressWarnings("deprecated")
    public static void appendPowerTooltip(ItemStack stack, Consumer<Component> builder, TooltipDisplay display, TooltipFlag tooltipFlag) {
        int currentPower = getPower(stack);
        int maxPower = stack.getMaxDamage();

        builder.accept(Component.translatable("tooltip.scalarpower.tool_power", currentPower, maxPower).withStyle(ChatFormatting.GRAY));

        if (currentPower <= 0) {
            builder.accept(Component.translatable("tooltip.scalarpower.tool_unpowered").withStyle(ChatFormatting.RED));
        }
    }
}


