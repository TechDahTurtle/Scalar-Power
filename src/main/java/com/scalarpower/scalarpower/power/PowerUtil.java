package com.scalarpower.scalarpower.power;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class PowerUtil {
    private PowerUtil() {
    }

    public static int pushEnergy(Level level, BlockPos sourcePos, PowerNode source, int maxTransferPerSide) {
        if (maxTransferPerSide <= 0) {
            return 0;
        }

        PowerNode[] targets = new PowerNode[Direction.values().length];
        int targetCount = 0;
        for (Direction direction : Direction.values()) {
            if (!source.canConnectPower(direction)) {
                continue;
            }

            BlockEntity be = level.getBlockEntity(sourcePos.relative(direction));
            if (!(be instanceof PowerNode target) || !target.canConnectPower(direction.getOpposite())) {
                continue;
            }

            if (target.receiveEnergy(1, true) > 0) {
                targets[targetCount++] = target;
            }
        }

        if (targetCount == 0) {
            return 0;
        }

        int available = source.getEnergyStored();
        if (available <= 0) {
            return 0;
        }

        int budget = Math.min(available, maxTransferPerSide * targetCount);
        int[] sent = new int[targetCount];
        int totalMoved = 0;

        // First pass: split the budget evenly so one target does not get filled first.
        int base = budget / targetCount;
        int remainder = budget % targetCount;
        for (int i = 0; i < targetCount; i++) {
            int planned = base + (i < remainder ? 1 : 0);
            if (planned <= 0) {
                continue;
            }
            int accepted = targets[i].receiveEnergy(planned, false);
            if (accepted > 0) {
                source.extractEnergy(accepted, false);
                sent[i] += accepted;
                totalMoved += accepted;
            }
        }

        int remainingBudget = budget - totalMoved;
        if (remainingBudget <= 0) {
            return totalMoved;
        }

        // Second pass: redistribute any leftover to targets that still have room this tick.
        for (int i = 0; i < targetCount && remainingBudget > 0; i++) {
            int roomThisTick = maxTransferPerSide - sent[i];
            if (roomThisTick <= 0) {
                continue;
            }
            int offer = Math.min(roomThisTick, remainingBudget);
            int accepted = targets[i].receiveEnergy(offer, false);
            if (accepted > 0) {
                source.extractEnergy(accepted, false);
                totalMoved += accepted;
                remainingBudget -= accepted;
            }
        }

        return totalMoved;
    }

    public static int pullEnergy(Level level, BlockPos receiverPos, PowerNode receiver, int maxPerSide) {
        int pulled = 0;
        for (Direction direction : Direction.values()) {
            if (!receiver.canConnectPower(direction)) {
                continue;
            }

            BlockEntity be = level.getBlockEntity(receiverPos.relative(direction));
            if (!(be instanceof PowerNode source) || !source.canConnectPower(direction.getOpposite())) {
                continue;
            }

            int wanted = Math.min(maxPerSide, receiver.getEnergyCapacity() - receiver.getEnergyStored());
            if (wanted <= 0) {
                break;
            }

            int extracted = source.extractEnergy(wanted, false);
            if (extracted > 0) {
                int accepted = receiver.receiveEnergy(extracted, false);
                if (accepted < extracted) {
                    source.receiveEnergy(extracted - accepted, false);
                }
                pulled += accepted;
            }
        }
        return pulled;
    }
}

