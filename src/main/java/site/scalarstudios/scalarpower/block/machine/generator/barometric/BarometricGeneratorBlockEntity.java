package site.scalarstudios.scalarpower.block.machine.generator.barometric;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import site.scalarstudios.scalarpower.block.ScalarPowerBlockEntities;
import site.scalarstudios.scalarpower.block.machine.MachineUtils;
import site.scalarstudios.scalarpower.power.NeoEnergyTransferUtil;

public class BarometricGeneratorBlockEntity extends BlockEntity implements MenuProvider {
    private static final int ENERGY_CAPACITY = MachineUtils.GENERAL_GENERATOR_CAPACITY;
    private static final int PUSH_PER_SIDE = MachineUtils.BAROMETRIC_GENERATOR_SPU_PER_SIDE;

    private int currentGeneration;
    private int altitudeGeneration;
    private boolean badWeather;

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

    public BarometricGeneratorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ScalarPowerBlockEntities.BAROMETRIC_GENERATOR.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BarometricGeneratorBlockEntity blockEntity) {
        if (level == null || level.isClientSide()) {
            return;
        }

        int altitudeSteps = Math.max(0, pos.getY() / MachineUtils.BAROMETRIC_GENERATOR_Y_STEP);
        int baseGeneration = altitudeSteps * MachineUtils.BAROMETRIC_GENERATOR_SPU_PER_Y_STEP;
        boolean hasBadWeather = isBadWeather(level, pos);

        double multiplier = hasBadWeather ? MachineUtils.BAROMETRIC_GENERATOR_BAD_WEATHER_MULTIPLIER : 1.0D;
        int potentialGeneration = (int) Math.round(baseGeneration * multiplier);

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
                || blockEntity.altitudeGeneration != baseGeneration
                || blockEntity.badWeather != hasBadWeather) {
            blockEntity.currentGeneration = generated;
            blockEntity.altitudeGeneration = baseGeneration;
            blockEntity.badWeather = hasBadWeather;
            blockEntity.setChanged();
        }
    }

    private static boolean isBadWeather(Level level, BlockPos pos) {
        BlockPos skyPos = pos.above();
        return level.isRainingAt(skyPos) || (level.isThundering() && level.canSeeSky(skyPos));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        energyHandler.serialize(output);
        output.putInt("CurrentGeneration", currentGeneration);
        output.putInt("AltitudeGeneration", altitudeGeneration);
        output.putBoolean("BadWeather", badWeather);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energyHandler.deserialize(input);
        currentGeneration = input.getIntOr("CurrentGeneration", 0);
        altitudeGeneration = input.getIntOr("AltitudeGeneration", 0);
        badWeather = input.getBooleanOr("BadWeather", false);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.scalarpower.barometric_generator");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new BarometricGeneratorMenu(id, inventory, this, new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> (int) energyHandler.getAmountAsLong();
                    case 1 -> (int) energyHandler.getCapacityAsLong();
                    case 2 -> currentGeneration;
                    case 3 -> altitudeGeneration;
                    case 4 -> badWeather ? 1 : 0;
                    case 5 -> getBlockPos().getY();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> energyHandler.set(value);
                    case 2 -> currentGeneration = value;
                    case 3 -> altitudeGeneration = value;
                    case 4 -> badWeather = value != 0;
                    default -> {
                    }
                }
            }

            @Override
            public int getCount() {
                return 6;
            }
        });
    }

    public EnergyHandler getEnergyHandler(Direction side) {
        return energyHandler;
    }
}

