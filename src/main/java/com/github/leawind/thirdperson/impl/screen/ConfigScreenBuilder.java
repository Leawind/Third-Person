package com.github.leawind.thirdperson.impl.screen;

import com.github.leawind.thirdperson.api.ThirdPerson;
import com.github.leawind.thirdperson.core.config.Config;
import com.github.leawind.thirdperson.core.config.ConfigManager;
import com.github.leawind.thirdperson.utils.PossibleSupplier;
import com.github.leawind.thirdperson.utils.annotation.VersionSensitive;
import dev.architectury.platform.Platform;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gui.screens.Screen;
import org.jspecify.annotations.NonNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 配置屏幕构建器
 *
 * @see ConfigManager#getConfigScreen(Screen)
 */
@SuppressWarnings("all")
@VersionSensitive("YACL version check")
public abstract class ConfigScreenBuilder {
  public static final Logger LOGGER = LoggerFactory.getLogger(ConfigScreenBuilder.class);

  /**
   * 构建配置屏幕
   *
   * @param config 配置实例
   * @param parent 父屏幕
   * @return 配置屏幕
   */
  @NonNull
  public abstract Screen build(@NonNull Config config, @Nullable Screen parent);

  /** 已经实现或将来可能实现的构建器们 */
  private static Map<String, PossibleSupplier<ConfigScreenBuilder>> builders = new HashMap<>();

  static {
    // TODO
    builders.put(
        "Cloth Config",
        PossibleSupplier.of(
            () -> new ClothConfigScreenBuilder(),
            () -> Platform.isModLoaded("cloth-config") || Platform.isModLoaded("cloth_config")));
    builders.put(
        "YACL",
        PossibleSupplier.of(
            () -> new YaclConfigScreenBuilder(),
            () -> Platform.isModLoaded("yet_another_config_lib_v3")));

    var availables = ConfigScreenBuilder.getAvailableBuidlers().keySet();
    availables.forEach(
        name -> {
          LOGGER.debug("Found available config screen builder: {}", name);
        });
    if (availables.isEmpty()) {
      LOGGER.warn("No config screen API available.");
    }
  }

  /** 根据配置获取屏幕构建器 */
  public static @Nullable ConfigScreenBuilder getBuilder() {
    final var availables = getAvailableBuidlers();
    if (availables.isEmpty()) {
      return null;
    }
    return availables
        .getOrDefault(
            ThirdPerson.getConfigManager().getConfig().config_screen_api,
            availables.values().iterator().next())
        .get();
  }

  /** 获取全部可用的构建器 */
  public static @NonNull Map<String, PossibleSupplier<ConfigScreenBuilder>> getAvailableBuidlers() {
    final Map<String, PossibleSupplier<ConfigScreenBuilder>> availableBuilders = new HashMap<>();
    builders.forEach(
        (name, builder) -> {
          if (builder.available()) {
            availableBuilders.put(name, builder);
          }
        });
    return availableBuilders;
  }
}
