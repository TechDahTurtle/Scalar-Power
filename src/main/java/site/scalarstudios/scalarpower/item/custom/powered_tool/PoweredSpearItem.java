package site.scalarstudios.scalarpower.item.custom.powered_tool;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import site.scalarstudios.scalarpower.item.ScalarPowerToolMaterials;

import java.util.function.Consumer;

public class PoweredSpearItem extends Item {
    public PoweredSpearItem(Properties properties) {
        super(properties
                .fireResistant()
                .spear(ScalarPowerToolMaterials.COBALT, 1.15F, 1.2F, 0.4F, 2.5F, 9.0F, 5.5F, 5.1F, 8.75F, 4.6F));
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        PoweredToolUtil.syncPoweredState(stack, this);
        if (!PoweredToolUtil.hasPower(stack)) {
            return;
        }

        PoweredToolUtil.drainPower(stack);
        PoweredToolUtil.syncPoweredState(stack, this);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity owner, net.minecraft.world.entity.EquipmentSlot slot) {
        super.inventoryTick(stack, level, owner, slot);
        PoweredToolUtil.syncPoweredState(stack, this);
    }

    @Override
    public void onCraftedBy(ItemStack stack, Player player) {
        super.onCraftedBy(stack, player);
        PoweredToolUtil.initializeCraftedUncharged(stack, this);
    }

    @SuppressWarnings("deprecated")
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, display, builder, tooltipFlag);
        PoweredToolUtil.syncPoweredState(stack, this);
        PoweredToolUtil.appendPowerTooltip(stack, builder, display, tooltipFlag);
    }
}

