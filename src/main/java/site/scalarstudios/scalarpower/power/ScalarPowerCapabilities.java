package site.scalarstudios.scalarpower.power;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import site.scalarstudios.scalarpower.block.ScalarPowerBlockEntities;
import site.scalarstudios.scalarpower.machines.alloysmelter.AlloySmelterBlockEntity;
import site.scalarstudios.scalarpower.machines.battery.BatteryBlockEntity;
import site.scalarstudios.scalarpower.machines.extractor.ExtractorBlockEntity;
import site.scalarstudios.scalarpower.machines.generator.coal.CoalGeneratorBlockEntity;
import site.scalarstudios.scalarpower.machines.generator.barometric.BarometricGeneratorBlockEntity;
import site.scalarstudios.scalarpower.machines.generator.culinary.CulinaryGeneratorBlockEntity;
import site.scalarstudios.scalarpower.machines.generator.entropy.EntropyGeneratorBlockEntity;
import site.scalarstudios.scalarpower.machines.generator.geothermal.GeothermalGeneratorBlockEntity;
import site.scalarstudios.scalarpower.machines.generator.watermill.WaterMillGeneratorBlockEntity;
import site.scalarstudios.scalarpower.machines.grinder.DoubleGrinderBlockEntity;
import site.scalarstudios.scalarpower.machines.grinder.GrinderBlockEntity;
import site.scalarstudios.scalarpower.machines.macerator.DoubleMaceratorBlockEntity;
import site.scalarstudios.scalarpower.machines.macerator.MaceratorBlockEntity;
import site.scalarstudios.scalarpower.machines.liquifier.LiquifierBlockEntity;
import site.scalarstudios.scalarpower.machines.poweredfurnace.DoublePoweredFurnaceBlockEntity;
import site.scalarstudios.scalarpower.machines.poweredfurnace.PoweredFurnaceBlockEntity;
import site.scalarstudios.scalarpower.machines.sawmill.SawmillBlockEntity;
import site.scalarstudios.scalarpower.machines.wire.copper.CopperWireBlockEntity;
import site.scalarstudios.scalarpower.machines.wire.copper.InsulatedCopperWireBlockEntity;
import site.scalarstudios.scalarpower.machines.wire.fiberglass.FiberGlassWireBlockEntity;
import site.scalarstudios.scalarpower.machines.wire.gold.GoldWireBlockEntity;
import site.scalarstudios.scalarpower.machines.wire.gold.InsulatedGoldWireBlockEntity;
import site.scalarstudios.scalarpower.machines.wire.reinforcedfiberglass.ReinforcedFiberGlassWireBlockEntity;

public final class ScalarPowerCapabilities {

