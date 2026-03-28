package site.scalarstudios.scalarpower.power;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import site.scalarstudios.scalarpower.block.ScalarPowerBlockEntities;

public final class ScalarPowerCapabilities {
    private ScalarPowerCapabilities() {
    }

    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.COAL_GENERATOR.get(),
                (blockEntity, side) -> blockEntity.getEnergyHandler(side));

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.CULINARY_GENERATOR.get(),
                (blockEntity, side) -> blockEntity.getEnergyHandler(side));

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.GRINDER.get(),
                (blockEntity, side) -> blockEntity.getEnergyHandler(side));

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.POWERED_FURNACE.get(),
                (blockEntity, side) -> blockEntity.getEnergyHandler(side));

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.ALLOY_SMELTER.get(),
                (blockEntity, side) -> blockEntity.getEnergyHandler(side));

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.BATTERY.get(),
                (blockEntity, side) -> blockEntity.getEnergyHandler(side));

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ScalarPowerBlockEntities.COPPER_WIRE.get(),
                (blockEntity, side) -> blockEntity.getEnergyHandler(side));
    }
}

