package site.scalarstudios.scalarpower.item.custom.powered_tool;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import site.scalarstudios.scalarpower.item.ScalarPowerToolMaterials;

import java.util.function.Consumer;

public class PoweredShovelItem extends ShovelItem {
    public PoweredShovelItem(Properties properties) {
        super(ScalarPowerToolMaterials.COBALT, 1.5F, -3.0F, properties.durability(ScalarPowerToolMaterials.COBALT.durability()).fireResistant());
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
        BlockState blockState = level.getBlockState(pos);
        if (context.getClickedFace() == Direction.DOWN) {
            return InteractionResult.PASS;
        }

        Player player = context.getPlayer();
        BlockState newState = blockState.getToolModifiedState(context, net.neoforged.neoforge.common.ItemAbilities.SHOVEL_FLATTEN, false);
        BlockState updatedState = null;
        if (newState != null && level.getBlockState(pos.above()).isAir()) {
            level.playSound(player, pos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
            updatedState = newState;
        } else if ((updatedState = blockState.getToolModifiedState(context, net.neoforged.neoforge.common.ItemAbilities.SHOVEL_DOUSE, false)) != null) {
            if (!level.isClientSide()) {
                level.levelEvent(null, 1009, pos, 0);
            }
        }

        if (updatedState == null) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            level.setBlock(pos, updatedState, 11);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, updatedState));
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


