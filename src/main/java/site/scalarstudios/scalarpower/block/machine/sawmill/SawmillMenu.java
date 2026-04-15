package site.scalarstudios.scalarpower.block.machine.sawmill;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import site.scalarstudios.scalarpower.gui.ScalarPowerMenus;

public class SawmillMenu extends AbstractContainerMenu {
    private final SawmillBlockEntity blockEntity;
    private final ContainerData data;

    public SawmillMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
        this(id, inventory, (SawmillBlockEntity) inventory.player.level().getBlockEntity(buf.readBlockPos()),
                new SimpleContainerData(4));
    }

    public SawmillMenu(int id, Inventory inventory, SawmillBlockEntity blockEntity, ContainerData data) {
        super(ScalarPowerMenus.SAWMILL_MENU.get(), id);
        this.blockEntity = blockEntity;
        this.data = data;
        checkContainerDataCount(data, 4);
        addDataSlots(data);

        // Input slot
        addSlot(new Slot(blockEntity, 0, 56, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return blockEntity.canSaw(stack);
            }
        });
        // Planks output slot (primary)
        addSlot(new Slot(blockEntity, 1, 116, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) { return false; }
        });
        // Sawdust output slot (secondary, to the right with extra spacing)
        addSlot(new Slot(blockEntity, 2, 142, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) { return false; }
        });

        // Player inventory rows
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));

        // Hotbar
        for (int slot = 0; slot < 9; slot++)
            addSlot(new Slot(inventory, slot, 8 + slot * 18, 142));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (index == 1 || index == 2) {
                // Output slots → player inventory
                if (!moveItemStackTo(stack, 3, 39, true)) return ItemStack.EMPTY;
            } else if (index == 0) {
                // Input slot → player inventory
                if (!moveItemStackTo(stack, 3, 39, false)) return ItemStack.EMPTY;
            } else if (blockEntity.canSaw(stack)) {
                // Player inventory with valid input → input slot
                if (!moveItemStackTo(stack, 0, 1, false)) {
                    if (index < 30) {
                        if (!moveItemStackTo(stack, 30, 39, false)) return ItemStack.EMPTY;
                    } else {
                        if (!moveItemStackTo(stack, 3, 30, false)) return ItemStack.EMPTY;
                    }
                }
            } else if (index < 30) {
                if (!moveItemStackTo(stack, 30, 39, false)) return ItemStack.EMPTY;
            } else {
                if (!moveItemStackTo(stack, 3, 30, false)) return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return result;
    }

    @Override
    public boolean stillValid(Player player) { return blockEntity != null && blockEntity.stillValid(player); }

    public int getProgress() { return data.get(0); }
    public int getMaxProgress() { return data.get(1); }
    public int getEnergy() { return data.get(2); }
    public int getEnergyCapacity() { return data.get(3); }
}

