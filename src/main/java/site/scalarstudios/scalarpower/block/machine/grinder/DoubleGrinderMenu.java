package site.scalarstudios.scalarpower.block.machine.grinder;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import site.scalarstudios.scalarpower.gui.ScalarPowerMenus;

public class DoubleGrinderMenu extends AbstractContainerMenu {
    private static final int INPUT_SLOT_X = 56;
    private static final int OUTPUT_SLOT_X = 116;
    private static final int TOP_SLOT_Y = 20;
    private static final int BOTTOM_SLOT_Y = 47;
    private static final int PLAYER_INVENTORY_Y = 84;
    private static final int HOTBAR_Y = 142;

    private static final int INPUT_START = 0;
    private static final int INPUT_END = 2;
    private static final int OUTPUT_START = 2;
    private static final int OUTPUT_END = 4;
    private static final int PLAYER_START = 4;
    private static final int PLAYER_MAIN_END = 31;
    private static final int PLAYER_END = 40;

    private final DoubleGrinderBlockEntity blockEntity;
    private final ContainerData data;

    public DoubleGrinderMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
        this(id, inventory,
                (DoubleGrinderBlockEntity) inventory.player.level().getBlockEntity(buf.readBlockPos()),
                new SimpleContainerData(5));
    }

    public DoubleGrinderMenu(int id, Inventory inventory, DoubleGrinderBlockEntity blockEntity, ContainerData data) {
        super(ScalarPowerMenus.DOUBLE_GRINDER_MENU.get(), id);
        this.blockEntity = blockEntity;
        this.data = data;
        checkContainerDataCount(data, 5);
        addDataSlots(data);

        addSlot(new Slot(blockEntity, 0, INPUT_SLOT_X, TOP_SLOT_Y + 1) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return blockEntity.canGrind(stack);
            }
        });
        addSlot(new Slot(blockEntity, 1, INPUT_SLOT_X, BOTTOM_SLOT_Y + 1) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return blockEntity.canGrind(stack);
            }
        });

        addSlot(new Slot(blockEntity, 2, OUTPUT_SLOT_X, TOP_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        addSlot(new Slot(blockEntity, 3, OUTPUT_SLOT_X, BOTTOM_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, PLAYER_INVENTORY_Y + row * 18));
            }
        }

        for (int slot = 0; slot < 9; slot++) {
            addSlot(new Slot(inventory, slot, 8 + slot * 18, HOTBAR_Y));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();

            if (index >= OUTPUT_START && index < OUTPUT_END) {
                if (!moveItemStackTo(stack, PLAYER_START, PLAYER_END, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= INPUT_START && index < INPUT_END) {
                if (!moveItemStackTo(stack, PLAYER_START, PLAYER_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (blockEntity.canGrind(stack)) {
                if (!moveItemStackTo(stack, INPUT_START, INPUT_END, false)) {
                    if (index < PLAYER_MAIN_END) {
                        if (!moveItemStackTo(stack, PLAYER_MAIN_END, PLAYER_END, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!moveItemStackTo(stack, PLAYER_START, PLAYER_MAIN_END, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (index < PLAYER_MAIN_END) {
                if (!moveItemStackTo(stack, PLAYER_MAIN_END, PLAYER_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, PLAYER_START, PLAYER_MAIN_END, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return blockEntity != null && blockEntity.stillValid(player);
    }

    public int getProgressA() {
        return data.get(0);
    }

    public int getProgressB() {
        return data.get(1);
    }

    public int getMaxProgress() {
        return data.get(2);
    }

    public int getEnergy() {
        return data.get(3);
    }

    public int getEnergyCapacity() {
        return data.get(4);
    }
}

