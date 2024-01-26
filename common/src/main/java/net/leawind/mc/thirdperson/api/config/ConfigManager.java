package net.leawind.mc.thirdperson.api.config;


import net.leawind.mc.thirdperson.api.ModConstants;
import net.leawind.mc.thirdperson.impl.config.Config;
import net.leawind.mc.thirdperson.impl.config.ConfigManagerImpl;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * 配置管理器
 * <p>
 * 负则配置的加载与保存
 */
public interface ConfigManager {
	static ConfigManager create () {
		return new ConfigManagerImpl();
	}

	/**
	 * 在可翻译文本的键前加上modid前缀
	 *
	 * @param name 键名
	 * @return ${MODID}.${id}
	 */
	static Component getText (String name) {
		return Component.translatable(ModConstants.MOD_ID + "." + name);
	}

	/**
	 * 加载配置
	 * <p>
	 * 如果找不到文件，则保存一份。
	 * <p>
	 * 如果失败，则记录错误到日志
	 */
	void tryLoad ();

	/**
	 * 尝试保存配置文件
	 * <p>
	 * 如果失败，则记录错误到日志
	 */
	void trySave ();

	/**
	 * 直接读取配置文件
	 */
	void load () throws IOException;

	/**
	 * 直接保存配置文件
	 */
	void save () throws IOException;

	/**
	 * 获取配置屏幕
	 */
	@Nullable Screen getConfigScreen (Screen parent);

	/**
	 * 获取配置对象
	 */
	@NotNull Config getConfig ();
}
