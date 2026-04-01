package site.scalarstudios.scalarpower.machines.generator.culinary;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import site.scalarstudios.scalarpower.block.ScalarPowerBlockEntities;
import site.scalarstudios.scalarpower.machines.MachineUtils;
import site.scalarstudios.scalarpower.power.NeoEnergyTransferUtil;

public class CulinaryGeneratorBlockEntity extends BlockEntity implements Container, MenuProvider {
    private static final int ENERGY_CAPACITY = MachineUtils.GENERAL_GENERATOR_CAPACITY;
    private static final int PUSH_PER_SIDE = MachineUtils.GENERAL_GENERATOR_SPU_PER_SIDE;

    private static final int CAKE_SLICES = 8;
    private static final int CAKE_SLICE_NUTRITION = 2;
    private static final float CAKE_SLICE_SATURATION_MODIFIER = 0.1F;
    
    private static final float SATURATION_MODIFIER = 10F;
    private static final float HUNGER_MODIFIER = 10F;

    private ItemStack fuelStack = ItemStack.EMPTY;
    private int burnTime;
    private int burnTimeTotal;
    private int burnEnergyPerTick;

    private final net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler energyHandler =
            new net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler(
                    ENERGY_CAPACITY,
                    ENERGY_CAPACITY,
                    ENERGY_CAPACITY,
                    0) {
                @Override
                protected void onEnergyChanged(int previousAmount) {
                    setChanged();
                }
            };

    public CulinaryGeneratorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ScalarPowerBlockEntities.CULINARY_GENERATOR.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CulinaryGeneratorBlockEntity blockEntity) {
        if (level == null || level.isClientSide()) {
            return;
        }

        boolean changed = false;

        long energy = blockEntity.energyHandler.getAmountAsLong();
        long capacity = blockEntity.energyHandler.getCapacityAsLong();
        boolean hasEnergyRoom = energy < capacity;

        if (blockEntity.burnTime > 0 && hasEnergyRoom) {
            blockEntity.burnTime--;
            int generated = Math.min(blockEntity.burnEnergyPerTick, (int) (capacity - energy));
            if (generated > 0) {
                blockEntity.energyHandler.set((int) (energy + generated));
                changed = true;
            }
        }

        if (blockEntity.burnTime <= 0 && hasEnergyRoom) {
            FuelValues fuelValues = getFuelValues(blockEntity.fuelStack);
            if (!blockEntity.fuelStack.isEmpty() && fuelValues != null) {
                blockEntity.fuelStack.shrink(1);
                blockEntity.burnTime = fuelValues.burnTicks();
                blockEntity.burnTimeTotal = fuelValues.burnTicks();
                blockEntity.burnEnergyPerTick = fuelValues.energyPerTick();
                changed = true;
            }
        }

        if (blockEntity.energyHandler.getAmountAsLong() > 0) {
            int moved = NeoEnergyTransferUtil.pushEnergyToTransferBlocks(level, pos, blockEntity.energyHandler, PUSH_PER_SIDE);
            changed |= moved > 0;
        }

        if (changed) {
            blockEntity.setChanged();
        }

        boolean isWorking = blockEntity.burnTime > 0 && hasEnergyRoom;
        if (state.hasProperty(CulinaryGeneratorBlock.LIT) && state.getValue(CulinaryGeneratorBlock.LIT) != isWorking) {
            level.setBlock(pos, state.setValue(CulinaryGeneratorBlock.LIT, isWorking), 3);
        }
    }

    private static FuelValues getFuelValues(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }

        Item item = stack.getItem();
        int nutrition;
        float saturationRestored;

        if (item == Items.CAKE) {
            nutrition = CAKE_SLICE_NUTRITION * CAKE_SLICES;
            saturationRestored = (CAKE_SLICE_NUTRITION * CAKE_SLICE_SATURATION_MODIFIER * 2.0F) * CAKE_SLICES;
        } else {
            FoodProperties food = stack.get(DataComponents.FOOD);
            if (food == null) {
                return null;
            }
            nutrition = food.nutrition();
            saturationRestored = food.nutrition() * food.saturation() * 2.0F;
        }

        if (nutrition <= 0 || saturationRestored <= 0.0F) {
            return null;
        }

        int burnTicks = Math.max(1, Math.round(saturationRestored * SATURATION_MODIFIER));
        int energyPerTick = Math.max(1, Math.round(nutrition * HUNGER_MODIFIER));
        return new FuelValues(burnTicks, energyPerTick);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        energyHandler.serialize(output);
        output.putInt("BurnTime", burnTime);
        output.putInt("BurnTimeTotal", burnTimeTotal);
        output.putInt("BurnEnergyPerTick", burnEnergyPerTick);
        output.store("Fuel", ItemStack.OPTIONAL_CODEC, fuelStack);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energyHandler.deserialize(input);
        burnTime = input.getIntOr("BurnTime", 0);
        burnTimeTotal = input.getIntOr("BurnTimeTotal", 0);
        burnEnergyPerTick = input.getIntOr("BurnEnergyPerTick", 0);
        fuelStack = input.read("Fuel", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.scalarpower.culinary_generator");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new CulinaryGeneratorMenu(id, inv, this, new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> burnTime;
                    case 1 -> burnTimeTotal;
                    case 2 -> (int) energyHandler.getAmountAsLong();
                    case 3 -> (int) energyHandler.getCapacityAsLong();
                    case 4 -> burnEnergyPerTick;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> burnTime = value;
                    case 1 -> burnTimeTotal = value;
                    case 2 -> energyHandler.set(value);
                    case 4 -> burnEnergyPerTick = value;
                    default -> {
                    }
                }
            }

            @Override
            public int getCount() {
                return 5;
            }
        });
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return fuelStack.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == 0 ? fuelStack : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        if (slot == 0) {
            ItemStack split = fuelStack.split(count);
            if (!split.isEmpty()) {
                setChanged();
            }
            return split;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot == 0) {
            ItemStack stack = fuelStack;
            fuelStack = ItemStack.EMPTY;
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == 0) {
            fuelStack = stack;
            if (stack.getCount() > getMaxStackSize()) {
                stack.setCount(getMaxStackSize());
            }
            setChanged();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        fuelStack = ItemStack.EMPTY;
    }

    public EnergyHandler getEnergyHandler(Direction side) {
        return energyHandler;
    }

    public boolean isFuel(ItemStack stack) {
        return getFuelValues(stack) != null;
    }

    private record FuelValues(int burnTicks, int energyPerTick) {
    }
}

