package net.leawind.mc.thirdperson.api.config;


import net.leawind.mc.thirdperson.ThirdPersonConstants;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * 配置管理器
 * <p>
 * 负则配置的加载与保存
 */
public interface ConfigManager {
	/**
	 * 在可翻译文本的键前加上modid前缀
	 *
	 * @param name 键名
	 * @return ${MODID}.${id}
	 */
	@Contract(value="_ -> new", pure=true)
	static @NotNull Component getText (@NotNull String name) {
		return Component.translatable(ThirdPersonConstants.MOD_ID + "." + name);
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
	 * 惰性保存
	 * <p>
	 * 两次保存时间间隔至少为 {@link ThirdPersonConstants#CONFIG_LAZY_SAVE_DELAY}
	 */
	void lazySave ();

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
	@Nullable Screen getConfigScreen (@Nullable Screen parent);

	/**
	 * 是否有可用的配置屏幕
	 */
	boolean isScreenAvailable ();

	/**
	 * 获取配置对象
	 */
	@NotNull Config getConfig ();
}
