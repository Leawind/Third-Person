package net.leawind.mc.thirdperson.api.screen;


import net.leawind.mc.thirdperson.impl.config.Config;
import net.leawind.mc.thirdperson.impl.screen.ClothConfigScreenBuilder;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 配置屏幕构建器 可能包含多种实现
 *
 * <li><input type="checkbox" TODO    /> Yet Another Config Lib</li>
 * <li><input type="checkbox" TODO    /> Carbon Config</li>
 * <li><input type="checkbox" checked /> Cloth Config API</li>
 */
public interface ConfigScreenBuilder {
	ConfigScreenBuilder YACL          = null;
	ConfigScreenBuilder CARBON_CONFIG = null;
	ConfigScreenBuilder CLOTH_CONFIG  = new ClothConfigScreenBuilder();

	/**
	 * 构建配置屏幕
	 *
	 * @param config 配置实例
	 * @param parent 父屏幕
	 * @return 配置屏幕
	 */
	@NotNull Screen build (@NotNull Config config, @Nullable Screen parent);
}
