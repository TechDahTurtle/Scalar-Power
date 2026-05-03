package site.scalarstudios.scalarpower.power;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import site.scalarstudios.scalarpower.block.machine.cable.CableBehavior;
import site.scalarstudios.scalarpower.block.transport.pipe.copper.CopperPipeBlockEntity;

public final class NeoFluidTransferUtil {
    private NeoFluidTransferUtil() {
    }

    public static int pushFluid(Level level, BlockPos sourcePos, CopperPipeBlockEntity sourcePipe, int maxTransferPerSide) {
        if (maxTransferPerSide <= 0 || sourcePipe.getFluidAmount() <= 0) {
            return 0;
        }

        FluidResource currentFluid = sourcePipe.getFluidResource();
        if (currentFluid.equals(FluidResource.EMPTY)) {
            return 0;
        }

        ResourceHandler<FluidResource> sourceHandler = sourcePipe.getFluidHandler(null);
        List<ResourceHandler<FluidResource>> nonPipeTargets = new ArrayList<>();
        List<ResourceHandler<FluidResource>> pipeTargets = new ArrayList<>();

        for (Direction direction : Direction.values()) {
            CableBehavior behavior = sourcePipe.getBehavior(direction);
            if (behavior == CableBehavior.DISABLED || behavior == CableBehavior.INPUT) {
                continue;
            }

            BlockPos targetPos = sourcePos.relative(direction);
            BlockEntity targetEntity = level.getBlockEntity(targetPos);
            boolean targetIsPipe = targetEntity instanceof CopperPipeBlockEntity;

            ResourceHandler<FluidResource> target = level.getCapability(
                    Capabilities.Fluid.BLOCK,
                    targetPos,
                    direction.getOpposite());
            if (target == null || !canInsert(target, currentFluid)) {
                continue;
            }

            if (targetIsPipe) {
                pipeTargets.add(target);
            } else {
                nonPipeTargets.add(target);
            }
        }

        int moved = 0;
        if (!nonPipeTargets.isEmpty()) {
            moved += distributeEvenly(sourceHandler, currentFluid, nonPipeTargets, maxTransferPerSide);
        }

        if (!pipeTargets.isEmpty() && nonPipeTargets.isEmpty()) {
            moved += distributeEvenly(sourceHandler, currentFluid, pipeTargets, maxTransferPerSide);
        }

        return moved;
    }

    public static int pullFluid(Level level, BlockPos receiverPos, CopperPipeBlockEntity receiverPipe, int maxPerSide) {
        if (maxPerSide <= 0) {
            return 0;
        }

        ResourceHandler<FluidResource> receiverHandler = receiverPipe.getFluidHandler(null);
        FluidResource desired = receiverPipe.getFluidResource();
        if (desired.equals(FluidResource.EMPTY)) {
            desired = findConnectedPipeFluid(level, receiverPos);
        }

        int pulled = 0;
        for (Direction direction : Direction.values()) {
            CableBehavior behavior = receiverPipe.getBehavior(direction);
            if (behavior == CableBehavior.DISABLED || behavior == CableBehavior.OUTPUT) {
                continue;
            }

            ResourceHandler<FluidResource> source = level.getCapability(
                    Capabilities.Fluid.BLOCK,
                    receiverPos.relative(direction),
                    direction.getOpposite());
            if (source == null) {
                continue;
            }

            FluidResource transferFluid = desired;
            if (transferFluid.equals(FluidResource.EMPTY)) {
                transferFluid = detectFluid(level, receiverPos.relative(direction), source);
                if (transferFluid.equals(FluidResource.EMPTY)) {
                    continue;
                }
            }

            int moved = move(source, receiverHandler, transferFluid, maxPerSide);
            if (moved > 0) {
                pulled += moved;
                if (desired.equals(FluidResource.EMPTY)) {
                    desired = transferFluid;
                }
            }
        }

        return pulled;
    }

