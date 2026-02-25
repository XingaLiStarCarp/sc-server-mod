package sc.server.api.registry;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import sc.server.ModEntry;

public class Registers {

	public static final String MOD_ID = ModEntry.MOD_ID;

	public static final DeferredRegister<Block> BLOCKS_REG = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
	public static final DeferredRegister<Item> ITEMS_REG = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

	public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS_REG = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

	public static final DeferredRegister<EntityType<?>> ENTITIES_REG = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MOD_ID);

	public static final void register(IEventBus bus) {
		BLOCKS_REG.register(bus);
		ITEMS_REG.register(bus);
		CREATIVE_TABS_REG.register(bus);
		ENTITIES_REG.register(bus);
	}

	private static final HashMap<String, RegistryObject<Block>> BLOCKS = new HashMap<>();

	public static final Map<String, RegistryObject<Block>> registeredBlocks() {
		return Collections.unmodifiableMap(BLOCKS);
	}

	/**
	 * 注册新方块
	 * 
	 * @param blockId      方块ID，不带命名空间
	 * @param blockFactory
	 * @return
	 */
	public static final RegistryObject<Block> registerBlock(String blockId, Supplier<Block> blockFactory) {
		RegistryObject<Block> block = BLOCKS_REG.register(blockId, blockFactory);
		BLOCKS.put(blockId, block);
		return block;
	}

	private static final HashMap<String, RegistryObject<Item>> ITEMS = new HashMap<>();

	public static final Map<String, RegistryObject<Item>> registeredItems() {
		return Collections.unmodifiableMap(ITEMS);
	}

	/**
	 * 注册新物品
	 * 
	 * @param itemId      物品ID，不带命名空间
	 * @param itemFactory
	 * @return
	 */
	public static final RegistryObject<Item> registerItem(String itemId, Supplier<Item> itemFactory) {
		RegistryObject<Item> item = ITEMS_REG.register(itemId, itemFactory);
		ITEMS.put(itemId, item);
		return item;
	}

	private static final HashMap<RegistryObject<Block>, RegistryObject<Item>> BLOCK_ITEMS = new HashMap<>();

	public static final Map<RegistryObject<Block>, RegistryObject<Item>> registeredBlockItems() {
		return Collections.unmodifiableMap(BLOCK_ITEMS);
	}

	/**
	 * 获取已经注册的方块的BlockItem
	 * 
	 * @param block
	 * @return
	 */
	public static final RegistryObject<Item> getBlockItem(RegistryObject<Block> block) {
		return BLOCK_ITEMS.get(block);
	}

	/**
	 * 为方块注册BlockItem
	 * 
	 * @param itemName
	 * @param block
	 * @return
	 */
	public static final RegistryObject<Item> registerBlockItem(String itemName, RegistryObject<Block> block) {
		RegistryObject<Item> item = registerItem(itemName, () -> new BlockItem(block.get(), new Item.Properties()));
		BLOCK_ITEMS.put(block, item);
		return item;
	}
}
