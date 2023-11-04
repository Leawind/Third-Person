package net.leawind.mc.thirdpersonperspective;


import java.nio.file.Path;

public class ExpectPlatform {
	@dev.architectury.injectables.annotations.ExpectPlatform
	public static Path getConfigDirectory () {
		throw new AssertionError();
	}
}
