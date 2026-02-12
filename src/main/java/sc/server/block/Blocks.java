package sc.server.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.RegistryObject;
import sc.server.api.registry.Registers;

public class Blocks {
	public static final RegistryObject<Block> EXAMPLE_BLOCK = Registers.BLOCKS.register("example_block",
			() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
	public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = Registers.ITEMS.register("example_block",
			() -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties()));

}
