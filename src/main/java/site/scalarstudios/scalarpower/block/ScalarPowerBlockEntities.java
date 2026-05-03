package site.scalarstudios.scalarpower.block;

import site.scalarstudios.scalarpower.ScalarPower;
import site.scalarstudios.scalarpower.block.machine.alloysmelter.AlloySmelterBlockEntity;
import site.scalarstudios.scalarpower.block.machine.battery.BatteryBlockEntity;
import site.scalarstudios.scalarpower.block.machine.battery.CreativeBatteryBlockEntity;
import site.scalarstudios.scalarpower.block.machine.battery.EnderBatteryBlockEntity;
import site.scalarstudios.scalarpower.block.machine.battery.SteelBatteryBlockEntity;
import site.scalarstudios.scalarpower.block.machine.generator.coal.CoalGeneratorBlockEntity;
import site.scalarstudios.scalarpower.block.machine.generator.barometric.BarometricGeneratorBlockEntity;
import site.scalarstudios.scalarpower.block.machine.generator.culinary.CulinaryGeneratorBlockEntity;
import site.scalarstudios.scalarpower.block.machine.generator.entropy.EntropyGeneratorBlockEntity;
import site.scalarstudios.scalarpower.block.machine.generator.geothermal.GeothermalGeneratorBlockEntity;
import site.scalarstudios.scalarpower.block.machine.generator.watermill.WaterMillGeneratorBlockEntity;
import site.scalarstudios.scalarpower.block.machine.grinder.DoubleGrinderBlockEntity;
import site.scalarstudios.scalarpower.block.machine.grinder.GrinderBlockEntity;
import site.scalarstudios.scalarpower.block.machine.macerator.DoubleMaceratorBlockEntity;
import site.scalarstudios.scalarpower.block.machine.macerator.MaceratorBlockEntity;
import site.scalarstudios.scalarpower.block.machine.freezer.FreezerBlockEntity;
import site.scalarstudios.scalarpower.block.machine.liquifier.LiquifierBlockEntity;
import site.scalarstudios.scalarpower.block.machine.extractor.ExtractorBlockEntity;
import site.scalarstudios.scalarpower.block.machine.sawmill.SawmillBlockEntity;
import site.scalarstudios.scalarpower.block.machine.poweredfurnace.DoublePoweredFurnaceBlockEntity;
import site.scalarstudios.scalarpower.block.machine.poweredfurnace.PoweredFurnaceBlockEntity;
import site.scalarstudios.scalarpower.block.machine.cable.copper.CopperCableBlockEntity;
import site.scalarstudios.scalarpower.block.machine.cable.copper.InsulatedCopperCableBlockEntity;
import site.scalarstudios.scalarpower.block.machine.cable.fiberglass.FiberGlassCableBlockEntity;
import site.scalarstudios.scalarpower.block.machine.cable.gold.GoldCableBlockEntity;
import site.scalarstudios.scalarpower.block.machine.cable.gold.InsulatedGoldCableBlockEntity;
import site.scalarstudios.scalarpower.block.machine.cable.reinforcedfiberglass.ReinforcedFiberGlassCableBlockEntity;
import site.scalarstudios.scalarpower.block.transport.pipe.copper.CopperPipeBlockEntity;
import site.scalarstudios.scalarpower.block.device.redstoneclock.RedstoneClockBlockEntity;
import site.scalarstudios.scalarpower.block.device.infinitewatersource.InfiniteWaterSourceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ScalarPowerBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister
            .create(net.minecraft.core.registries.Registries.BLOCK_ENTITY_TYPE, ScalarPower.MODID);

    /* Generators */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BarometricGeneratorBlockEntity>> BAROMETRIC_GENERATOR = BLOCK_ENTITY_TYPES
            .register("barometric_generator",
                    () -> new BlockEntityType<>(BarometricGeneratorBlockEntity::new, ScalarPowerBlocks.BAROMETRIC_GENERATOR.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CoalGeneratorBlockEntity>> COAL_GENERATOR = BLOCK_ENTITY_TYPES
            .register("coal_generator",
                    () -> new BlockEntityType<>(CoalGeneratorBlockEntity::new, ScalarPowerBlocks.COAL_GENERATOR.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CulinaryGeneratorBlockEntity>> CULINARY_GENERATOR = BLOCK_ENTITY_TYPES
            .register("culinary_generator",
                    () -> new BlockEntityType<>(CulinaryGeneratorBlockEntity::new, ScalarPowerBlocks.CULINARY_GENERATOR.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EntropyGeneratorBlockEntity>> ENTROPY_GENERATOR = BLOCK_ENTITY_TYPES
            .register("entropy_generator",
                    () -> new BlockEntityType<>(EntropyGeneratorBlockEntity::new, ScalarPowerBlocks.ENTROPY_GENERATOR.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GeothermalGeneratorBlockEntity>> GEOTHERMAL_GENERATOR = BLOCK_ENTITY_TYPES
            .register("geothermal_generator",
                    () -> new BlockEntityType<>(GeothermalGeneratorBlockEntity::new, ScalarPowerBlocks.GEOTHERMAL_GENERATOR.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WaterMillGeneratorBlockEntity>> WATER_MILL_GENERATOR = BLOCK_ENTITY_TYPES
            .register("water_mill_generator",
                    () -> new BlockEntityType<>(WaterMillGeneratorBlockEntity::new, ScalarPowerBlocks.WATER_MILL_GENERATOR.get()));

    /* Machines */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AlloySmelterBlockEntity>> ALLOY_SMELTER = BLOCK_ENTITY_TYPES
            .register("alloy_smelter",
                    () -> new BlockEntityType<>(AlloySmelterBlockEntity::new, ScalarPowerBlocks.ALLOY_SMELTER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ExtractorBlockEntity>> EXTRACTOR = BLOCK_ENTITY_TYPES
            .register("extractor",
                    () -> new BlockEntityType<>(ExtractorBlockEntity::new, ScalarPowerBlocks.EXTRACTOR.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FreezerBlockEntity>> FREEZER = BLOCK_ENTITY_TYPES
            .register("freezer",
                    () -> new BlockEntityType<>(FreezerBlockEntity::new, ScalarPowerBlocks.FREEZER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GrinderBlockEntity>> GRINDER = BLOCK_ENTITY_TYPES
            .register("grinder",
                    () -> new BlockEntityType<>(GrinderBlockEntity::new, ScalarPowerBlocks.GRINDER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DoubleGrinderBlockEntity>> DOUBLE_GRINDER = BLOCK_ENTITY_TYPES
            .register("double_grinder",
                    () -> new BlockEntityType<>(DoubleGrinderBlockEntity::new, ScalarPowerBlocks.DOUBLE_GRINDER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LiquifierBlockEntity>> LIQUIFIER = BLOCK_ENTITY_TYPES
            .register("liquifier",
                    () -> new BlockEntityType<>(LiquifierBlockEntity::new, ScalarPowerBlocks.LIQUIFIER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MaceratorBlockEntity>> MACERATOR = BLOCK_ENTITY_TYPES
            .register("macerator",
                    () -> new BlockEntityType<>(MaceratorBlockEntity::new, ScalarPowerBlocks.MACERATOR.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DoubleMaceratorBlockEntity>> DOUBLE_MACERATOR = BLOCK_ENTITY_TYPES
            .register("double_macerator",
                    () -> new BlockEntityType<>(DoubleMaceratorBlockEntity::new, ScalarPowerBlocks.DOUBLE_MACERATOR.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PoweredFurnaceBlockEntity>> POWERED_FURNACE = BLOCK_ENTITY_TYPES
            .register("powered_furnace",
                    () -> new BlockEntityType<>(PoweredFurnaceBlockEntity::new, ScalarPowerBlocks.POWERED_FURNACE.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DoublePoweredFurnaceBlockEntity>> DOUBLE_POWERED_FURNACE = BLOCK_ENTITY_TYPES
            .register("double_powered_furnace",
                    () -> new BlockEntityType<>(DoublePoweredFurnaceBlockEntity::new, ScalarPowerBlocks.DOUBLE_POWERED_FURNACE.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SawmillBlockEntity>> SAWMILL = BLOCK_ENTITY_TYPES
            .register("sawmill",
                    () -> new BlockEntityType<>(SawmillBlockEntity::new, ScalarPowerBlocks.SAWMILL.get()));

    /* Energy Storage & Transfer */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BatteryBlockEntity>> BATTERY = BLOCK_ENTITY_TYPES
            .register("battery", () -> new BlockEntityType<>(BatteryBlockEntity::new, ScalarPowerBlocks.BATTERY.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SteelBatteryBlockEntity>> STEEL_BATTERY = BLOCK_ENTITY_TYPES
            .register("steel_battery", () -> new BlockEntityType<>(SteelBatteryBlockEntity::new, ScalarPowerBlocks.STEEL_BATTERY.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnderBatteryBlockEntity>> ENDER_BATTERY = BLOCK_ENTITY_TYPES
            .register("ender_battery", () -> new BlockEntityType<>(EnderBatteryBlockEntity::new, ScalarPowerBlocks.ENDER_BATTERY.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CreativeBatteryBlockEntity>> CREATIVE_BATTERY = BLOCK_ENTITY_TYPES
            .register("creative_battery", () -> new BlockEntityType<>(CreativeBatteryBlockEntity::new, ScalarPowerBlocks.CREATIVE_BATTERY.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CopperCableBlockEntity>> COPPER_CABLE = BLOCK_ENTITY_TYPES
            .register("copper_cable", () -> new BlockEntityType<>(CopperCableBlockEntity::new, ScalarPowerBlocks.COPPER_CABLE.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InsulatedCopperCableBlockEntity>> INSULATED_COPPER_CABLE = BLOCK_ENTITY_TYPES
            .register("insulated_copper_cable", () -> new BlockEntityType<>(InsulatedCopperCableBlockEntity::new, ScalarPowerBlocks.INSULATED_COPPER_CABLE.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GoldCableBlockEntity>> GOLD_CABLE = BLOCK_ENTITY_TYPES
            .register("gold_cable", () -> new BlockEntityType<>(GoldCableBlockEntity::new, ScalarPowerBlocks.GOLD_CABLE.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InsulatedGoldCableBlockEntity>> INSULATED_GOLD_CABLE = BLOCK_ENTITY_TYPES
            .register("insulated_gold_cable", () -> new BlockEntityType<>(InsulatedGoldCableBlockEntity::new, ScalarPowerBlocks.INSULATED_GOLD_CABLE.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FiberGlassCableBlockEntity>> FIBER_GLASS_CABLE = BLOCK_ENTITY_TYPES
            .register("fiber_glass_cable", () -> new BlockEntityType<>(FiberGlassCableBlockEntity::new, ScalarPowerBlocks.FIBER_GLASS_CABLE.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReinforcedFiberGlassCableBlockEntity>> REINFORCED_FIBER_GLASS_CABLE = BLOCK_ENTITY_TYPES
            .register("reinforced_fiber_glass_cable", () -> new BlockEntityType<>(ReinforcedFiberGlassCableBlockEntity::new, ScalarPowerBlocks.REINFORCED_FIBER_GLASS_CABLE.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CopperPipeBlockEntity>> COPPER_PIPE = BLOCK_ENTITY_TYPES
            .register("copper_pipe", () -> new BlockEntityType<>(CopperPipeBlockEntity::new, ScalarPowerBlocks.COPPER_PIPE.get()));

    /* Devices */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InfiniteWaterSourceBlockEntity>> INFINITE_WATER_SOURCE = BLOCK_ENTITY_TYPES
            .register("infinite_water_source", () -> new BlockEntityType<>(InfiniteWaterSourceBlockEntity::new, ScalarPowerBlocks.INFINITE_WATER_SOURCE.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RedstoneClockBlockEntity>> REDSTONE_CLOCK = BLOCK_ENTITY_TYPES
            .register("redstone_clock", () -> new BlockEntityType<>(RedstoneClockBlockEntity::new, ScalarPowerBlocks.REDSTONE_CLOCK.get()));

    private ScalarPowerBlockEntities() {
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}
