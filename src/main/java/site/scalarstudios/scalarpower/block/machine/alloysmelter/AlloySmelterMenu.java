package site.scalarstudios.scalarpower.block.machine.alloysmelter;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import site.scalarstudios.scalarpower.gui.ScalarPowerMenus;

public class AlloySmelterMenu extends AbstractContainerMenu {
    private final AlloySmelterBlockEntity blockEntity;
    private final ContainerData data;

    public AlloySmelterMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
        this(id, inventory,
                (AlloySmelterBlockEntity) inventory.player.level().getBlockEntity(buf.readBlockPos()),
                new SimpleContainerData(4));
    }

    public AlloySmelterMenu(int id, Inventory inventory, AlloySmelterBlockEntity blockEntity, ContainerData data) {
        super(ScalarPowerMenus.ALLOY_SMELTER_MENU.get(), id);
        this.blockEntity = blockEntity;
        this.data = data;
        checkContainerDataCount(data, 4);
        addDataSlots(data);

        addSlot(new Slot(blockEntity, 0, 26, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return blockEntity.canAlloy(stack);
            }
        });
        addSlot(new Slot(blockEntity, 1, 44, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return blockEntity.canAlloy(stack);
            }
        });
        addSlot(new Slot(blockEntity, 2, 62, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return blockEntity.canAlloy(stack);
            }
        });
        addSlot(new Slot(blockEntity, 3, 116, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int slot = 0; slot < 9; slot++) {
            addSlot(new Slot(inventory, slot, 8 + slot * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();

            if (index == 3) {
                if (!moveItemStackTo(stack, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 0 && index <= 2) {
                if (!moveItemStackTo(stack, 4, 40, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (blockEntity.canAlloy(stack)) {
                if (!moveItemStackTo(stack, 0, 3, false)) {
                    if (index < 31) {
                        if (!moveItemStackTo(stack, 31, 40, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!moveItemStackTo(stack, 4, 31, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (index < 31) {
                if (!moveItemStackTo(stack, 31, 40, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, 4, 31, false)) {
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

    public int getProgress() {
        return data.get(0);
    }

    public int getMaxProgress() {
        return data.get(1);
    }

    public int getEnergy() {
        return data.get(2);
    }

    public int getEnergyCapacity() {
        return data.get(3);
    }
}

