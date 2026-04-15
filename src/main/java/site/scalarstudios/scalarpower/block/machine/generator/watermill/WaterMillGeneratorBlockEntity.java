package site.scalarstudios.scalarpower.block.machine.generator.watermill;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import site.scalarstudios.scalarpower.block.ScalarPowerBlockEntities;
import site.scalarstudios.scalarpower.block.machine.MachineUtils;
import site.scalarstudios.scalarpower.power.NeoEnergyTransferUtil;

public class WaterMillGeneratorBlockEntity extends BlockEntity implements MenuProvider {
    private static final int ENERGY_CAPACITY = MachineUtils.GENERAL_GENERATOR_CAPACITY;
    private static final int PUSH_PER_SIDE = MachineUtils.WATER_MILL_GENERATOR_SPU_PER_SIDE;

    private int currentGeneration;
    private int northGeneration;
    private int southGeneration;
    private int eastGeneration;
    private int westGeneration;

    private final net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler energyHandler =
            new net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler(
                    ENERGY_CAPACITY,
                    0,
                    ENERGY_CAPACITY,
                    0) {
                @Override
                protected void onEnergyChanged(int previousAmount) {
                    setChanged();
                }
            };

    public WaterMillGeneratorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ScalarPowerBlockEntities.WATER_MILL_GENERATOR.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, WaterMillGeneratorBlockEntity blockEntity) {
        if (level == null || level.isClientSide()) {
            return;
        }

        int northPotential = getFlowGeneration(level, pos.relative(Direction.NORTH));
        int southPotential = getFlowGeneration(level, pos.relative(Direction.SOUTH));
        int eastPotential = getFlowGeneration(level, pos.relative(Direction.EAST));
        int westPotential = getFlowGeneration(level, pos.relative(Direction.WEST));

        int potentialGeneration = Math.min(
                MachineUtils.WATER_MILL_GENERATOR_MAX_SPU_PER_TICK,
                northPotential + southPotential + eastPotential + westPotential);

        long energy = blockEntity.energyHandler.getAmountAsLong();
        long capacity = blockEntity.energyHandler.getCapacityAsLong();

        int generated = 0;
        if (potentialGeneration > 0 && energy < capacity) {
            generated = Math.min(potentialGeneration, (int) (capacity - energy));
            if (generated > 0) {
                blockEntity.energyHandler.set((int) (energy + generated));
            }
        }

        if (blockEntity.energyHandler.getAmountAsLong() > 0) {
            NeoEnergyTransferUtil.pushEnergyToTransferBlocks(level, pos, blockEntity.energyHandler, PUSH_PER_SIDE);
        }

        if (blockEntity.currentGeneration != generated
                || blockEntity.northGeneration != northPotential
                || blockEntity.southGeneration != southPotential
                || blockEntity.eastGeneration != eastPotential
                || blockEntity.westGeneration != westPotential) {
            blockEntity.currentGeneration = generated;
            blockEntity.northGeneration = northPotential;
            blockEntity.southGeneration = southPotential;
            blockEntity.eastGeneration = eastPotential;
            blockEntity.westGeneration = westPotential;
            blockEntity.setChanged();
        }
    }

    private static int getFlowGeneration(Level level, BlockPos neighborPos) {
        BlockState neighborState = level.getBlockState(neighborPos);
        FluidState fluidState = neighborState.getFluidState();
        if (!fluidState.is(FluidTags.WATER) || fluidState.isSource() || !neighborState.hasProperty(LiquidBlock.LEVEL)) {
            return 0;
        }

        int flowLevel = neighborState.getValue(LiquidBlock.LEVEL);
        if (flowLevel < MachineUtils.WATER_MILL_GENERATOR_MIN_FLOW_LEVEL || flowLevel > MachineUtils.WATER_MILL_GENERATOR_MAX_FLOW_LEVEL) {
            return 0;
        }

        return Math.max(0, MachineUtils.WATER_MILL_GENERATOR_SPU_AT_FLOW_LEVEL_ONE - (flowLevel - 1));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        energyHandler.serialize(output);
        output.putInt("CurrentGeneration", currentGeneration);
        output.putInt("NorthGeneration", northGeneration);
        output.putInt("SouthGeneration", southGeneration);
        output.putInt("EastGeneration", eastGeneration);
        output.putInt("WestGeneration", westGeneration);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energyHandler.deserialize(input);
        currentGeneration = input.getIntOr("CurrentGeneration", 0);
        northGeneration = input.getIntOr("NorthGeneration", 0);
        southGeneration = input.getIntOr("SouthGeneration", 0);
        eastGeneration = input.getIntOr("EastGeneration", 0);
        westGeneration = input.getIntOr("WestGeneration", 0);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.scalarpower.water_mill_generator");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new WaterMillGeneratorMenu(id, inventory, this, new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> (int) energyHandler.getAmountAsLong();
                    case 1 -> (int) energyHandler.getCapacityAsLong();
                    case 2 -> currentGeneration;
                    case 3 -> northGeneration;
                    case 4 -> southGeneration;
                    case 5 -> eastGeneration;
                    case 6 -> westGeneration;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> energyHandler.set(value);
                    case 2 -> currentGeneration = value;
                    case 3 -> northGeneration = value;
                    case 4 -> southGeneration = value;
                    case 5 -> eastGeneration = value;
                    case 6 -> westGeneration = value;
                    default -> {
                    }
                }
            }

            @Override
            public int getCount() {
                return 7;
            }
        });
    }

    public EnergyHandler getEnergyHandler(Direction side) {
        return energyHandler;
    }
}


