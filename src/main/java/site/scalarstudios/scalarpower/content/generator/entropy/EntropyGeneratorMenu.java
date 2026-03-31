package site.scalarstudios.scalarpower.content.generator.entropy;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import site.scalarstudios.scalarpower.gui.ScalarPowerMenus;

public class EntropyGeneratorMenu extends AbstractContainerMenu {
    private final EntropyGeneratorBlockEntity blockEntity;
    private final ContainerData data;

    public EntropyGeneratorMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
        this(id, inventory,
                (EntropyGeneratorBlockEntity) inventory.player.level().getBlockEntity(buf.readBlockPos()),
                new SimpleContainerData(5));
    }

    public EntropyGeneratorMenu(int id, Inventory inventory, EntropyGeneratorBlockEntity blockEntity, ContainerData data) {
        super(ScalarPowerMenus.ENTROPY_GENERATOR_MENU.get(), id);
        this.blockEntity = blockEntity;
        this.data = data;

        checkContainerDataCount(data, 5);
        addDataSlots(data);

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
            if (index < 27) {
                if (!moveItemStackTo(stack, 27, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!moveItemStackTo(stack, 0, 27, false)) {
                    return ItemStack.EMPTY;
                }
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
        return blockEntity != null && blockEntity.getLevel() == player.level()
                && player.distanceToSqr(blockEntity.getBlockPos().getCenter()) <= 64.0;
    }

    public int getEnergy() {
        return data.get(0);
    }

    public int getEnergyCapacity() {
        return data.get(1);
    }

    public int getSpuPerTick() {
        return data.get(2);
    }

    public int getLeftTemperature() {
        return data.get(3);
    }

    public int getRightTemperature() {
        return data.get(4);
    }
}

