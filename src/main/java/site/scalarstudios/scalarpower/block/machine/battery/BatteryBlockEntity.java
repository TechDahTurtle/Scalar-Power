package site.scalarstudios.scalarpower.block.machine.battery;

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
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import site.scalarstudios.scalarpower.block.ScalarPowerBlockEntities;
import site.scalarstudios.scalarpower.block.machine.MachineUtils;
import site.scalarstudios.scalarpower.power.NeoEnergyTransferUtil;

public class BatteryBlockEntity extends BlockEntity implements MenuProvider {
    private static final int ENERGY_CAPACITY = MachineUtils.BASIC_BATTERY_CAPACITY;
    private static final int ENERGY_TRANSFER_PER_SIDE = MachineUtils.BASIC_BATTERY_SPU_PER_SIDE;
    private static final String CONTAINER_TRANSLATION_KEY = "container.scalarpower.battery";

    protected static final int DEFAULT_MENU_DATA_COUNT = 3;
    protected final String containerTranslationKey;
    protected final int energyTransferPerSide;
    protected final SimpleEnergyHandler energyHandler;

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
        this.energyHandler = new SimpleEnergyHandler(
                energyCapacity,
                energyCapacity,
                energyCapacity,
                0) {
            @Override
            protected void onEnergyChanged(int previousAmount) {
                BatteryBlockEntity.this.onEnergyChanged(previousAmount);
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
        saveEnergy(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        loadEnergy(input);
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
                    case 0 -> getMenuEnergy();
                    case 1 -> getMenuEnergyCapacity();
                    case 2 -> hasInfiniteEnergy() ? 1 : 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                if (index == 0) {
                    setMenuEnergy(value);
                }
            }

            @Override
            public int getCount() {
                return DEFAULT_MENU_DATA_COUNT;
            }
        });
    }

    public EnergyHandler getEnergyHandler(Direction side) {
        return energyHandler;
    }

    protected boolean hasInfiniteEnergy() {
        return false;
    }

    protected void onEnergyChanged(int previousAmount) {
        setChanged();
    }

    protected void saveEnergy(ValueOutput output) {
        energyHandler.serialize(output);
    }

    protected void loadEnergy(ValueInput input) {
        energyHandler.deserialize(input);
    }

    protected int getMenuEnergy() {
        return clampToInt(energyHandler.getAmountAsLong());
    }

    protected int getMenuEnergyCapacity() {
        return clampToInt(energyHandler.getCapacityAsLong());
    }

    protected void setMenuEnergy(int value) {
        energyHandler.set(value);
    }

    protected static int clampToInt(long value) {
        return (int) Math.min(Integer.MAX_VALUE, Math.max(0L, value));
    }
}

