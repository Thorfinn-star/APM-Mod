package com.apmsmp.apm;

import java.util.function.Function;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class ApmBlocks {
    public static final Block APM_ORE = register("apm_ore", Block::new, BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_DIAMOND_ORE).strength(4.5F, 6.0F).sound(SoundType.DEEPSLATE));
    public static final Block APM_BLOCK = register("apm_block", Block::new, BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_BLOCK).strength(5.0F, 6.0F).sound(SoundType.METAL));

    private static Block register(String name, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties props) {
        Identifier id = ApmMod.id(name);
        BlockItemId ids = BlockItemId.create(id, id);
        ResourceKey<Block> blockKey = ids.block();
        Block block = factory.apply(props.setId(blockKey));
        Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
        BlockItem item = new BlockItem(block, new Item.Properties().useBlockDescriptionPrefix().setId(ids.item()));
        Registry.register(BuiltInRegistries.ITEM, ids.item(), item);
        return block;
    }
    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS).register(out -> out.accept(APM_ORE.asItem()));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS).register(out -> out.accept(APM_BLOCK.asItem()));
    }
    private ApmBlocks() {}
}
