package site.scalarstudios.scalarpower.content.generator.entropy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import site.scalarstudios.scalarpower.block.ScalarPowerBlockEntities;
import site.scalarstudios.scalarpower.power.NeoEnergyTransferUtil;

import java.util.Map;

public class EntropyGeneratorBlockEntity extends BlockEntity implements MenuProvider {
    private static final int ENERGY_CAPACITY = 1_000_000;
    private static final int PUSH_PER_SIDE = 2_000;

    private static final Map<Block, Integer> TEMPERATURE_VALUES = Map.ofEntries(
            Map.entry(Blocks.LAVA, 150),
            Map.entry(Blocks.MAGMA_BLOCK, 120),
            Map.entry(Blocks.SOUL_FIRE, 110),
            Map.entry(Blocks.FIRE, 100),
            Map.entry(Blocks.SOUL_CAMPFIRE, 70),
            Map.entry(Blocks.CAMPFIRE, 60),
            Map.entry(Blocks.SOUL_LANTERN, 30),
            Map.entry(Blocks.JACK_O_LANTERN, 25),
            Map.entry(Blocks.LANTERN, 20),
            Map.entry(Blocks.TORCH, 15),
            Map.entry(Blocks.WALL_TORCH, 15),
            Map.entry(Blocks.WATER, -15),
            Map.entry(Blocks.POWDER_SNOW, -60),
            Map.entry(Blocks.SNOW_BLOCK, -80),
            Map.entry(Blocks.ICE, -100),
            Map.entry(Blocks.PACKED_ICE, -120),
            Map.entry(Blocks.BLUE_ICE, -150));

    private int currentGeneration;
    private int leftTemperature;
    private int rightTemperature;

    private final net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler energyHandler =
            new net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler(
                    ENERGY_CAPACITY,
                    ENERGY_CAPACITY,
                    ENERGY_CAPACITY,
                    0) {
                @Override
                protected void onEnergyChanged(int previousAmount) {
                    setChanged();
                }
            };

    public EntropyGeneratorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ScalarPowerBlockEntities.ENTROPY_GENERATOR.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, EntropyGeneratorBlockEntity blockEntity) {
        if (level == null || level.isClientSide()) {
            return;
        }

        Direction facing = state.getValue(EntropyGeneratorBlock.FACING);
        int leftTemp = getTemperature(level.getBlockState(pos.relative(facing.getCounterClockWise())));
        int rightTemp = getTemperature(level.getBlockState(pos.relative(facing.getClockWise())));
        boolean leftNegative = leftTemp < 0 && rightTemp > 0;

        int potentialGeneration = calculateGeneration(leftTemp, rightTemp);

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
            NeoEnergyTransferUtil.pushEnergy(level, pos, blockEntity.energyHandler, PUSH_PER_SIDE);
        }

        if (state.hasProperty(EntropyGeneratorBlock.LEFT_NEGATIVE)
                && state.getValue(EntropyGeneratorBlock.LEFT_NEGATIVE) != leftNegative) {
            level.setBlock(pos, state.setValue(EntropyGeneratorBlock.LEFT_NEGATIVE, leftNegative), 3);
        }

        if (blockEntity.currentGeneration != generated
                || blockEntity.leftTemperature != leftTemp
                || blockEntity.rightTemperature != rightTemp) {
            blockEntity.currentGeneration = generated;
            blockEntity.leftTemperature = leftTemp;
            blockEntity.rightTemperature = rightTemp;
            blockEntity.setChanged();
        }
    }

    private static int getTemperature(BlockState blockState) {
        return TEMPERATURE_VALUES.getOrDefault(blockState.getBlock(), 0);
    }

    private static int calculateGeneration(int leftTemperature, int rightTemperature) {
        boolean leftHot = leftTemperature > 0;
        boolean rightHot = rightTemperature > 0;
        boolean leftCold = leftTemperature < 0;
        boolean rightCold = rightTemperature < 0;
        if (!((leftHot && rightCold) || (leftCold && rightHot))) {
            return 0;
        }

        int temperatureDelta = Math.abs(leftTemperature - rightTemperature);
        if (temperatureDelta <= 0) {
            return 0;
        }

        int combinedMagnitude = Math.abs(leftTemperature) + Math.abs(rightTemperature);
        double multiplier = 1.0D + (combinedMagnitude / 1000.0D);
        return Math.max(1, (int) Math.round(temperatureDelta * multiplier));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        energyHandler.serialize(output);
        output.putInt("CurrentGeneration", currentGeneration);
        output.putInt("LeftTemperature", leftTemperature);
        output.putInt("RightTemperature", rightTemperature);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energyHandler.deserialize(input);
        currentGeneration = input.getIntOr("CurrentGeneration", 0);
        leftTemperature = input.getIntOr("LeftTemperature", 0);
        rightTemperature = input.getIntOr("RightTemperature", 0);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.scalarpower.entropy_generator");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new EntropyGeneratorMenu(id, inventory, this, new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> (int) energyHandler.getAmountAsLong();
                    case 1 -> (int) energyHandler.getCapacityAsLong();
                    case 2 -> currentGeneration;
                    case 3 -> leftTemperature;
                    case 4 -> rightTemperature;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> energyHandler.set(value);
                    case 2 -> currentGeneration = value;
                    case 3 -> leftTemperature = value;
                    case 4 -> rightTemperature = value;
                    default -> {
                    }
                }
            }

            @Override
            public int getCount() {
                return 5;
            }
        });
    }

    public EnergyHandler getEnergyHandler(Direction side) {
        return energyHandler;
    }
}

