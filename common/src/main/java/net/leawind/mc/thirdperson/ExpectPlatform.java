package net.leawind.mc.thirdperson;


@SuppressWarnings("unused")
public class ExpectPlatform {
	/**
	 * 虽然暂时没用，但是留着防止忘记，省的以后又去翻文档。
	 */
	@dev.architectury.injectables.annotations.ExpectPlatform
	public static String getMessage () {
		throw new AssertionError();
	}
}