    public static void register(RegisterCapabilitiesEvent event) {

        // Energy Handlers
        /* Generators */
        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.BAROMETRIC_GENERATOR.get(),
                BarometricGeneratorBlockEntity::getEnergyHandler);

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.COAL_GENERATOR.get(),
                CoalGeneratorBlockEntity::getEnergyHandler);

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.CULINARY_GENERATOR.get(),
                CulinaryGeneratorBlockEntity::getEnergyHandler);

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.ENTROPY_GENERATOR.get(),
                EntropyGeneratorBlockEntity::getEnergyHandler);

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.GEOTHERMAL_GENERATOR.get(),
                GeothermalGeneratorBlockEntity::getEnergyHandler);

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.WATER_MILL_GENERATOR.get(),
                WaterMillGeneratorBlockEntity::getEnergyHandler);

        /* Machines */
        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.ALLOY_SMELTER.get(),
                AlloySmelterBlockEntity::getEnergyHandler);

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.EXTRACTOR.get(),
                ExtractorBlockEntity::getEnergyHandler);

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.GRINDER.get(),
                GrinderBlockEntity::getEnergyHandler);

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.DOUBLE_GRINDER.get(),
                DoubleGrinderBlockEntity::getEnergyHandler);

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.LIQUIFIER.get(),
                LiquifierBlockEntity::getEnergyHandler);

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.MACERATOR.get(),
                MaceratorBlockEntity::getEnergyHandler);

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.DOUBLE_MACERATOR.get(),
                DoubleMaceratorBlockEntity::getEnergyHandler);

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.POWERED_FURNACE.get(),
                PoweredFurnaceBlockEntity::getEnergyHandler);

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.DOUBLE_POWERED_FURNACE.get(),
                DoublePoweredFurnaceBlockEntity::getEnergyHandler);

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.SAWMILL.get(),
                SawmillBlockEntity::getEnergyHandler);

        /* Energy Storage & Transfer */
        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.BATTERY.get(),
                BatteryBlockEntity::getEnergyHandler);

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.STEEL_BATTERY.get(),
                BatteryBlockEntity::getEnergyHandler);

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.ENDER_BATTERY.get(),
                BatteryBlockEntity::getEnergyHandler);

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.CREATIVE_BATTERY.get(),
                BatteryBlockEntity::getEnergyHandler);

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.COPPER_WIRE.get(),
                CopperWireBlockEntity::getEnergyHandler);

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.INSULATED_COPPER_WIRE.get(),
                InsulatedCopperWireBlockEntity::getEnergyHandler);

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.GOLD_WIRE.get(),
                GoldWireBlockEntity::getEnergyHandler);

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.INSULATED_GOLD_WIRE.get(),
                InsulatedGoldWireBlockEntity::getEnergyHandler);

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.FIBER_GLASS_WIRE.get(),
                FiberGlassWireBlockEntity::getEnergyHandler);

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.REINFORCED_FIBER_GLASS_WIRE.get(),
                ReinforcedFiberGlassWireBlockEntity::getEnergyHandler);

        // VanillaContainerWrappers
        /* Generators */
        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                ScalarPowerBlockEntities.COAL_GENERATOR.get(),
                (blockEntity, side) -> VanillaContainerWrapper.of(blockEntity));

        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                ScalarPowerBlockEntities.CULINARY_GENERATOR.get(),
                (blockEntity, side) -> VanillaContainerWrapper.of(blockEntity));

        event.registerBlockEntity(
                Capabilities.Fluid.BLOCK,
                ScalarPowerBlockEntities.GEOTHERMAL_GENERATOR.get(),
                GeothermalGeneratorBlockEntity::getFluidHandler);

        /* Machines */
        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                ScalarPowerBlockEntities.ALLOY_SMELTER.get(),
                (blockEntity, side) -> VanillaContainerWrapper.of(blockEntity));

        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                ScalarPowerBlockEntities.EXTRACTOR.get(),
                (blockEntity, side) -> VanillaContainerWrapper.of(blockEntity));

        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                ScalarPowerBlockEntities.GRINDER.get(),
                (blockEntity, side) -> VanillaContainerWrapper.of(blockEntity));

        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                ScalarPowerBlockEntities.DOUBLE_GRINDER.get(),
                (blockEntity, side) -> VanillaContainerWrapper.of(blockEntity));

        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                ScalarPowerBlockEntities.LIQUIFIER.get(),
                (blockEntity, side) -> VanillaContainerWrapper.of(blockEntity));

        event.registerBlockEntity(
                Capabilities.Fluid.BLOCK,
                ScalarPowerBlockEntities.LIQUIFIER.get(),
                LiquifierBlockEntity::getFluidHandler);

        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                ScalarPowerBlockEntities.MACERATOR.get(),
                (blockEntity, side) -> VanillaContainerWrapper.of(blockEntity));

        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                ScalarPowerBlockEntities.DOUBLE_MACERATOR.get(),
                (blockEntity, side) -> VanillaContainerWrapper.of(blockEntity));

        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                ScalarPowerBlockEntities.POWERED_FURNACE.get(),
                (blockEntity, side) -> VanillaContainerWrapper.of(blockEntity));

        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                ScalarPowerBlockEntities.DOUBLE_POWERED_FURNACE.get(),
                (blockEntity, side) -> VanillaContainerWrapper.of(blockEntity));

        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                ScalarPowerBlockEntities.SAWMILL.get(),
                (blockEntity, side) -> VanillaContainerWrapper.of(blockEntity));
    }
}
