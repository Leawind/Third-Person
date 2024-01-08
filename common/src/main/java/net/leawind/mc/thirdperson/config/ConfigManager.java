package net.leawind.mc.thirdperson.config;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.leawind.mc.thirdperson.ExpectPlatform;
import net.leawind.mc.thirdperson.ThirdPersonMod;
import net.leawind.mc.thirdperson.core.ModConstants;
import net.minecraft.client.gui.screens.Screen;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ConfigManager {
	public static final Logger LOGGER = LoggerFactory.getLogger(ThirdPersonMod.MOD_ID);
	private final       Gson   GSON   = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private             Config config = new Config();

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
				this.config = GSON.fromJson(Files.readString(ModConstants.CONFIG_FILE.toPath(), StandardCharsets.UTF_8), Config.class);
			} else {
				LOGGER.info("Config not found, creating one.");
				this.save();
			}
		} catch (IOException e) {
			LOGGER.error("Failed to load config.", e);
		}
	}

	public void save () {
		try {
			FileUtils.writeStringToFile(ModConstants.CONFIG_FILE, GSON.toJson(this.config), StandardCharsets.UTF_8);
		} catch (IOException e) {
			LOGGER.error("Failed to save config.", e);
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

	public static ConfigManager get () {
		return ThirdPersonMod.CONFIG_MANAGER;
	}
}
