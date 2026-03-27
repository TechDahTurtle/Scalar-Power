package com.scalarpower.scalarpower.registry;

import com.scalarpower.scalarpower.ScalarPower;
import com.scalarpower.scalarpower.content.generator.CoalGeneratorBlockEntity;
import com.scalarpower.scalarpower.content.grinder.GrinderBlockEntity;
import com.scalarpower.scalarpower.content.poweredfurnace.PoweredFurnaceBlockEntity;
import com.scalarpower.scalarpower.content.wire.CopperWireBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ScalarPowerEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister
            .create(net.minecraft.core.registries.Registries.BLOCK_ENTITY_TYPE, ScalarPower.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CoalGeneratorBlockEntity>> COAL_GENERATOR = BLOCK_ENTITY_TYPES
            .register("coal_generator",
                    () -> new BlockEntityType<>(CoalGeneratorBlockEntity::new, ScalarPowerBlocks.COAL_GENERATOR.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GrinderBlockEntity>> GRINDER = BLOCK_ENTITY_TYPES
            .register("grinder",
                    () -> new BlockEntityType<>(GrinderBlockEntity::new, ScalarPowerBlocks.GRINDER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PoweredFurnaceBlockEntity>> POWERED_FURNACE = BLOCK_ENTITY_TYPES
            .register("powered_furnace",
                    () -> new BlockEntityType<>(PoweredFurnaceBlockEntity::new, ScalarPowerBlocks.POWERED_FURNACE.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CopperWireBlockEntity>> COPPER_WIRE = BLOCK_ENTITY_TYPES
            .register("copper_wire", () -> new BlockEntityType<>(CopperWireBlockEntity::new, ScalarPowerBlocks.COPPER_WIRE.get()));

    private ScalarPowerEntities() {
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}


