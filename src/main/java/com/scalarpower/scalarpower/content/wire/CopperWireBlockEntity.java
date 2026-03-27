package com.scalarpower.scalarpower.content.wire;

import com.scalarpower.scalarpower.power.PowerNode;
import com.scalarpower.scalarpower.power.PowerUtil;
import com.scalarpower.scalarpower.registry.ScalarPowerEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class CopperWireBlockEntity extends BlockEntity implements PowerNode {
    private static final int ENERGY_CAPACITY = 2000;
    private static final int PUSH_PER_SIDE = 90;

    private int energy;

    public CopperWireBlockEntity(BlockPos pos, BlockState blockState) {
        super(ScalarPowerEntities.COPPER_WIRE.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CopperWireBlockEntity blockEntity) {
        if (level == null || level.isClientSide() || blockEntity.energy <= 0) {
            return;
        }

        int moved = PowerUtil.pushEnergy(level, pos, blockEntity, PUSH_PER_SIDE);
        if (moved > 0) {
            blockEntity.setChanged();
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("Energy", energy);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energy = input.getIntOr("Energy", 0);
    }

    @Override
    public int getEnergyStored() { return energy; }
    @Override
    public int getEnergyCapacity() { return ENERGY_CAPACITY; }
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
        int extracted = Math.min(amount, energy);
        if (!simulate) {
            energy -= extracted;
            setChanged();
        }
        return extracted;
    }
    @Override
    public boolean canConnectPower(Direction side) { return true; }
}


