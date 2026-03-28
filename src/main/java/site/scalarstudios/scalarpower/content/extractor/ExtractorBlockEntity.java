package site.scalarstudios.scalarpower.content.extractor;

import site.scalarstudios.scalarpower.power.NeoEnergyTransferUtil;
import site.scalarstudios.scalarpower.block.ScalarPowerBlockEntities;
import site.scalarstudios.scalarpower.recipe.ExtractionRecipe;
import site.scalarstudios.scalarpower.recipe.ScalarPowerRecipes;
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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;

import java.util.Optional;

public class ExtractorBlockEntity extends BlockEntity implements Container, MenuProvider {
    private static final int ENERGY_CAPACITY = 12000;
    private static final int ENERGY_PER_TICK = 20;
    private static final int RECIPE_TIME = 100;
    private static final int PULL_PER_SIDE = 60;

    private ItemStack inputStack = ItemStack.EMPTY;
    private ItemStack outputStack = ItemStack.EMPTY;
    private int progress;
    private final SimpleEnergyHandler energyHandler = new SimpleEnergyHandler(ENERGY_CAPACITY, ENERGY_CAPACITY, ENERGY_CAPACITY, 0) {
        @Override
        protected void onEnergyChanged(int previousAmount) {
            setChanged();
        }
    };

    public ExtractorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ScalarPowerBlockEntities.EXTRACTOR.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ExtractorBlockEntity blockEntity) {
        if (level == null || level.isClientSide()) {
            return;
        }

        boolean changed = false;
        boolean isWorking = false;

        if (blockEntity.energyHandler.getAmountAsLong() < ENERGY_CAPACITY) {
            int pulled = NeoEnergyTransferUtil.pullEnergy(level, pos, blockEntity.energyHandler, PULL_PER_SIDE);
            changed |= pulled > 0;
        }

        Optional<RecipeHolder<ExtractionRecipe>> recipeHolder = blockEntity.findRecipe(blockEntity.inputStack);
        ItemStack result = recipeHolder
                .map(holder -> holder.value().assemble(new SingleRecipeInput(blockEntity.inputStack)))
                .orElse(ItemStack.EMPTY);

        if (!result.isEmpty() && blockEntity.canOutput(blockEntity.outputStack, result) && blockEntity.energyHandler.getAmountAsLong() >= ENERGY_PER_TICK) {
            blockEntity.energyHandler.set((int)(blockEntity.energyHandler.getAmountAsLong() - ENERGY_PER_TICK));
            blockEntity.progress++;
            changed = true;
            isWorking = true;

            if (blockEntity.progress >= RECIPE_TIME) {
                blockEntity.inputStack.shrink(1);
                int produced = result.getCount();
                produced = Math.min(produced, blockEntity.getMaxAddable(blockEntity.outputStack, result));

                if (blockEntity.outputStack.isEmpty()) {
                    blockEntity.outputStack = result.copyWithCount(produced);
                } else {
                    blockEntity.outputStack.grow(produced);
                }
                blockEntity.progress = 0;
            }
        } else if (blockEntity.progress > 0) {
            blockEntity.progress = Math.max(0, blockEntity.progress - 2);
            changed = true;
        }

        if (changed) {
            blockEntity.setChanged();
        }

        if (state.hasProperty(ExtractorBlock.LIT) && state.getValue(ExtractorBlock.LIT) != isWorking) {
            level.setBlock(pos, state.setValue(ExtractorBlock.LIT, isWorking), 3);
        }
    }

    private boolean canOutput(ItemStack current, ItemStack recipe) {
        if (current.isEmpty()) return true;
        if (!ItemStack.isSameItemSameComponents(current, recipe)) return false;
        return current.getCount() + recipe.getCount() <= current.getMaxStackSize();
    }

    private int getMaxAddable(ItemStack current, ItemStack recipe) {
        if (current.isEmpty()) {
            return recipe.getMaxStackSize();
        }
        if (!ItemStack.isSameItemSameComponents(current, recipe)) {
            return 0;
        }
        return current.getMaxStackSize() - current.getCount();
    }

    public ItemStack getExtractionOutput(ItemStack stack) {
        return findRecipe(stack)
                .map(holder -> holder.value().assemble(new SingleRecipeInput(stack)))
                .orElse(ItemStack.EMPTY);
    }

    public boolean canExtract(ItemStack stack) {
        return !stack.isEmpty();
    }

    private Optional<RecipeHolder<ExtractionRecipe>> findRecipe(ItemStack stack) {
        if (stack.isEmpty() || !(level instanceof ServerLevel serverLevel)) {
            return Optional.empty();
        }

        SingleRecipeInput input = new SingleRecipeInput(stack);
        Optional<RecipeHolder<ExtractionRecipe>> byType = serverLevel.recipeAccess().getRecipeFor(
                ScalarPowerRecipes.EXTRACTION_RECIPE_TYPE,
                input,
                serverLevel);
        if (byType.isPresent()) {
            return byType;
        }

        RecipeManager recipeManager = serverLevel.recipeAccess();
        return recipeManager.getRecipes().stream()
                .filter(holder -> holder.value() instanceof ExtractionRecipe recipe && recipe.matches(input, serverLevel))
                .findFirst()
                .map(holder -> (RecipeHolder<ExtractionRecipe>) holder);
    }


    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        energyHandler.serialize(output);
        output.putInt("Progress", progress);
        output.store("Input", ItemStack.OPTIONAL_CODEC, inputStack);
        output.store("Output", ItemStack.OPTIONAL_CODEC, outputStack);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energyHandler.deserialize(input);
        progress = input.getIntOr("Progress", 0);
        inputStack = input.read("Input", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        outputStack = input.read("Output", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    public Component getDisplayName() { return Component.literal("Extractor"); }
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new ExtractorMenu(id, inv, this, new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> progress;
                    case 1 -> RECIPE_TIME;
                    case 2 -> (int)energyHandler.getAmountAsLong();
                    case 3 -> (int)energyHandler.getCapacityAsLong();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> progress = value;
                    case 2 -> energyHandler.set(value);
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
    public int getContainerSize() { return 2; }
    @Override
    public boolean isEmpty() { return inputStack.isEmpty() && outputStack.isEmpty(); }
    @Override
    public ItemStack getItem(int slot) { return slot == 0 ? inputStack : (slot == 1 ? outputStack : ItemStack.EMPTY); }
    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemStack stack = getItem(slot);
        if (!stack.isEmpty()) {
            ItemStack split = stack.split(count);
            if (slot == 0) inputStack = stack;
            else if (slot == 1) outputStack = stack;
            setChanged();
            return split;
        }
        return ItemStack.EMPTY;
    }
    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = getItem(slot);
        if (slot == 0) inputStack = ItemStack.EMPTY;
        else if (slot == 1) outputStack = ItemStack.EMPTY;
        return stack;
    }
    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == 0) {
            inputStack = stack;
            progress = 0;
        } else if (slot == 1) {
            outputStack = stack;
        }
        if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize());
        setChanged();
    }
    @Override
    public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override
    public void clearContent() { inputStack = ItemStack.EMPTY; outputStack = ItemStack.EMPTY; }

    public EnergyHandler getEnergyHandler(Direction side) { return energyHandler; }

    public int getProgress() { return progress; }
    public int getMaxProgress() { return RECIPE_TIME; }
    public int getEnergy() { return (int)energyHandler.getAmountAsLong(); }
}