    public static FluidResource findConnectedPipeFluid(Level level, BlockPos startPos) {
        BlockEntity start = level.getBlockEntity(startPos);
        if (!(start instanceof CopperPipeBlockEntity)) {
            return FluidResource.EMPTY;
        }

        Set<BlockPos> visited = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        queue.add(startPos);

        while (!queue.isEmpty()) {
            BlockPos currentPos = queue.removeFirst();
            if (!visited.add(currentPos)) {
                continue;
            }

            BlockEntity entity = level.getBlockEntity(currentPos);
            if (!(entity instanceof CopperPipeBlockEntity pipe)) {
                continue;
            }

            FluidResource resource = pipe.getFluidResource();
            if (!resource.equals(FluidResource.EMPTY) && pipe.getFluidAmount() > 0) {
                return resource;
            }

            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = currentPos.relative(direction);
                if (!visited.contains(neighborPos) && level.getBlockEntity(neighborPos) instanceof CopperPipeBlockEntity) {
                    queue.add(neighborPos);
                }
            }
        }

        return FluidResource.EMPTY;
    }

    private static int distributeEvenly(ResourceHandler<FluidResource> source,
            FluidResource resource,
            List<ResourceHandler<FluidResource>> targets,
            int maxTransferPerSide) {
        if (targets.isEmpty()) {
            return 0;
        }

        int budget = maxTransferPerSide * targets.size();
        int[] sent = new int[targets.size()];
        int totalMoved = 0;

        int base = budget / targets.size();
        int remainder = budget % targets.size();
        for (int i = 0; i < targets.size(); i++) {
            int planned = base + (i < remainder ? 1 : 0);
            if (planned <= 0) {
                continue;
            }
            int moved = move(source, targets.get(i), resource, planned);
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
            int moved = move(source, targets.get(i), resource, offer);
            if (moved > 0) {
                totalMoved += moved;
                remainingBudget -= moved;
            }
        }

        return totalMoved;
    }

    private static FluidResource detectFluid(Level level, BlockPos sourcePos, ResourceHandler<FluidResource> source) {
        BlockEntity sourceEntity = level.getBlockEntity(sourcePos);
        if (sourceEntity instanceof CopperPipeBlockEntity sourcePipe && sourcePipe.getFluidAmount() > 0) {
            return sourcePipe.getFluidResource();
        }

        if (source instanceof FluidStacksResourceHandler sourceTank && sourceTank.getAmountAsInt(0) > 0) {
            FluidResource stored = sourceTank.getResource(0);
            if (!stored.equals(FluidResource.EMPTY)) {
                return stored;
            }
        }

        // Fallback for handlers that do not expose a direct "current resource" API.
        for (var fluid : BuiltInRegistries.FLUID) {
            if (fluid == Fluids.EMPTY) {
                continue;
            }

            FluidResource candidate = FluidResource.of(fluid);
            try (var tx = Transaction.openRoot()) {
                if (source.extract(candidate, 1, tx) > 0) {
                    return candidate;
                }
            }
        }

        return FluidResource.EMPTY;
    }

    private static boolean canInsert(ResourceHandler<FluidResource> target, FluidResource resource) {
        try (var tx = Transaction.openRoot()) {
            return target.insert(resource, 1, tx) > 0;
        }
    }

    private static int move(ResourceHandler<FluidResource> from,
            ResourceHandler<FluidResource> to,
            FluidResource resource,
            int amount) {
        if (amount <= 0 || resource.equals(FluidResource.EMPTY)) {
            return 0;
        }

        try (var tx = Transaction.openRoot()) {
            int extracted = from.extract(resource, amount, tx);
            if (extracted <= 0) {
                return 0;
            }

            int inserted = to.insert(resource, extracted, tx);
            if (inserted != extracted) {
                return 0;
            }

            tx.commit();
            return inserted;
        }
    }
}



