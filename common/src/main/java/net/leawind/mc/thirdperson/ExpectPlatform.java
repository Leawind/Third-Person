package net.leawind.mc.thirdperson;


public class ExpectPlatform {
	/**
	 * 占一下位，防止忘记。
	 * <p>
	 * 省的以后又去翻文档。
	 */
	@dev.architectury.injectables.annotations.ExpectPlatform
	public static String getMessage () {
		throw new AssertionError();
	}
}
