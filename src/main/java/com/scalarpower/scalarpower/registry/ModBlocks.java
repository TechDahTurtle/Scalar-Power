package com.scalarpower.scalarpower.registry;

import com.scalarpower.scalarpower.ScalarPower;
import com.scalarpower.scalarpower.content.generator.CoalGeneratorBlock;
import com.scalarpower.scalarpower.content.grinder.GrinderBlock;
import com.scalarpower.scalarpower.content.poweredfurnace.PoweredFurnaceBlock;
import com.scalarpower.scalarpower.content.wire.CopperWireBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ScalarPower.MODID);

    public static final DeferredBlock<Block> COAL_GENERATOR = BLOCKS.register("coal_generator",
            () -> new CoalGeneratorBlock(
                    blockProperties("coal_generator", BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
                            .strength(4.0F).sound(SoundType.METAL).requiresCorrectToolForDrops())));

    public static final DeferredBlock<Block> GRINDER = BLOCKS.register("grinder",
            () -> new GrinderBlock(
                    blockProperties("grinder", BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                            .strength(3.5F).sound(SoundType.STONE).requiresCorrectToolForDrops())));

    public static final DeferredBlock<Block> POWERED_FURNACE = BLOCKS.register("powered_furnace",
            () -> new PoweredFurnaceBlock(
                    blockProperties("powered_furnace", BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                            .strength(3.5F).sound(SoundType.STONE).requiresCorrectToolForDrops())));

    public static final DeferredBlock<Block> COPPER_WIRE = BLOCKS.register("copper_wire",
            () -> new CopperWireBlock(
                    blockProperties("copper_wire", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE)
                            .strength(1.0F).sound(SoundType.COPPER).noOcclusion())));

    private static BlockBehaviour.Properties blockProperties(String name, BlockBehaviour.Properties properties) {
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK,
                Identifier.fromNamespaceAndPath(ScalarPower.MODID, name));
        return properties.setId(blockKey);
    }

    private ModBlocks() {
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}

