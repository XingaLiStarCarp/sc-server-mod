package sc.server.block;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.RegistryObject;
import sc.server.api.registry.Registers;

public class Blocks {
	public static final void register() {

	}

	public static final RegistryObject<Block> NPC_SPAWNER = Registers.registerBlock("npc_spawner",
			() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)) {

			});
	public static final RegistryObject<Item> NPC_SPAWNER_ITEM = Registers.registerBlockItem("npc_spawner", NPC_SPAWNER);

}
