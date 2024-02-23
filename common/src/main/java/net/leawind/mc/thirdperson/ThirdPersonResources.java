package net.leawind.mc.thirdperson;


import dev.architectury.injectables.annotations.ExpectPlatform;
import net.leawind.mc.thirdperson.resources.ItemPatternManager;
import net.leawind.mc.util.itempattern.ItemPattern;

/**
 * 自定义资源包
 */
public class ThirdPersonResources {
	/**
	 * 物品模式管理器
	 *
	 * @see ItemPattern
	 */
	public static ItemPatternManager itemPatternManager;

	@ExpectPlatform
	public static void register () {
		throw new AssertionError();
	}
}
