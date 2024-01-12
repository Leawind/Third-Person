package net.leawind.mc.thirdperson.config;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.leawind.mc.thirdperson.ExpectPlatform;
import net.leawind.mc.thirdperson.ThirdPersonMod;
import net.leawind.mc.thirdperson.core.ModConstants;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ConfigManager {
	private final Gson   GSON   = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().disableHtmlEscaping().create();
	private       Config config = new Config();

	/**
	 * 获取配置管理器实例
	 */
	@NotNull
	public static ConfigManager get () {
		return ThirdPersonMod.CONFIG_MANAGER;
	}

	/**
	 * 在可翻译文本的键前加上modid前缀
	 *
	 * @param name 键名
	 * @return ${MODID}.${name}
	 */
	public static Component getText (String name) {
		return Component.translatable(ThirdPersonMod.MOD_ID + "." + name);
	}

	public ConfigManager () {
		load();
	}

	/**
	 * 加载配置
	 * <p>
	 * 如果找不到文件，则保存一份。
	 */
	public void load () {
		try {
			assert ModConstants.CONFIG_FILE.getParentFile().mkdirs();
			if (ModConstants.CONFIG_FILE.exists()) {
				config = GSON.fromJson(Files.readString(ModConstants.CONFIG_FILE.toPath(), StandardCharsets.UTF_8), Config.class);
			} else {
				ThirdPersonMod.LOGGER.info("Config not found, creating one.");
				save();
			}
		} catch (IOException e) {
			ThirdPersonMod.LOGGER.error("Failed to load config.", e);
		}
		config.update();
	}

	/**
	 * 保存配置文件
	 * TODO delay saving
	 */
	public void save () {
		try {
			FileUtils.writeStringToFile(ModConstants.CONFIG_FILE, GSON.toJson(this.config), StandardCharsets.UTF_8);
			ThirdPersonMod.LOGGER.info("Config is saved.");
		} catch (IOException e) {
			ThirdPersonMod.LOGGER.error("Failed to save config.", e);
		}
	}

	/**
	 * 获取配置屏幕
	 * <p>
	 * 提供给 ModMenu
	 */
	public Screen getConfigScreen (Screen parent) {
		return ExpectPlatform.buildConfigScreen(config, parent);
	}

	public Config getConfig () {
		return this.config;
	}
}
