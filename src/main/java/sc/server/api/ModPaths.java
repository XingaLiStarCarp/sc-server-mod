package sc.server.api;

import java.nio.file.Path;

import net.minecraftforge.fml.loading.FMLPaths;

public class ModPaths {
	public static final Path config() {
		return FMLPaths.CONFIGDIR.get();
	}

	public static final Path config(String relativePath) {
		return config().resolve(relativePath);
	}
}
