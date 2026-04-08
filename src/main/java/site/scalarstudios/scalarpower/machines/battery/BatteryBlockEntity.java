package site.scalarstudios.scalarpower.machines.battery;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import site.scalarstudios.scalarpower.block.ScalarPowerBlockEntities;
import site.scalarstudios.scalarpower.machines.MachineUtils;
import site.scalarstudios.scalarpower.power.NeoEnergyTransferUtil;

public class BatteryBlockEntity extends BlockEntity implements MenuProvider {
    private static final int ENERGY_CAPACITY = MachineUtils.BASIC_BATTERY_CAPACITY;
    private static final int ENERGY_TRANSFER_PER_SIDE = MachineUtils.BASIC_BATTERY_SPU_PER_SIDE;
    private static final String CONTAINER_TRANSLATION_KEY = "container.scalarpower.battery";

    protected final String containerTranslationKey;
    protected final int energyTransferPerSide;
    protected final net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler energyHandler;

    public BatteryBlockEntity(BlockPos pos, BlockState blockState) {
        this(
                ScalarPowerBlockEntities.BATTERY.get(),
                pos,
                blockState,
                ENERGY_CAPACITY,
                ENERGY_TRANSFER_PER_SIDE,
                CONTAINER_TRANSLATION_KEY);
    }

    protected BatteryBlockEntity(BlockEntityType<? extends BatteryBlockEntity> type, BlockPos pos, BlockState blockState,
            int energyCapacity, int energyTransferPerSide, String containerTranslationKey) {
        super(type, pos, blockState);
        this.containerTranslationKey = containerTranslationKey;
        this.energyTransferPerSide = energyTransferPerSide;
        this.energyHandler = new net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler(
                energyCapacity,
                energyCapacity,
                energyCapacity,
                0) {
            @Override
            protected void onEnergyChanged(int previousAmount) {
                setChanged();
            }
        };
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BatteryBlockEntity blockEntity) {
        if (level == null || level.isClientSide() || blockEntity.energyHandler.getAmountAsLong() <= 0) {
            return;
        }

        int moved = NeoEnergyTransferUtil.pushEnergy(level, pos, blockEntity.energyHandler, blockEntity.energyTransferPerSide);
        if (moved > 0) {
            blockEntity.setChanged();
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        energyHandler.serialize(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energyHandler.deserialize(input);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(containerTranslationKey);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new BatteryMenu(id, inv, this, new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> (int) energyHandler.getAmountAsLong();
                    case 1 -> (int) energyHandler.getCapacityAsLong();
                    case 2 -> hasInfiniteEnergy() ? 1 : 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                if (index == 0) {
                    energyHandler.set(value);
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        });
    }

    public EnergyHandler getEnergyHandler(Direction side) {
        return energyHandler;
    }

    protected boolean hasInfiniteEnergy() {
        return false;
    }
}

