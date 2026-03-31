package site.scalarstudios.scalarpower.block;

import site.scalarstudios.scalarpower.ScalarPower;
import site.scalarstudios.scalarpower.content.alloysmelter.AlloySmelterBlockEntity;
import site.scalarstudios.scalarpower.content.battery.BatteryBlockEntity;
import site.scalarstudios.scalarpower.content.battery.SteelBatteryBlockEntity;
import site.scalarstudios.scalarpower.content.generator.coal.CoalGeneratorBlockEntity;
import site.scalarstudios.scalarpower.content.generator.culinary.CulinaryGeneratorBlockEntity;
import site.scalarstudios.scalarpower.content.generator.entropy.EntropyGeneratorBlockEntity;
import site.scalarstudios.scalarpower.content.grinder.DoubleGrinderBlockEntity;
import site.scalarstudios.scalarpower.content.grinder.GrinderBlockEntity;
import site.scalarstudios.scalarpower.content.extractor.ExtractorBlockEntity;
import site.scalarstudios.scalarpower.content.poweredfurnace.DoublePoweredFurnaceBlockEntity;
import site.scalarstudios.scalarpower.content.poweredfurnace.PoweredFurnaceBlockEntity;
import site.scalarstudios.scalarpower.content.wire.copper.CopperWireBlockEntity;
import site.scalarstudios.scalarpower.content.wire.copper.InsulatedCopperWireBlockEntity;
import site.scalarstudios.scalarpower.content.wire.glassfiber.GlassFiberWireBlockEntity;
import site.scalarstudios.scalarpower.content.wire.gold.GoldWireBlockEntity;
import site.scalarstudios.scalarpower.content.wire.gold.InsulatedGoldWireBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ScalarPowerBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister
            .create(net.minecraft.core.registries.Registries.BLOCK_ENTITY_TYPE, ScalarPower.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CoalGeneratorBlockEntity>> COAL_GENERATOR = BLOCK_ENTITY_TYPES
            .register("coal_generator",
                    () -> new BlockEntityType<>(CoalGeneratorBlockEntity::new, ScalarPowerBlocks.COAL_GENERATOR.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CulinaryGeneratorBlockEntity>> CULINARY_GENERATOR = BLOCK_ENTITY_TYPES
            .register("culinary_generator",
                    () -> new BlockEntityType<>(CulinaryGeneratorBlockEntity::new, ScalarPowerBlocks.CULINARY_GENERATOR.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EntropyGeneratorBlockEntity>> ENTROPY_GENERATOR = BLOCK_ENTITY_TYPES
            .register("entropy_generator",
                    () -> new BlockEntityType<>(EntropyGeneratorBlockEntity::new, ScalarPowerBlocks.ENTROPY_GENERATOR.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GrinderBlockEntity>> GRINDER = BLOCK_ENTITY_TYPES
            .register("grinder",
                    () -> new BlockEntityType<>(GrinderBlockEntity::new, ScalarPowerBlocks.GRINDER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DoubleGrinderBlockEntity>> DOUBLE_GRINDER = BLOCK_ENTITY_TYPES
            .register("double_grinder",
                    () -> new BlockEntityType<>(DoubleGrinderBlockEntity::new, ScalarPowerBlocks.DOUBLE_GRINDER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ExtractorBlockEntity>> EXTRACTOR = BLOCK_ENTITY_TYPES
            .register("extractor",
                    () -> new BlockEntityType<>(ExtractorBlockEntity::new, ScalarPowerBlocks.EXTRACTOR.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PoweredFurnaceBlockEntity>> POWERED_FURNACE = BLOCK_ENTITY_TYPES
            .register("powered_furnace",
                    () -> new BlockEntityType<>(PoweredFurnaceBlockEntity::new, ScalarPowerBlocks.POWERED_FURNACE.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DoublePoweredFurnaceBlockEntity>> DOUBLE_POWERED_FURNACE = BLOCK_ENTITY_TYPES
            .register("double_powered_furnace",
                    () -> new BlockEntityType<>(DoublePoweredFurnaceBlockEntity::new, ScalarPowerBlocks.DOUBLE_POWERED_FURNACE.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AlloySmelterBlockEntity>> ALLOY_SMELTER = BLOCK_ENTITY_TYPES
            .register("alloy_smelter",
                    () -> new BlockEntityType<>(AlloySmelterBlockEntity::new, ScalarPowerBlocks.ALLOY_SMELTER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BatteryBlockEntity>> BATTERY = BLOCK_ENTITY_TYPES
            .register("battery", () -> new BlockEntityType<>(BatteryBlockEntity::new, ScalarPowerBlocks.BATTERY.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SteelBatteryBlockEntity>> STEEL_BATTERY = BLOCK_ENTITY_TYPES
            .register("steel_battery", () -> new BlockEntityType<>(SteelBatteryBlockEntity::new, ScalarPowerBlocks.STEEL_BATTERY.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CopperWireBlockEntity>> COPPER_WIRE = BLOCK_ENTITY_TYPES
            .register("copper_wire", () -> new BlockEntityType<>(CopperWireBlockEntity::new, ScalarPowerBlocks.COPPER_WIRE.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InsulatedCopperWireBlockEntity>> INSULATED_COPPER_WIRE = BLOCK_ENTITY_TYPES
            .register("insulated_copper_wire", () -> new BlockEntityType<>(InsulatedCopperWireBlockEntity::new, ScalarPowerBlocks.INSULATED_COPPER_WIRE.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GoldWireBlockEntity>> GOLD_WIRE = BLOCK_ENTITY_TYPES
            .register("gold_wire", () -> new BlockEntityType<>(GoldWireBlockEntity::new, ScalarPowerBlocks.GOLD_WIRE.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InsulatedGoldWireBlockEntity>> INSULATED_GOLD_WIRE = BLOCK_ENTITY_TYPES
            .register("insulated_gold_wire", () -> new BlockEntityType<>(InsulatedGoldWireBlockEntity::new, ScalarPowerBlocks.INSULATED_GOLD_WIRE.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GlassFiberWireBlockEntity>> GLASS_FIBER_WIRE = BLOCK_ENTITY_TYPES
            .register("glass_fiber_wire", () -> new BlockEntityType<>(GlassFiberWireBlockEntity::new, ScalarPowerBlocks.GLASS_FIBER_WIRE.get()));

    private ScalarPowerBlockEntities() {
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}
