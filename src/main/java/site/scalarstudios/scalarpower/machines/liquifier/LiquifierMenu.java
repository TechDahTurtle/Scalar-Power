package site.scalarstudios.scalarpower.machines.liquifier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import site.scalarstudios.scalarpower.gui.ScalarPowerMenus;

public class LiquifierMenu extends AbstractContainerMenu {
    private final LiquifierBlockEntity blockEntity;
    private final ContainerData data;

    public LiquifierMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
        this(id, inventory,
                (LiquifierBlockEntity) inventory.player.level().getBlockEntity(buf.readBlockPos()),
                new SimpleContainerData(6));
    }

    public LiquifierMenu(int id, Inventory inventory, LiquifierBlockEntity blockEntity, ContainerData data) {
        super(ScalarPowerMenus.LIQUIFIER_MENU.get(), id);
        this.blockEntity = blockEntity;
        this.data = data;

        checkContainerDataCount(data, 6);
        addDataSlots(data);

        addSlot(new Slot(blockEntity, 0, 56, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return blockEntity.canLiquify(stack);
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
            if (index == 0) {
                if (!moveItemStackTo(stack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (blockEntity.canLiquify(stack)) {
                if (!moveItemStackTo(stack, 0, 1, false)) {
                    if (index < 28) {
                        if (!moveItemStackTo(stack, 28, 37, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!moveItemStackTo(stack, 1, 28, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (index < 28) {
                if (!moveItemStackTo(stack, 28, 37, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, 1, 28, false)) {
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

    public int getProgressSpu() {
        return data.get(0);
    }

    public int getRecipeSpuCost() {
        return data.get(1);
    }

    public int getEnergy() {
        return data.get(2);
    }

    public int getEnergyCapacity() {
        return data.get(3);
    }

    public int getFluidAmount() {
        return data.get(4);
    }

    public int getFluidCapacity() {
        return data.get(5);
    }
}


