package site.scalarstudios.scalarpower.block.machine.generator.geothermal;

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
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import site.scalarstudios.scalarpower.block.ScalarPowerBlockEntities;
import site.scalarstudios.scalarpower.block.machine.MachineUtils;
import site.scalarstudios.scalarpower.power.NeoEnergyTransferUtil;

public class GeothermalGeneratorBlockEntity extends BlockEntity implements MenuProvider {
    private static final int ENERGY_CAPACITY = MachineUtils.GENERAL_GENERATOR_CAPACITY;
    private static final int ENERGY_PER_TICK = MachineUtils.GEOTHERMAL_GENERATOR_SPU_PER_TICK;
    private static final int LAVA_PER_TICK = MachineUtils.GEOTHERMAL_GENERATOR_MB_PER_TICK;
    private static final int TANK_CAPACITY = MachineUtils.GEOTHERMAL_GENERATOR_TANK_CAPACITY_MB;
    private static final int PUSH_PER_SIDE = MachineUtils.GENERAL_GENERATOR_SPU_PER_SIDE;
    private static final FluidResource LAVA_RESOURCE = FluidResource.of(Fluids.LAVA);

    private int currentGeneration;

    private final net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler energyHandler = new net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler(ENERGY_CAPACITY, 0, ENERGY_CAPACITY, 0) {
        @Override
        protected void onEnergyChanged(int previousAmount) {
            setChanged();
        }
    };

    private final FluidStacksResourceHandler fluidHandler = new FluidStacksResourceHandler(1, TANK_CAPACITY) {
        @Override
        public boolean isValid(int index, FluidResource resource) {
            return index == 0 && resource.equals(LAVA_RESOURCE);
        }

        @Override
        protected void onContentsChanged(int index, net.neoforged.neoforge.fluids.FluidStack previousContents) {
            setChanged();
        }
    };

    public GeothermalGeneratorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ScalarPowerBlockEntities.GEOTHERMAL_GENERATOR.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, GeothermalGeneratorBlockEntity blockEntity) {
        if (level == null || level.isClientSide()) {
            return;
        }

        boolean changed = false;

        long energy = blockEntity.energyHandler.getAmountAsLong();
        long capacity = blockEntity.energyHandler.getCapacityAsLong();
        int lavaAmount = blockEntity.getLavaAmount();
        boolean canGenerate = lavaAmount >= LAVA_PER_TICK && (capacity - energy) >= ENERGY_PER_TICK;

        int generated = 0;
        if (canGenerate) {
            int remainingLava = lavaAmount - LAVA_PER_TICK;
            blockEntity.fluidHandler.set(0, remainingLava > 0 ? LAVA_RESOURCE : FluidResource.EMPTY, remainingLava);
            generated = ENERGY_PER_TICK;
            blockEntity.energyHandler.set((int) (energy + generated));
            changed = true;
        }

        if (blockEntity.energyHandler.getAmountAsLong() > 0) {
            int moved = NeoEnergyTransferUtil.pushEnergyToTransferBlocks(level, pos, blockEntity.energyHandler, PUSH_PER_SIDE);
            changed |= moved > 0;
        }

        if (blockEntity.currentGeneration != generated) {
            blockEntity.currentGeneration = generated;
            changed = true;
        }

        if (changed) {
            blockEntity.setChanged();
        }

        boolean isWorking = generated > 0;
        if (state.hasProperty(GeothermalGeneratorBlock.LIT) && state.getValue(GeothermalGeneratorBlock.LIT) != isWorking) {
            level.setBlock(pos, state.setValue(GeothermalGeneratorBlock.LIT, isWorking), 3);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        energyHandler.serialize(output);
        fluidHandler.serialize(output);
        output.putInt("CurrentGeneration", currentGeneration);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energyHandler.deserialize(input);
        fluidHandler.deserialize(input);
        currentGeneration = input.getIntOr("CurrentGeneration", 0);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.scalarpower.geothermal_generator");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new GeothermalGeneratorMenu(id, inventory, this, new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> (int) energyHandler.getAmountAsLong();
                    case 1 -> (int) energyHandler.getCapacityAsLong();
                    case 2 -> getLavaAmount();
                    case 3 -> TANK_CAPACITY;
                    case 4 -> currentGeneration;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> energyHandler.set(value);
                    case 2 -> fluidHandler.set(0, value > 0 ? LAVA_RESOURCE : FluidResource.EMPTY, value);
                    case 4 -> currentGeneration = value;
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

    private int getLavaAmount() {
        return fluidHandler.getAmountAsInt(0);
    }

    public EnergyHandler getEnergyHandler(Direction side) {
        return energyHandler;
    }

    public ResourceHandler<FluidResource> getFluidHandler(Direction side) {
        return fluidHandler;
    }
}


