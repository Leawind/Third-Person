package net.leawind.mc.thirdperson.forge;


import net.leawind.mc.thirdperson.ExpectPlatform;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class ExpectPlatformImpl {
	/**
	 * This is our actual method to {@link ExpectPlatform#getConfigDirectory()}.
	 */
	public static Path getConfigDirectory () {
		return FMLPaths.CONFIGDIR.get();
	}
}
