package site.scalarstudios.scalarpower.block.machine.freezer;

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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import site.scalarstudios.scalarpower.block.ScalarPowerBlockEntities;
import site.scalarstudios.scalarpower.block.machine.MachineUtils;
import site.scalarstudios.scalarpower.power.NeoEnergyTransferUtil;

public class FreezerBlockEntity extends BlockEntity implements Container, MenuProvider {
    private static final int ENERGY_CAPACITY = MachineUtils.LIQUIFIER_CAPACITY;
    private static final int ENERGY_PER_TICK = MachineUtils.LIQUIFIER_SPU_PER_TICK;
    private static final int PULL_PER_SIDE = MachineUtils.LIQUIFIER_SPU_PER_SIDE;
    private static final int TANK_CAPACITY = MachineUtils.LIQUIFIER_TANK_CAPACITY_MB;
    private static final int FLUID_PER_OPERATION = MachineUtils.LIQUIFIER_MB_PER_OPERATION;
    private static final int SPU_PER_OPERATION = 40_000;

    private ItemStack outputStack = ItemStack.EMPTY;
    private int progressSpu;

    private final SimpleEnergyHandler energyHandler = new SimpleEnergyHandler(ENERGY_CAPACITY, ENERGY_CAPACITY, ENERGY_CAPACITY, 0) {
        @Override
        protected void onEnergyChanged(int previousAmount) {
            setChanged();
        }
    };

    private final FluidStacksResourceHandler fluidHandler = new FluidStacksResourceHandler(1, TANK_CAPACITY) {
        @Override
        public boolean isValid(int index, FluidResource resource) {
            return resource.equals(FluidResource.of(Fluids.WATER)) || resource.equals(FluidResource.of(Fluids.LAVA));
        }

        @Override
        protected void onContentsChanged(int index, net.neoforged.neoforge.fluids.FluidStack previousContents) {
            setChanged();
        }
    };

    public FreezerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ScalarPowerBlockEntities.FREEZER.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FreezerBlockEntity blockEntity) {
        if (level == null || level.isClientSide()) {
            return;
        }

        boolean changed = false;
        boolean isWorking = false;

        if (blockEntity.energyHandler.getAmountAsLong() < ENERGY_CAPACITY) {
            int pulled = NeoEnergyTransferUtil.pullEnergy(level, pos, blockEntity.energyHandler, PULL_PER_SIDE);
            changed |= pulled > 0;
        }

        // Read actual fluid type directly from the handler - no guessing
        FluidResource detectedFluid = blockEntity.fluidHandler.getResource(0);
        int fluidAmount = blockEntity.getFluidAmount();

        ItemStack resultItem = blockEntity.getResultForFluid(detectedFluid);

        boolean hasFluid = fluidAmount >= FLUID_PER_OPERATION;
        boolean canOutput = blockEntity.canOutput(resultItem);

        if (hasFluid && canOutput && !resultItem.isEmpty()) {
            int needed = SPU_PER_OPERATION - blockEntity.progressSpu;
            int availableEnergy = (int) blockEntity.energyHandler.getAmountAsLong();
            int spent = Math.min(ENERGY_PER_TICK, Math.min(needed, availableEnergy));

            if (spent > 0) {
                blockEntity.energyHandler.set(availableEnergy - spent);
                blockEntity.progressSpu += spent;
                changed = true;
                isWorking = true;
            }

            if (blockEntity.progressSpu >= SPU_PER_OPERATION) {
                int remainingFluid = blockEntity.getFluidAmount() - FLUID_PER_OPERATION;
                blockEntity.fluidHandler.set(0, remainingFluid > 0 ? detectedFluid : FluidResource.EMPTY, remainingFluid);
                if (blockEntity.outputStack.isEmpty()) {
                    blockEntity.outputStack = resultItem.copy();
                } else {
                    blockEntity.outputStack.grow(resultItem.getCount());
                }
                blockEntity.progressSpu = 0;
                changed = true;
            }
        } else if (blockEntity.progressSpu > 0) {
            blockEntity.progressSpu = 0;
            changed = true;
        }

        if (changed) {
            blockEntity.setChanged();
        }

        if (state.hasProperty(FreezerBlock.LIT) && state.getValue(FreezerBlock.LIT) != isWorking) {
            level.setBlock(pos, state.setValue(FreezerBlock.LIT, isWorking), 3);
        }
    }

    private ItemStack getResultForFluid(FluidResource fluid) {
        if (fluid.equals(FluidResource.of(Fluids.WATER))) {
            return new ItemStack(Items.ICE);
        } else if (fluid.equals(FluidResource.of(Fluids.LAVA))) {
            return new ItemStack(Items.OBSIDIAN);
        }
        return ItemStack.EMPTY;
    }

    private boolean canOutput(ItemStack result) {
        if (result.isEmpty()) {
            return false;
        }
        if (outputStack.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameComponents(outputStack, result)) {
            return false;
        }
        return outputStack.getCount() + result.getCount() <= outputStack.getMaxStackSize();
    }

    private int getFluidTypeCode() {
        FluidResource resource = fluidHandler.getResource(0);
        if (resource.equals(FluidResource.EMPTY)) return 0;
        if (resource.getFluid().isSame(Fluids.WATER)) return 1;
        if (resource.getFluid().isSame(Fluids.LAVA)) return 2;
        return 0;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        energyHandler.serialize(output);
        fluidHandler.serialize(output);
        output.putInt("ProgressSpu", progressSpu);
        output.store("Output", ItemStack.OPTIONAL_CODEC, outputStack);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energyHandler.deserialize(input);
        fluidHandler.deserialize(input);
        progressSpu = input.getIntOr("ProgressSpu", 0);
        outputStack = input.read("Output", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.scalarpower.freezer");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new FreezerMenu(id, inventory, this, new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> progressSpu;
                    case 1 -> SPU_PER_OPERATION;
                    case 2 -> (int) energyHandler.getAmountAsLong();
                    case 3 -> (int) energyHandler.getCapacityAsLong();
                    case 4 -> getFluidAmount();
                    case 5 -> TANK_CAPACITY;
                    case 6 -> getFluidTypeCode();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> progressSpu = value;
                    case 2 -> energyHandler.set(value);
                    default -> {
                    }
                }
            }

            @Override
            public int getCount() {
                return 7;
            }
        });
    }

    public int getFluidAmount() {
        return fluidHandler.getAmountAsInt(0);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return outputStack.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == 0 ? outputStack : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemStack stack = getItem(slot);
        if (!stack.isEmpty()) {
            ItemStack split = stack.split(count);
            outputStack = stack;
            setChanged();
            return split;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = getItem(slot);
        if (slot == 0) {
            outputStack = ItemStack.EMPTY;
        }
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == 0) {
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
        outputStack = ItemStack.EMPTY;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return false;
    }

    @Override
    public boolean canTakeItem(Container target, int slot, ItemStack stack) {
        return slot == 0;
    }

    public EnergyHandler getEnergyHandler(Direction side) {
        return energyHandler;
    }

    public ResourceHandler<FluidResource> getFluidHandler(Direction side) {
        return fluidHandler;
    }
}

