package sc.server.block;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;
import sc.server.api.registry.Registers;

public class CreativeTabs {
	public static final RegistryObject<Item> BLOCKS_TAB_ICON = null;

	public static final RegistryObject<CreativeModeTab> BLOCKS_TAB = Registers.CREATIVE_TABS.register("sc_blocks",
			() -> CreativeModeTab.builder().withTabsBefore(CreativeModeTabs.COMBAT)
					.icon(() -> BLOCKS_TAB_ICON.get().getDefaultInstance()).displayItems((parameters, output) -> {
						output.accept(BLOCKS_TAB_ICON.get());
					}).build());
}
