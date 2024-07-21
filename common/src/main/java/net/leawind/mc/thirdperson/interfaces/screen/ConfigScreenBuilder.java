package net.leawind.mc.thirdperson.interfaces.screen;


import net.leawind.mc.thirdperson.mod.config.Config;
import net.leawind.mc.thirdperson.mod.config.ConfigManagerImpl;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 配置屏幕构建器
 *
 * @see ConfigManagerImpl#getConfigScreen(Screen)
 */
public interface ConfigScreenBuilder {
	/**
	 * 构建配置屏幕
	 *
	 * @param config 配置实例
	 * @param parent 父屏幕
	 * @return 配置屏幕
	 */
	@NotNull
	Screen build (@NotNull Config config, @Nullable Screen parent);
}
