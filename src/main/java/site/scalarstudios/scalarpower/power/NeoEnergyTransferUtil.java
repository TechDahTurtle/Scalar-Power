package site.scalarstudios.scalarpower.power;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandlerUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import site.scalarstudios.scalarpower.machines.wire.copper.CopperWireBlockEntity;
import site.scalarstudios.scalarpower.machines.wire.copper.InsulatedCopperWireBlockEntity;
import site.scalarstudios.scalarpower.machines.wire.glassfiber.GlassFiberWireBlockEntity;
import site.scalarstudios.scalarpower.machines.wire.gold.GoldWireBlockEntity;
import site.scalarstudios.scalarpower.machines.wire.gold.InsulatedGoldWireBlockEntity;

public final class NeoEnergyTransferUtil {
    private NeoEnergyTransferUtil() {
    }

    public static int pushEnergy(Level level, BlockPos sourcePos, EnergyHandler source, int maxTransferPerSide) {
        return pushEnergy(level, sourcePos, source, maxTransferPerSide, false);
    }

    public static int pushEnergyToTransferBlocks(Level level, BlockPos sourcePos, EnergyHandler source, int maxTransferPerSide) {
        return pushEnergy(level, sourcePos, source, maxTransferPerSide, true);
    }

    private static int pushEnergy(Level level, BlockPos sourcePos, EnergyHandler source, int maxTransferPerSide, boolean transferBlocksOnly) {
        if (maxTransferPerSide <= 0) {
            return 0;
        }

        List<EnergyHandler> targets = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            BlockPos targetPos = sourcePos.relative(direction);
            if (transferBlocksOnly && !isTransferBlock(level.getBlockEntity(targetPos))) {
                continue;
            }

            EnergyHandler target = level.getCapability(
                    Capabilities.Energy.BLOCK,
                    targetPos,
                    direction.getOpposite());
            if (target != null && canInsert(target)) {
                targets.add(target);
            }
        }

        if (targets.isEmpty()) {
            return 0;
        }

        int available = clampToInt(source.getAmountAsLong());
        if (available <= 0) {
            return 0;
        }

        int budget = Math.min(available, maxTransferPerSide * targets.size());
        int[] sent = new int[targets.size()];
        int totalMoved = 0;

        int base = budget / targets.size();
        int remainder = budget % targets.size();
        for (int i = 0; i < targets.size(); i++) {
            int planned = base + (i < remainder ? 1 : 0);
            if (planned <= 0) {
                continue;
            }
            int moved = move(source, targets.get(i), planned);
            if (moved > 0) {
                sent[i] += moved;
                totalMoved += moved;
            }
        }

        int remainingBudget = budget - totalMoved;
        if (remainingBudget <= 0) {
            return totalMoved;
        }

        for (int i = 0; i < targets.size() && remainingBudget > 0; i++) {
            int roomThisTick = maxTransferPerSide - sent[i];
            if (roomThisTick <= 0) {
                continue;
            }
            int offer = Math.min(roomThisTick, remainingBudget);
            int moved = move(source, targets.get(i), offer);
            if (moved > 0) {
                totalMoved += moved;
                remainingBudget -= moved;
            }
        }

        return totalMoved;
    }

    public static int pullEnergy(Level level, BlockPos receiverPos, EnergyHandler receiver, int maxPerSide) {
        if (maxPerSide <= 0) {
            return 0;
        }

        int pulled = 0;
        for (Direction direction : Direction.values()) {
            long remainingLong = receiver.getCapacityAsLong() - receiver.getAmountAsLong();
            if (remainingLong <= 0) {
                break;
            }

            int wanted = Math.min(maxPerSide, clampToInt(remainingLong));
            if (wanted <= 0) {
                break;
            }

            EnergyHandler source = level.getCapability(
                    Capabilities.Energy.BLOCK,
                    receiverPos.relative(direction),
                    direction.getOpposite());
            if (source == null) {
                continue;
            }

            int moved = move(source, receiver, wanted);
            if (moved > 0) {
                pulled += moved;
            }
        }
        return pulled;
    }

    private static boolean canInsert(EnergyHandler handler) {
        try (var tx = Transaction.openRoot()) {
            return handler.insert(1, tx) > 0;
        }
    }

    private static boolean isTransferBlock(BlockEntity blockEntity) {
        return blockEntity instanceof CopperWireBlockEntity
                || blockEntity instanceof InsulatedCopperWireBlockEntity
                || blockEntity instanceof GoldWireBlockEntity
                || blockEntity instanceof InsulatedGoldWireBlockEntity
                || blockEntity instanceof GlassFiberWireBlockEntity;
    }

    private static int move(EnergyHandler from, EnergyHandler to, int amount) {
        if (amount <= 0) {
            return 0;
        }

        try (var tx = Transaction.openRoot()) {
            int moved = EnergyHandlerUtil.move(from, to, amount, tx);
            if (moved > 0) {
                tx.commit();
            }
            return moved;
        }
    }

    private static int clampToInt(long value) {
        return (int) Math.min(Integer.MAX_VALUE, Math.max(0L, value));
    }
}

