package site.scalarstudios.scalarpower.power;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import site.scalarstudios.scalarpower.block.ScalarPowerBlockEntities;
import site.scalarstudios.scalarpower.block.machine.alloysmelter.AlloySmelterBlockEntity;
import site.scalarstudios.scalarpower.block.machine.battery.BatteryBlockEntity;
import site.scalarstudios.scalarpower.block.machine.extractor.ExtractorBlockEntity;
import site.scalarstudios.scalarpower.block.machine.generator.coal.CoalGeneratorBlockEntity;
import site.scalarstudios.scalarpower.block.machine.generator.barometric.BarometricGeneratorBlockEntity;
import site.scalarstudios.scalarpower.block.machine.generator.culinary.CulinaryGeneratorBlockEntity;
import site.scalarstudios.scalarpower.block.machine.generator.entropy.EntropyGeneratorBlockEntity;
import site.scalarstudios.scalarpower.block.machine.generator.geothermal.GeothermalGeneratorBlockEntity;
import site.scalarstudios.scalarpower.block.machine.generator.watermill.WaterMillGeneratorBlockEntity;
import site.scalarstudios.scalarpower.block.machine.grinder.DoubleGrinderBlockEntity;
import site.scalarstudios.scalarpower.block.machine.grinder.GrinderBlockEntity;
import site.scalarstudios.scalarpower.block.machine.freezer.FreezerBlockEntity;
import site.scalarstudios.scalarpower.block.machine.macerator.DoubleMaceratorBlockEntity;
import site.scalarstudios.scalarpower.block.machine.macerator.MaceratorBlockEntity;
import site.scalarstudios.scalarpower.block.machine.liquifier.LiquifierBlockEntity;
import site.scalarstudios.scalarpower.block.machine.poweredfurnace.DoublePoweredFurnaceBlockEntity;
import site.scalarstudios.scalarpower.block.machine.poweredfurnace.PoweredFurnaceBlockEntity;
import site.scalarstudios.scalarpower.block.machine.sawmill.SawmillBlockEntity;
import site.scalarstudios.scalarpower.block.machine.cable.copper.CopperCableBlockEntity;
import site.scalarstudios.scalarpower.block.machine.cable.copper.InsulatedCopperCableBlockEntity;
import site.scalarstudios.scalarpower.block.machine.cable.fiberglass.FiberGlassCableBlockEntity;
import site.scalarstudios.scalarpower.block.machine.cable.gold.GoldCableBlockEntity;
import site.scalarstudios.scalarpower.block.machine.cable.gold.InsulatedGoldCableBlockEntity;
import site.scalarstudios.scalarpower.block.machine.cable.reinforcedfiberglass.ReinforcedFiberGlassCableBlockEntity;
import site.scalarstudios.scalarpower.block.transport.pipe.copper.CopperPipeBlockEntity;
import site.scalarstudios.scalarpower.block.device.infinitewatersource.InfiniteWaterSourceBlockEntity;

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
                ScalarPowerBlockEntities.FREEZER.get(),
                FreezerBlockEntity::getEnergyHandler);

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
                ScalarPowerBlockEntities.CREATIVE_BATTERY.get(),
                BatteryBlockEntity::getEnergyHandler);

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.COPPER_CABLE.get(),
                CopperCableBlockEntity::getEnergyHandler);

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.INSULATED_COPPER_CABLE.get(),
                InsulatedCopperCableBlockEntity::getEnergyHandler);

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.GOLD_CABLE.get(),
                GoldCableBlockEntity::getEnergyHandler);

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.INSULATED_GOLD_CABLE.get(),
                InsulatedGoldCableBlockEntity::getEnergyHandler);

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.FIBER_GLASS_CABLE.get(),
                FiberGlassCableBlockEntity::getEnergyHandler);

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.REINFORCED_FIBER_GLASS_CABLE.get(),
                ReinforcedFiberGlassCableBlockEntity::getEnergyHandler);

        event.registerBlockEntity(
                Capabilities.Fluid.BLOCK,
                ScalarPowerBlockEntities.COPPER_PIPE.get(),
                CopperPipeBlockEntity::getFluidHandler);

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
                ScalarPowerBlockEntities.FREEZER.get(),
                (blockEntity, side) -> VanillaContainerWrapper.of(blockEntity));

        event.registerBlockEntity(
                Capabilities.Fluid.BLOCK,
                ScalarPowerBlockEntities.FREEZER.get(),
                FreezerBlockEntity::getFluidHandler);

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

        /* Devices */
        event.registerBlockEntity(
                Capabilities.Fluid.BLOCK,
                ScalarPowerBlockEntities.INFINITE_WATER_SOURCE.get(),
                InfiniteWaterSourceBlockEntity::getFluidHandler);
    }
}
