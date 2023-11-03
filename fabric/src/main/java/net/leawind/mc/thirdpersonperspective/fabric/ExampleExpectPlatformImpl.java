package net.leawind.mc.thirdpersonperspective.fabric;


import net.fabricmc.loader.api.FabricLoader;
import net.leawind.mc.thirdpersonperspective.ExampleExpectPlatform;

import java.nio.file.Path;

public class ExampleExpectPlatformImpl {
	/**
	 * This is our actual method to {@link ExampleExpectPlatform#getConfigDirectory()}.
	 */
	public static Path getConfigDirectory () {
		return FabricLoader.getInstance().getConfigDir();
	}
}
