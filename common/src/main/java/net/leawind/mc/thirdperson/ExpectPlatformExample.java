package net.leawind.mc.thirdperson;


import dev.architectury.injectables.annotations.ExpectPlatform;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class ExpectPlatformExample {
	/**
	 * 虽然暂时没用，但是留着防止忘记，省的以后又去翻文档。
	 */
	@ExpectPlatform
	public static @NotNull String getMessage () {
		throw new AssertionError();
	}
}
