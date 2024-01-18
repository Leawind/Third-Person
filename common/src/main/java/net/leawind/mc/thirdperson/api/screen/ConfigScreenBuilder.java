package net.leawind.mc.thirdperson.api.screen;


import net.leawind.mc.thirdperson.impl.config.Config;
import net.leawind.mc.thirdperson.impl.screen.ClothConfigScreenBuilder;
import net.minecraft.client.gui.screens.Screen;

/**
 * 配置屏幕构建器 可能包含多种实现
 *
 * <li>Yet Another Config Lib</li>
 * <li>Carbon Config</li>
 * <li>Cloth Config API</li>
 */
public interface ConfigScreenBuilder {
	ConfigScreenBuilder YACL          = null;
	ConfigScreenBuilder CARBON_CONFIG = null;
	ConfigScreenBuilder CLOTH_CONFIG  = new ClothConfigScreenBuilder();

	Screen build (Config config, Screen parent);
}
