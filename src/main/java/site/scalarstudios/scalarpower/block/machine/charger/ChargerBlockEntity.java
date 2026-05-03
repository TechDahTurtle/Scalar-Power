package site.scalarstudios.scalarpower.block.machine.charger;

import site.scalarstudios.scalarpower.block.ScalarPowerBlockEntities;
import site.scalarstudios.scalarpower.block.machine.MachineUtils;
import site.scalarstudios.scalarpower.item.custom.powered_tool.PoweredToolUtil;
import site.scalarstudios.scalarpower.power.NeoEnergyTransferUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;

public class ChargerBlockEntity extends BlockEntity implements Container, MenuProvider {
    private static final int ENERGY_CAPACITY = MachineUtils.BASIC_MACHINE_CAPACITY;
    private static final int ENERGY_PER_TICK = 40;
    private static final int PULL_PER_SIDE = MachineUtils.BASIC_MACHINE_SPU_PER_SIDE;

    private ItemStack itemStack = ItemStack.EMPTY;
    private final SimpleEnergyHandler energyHandler = new SimpleEnergyHandler(ENERGY_CAPACITY, ENERGY_CAPACITY, ENERGY_CAPACITY, 0) {
        @Override
        protected void onEnergyChanged(int previousAmount) {
            setChanged();
        }
    };
    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> (int) energyHandler.getAmountAsLong();
                case 1 -> ENERGY_CAPACITY;
                case 2 -> itemStack.isEmpty() ? 0 : itemStack.getDamageValue();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                energyHandler.set(value);
            } else if (index == 2 && !itemStack.isEmpty()) {
                int clampedDamage = Math.max(0, Math.min(itemStack.getMaxDamage(), value));
                itemStack.setDamageValue(clampedDamage);
                PoweredToolUtil.syncPoweredState(itemStack, itemStack.getItem());
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    };

    public ChargerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ScalarPowerBlockEntities.CHARGER.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ChargerBlockEntity blockEntity) {
        if (level == null || level.isClientSide()) {
            return;
        }

        boolean changed = false;
        boolean itemChanged = false;

        // Pull energy from adjacent blocks
        if (blockEntity.energyHandler.getAmountAsLong() < ENERGY_CAPACITY) {
            int pulled = NeoEnergyTransferUtil.pullEnergy(level, pos, blockEntity.energyHandler, PULL_PER_SIDE);
            changed |= pulled > 0;
        }

        // Charge the item if present and has space
        if (!blockEntity.itemStack.isEmpty()
                && blockEntity.energyHandler.getAmountAsLong() >= ENERGY_PER_TICK
                && blockEntity.itemStack.getDamageValue() > 0) {
            blockEntity.energyHandler.set((int) (blockEntity.energyHandler.getAmountAsLong() - ENERGY_PER_TICK));
            ItemStack chargedStack = blockEntity.itemStack.copy();
            chargedStack.setDamageValue(chargedStack.getDamageValue() - 1);
            PoweredToolUtil.syncPoweredState(chargedStack, chargedStack.getItem());
            blockEntity.itemStack = chargedStack;
            changed = true;
            itemChanged = true;
        }

        if (changed) {
            blockEntity.setChanged();
            if (itemChanged) {
                // Force a client update so the menu slot reflects the exact final charge state.
                level.sendBlockUpdated(pos, state, state, 3);
            }
        }
    }

    public boolean isPoweredTool(ItemStack stack) {
        return !stack.isEmpty() && stack.isDamageableItem() && PoweredToolUtil.getPower(stack) < stack.getMaxDamage();
    }

    @Override
    public boolean isEmpty() {
        return itemStack.isEmpty();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.scalarpower.charger");
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
        return new ChargerMenu(windowId, inventory, this, data);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == 0 ? itemStack : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (slot == 0 && !itemStack.isEmpty()) {
            ItemStack result = itemStack.split(amount);
            setChanged();
            return result;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot == 0) {
            ItemStack result = itemStack;
            itemStack = ItemStack.EMPTY;
            setChanged();
            return result;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == 0) {
            if (!stack.isEmpty() && !isPoweredTool(stack)) {
                return;
            }
            itemStack = stack;
            setChanged();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        itemStack = ItemStack.EMPTY;
    }

    public SimpleEnergyHandler getEnergyHandler() {
        return energyHandler;
    }

    public ContainerData getContainerData() {
        return data;
    }
}

