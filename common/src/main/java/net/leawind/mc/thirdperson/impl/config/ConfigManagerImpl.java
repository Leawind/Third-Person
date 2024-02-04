package net.leawind.mc.thirdperson.impl.config;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.ThirdPersonConstants;
import net.leawind.mc.thirdperson.api.config.Config;
import net.leawind.mc.thirdperson.api.config.ConfigManager;
import net.leawind.mc.thirdperson.impl.screen.ConfigScreenBuilders;
import net.minecraft.client.gui.screens.Screen;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ConfigManagerImpl implements ConfigManager {
	private final @NotNull Gson   GSON   = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().disableHtmlEscaping().create();
	private @NotNull       Config config = Config.create();

	public ConfigManagerImpl () {
	}

	@Override
	public void tryLoad () {
		try {
			assert ThirdPersonConstants.CONFIG_FILE.getParentFile().mkdirs();
			if (ThirdPersonConstants.CONFIG_FILE.exists()) {
				load();
				ThirdPerson.LOGGER.info("Config is loaded from {}", ThirdPersonConstants.CONFIG_FILE);
			} else {
				ThirdPerson.LOGGER.info("Config not found, creating one.");
				trySave();
			}
		} catch (IOException e) {
			ThirdPerson.LOGGER.error("Failed to load config.", e);
		}
		config.update();
	}

	@Override
	public void trySave () {
		try {
			save();
			ThirdPerson.LOGGER.info("Config is saved.");
		} catch (IOException e) {
			ThirdPerson.LOGGER.error("Failed to save config.", e);
		}
		config.update();
	}

	@Override
	public void load () throws IOException {
		config = GSON.fromJson(Files.readString(ThirdPersonConstants.CONFIG_FILE.toPath(), StandardCharsets.UTF_8), Config.IMPL);
	}

	@Override
	public void save () throws IOException {
		FileUtils.writeStringToFile(ThirdPersonConstants.CONFIG_FILE, GSON.toJson(this.config), StandardCharsets.UTF_8);
	}

	@Override
	public @Nullable Screen getConfigScreen (@Nullable Screen parent) {
		return ConfigScreenBuilders.CLOTH_CONFIG.build(config, parent);
	}

	@Override
	public @NotNull Config getConfig () {
		return this.config;
	}
}
