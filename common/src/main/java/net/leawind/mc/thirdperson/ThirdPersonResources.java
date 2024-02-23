package net.leawind.mc.thirdperson;


import dev.architectury.injectables.annotations.ExpectPlatform;
import net.leawind.mc.thirdperson.resources.ItemPatternManager;

public class ThirdPersonResources {
	public static ItemPatternManager itemPatternManager;

	@ExpectPlatform
	public static void register () {
		throw new AssertionError();
	}
}
