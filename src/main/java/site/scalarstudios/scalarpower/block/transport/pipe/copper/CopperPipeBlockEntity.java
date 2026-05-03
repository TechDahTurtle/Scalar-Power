package site.scalarstudios.scalarpower.block.transport.pipe.copper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import site.scalarstudios.scalarpower.block.ScalarPowerBlockEntities;
import site.scalarstudios.scalarpower.block.machine.MachineUtils;
import site.scalarstudios.scalarpower.block.transport.BaseTransportBlockEntity;
import site.scalarstudios.scalarpower.power.NeoFluidTransferUtil;

public class CopperPipeBlockEntity extends BaseTransportBlockEntity {
    private static final int FLUID_CAPACITY_MB = MachineUtils.PIPE_BASE_CAPACITY_MB;
    private static final int TRANSFER_PER_SIDE_MB = MachineUtils.PIPE_BASE_THROUGHPUT_MB;

    private final FluidStacksResourceHandler fluidHandler = new FluidStacksResourceHandler(1, FLUID_CAPACITY_MB) {
        @Override
        public boolean isValid(int index, FluidResource resource) {
            return index == 0 && canAcceptFluid(resource);
        }

        @Override
        protected void onContentsChanged(int index, net.neoforged.neoforge.fluids.FluidStack previousContents) {
            setChanged();
        }
    };

    public CopperPipeBlockEntity(BlockPos pos, BlockState blockState) {
        super(ScalarPowerBlockEntities.COPPER_PIPE.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CopperPipeBlockEntity blockEntity) {
        if (level == null || level.isClientSide()) {
            return;
        }

        int pulled = NeoFluidTransferUtil.pullFluid(level, pos, blockEntity, TRANSFER_PER_SIDE_MB);
        int pushed = NeoFluidTransferUtil.pushFluid(level, pos, blockEntity, TRANSFER_PER_SIDE_MB);
        if (pulled > 0 || pushed > 0) {
            blockEntity.setChanged();
        }
    }

    private boolean canAcceptFluid(FluidResource resource) {
        if (resource.equals(FluidResource.EMPTY)) {
            return false;
        }

        FluidResource stored = fluidHandler.getResource(0);
        if (!stored.equals(FluidResource.EMPTY)) {
            return stored.equals(resource);
        }

        if (level == null) {
            return true;
        }

        FluidResource networkFluid = NeoFluidTransferUtil.findConnectedPipeFluid(level, worldPosition);
        return networkFluid.equals(FluidResource.EMPTY) || networkFluid.equals(resource);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        fluidHandler.serialize(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        fluidHandler.deserialize(input);
    }

    public int getFluidAmount() {
        return fluidHandler.getAmountAsInt(0);
    }

    public FluidResource getFluidResource() {
        return fluidHandler.getResource(0);
    }

    public ResourceHandler<FluidResource> getFluidHandler(Direction side) {
        return exposesSide(side) ? fluidHandler : null;
    }

    @Override
    public boolean canPhysicallyConnect(Level level, BlockPos neighborPos, Direction incomingSide) {
        return level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.Fluid.BLOCK, neighborPos, incomingSide) != null;
    }
}

