package site.scalarstudios.scalarpower.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import site.scalarstudios.scalarpower.ScalarPower;
import site.scalarstudios.scalarpower.machines.alloysmelter.AlloySmelterBlock;
import site.scalarstudios.scalarpower.machines.battery.BatteryBlock;
import site.scalarstudios.scalarpower.machines.battery.SteelBatteryBlock;
import site.scalarstudios.scalarpower.machines.generator.coal.CoalGeneratorBlock;
import site.scalarstudios.scalarpower.machines.generator.barometric.BarometricGeneratorBlock;
import site.scalarstudios.scalarpower.machines.generator.culinary.CulinaryGeneratorBlock;
import site.scalarstudios.scalarpower.machines.generator.entropy.EntropyGeneratorBlock;
import site.scalarstudios.scalarpower.machines.grinder.DoubleGrinderBlock;
import site.scalarstudios.scalarpower.machines.grinder.GrinderBlock;
import site.scalarstudios.scalarpower.machines.extractor.ExtractorBlock;
import site.scalarstudios.scalarpower.machines.sawmill.SawmillBlock;
import site.scalarstudios.scalarpower.machines.poweredfurnace.DoublePoweredFurnaceBlock;
import site.scalarstudios.scalarpower.machines.poweredfurnace.PoweredFurnaceBlock;
import site.scalarstudios.scalarpower.machines.wire.copper.CopperWireBlock;
import site.scalarstudios.scalarpower.machines.wire.copper.InsulatedCopperWireBlock;
import site.scalarstudios.scalarpower.machines.wire.glassfiber.GlassFiberWireBlock;
import site.scalarstudios.scalarpower.machines.wire.gold.GoldWireBlock;
import site.scalarstudios.scalarpower.machines.wire.gold.InsulatedGoldWireBlock;
import site.scalarstudios.scalarpower.item.ScalarPowerItems;
import site.scalarstudios.scalarpower.item.custom.TooltipBlockItem;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public class ScalarPowerBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ScalarPower.MODID);

    public static final DeferredBlock<CoalGeneratorBlock> COAL_GENERATOR = registerBlock("coal_generator",
            CoalGeneratorBlock::new,
            properties -> properties.strength(3.5F, 3.5F).requiresCorrectToolForDrops().sound(SoundType.IRON).mapColor(MapColor.STONE));

    public static final DeferredBlock<CulinaryGeneratorBlock> CULINARY_GENERATOR = registerBlock("culinary_generator",
            CulinaryGeneratorBlock::new,
            properties -> properties.strength(3.5F, 3.5F).requiresCorrectToolForDrops().sound(SoundType.IRON).mapColor(MapColor.STONE));

    public static final DeferredBlock<BarometricGeneratorBlock> BAROMETRIC_GENERATOR = registerBlock("barometric_generator",
            BarometricGeneratorBlock::new,
            properties -> properties.strength(4.0F, 4.0F).requiresCorrectToolForDrops().sound(SoundType.IRON).mapColor(MapColor.STONE));

    public static final DeferredBlock<EntropyGeneratorBlock> ENTROPY_GENERATOR = registerBlock("entropy_generator",
            EntropyGeneratorBlock::new,
            properties -> properties.strength(4.0F, 4.0F).requiresCorrectToolForDrops().sound(SoundType.IRON).mapColor(MapColor.STONE));

    public static final DeferredBlock<GrinderBlock> GRINDER = registerBlock("grinder",
            GrinderBlock::new,
            properties -> properties.strength(3.5F, 3.5F).requiresCorrectToolForDrops().sound(SoundType.METAL).mapColor(MapColor.STONE));

    public static final DeferredBlock<DoubleGrinderBlock> DOUBLE_GRINDER = registerBlock("double_grinder",
            DoubleGrinderBlock::new,
            properties -> properties.strength(3.5F, 3.5F).requiresCorrectToolForDrops().sound(SoundType.METAL).mapColor(MapColor.STONE));

    public static final DeferredBlock<SawmillBlock> SAWMILL = registerBlock("sawmill",
            SawmillBlock::new,
            properties -> properties.strength(3.5F, 3.5F).requiresCorrectToolForDrops().sound(SoundType.METAL).mapColor(MapColor.STONE));

    public static final DeferredBlock<ExtractorBlock> EXTRACTOR = registerBlock("extractor",
            ExtractorBlock::new,
            properties -> properties.strength(3.5F, 3.5F).requiresCorrectToolForDrops().sound(SoundType.METAL).mapColor(MapColor.STONE));

    public static final DeferredBlock<PoweredFurnaceBlock> POWERED_FURNACE = registerBlock("powered_furnace",
            PoweredFurnaceBlock::new,
            properties -> properties.strength(3.5F, 3.5F).requiresCorrectToolForDrops().sound(SoundType.METAL).mapColor(MapColor.STONE));

    public static final DeferredBlock<DoublePoweredFurnaceBlock> DOUBLE_POWERED_FURNACE = registerBlock("double_powered_furnace",
            DoublePoweredFurnaceBlock::new,
            properties -> properties.strength(3.5F, 3.5F).requiresCorrectToolForDrops().sound(SoundType.METAL).mapColor(MapColor.STONE));

    public static final DeferredBlock<AlloySmelterBlock> ALLOY_SMELTER = registerBlock("alloy_smelter",
            AlloySmelterBlock::new,
            properties -> properties.strength(3.5F, 3.5F).requiresCorrectToolForDrops().sound(SoundType.METAL).mapColor(MapColor.STONE));

    public static final DeferredBlock<BatteryBlock> BATTERY = registerBlock("battery",
            BatteryBlock::new,
            properties -> properties.strength(3.5F, 3.5F).requiresCorrectToolForDrops().sound(SoundType.METAL).mapColor(MapColor.STONE));

    public static final DeferredBlock<SteelBatteryBlock> STEEL_BATTERY = registerBlock("steel_battery",
            SteelBatteryBlock::new,
            properties -> properties.strength(3.5F, 3.5F).requiresCorrectToolForDrops().sound(SoundType.METAL).mapColor(MapColor.STONE));

    public static final DeferredBlock<CopperWireBlock> COPPER_WIRE = registerTooltipBlockItem("copper_wire",
            CopperWireBlock::new,
            properties -> properties.strength(0.5F, 0.5F).noOcclusion().sound(SoundType.COPPER).mapColor(MapColor.COLOR_ORANGE));

    public static final DeferredBlock<InsulatedCopperWireBlock> INSULATED_COPPER_WIRE = registerTooltipBlockItem("insulated_copper_wire",
            InsulatedCopperWireBlock::new,
            properties -> properties.strength(0.5F, 0.5F).noOcclusion().sound(SoundType.COPPER).mapColor(MapColor.COLOR_ORANGE));

    public static final DeferredBlock<GoldWireBlock> GOLD_WIRE = registerTooltipBlockItem("gold_wire",
            GoldWireBlock::new,
            properties -> properties.strength(0.5F, 0.5F).noOcclusion().sound(SoundType.COPPER).mapColor(MapColor.GOLD));

    public static final DeferredBlock<InsulatedGoldWireBlock> INSULATED_GOLD_WIRE = registerTooltipBlockItem("insulated_gold_wire",
            InsulatedGoldWireBlock::new,
            properties -> properties.strength(0.5F, 0.5F).noOcclusion().sound(SoundType.COPPER).mapColor(MapColor.GOLD));

    public static final DeferredBlock<GlassFiberWireBlock> GLASS_FIBER_WIRE = registerTooltipBlockItem("glass_fiber_wire",
            GlassFiberWireBlock::new,
            properties -> properties.strength(0.5F, 0.5F).noOcclusion().sound(SoundType.GLASS).mapColor(MapColor.COLOR_CYAN));

    // Registry Shortcuts
    private static <T extends Block> DeferredBlock<T> registerTooltipBlockItem(String name, Function<BlockBehaviour.Properties, ? extends T> blockFactory, UnaryOperator<BlockBehaviour.Properties> properties) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, blockFactory, properties);
        String tooltipKey = "tooltip.scalarpower." + name;
        ScalarPowerItems.ITEMS.registerItem(name, itemProperties -> new TooltipBlockItem(toReturn.get(), itemProperties, tooltipKey));
        return toReturn;
    }
    
    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends T> blockFactory, UnaryOperator<BlockBehaviour.Properties> properties) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, blockFactory, properties);
        registerBlockItem(toReturn);
        return toReturn;
    }

    private static void registerBlockItem(DeferredBlock<? extends Block> block) {
        ScalarPowerItems.ITEMS.registerSimpleBlockItem(block);
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
