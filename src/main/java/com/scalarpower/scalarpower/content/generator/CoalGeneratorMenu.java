package com.scalarpower.scalarpower.content.generator;

import com.scalarpower.scalarpower.registry.ScalarPowerMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CoalGeneratorMenu extends AbstractContainerMenu {
    private final CoalGeneratorBlockEntity blockEntity;
    private final ContainerData data;

    // Client-side: called via IMenuTypeExtension with a FriendlyByteBuf
    public CoalGeneratorMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
        this(id, inventory,
                (CoalGeneratorBlockEntity) inventory.player.level().getBlockEntity(buf.readBlockPos()),
                new SimpleContainerData(4));
    }

    // Server-side: called from CoalGeneratorBlockEntity.createMenu
    public CoalGeneratorMenu(int id, Inventory inventory, CoalGeneratorBlockEntity blockEntity, ContainerData data) {
        super(ScalarPowerMenus.COAL_GENERATOR_MENU.get(), id);
        this.blockEntity = blockEntity;
        this.data = data;
        checkContainerDataCount(data, 4);
        addDataSlots(data);

        addSlot(new Slot(blockEntity, 0, 82, 39) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return blockEntity.isFuel(stack);
            }
        });

        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));

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
            if (index == 0) {
                if (!moveItemStackTo(stack, 1, 37, true)) return ItemStack.EMPTY;
            } else if (blockEntity.isFuel(stack)) {
                if (!moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY;
            } else if (index < 28) {
                if (!moveItemStackTo(stack, 28, 37, false)) return ItemStack.EMPTY;
            } else {
                if (!moveItemStackTo(stack, 1, 28, false)) return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return result;
    }

    @Override
    public boolean stillValid(Player player) { return blockEntity != null && blockEntity.stillValid(player); }

    public int getBurnTime()     { return data.get(0); }
    public int getBurnTimeTotal(){ return data.get(1); }
    public int getEnergy()       { return data.get(2); }
    public int getEnergyCapacity(){ return data.get(3); }
}
