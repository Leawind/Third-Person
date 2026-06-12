package com.github.leawind.thirdperson.core.config;

import com.github.leawind.thirdperson.api.ThirdPerson;
import com.github.leawind.thirdperson.core.Constants;
import com.github.leawind.thirdperson.impl.screen.ConfigScreenBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Timer;
import java.util.TimerTask;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigManager {
  public static final Logger LOGGER = LoggerFactory.getLogger(ConfigManager.class);
  public static final ConfigManager INSTANCE = new ConfigManager();

  private final Gson GSON =
      new GsonBuilder()
          .excludeFieldsWithoutExposeAnnotation()
          .setPrettyPrinting()
          .disableHtmlEscaping()
          .create();

  private final Timer lazySaveTimer = new Timer();
  private @NonNull Config config = new Config();
  private boolean isLazySaveScheduled = false;

  public ConfigManager() {}

  /**
   * 加载配置
   *
   * <p>如果找不到文件，则保存一份。
   *
   * <p>如果失败，则记录错误到日志
   */
  public void tryLoad() {
    var configFile = Constants.CONFIG_FILE.get();
    LOGGER.debug("Trying loading config from {}", configFile);
    try {
      configFile.getParentFile().mkdirs();
      if (configFile.exists()) {
        load();
        LOGGER.info("Config is loaded from {}", configFile);
      } else {
        LOGGER.info("Config not found, creating one.");
        trySave();
      }
    } catch (IOException e) {
      LOGGER.error("Failed to load config.", e);
    } catch (JsonSyntaxException e) {
      LOGGER.error("Config file is broken.", e);
    }
    config.update();
  }

  /** 两次保存时间间隔至少为 {@link Constants#CONFIG_LAZY_SAVE_DELAY} */
  public void lazySave() {
    if (!isLazySaveScheduled) {
      isLazySaveScheduled = true;
      lazySaveTimer.schedule(
          new TimerTask() {
            @Override
            public void run() {
              trySave();
              isLazySaveScheduled = false;
            }
          },
          Constants.CONFIG_LAZY_SAVE_DELAY);
    }
  }

  /**
   * 尝试保存配置文件
   *
   * <p>如果失败，则记录错误到日志
   */
  public void trySave() {
    LOGGER.debug("Trying saving config to {}", Constants.CONFIG_FILE.get());
    try {
      save();
      LOGGER.info("Config is saved.");
    } catch (IOException e) {
      LOGGER.error("Failed to save config.", e);
    }
    config.update();
  }

  /** 直接读取配置文件 */
  public void load() throws IOException {
    config =
        GSON.fromJson(
            Files.readString(Constants.CONFIG_FILE.get().toPath(), StandardCharsets.UTF_8),
            Config.class);
  }

  /** 直接保存配置文件 */
  public void save() throws IOException {
    FileUtils.writeStringToFile(
        Constants.CONFIG_FILE.get(), GSON.toJson(this.config), StandardCharsets.UTF_8);
  }

  /** 获取配置屏幕 */
  public @Nullable Screen getConfigScreen(@Nullable Screen parent) {
    var builder = ConfigScreenBuilder.getBuilder();
    if (builder == null) {
      LOGGER.warn("No config screen builder available.");
      return null;
    }
    LOGGER.debug("Building config screen");
    return builder.build(config, parent);
  }

  /** 获取配置对象 */
  public @NonNull Config getConfig() {
    return this.config;
  }

  /**
   * 在可翻译文本的键前加上mod_id前缀
   *
   * @param name 键名
   * @return ${mod_id}.${id}
   */
  @Contract(value = "_ -> new", pure = true)
  public static @NonNull Component getText(@NonNull String name) {
    return Component.translatable(ThirdPerson.MOD_ID + "." + name);
  }
}
