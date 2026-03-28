package site.scalarstudios.scalarpower.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import site.scalarstudios.scalarpower.ScalarPower;
import site.scalarstudios.scalarpower.content.alloysmelter.AlloySmelterBlock;
import site.scalarstudios.scalarpower.content.battery.BatteryBlock;
import site.scalarstudios.scalarpower.content.generator.coal.CoalGeneratorBlock;
import site.scalarstudios.scalarpower.content.generator.culinary.CulinaryGeneratorBlock;
import site.scalarstudios.scalarpower.content.grinder.GrinderBlock;
import site.scalarstudios.scalarpower.content.poweredfurnace.PoweredFurnaceBlock;
import site.scalarstudios.scalarpower.content.wire.copper.CopperWireBlock;
import site.scalarstudios.scalarpower.item.ScalarPowerItems;

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

    public static final DeferredBlock<GrinderBlock> GRINDER = registerBlock("grinder",
            GrinderBlock::new,
            properties -> properties.strength(3.5F, 3.5F).requiresCorrectToolForDrops().sound(SoundType.METAL).mapColor(MapColor.STONE));

    public static final DeferredBlock<PoweredFurnaceBlock> POWERED_FURNACE = registerBlock("powered_furnace",
            PoweredFurnaceBlock::new,
            properties -> properties.strength(3.5F, 3.5F).requiresCorrectToolForDrops().sound(SoundType.METAL).mapColor(MapColor.STONE));

    public static final DeferredBlock<AlloySmelterBlock> ALLOY_SMELTER = registerBlock("alloy_smelter",
            AlloySmelterBlock::new,
            properties -> properties.strength(3.5F, 3.5F).requiresCorrectToolForDrops().sound(SoundType.METAL).mapColor(MapColor.STONE));

    public static final DeferredBlock<BatteryBlock> BATTERY = registerBlock("battery",
            BatteryBlock::new,
            properties -> properties.strength(3.5F, 3.5F).requiresCorrectToolForDrops().sound(SoundType.METAL).mapColor(MapColor.STONE));

    public static final DeferredBlock<CopperWireBlock> COPPER_WIRE = registerBlock("copper_wire",
            CopperWireBlock::new,
            properties -> properties.strength(0.5F, 0.5F).noOcclusion().sound(SoundType.COPPER).mapColor(MapColor.COLOR_ORANGE));

    // Registry Shortcuts
    private static DeferredBlock<Block> registerBlock(String name, UnaryOperator<BlockBehaviour.Properties> properties) {
        return registerBlock(name, Block::new, properties);
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends T> blockFactory, BlockBehaviour.Properties properties) {
        return registerBlock(name, blockFactory, ignored -> properties);
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
