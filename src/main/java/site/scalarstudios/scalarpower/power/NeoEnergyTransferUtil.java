package site.scalarstudios.scalarpower.power;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandlerUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import site.scalarstudios.scalarpower.block.machine.cable.BaseCableBlockEntity;
import site.scalarstudios.scalarpower.block.machine.cable.CableBehavior;
import net.minecraft.core.registries.BuiltInRegistries;


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

        BlockEntity sourceEntity = level.getBlockEntity(sourcePos);
        boolean sourceIsTransferBlock = isTransferBlock(sourceEntity);

        List<EnergyHandler> nonTransferTargets = new ArrayList<>();
        List<EnergyHandler> transferTargets = new ArrayList<>();

        for (Direction direction : orderedDirections(level, sourcePos)) {
            if (sourceEntity instanceof BaseCableBlockEntity sourceCable) {
                CableBehavior behavior = sourceCable.getBehavior(direction);
                if (behavior == CableBehavior.DISABLED || behavior == CableBehavior.INPUT) {
                    continue;
                }
            }

            BlockPos targetPos = sourcePos.relative(direction);
            BlockEntity targetEntity = level.getBlockEntity(targetPos);
            boolean targetIsTransferBlock = isTransferBlock(targetEntity);

            if (transferBlocksOnly && !targetIsTransferBlock) {
                continue;
            }

            EnergyHandler target = level.getCapability(
                    Capabilities.Energy.BLOCK,
                    targetPos,
                    direction.getOpposite());

            if (target != null && canInsert(target)) {
                if (targetIsTransferBlock) {
                    transferTargets.add(target);
                } else {
                    nonTransferTargets.add(target);
                }
            }
        }

        if (nonTransferTargets.isEmpty() && transferTargets.isEmpty()) {
            return 0;
        }

        int moved = 0;

        // Prefer endpoints first; only feed transfer blocks when appropriate.
        if (!nonTransferTargets.isEmpty()) {
            moved += distributeFairly(source, nonTransferTargets, maxTransferPerSide);
        }

        if (!transferTargets.isEmpty() && (!sourceIsTransferBlock || nonTransferTargets.isEmpty())) {
            moved += distributeFairly(source, transferTargets, maxTransferPerSide);
        }

        return moved;
    }

    private static int distributeFairly(EnergyHandler source, List<EnergyHandler> targets, int maxTransferPerSide) {
        int available = clampToInt(source.getAmountAsLong());
        if (available <= 0 || targets.isEmpty() || maxTransferPerSide <= 0) {
            return 0;
        }

        int budget = Math.min(available, maxTransferPerSide * targets.size());
        int[] sent = new int[targets.size()];
        int totalMoved = 0;
        int remainingBudget = budget;

        // Progressive fair fill:
        // keeps iterating targets while progress is made, reducing starvation and bias.
        while (remainingBudget > 0) {
            boolean progressedThisPass = false;

            for (int i = 0; i < targets.size() && remainingBudget > 0; i++) {
                int roomThisTick = maxTransferPerSide - sent[i];
                if (roomThisTick <= 0) {
                    continue;
                }

                int offer = Math.min(roomThisTick, remainingBudget);
                int moved = move(source, targets.get(i), offer);
                if (moved > 0) {
                    sent[i] += moved;
                    totalMoved += moved;
                    remainingBudget -= moved;
                    progressedThisPass = true;
                }
            }

            if (!progressedThisPass) {
                break;
            }
        }

        return totalMoved;
    }

    public static int pullEnergy(Level level, BlockPos receiverPos, EnergyHandler receiver, int maxPerSide) {
        if (maxPerSide <= 0) {
            return 0;
        }

        int pulled = 0;
        BlockEntity receiverEntity = level.getBlockEntity(receiverPos);

        for (Direction direction : orderedDirections(level, receiverPos)) {
            if (receiverEntity instanceof BaseCableBlockEntity receiverCable) {
                CableBehavior behavior = receiverCable.getBehavior(direction);
                if (behavior == CableBehavior.DISABLED || behavior == CableBehavior.OUTPUT) {
                    continue;
                }
            }

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
        if (blockEntity == null) {
            return false;
        }

        // Internal transfer network blocks.
        if (blockEntity instanceof BaseCableBlockEntity) {
            return true;
        }

        // Pipez compatibility.
        var blockId = BuiltInRegistries.BLOCK.getKey(blockEntity.getBlockState().getBlock());
        return "pipez".equals(blockId.getNamespace());
    }



    private static Direction[] orderedDirections(Level level, BlockPos pos) {
        Direction[] base = Direction.values();
        Direction[] ordered = new Direction[base.length];

        int start = Math.floorMod((int) (level.getGameTime() + pos.asLong()), base.length);
        for (int i = 0; i < base.length; i++) {
            ordered[i] = base[(start + i) % base.length];
        }

        return ordered;
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
        return (int) Math.clamp(value, 0L, Integer.MAX_VALUE);
    }
}
