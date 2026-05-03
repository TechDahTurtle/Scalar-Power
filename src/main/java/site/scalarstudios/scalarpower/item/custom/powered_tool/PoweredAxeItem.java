package site.scalarstudios.scalarpower.item.custom.powered_tool;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import site.scalarstudios.scalarpower.item.ScalarPowerToolMaterials;

import java.util.Optional;
import java.util.function.Consumer;

public class PoweredAxeItem extends AxeItem {
    public PoweredAxeItem(Properties properties) {
        super(ScalarPowerToolMaterials.COBALT, 5.0F, -3.0F, properties.durability(ScalarPowerToolMaterials.COBALT.durability()).fireResistant());
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
        Player player = context.getPlayer();
        if (playerHasBlockingItemUseIntent(context)) {
            return InteractionResult.PASS;
        }

        Optional<BlockState> newBlock = this.evaluateNewBlockState(level, pos, player, level.getBlockState(pos), context);
        if (newBlock.isEmpty()) {
            return InteractionResult.PASS;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            net.minecraft.advancements.CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, itemInHand);
        }

        level.setBlock(pos, newBlock.get(), 11);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, newBlock.get()));
        if (player != null && !player.isCreative()) {
            PoweredToolUtil.drainPower(itemInHand);
            PoweredToolUtil.syncPoweredState(itemInHand, this);
        }

        return InteractionResult.SUCCESS;
    }

    private static boolean playerHasBlockingItemUseIntent(UseOnContext context) {
        Player player = context.getPlayer();
        return player != null
                && context.getHand().equals(InteractionHand.MAIN_HAND)
                && player.getOffhandItem().has(DataComponents.BLOCKS_ATTACKS)
                && !player.isSecondaryUseActive();
    }

    private Optional<BlockState> evaluateNewBlockState(Level level, BlockPos pos, Player player, BlockState oldState, UseOnContext context) {
        Optional<BlockState> strippedBlock = Optional.ofNullable(oldState.getToolModifiedState(context, net.neoforged.neoforge.common.ItemAbilities.AXE_STRIP, false));
        if (strippedBlock.isPresent()) {
            level.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
            return strippedBlock;
        }

        Optional<BlockState> scrapedBlock = Optional.ofNullable(oldState.getToolModifiedState(context, net.neoforged.neoforge.common.ItemAbilities.AXE_SCRAPE, false));
        if (scrapedBlock.isPresent()) {
            spawnSoundAndParticle(level, pos, player, oldState, SoundEvents.AXE_SCRAPE, 3005);
            return scrapedBlock;
        }

        Optional<BlockState> waxOffBlock = Optional.ofNullable(oldState.getToolModifiedState(context, net.neoforged.neoforge.common.ItemAbilities.AXE_WAX_OFF, false));
        if (waxOffBlock.isPresent()) {
            spawnSoundAndParticle(level, pos, player, oldState, SoundEvents.AXE_WAX_OFF, 3004);
        }

        return waxOffBlock;
    }

    private static void spawnSoundAndParticle(Level level, BlockPos pos, Player player, BlockState oldState, SoundEvent soundEvent, int particle) {
        level.playSound(player, pos, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.levelEvent(player, particle, pos, 0);
        if (oldState.getBlock() instanceof ChestBlock && oldState.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
            BlockPos neighborPos = ChestBlock.getConnectedBlockPos(pos, oldState);
            level.gameEvent(GameEvent.BLOCK_CHANGE, neighborPos, GameEvent.Context.of(player, level.getBlockState(neighborPos)));
            level.levelEvent(player, particle, neighborPos, 0);
        }
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


