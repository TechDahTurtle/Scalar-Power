package site.scalarstudios.scalarpower.item.custom.powered_tool;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import site.scalarstudios.scalarpower.item.ScalarPowerToolMaterials;

import java.util.function.Consumer;

public class PoweredPickaxeItem extends Item {
    public PoweredPickaxeItem(Properties properties) {
        super(properties.fireResistant().pickaxe(ScalarPowerToolMaterials.COBALT, 1.0F, -2.8F));
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
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        PoweredToolUtil.syncPoweredState(stack, this);
        if (!PoweredToolUtil.hasPower(stack)) {
            return false;
        }

        if (!level.isClientSide() && state.getDestroySpeed(level, pos) != 0.0F) {
            PoweredToolUtil.drainPower(stack);
            PoweredToolUtil.syncPoweredState(stack, this);
        }

        return true;
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


