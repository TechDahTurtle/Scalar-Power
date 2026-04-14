package site.scalarstudios.scalarpower.machines.liquifier;

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
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
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
import site.scalarstudios.scalarpower.machines.MachineUtils;
import site.scalarstudios.scalarpower.power.NeoEnergyTransferUtil;
import site.scalarstudios.scalarpower.recipe.LiquifyingRecipe;
import site.scalarstudios.scalarpower.recipe.ScalarPowerRecipes;

import java.util.Optional;

public class LiquifierBlockEntity extends BlockEntity implements Container, MenuProvider {
    private static final int ENERGY_CAPACITY = MachineUtils.LIQUIFIER_CAPACITY;
    private static final int ENERGY_PER_TICK = MachineUtils.LIQUIFIER_SPU_PER_TICK;
    private static final int PULL_PER_SIDE = MachineUtils.LIQUIFIER_SPU_PER_SIDE;
    private static final int TANK_CAPACITY = MachineUtils.LIQUIFIER_TANK_CAPACITY_MB;
    private static final int LAVA_PER_RECIPE = MachineUtils.LIQUIFIER_MB_PER_OPERATION;
    private static final FluidResource LAVA_RESOURCE = FluidResource.of(Fluids.LAVA);

    private ItemStack inputStack = ItemStack.EMPTY;
    private int progressSpu;
    private int currentRecipeCost;

    private final SimpleEnergyHandler energyHandler = new SimpleEnergyHandler(ENERGY_CAPACITY, ENERGY_CAPACITY, ENERGY_CAPACITY, 0) {
        @Override
        protected void onEnergyChanged(int previousAmount) {
            setChanged();
        }
    };

    private final FluidStacksResourceHandler fluidHandler = new FluidStacksResourceHandler(1, TANK_CAPACITY) {
        @Override
        public boolean isValid(int index, FluidResource resource) {
            return false;
        }

        @Override
        protected void onContentsChanged(int index, net.neoforged.neoforge.fluids.FluidStack previousContents) {
            setChanged();
        }
    };

    public LiquifierBlockEntity(BlockPos pos, BlockState blockState) {
        super(ScalarPowerBlockEntities.LIQUIFIER.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, LiquifierBlockEntity blockEntity) {
        if (level == null || level.isClientSide()) {
            return;
        }

        boolean changed = false;
        boolean isWorking = false;

        if (blockEntity.energyHandler.getAmountAsLong() < ENERGY_CAPACITY) {
            int pulled = NeoEnergyTransferUtil.pullEnergy(level, pos, blockEntity.energyHandler, PULL_PER_SIDE);
            changed |= pulled > 0;
        }

        Optional<RecipeHolder<LiquifyingRecipe>> recipeHolder = blockEntity.findRecipe(blockEntity.inputStack);
        int recipeCost = recipeHolder.map(holder -> holder.value().spuCost()).orElse(0);
        if (blockEntity.currentRecipeCost != recipeCost) {
            blockEntity.currentRecipeCost = recipeCost;
            changed = true;
        }

        boolean hasTankSpace = blockEntity.getLavaAmount() + LAVA_PER_RECIPE <= TANK_CAPACITY;

        if (recipeHolder.isPresent() && hasTankSpace && recipeCost > 0) {
            int needed = recipeCost - blockEntity.progressSpu;
            int availableEnergy = (int) blockEntity.energyHandler.getAmountAsLong();
            int spent = Math.min(ENERGY_PER_TICK, Math.min(needed, availableEnergy));

            if (spent > 0) {
                blockEntity.energyHandler.set(availableEnergy - spent);
                blockEntity.progressSpu += spent;
                changed = true;
                isWorking = true;
            }

            if (blockEntity.progressSpu >= recipeCost) {
                blockEntity.inputStack.shrink(1);
                int newAmount = blockEntity.getLavaAmount() + LAVA_PER_RECIPE;
                blockEntity.fluidHandler.set(0, LAVA_RESOURCE, newAmount);
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

        if (state.hasProperty(LiquifierBlock.LIT) && state.getValue(LiquifierBlock.LIT) != isWorking) {
            level.setBlock(pos, state.setValue(LiquifierBlock.LIT, isWorking), 3);
        }
    }

    @SuppressWarnings("unchecked")
    private Optional<RecipeHolder<LiquifyingRecipe>> findRecipe(ItemStack stack) {
        if (stack.isEmpty() || !(level instanceof ServerLevel serverLevel)) {
            return Optional.empty();
        }

        SingleRecipeInput input = new SingleRecipeInput(stack);
        Optional<RecipeHolder<LiquifyingRecipe>> byType = serverLevel.recipeAccess().getRecipeFor(
                ScalarPowerRecipes.LIQUIFYING_RECIPE_TYPE,
                input,
                serverLevel);
        if (byType.isPresent()) {
            return byType;
        }

        RecipeManager recipeManager = serverLevel.recipeAccess();
        return recipeManager.getRecipes().stream()
                .filter(holder -> holder.value().getType() == ScalarPowerRecipes.LIQUIFYING_RECIPE_TYPE)
                .map(holder -> (RecipeHolder<LiquifyingRecipe>) holder)
                .filter(holder -> holder.value().matches(input, serverLevel))
                .findFirst();
    }

    public boolean canLiquify(ItemStack stack) {
        return !stack.isEmpty();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        energyHandler.serialize(output);
        fluidHandler.serialize(output);
        output.putInt("ProgressSpu", progressSpu);
        output.putInt("CurrentRecipeCost", currentRecipeCost);
        output.store("Input", ItemStack.OPTIONAL_CODEC, inputStack);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energyHandler.deserialize(input);
        fluidHandler.deserialize(input);
        progressSpu = input.getIntOr("ProgressSpu", 0);
        currentRecipeCost = input.getIntOr("CurrentRecipeCost", 0);
        inputStack = input.read("Input", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.scalarpower.liquifier");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new LiquifierMenu(id, inventory, this, new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> progressSpu;
                    case 1 -> currentRecipeCost;
                    case 2 -> (int) energyHandler.getAmountAsLong();
                    case 3 -> (int) energyHandler.getCapacityAsLong();
                    case 4 -> getLavaAmount();
                    case 5 -> TANK_CAPACITY;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> progressSpu = value;
                    case 1 -> currentRecipeCost = value;
                    case 2 -> energyHandler.set(value);
                    case 4 -> fluidHandler.set(0, value > 0 ? LAVA_RESOURCE : FluidResource.EMPTY, value);
                    default -> {
                    }
                }
            }

            @Override
            public int getCount() {
                return 6;
            }
        });
    }

    private int getLavaAmount() {
        return fluidHandler.getAmountAsInt(0);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return inputStack.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == 0 ? inputStack : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemStack stack = getItem(slot);
        if (!stack.isEmpty()) {
            ItemStack split = stack.split(count);
            inputStack = stack;
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
        }
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == 0) {
            inputStack = stack;
            progressSpu = 0;
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
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot == 0 && canLiquify(stack);
    }

    @Override
    public boolean canTakeItem(Container target, int slot, ItemStack stack) {
        return false;
    }

    public EnergyHandler getEnergyHandler(Direction side) {
        return energyHandler;
    }

    public ResourceHandler<FluidResource> getFluidHandler(Direction side) {
        return fluidHandler;
    }
}

