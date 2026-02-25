package sc.server.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import sc.server.ModEntry;
import sc.server.block.Blocks;

@EventBusSubscriber(modid = ModEntry.MOD_ID, bus = Bus.MOD)
public class DataGenEntry {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput output = generator.getPackOutput();
		ExistingFileHelper helper = event.getExistingFileHelper();
		// 方块
		generator.addProvider(event.includeClient(), new ModBlockStateProvider(output, helper));
		// 物品模型
		generator.addProvider(event.includeClient(), new ModItemModelProvider(output, helper));
		// 语言文件
		ModLanguageProvider.setTranslations(output, "en_us", (provider) -> {
			provider.add(Blocks.NPC_SPAWNER, "NPC Spawner");
		});
		ModLanguageProvider.setTranslations(output, "zh_cn", (provider) -> {
			provider.add(Blocks.NPC_SPAWNER, "NPC生成器");
		});
		ModLanguageProvider.generate(event);
	}
}
