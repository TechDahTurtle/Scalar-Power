package com.scalarpower.scalarpower.content.poweredfurnace;

import com.scalarpower.scalarpower.power.PowerNode;
import com.scalarpower.scalarpower.power.PowerUtil;
import com.scalarpower.scalarpower.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Optional;

public class PoweredFurnaceBlockEntity extends BlockEntity implements Container, PowerNode, MenuProvider {
    private static final int ENERGY_CAPACITY = 20000;
    private static final int ENERGY_PER_TICK = 25;
    private static final int DEFAULT_RECIPE_TIME = 200;
    private static final int PULL_PER_SIDE = 80;

    private ItemStack inputStack = ItemStack.EMPTY;
    private ItemStack outputStack = ItemStack.EMPTY;
    private int energy;
    private int progress;
    private int recipeTime = DEFAULT_RECIPE_TIME;

    public PoweredFurnaceBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.POWERED_FURNACE.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PoweredFurnaceBlockEntity blockEntity) {
        if (level == null || level.isClientSide()) {
            return;
        }

        boolean changed = false;
        boolean isWorking = false;

        if (blockEntity.energy < ENERGY_CAPACITY) {
            int pulled = PowerUtil.pullEnergy(level, pos, blockEntity, PULL_PER_SIDE);
            changed |= pulled > 0;
        }

        Optional<RecipeHolder<SmeltingRecipe>> recipe = blockEntity.findRecipe(level, blockEntity.inputStack);
        ItemStack result = recipe
                .map(holder -> holder.value().assemble(new SingleRecipeInput(blockEntity.inputStack), level.registryAccess()))
                .orElse(ItemStack.EMPTY);

        if (!result.isEmpty() && blockEntity.canOutput(blockEntity.outputStack, result)) {
            int newRecipeTime = recipe.map(holder -> holder.value().cookingTime()).orElse(DEFAULT_RECIPE_TIME);
            if (newRecipeTime <= 0) {
                newRecipeTime = DEFAULT_RECIPE_TIME;
            }
            if (blockEntity.recipeTime != newRecipeTime) {
                blockEntity.recipeTime = newRecipeTime;
                if (blockEntity.progress > blockEntity.recipeTime) {
                    blockEntity.progress = blockEntity.recipeTime;
                }
                changed = true;
            }

            if (blockEntity.energy >= ENERGY_PER_TICK) {
                blockEntity.energy -= ENERGY_PER_TICK;
                blockEntity.progress++;
                changed = true;
                isWorking = true;

                if (blockEntity.progress >= blockEntity.recipeTime) {
                    blockEntity.inputStack.shrink(1);
                    if (blockEntity.outputStack.isEmpty()) {
                        blockEntity.outputStack = result.copy();
                    } else {
                        blockEntity.outputStack.grow(result.getCount());
                    }
                    blockEntity.progress = 0;
                    changed = true;
                }
            }
        } else {
            if (blockEntity.progress > 0) {
                blockEntity.progress = Math.max(0, blockEntity.progress - 2);
                changed = true;
            }
            if (blockEntity.recipeTime != DEFAULT_RECIPE_TIME) {
                blockEntity.recipeTime = DEFAULT_RECIPE_TIME;
                changed = true;
            }
        }

        if (changed) {
            blockEntity.setChanged();
        }

        if (state.hasProperty(PoweredFurnaceBlock.LIT) && state.getValue(PoweredFurnaceBlock.LIT) != isWorking) {
            level.setBlock(pos, state.setValue(PoweredFurnaceBlock.LIT, isWorking), 3);
        }
    }

    private Optional<RecipeHolder<SmeltingRecipe>> findRecipe(Level level, ItemStack input) {
        if (input.isEmpty() || !(level instanceof ServerLevel serverLevel)) {
            return Optional.empty();
        }
        return serverLevel.recipeAccess().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(input), serverLevel);
    }

    private boolean canOutput(ItemStack current, ItemStack recipe) {
        if (current.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameComponents(current, recipe)) {
            return false;
        }
        return current.getCount() + recipe.getCount() <= current.getMaxStackSize();
    }

    public boolean canSmelt(ItemStack stack) {
        return level != null && findRecipe(level, stack).isPresent();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("Energy", energy);
        output.putInt("Progress", progress);
        output.putInt("RecipeTime", recipeTime);
        output.store("Input", ItemStack.OPTIONAL_CODEC, inputStack);
        output.store("Output", ItemStack.OPTIONAL_CODEC, outputStack);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energy = input.getIntOr("Energy", 0);
        progress = input.getIntOr("Progress", 0);
        recipeTime = input.getIntOr("RecipeTime", DEFAULT_RECIPE_TIME);
        inputStack = input.read("Input", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        outputStack = input.read("Output", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.scalarpower.powered_furnace");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new PoweredFurnaceMenu(id, inv, this, new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> progress;
                    case 1 -> recipeTime;
                    case 2 -> energy;
                    case 3 -> ENERGY_CAPACITY;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> progress = value;
                    case 1 -> recipeTime = value;
                    case 2 -> energy = value;
                    default -> {
                    }
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        });
    }

    @Override
    public int getContainerSize() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return inputStack.isEmpty() && outputStack.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == 0 ? inputStack : (slot == 1 ? outputStack : ItemStack.EMPTY);
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemStack stack = getItem(slot);
        if (!stack.isEmpty()) {
            ItemStack split = stack.split(count);
            if (slot == 0) {
                inputStack = stack;
            } else if (slot == 1) {
                outputStack = stack;
            }
            setChanged();
            return split;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = getItem(slot);
        if (slot == 0) {
            inputStack = ItemStack.EMPTY;
        } else if (slot == 1) {
            outputStack = ItemStack.EMPTY;
        }
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == 0) {
            inputStack = stack;
            progress = 0;
            recipeTime = DEFAULT_RECIPE_TIME;
        } else if (slot == 1) {
            outputStack = stack;
        }
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        inputStack = ItemStack.EMPTY;
        outputStack = ItemStack.EMPTY;
    }

    @Override
    public int getEnergyStored() {
        return energy;
    }

    @Override
    public int getEnergyCapacity() {
        return ENERGY_CAPACITY;
    }

    @Override
    public int receiveEnergy(int amount, boolean simulate) {
        int accepted = Math.min(amount, ENERGY_CAPACITY - energy);
        if (!simulate) {
            energy += accepted;
            setChanged();
        }
        return accepted;
    }

    @Override
    public int extractEnergy(int amount, boolean simulate) {
        return 0;
    }

    @Override
    public boolean canConnectPower(Direction side) {
        return true;
    }
}



