package sc.server.datagen;

import java.util.HashMap;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.registries.RegistryObject;
import sc.server.ModEntry;

public class ModLanguageProvider extends LanguageProvider {
	private static final HashMap<String, ModLanguageProvider> LANGUAGE_PROVIDERS = new HashMap<>();

	private static final ModLanguageProvider getLanguageProvider(PackOutput output, String locale) {
		ModLanguageProvider provider = LANGUAGE_PROVIDERS.get(locale);
		if (provider == null) {
			provider = new ModLanguageProvider(output, locale);
			LANGUAGE_PROVIDERS.put(locale, provider);
		}
		return provider;
	}

	public ModLanguageProvider(PackOutput output, String locale) {
		super(output, ModEntry.MOD_ID, locale);
	}

	public void add(ResourceLocation key, String value) {
		super.add(key.toLanguageKey(), value);
	}

	public void add(ResourceKey<?> key, String value) {
		super.add(key.location().toLanguageKey(key.registry().getPath()), value);
	}

	public void add(RegistryObject<?> key, String value) {
		this.add(key.getKey(), value);
	}

	@FunctionalInterface
	public static interface EntriesInit {
		public abstract void addTranslations(ModLanguageProvider provider);
	}

	EntriesInit init = null;

	public final void setTranslations(EntriesInit init) {
		this.init = init;
	}

	@Override
	protected void addTranslations() {
		if (init != null)
			init.addTranslations(this);
	}

	/**
	 * 设置翻译
	 * 
	 * @param output
	 * @param locale
	 * @param init
	 */
	public static final void setTranslations(PackOutput output, String locale, EntriesInit init) {
		getLanguageProvider(output, locale).setTranslations(init);
	}

	public static final void generate(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		for (ModLanguageProvider provider : LANGUAGE_PROVIDERS.values()) {
			generator.addProvider(event.includeClient(), provider);
		}
	}
}
