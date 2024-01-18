package net.leawind.mc.thirdperson.impl.config;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.leawind.mc.thirdperson.ExpectPlatform;
import net.leawind.mc.thirdperson.ThirdPersonMod;
import net.leawind.mc.thirdperson.api.ModConstants;
import net.leawind.mc.thirdperson.api.config.ConfigManager;
import net.minecraft.client.gui.screens.Screen;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ConfigManagerImpl implements ConfigManager {
	private final @NotNull Gson   GSON   = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().disableHtmlEscaping().create();
	private @NotNull       Config config = new Config();

	public ConfigManagerImpl () {
	}

	@Override
	public void tryLoad () {
		try {
			assert ModConstants.CONFIG_FILE.getParentFile().mkdirs();
			if (ModConstants.CONFIG_FILE.exists()) {
				load();
				ThirdPersonMod.LOGGER.info("Config is loaded from {}", ModConstants.CONFIG_FILE);
			} else {
				ThirdPersonMod.LOGGER.info("Config not found, creating one.");
				trySave();
			}
		} catch (IOException e) {
			ThirdPersonMod.LOGGER.error("Failed to load config.", e);
		}
		config.update();
	}

	@Override
	public void trySave () {
		try {
			save();
			ThirdPersonMod.LOGGER.info("Config is saved.");
		} catch (IOException e) {
			ThirdPersonMod.LOGGER.error("Failed to save config.", e);
		}
		config.update();
	}

	@Override
	public void load () throws IOException {
		config = GSON.fromJson(Files.readString(ModConstants.CONFIG_FILE.toPath(), StandardCharsets.UTF_8), Config.class);
	}

	@Override
	public void save () throws IOException {
		FileUtils.writeStringToFile(ModConstants.CONFIG_FILE, GSON.toJson(this.config), StandardCharsets.UTF_8);
	}

	@Override
	public @Nullable Screen getConfigScreen (Screen parent) {
		//		return ConfigBuilder.buildConfigScreen(config, parent);
		return ExpectPlatform.buildConfigScreen(config, parent);
	}

	@Override
	public @NotNull Config getConfig () {
		return this.config;
	}
}
