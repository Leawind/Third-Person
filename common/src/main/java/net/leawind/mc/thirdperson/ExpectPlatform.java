package net.leawind.mc.thirdperson;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.nio.file.Path;

@Environment(EnvType.CLIENT)
public class ExpectPlatform {
	@dev.architectury.injectables.annotations.ExpectPlatform
	public static Path getConfigDirectory () {
		throw new AssertionError();
	}
}
