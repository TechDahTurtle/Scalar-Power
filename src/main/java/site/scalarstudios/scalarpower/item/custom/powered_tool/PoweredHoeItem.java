package site.scalarstudios.scalarpower.item.custom.powered_tool;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import site.scalarstudios.scalarpower.item.ScalarPowerToolMaterials;

import java.util.function.Predicate;
import java.util.function.Consumer;

public class PoweredHoeItem extends HoeItem {
    public PoweredHoeItem(Properties properties) {
        super(ScalarPowerToolMaterials.COBALT, -4.0F, 0.0F, properties.durability(ScalarPowerToolMaterials.COBALT.durability()).fireResistant());
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
    public InteractionResult useOn(UseOnContext context) {
        ItemStack itemInHand = context.getItemInHand();
        PoweredToolUtil.syncPoweredState(itemInHand, this);
        if (!PoweredToolUtil.hasPower(itemInHand)) {
            return InteractionResult.PASS;
        }

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState toolModifiedState = level.getBlockState(pos).getToolModifiedState(context, net.neoforged.neoforge.common.ItemAbilities.HOE_TILL, false);
        com.mojang.datafixers.util.Pair<Predicate<UseOnContext>, Consumer<UseOnContext>> logicPair = toolModifiedState == null
                ? null
                : com.mojang.datafixers.util.Pair.of(ctx -> true, HoeItem.changeIntoState(toolModifiedState));

        if (logicPair == null) {
            return InteractionResult.PASS;
        }

        Predicate<UseOnContext> predicate = logicPair.getFirst();
        Consumer<UseOnContext> action = logicPair.getSecond();
        if (!predicate.test(context)) {
            return InteractionResult.PASS;
        }

        Player player = context.getPlayer();
        level.playSound(player, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (!level.isClientSide()) {
            action.accept(context);
            if (player != null) {
                PoweredToolUtil.drainPower(itemInHand);
                PoweredToolUtil.syncPoweredState(itemInHand, this);
            }
        }

        return InteractionResult.SUCCESS;
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


