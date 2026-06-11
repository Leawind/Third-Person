package com.github.leawind.thirdperson.api;

import com.github.leawind.thirdperson.impl.ThirdPersonConstants;
import com.github.leawind.thirdperson.impl.ThirdPersonEvents;
import com.github.leawind.thirdperson.impl.ThirdPersonKeys;
import com.github.leawind.thirdperson.impl.ThirdPersonResources;
import com.github.leawind.thirdperson.impl.config.Config;
import com.github.leawind.thirdperson.impl.config.ConfigManager;
import com.github.leawind.thirdperson.impl.CameraAgent;
import com.github.leawind.thirdperson.impl.EntityAgent;
import com.github.leawind.thirdperson.utils.FiniteChecker;
import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import dev.architectury.platform.Platform;
import dev.architectury.platform.client.ConfigurationScreenRegistry;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ThirdPerson {
  public static final Logger LOGGER = LoggerFactory.getLogger(ThirdPersonConstants.MOD_NAME);

  public static final FiniteChecker FINITE_CHECKER =
      new FiniteChecker(err -> ThirdPerson.LOGGER.error(err.toString()));
  public static final ConfigManager CONFIG_MANAGER = new ConfigManager();

  public static EntityAgent ENTITY_AGENT;
  public static CameraAgent CAMERA_AGENT;

  public static void init(Minecraft minecraft) {
    LOGGER.debug("Initializing mod {}", ThirdPersonConstants.MOD_NAME);
    MixinExtrasBootstrap.init();

    ENTITY_AGENT = new EntityAgent(minecraft);
    CAMERA_AGENT = new CameraAgent(minecraft);

    CONFIG_MANAGER.tryLoad();

    ThirdPersonResources.register();
    ThirdPersonKeys.register();
    ThirdPersonEvents.register();

    ConfigurationScreenRegistry.register(
        Platform.getMod(ThirdPersonConstants.MOD_ID), ThirdPerson.CONFIG_MANAGER::getConfigScreen);
  }

  /** 判断：模组功能已启用，且相机和玩家都已经初始化 */
  public static boolean isAvailable() {
    var minecraft = Minecraft.getInstance();
    return minecraft != null
        && minecraft.player != null
        && minecraft.getCameraEntity() != null
        && getConfig().is_mod_enabled
        && minecraft.gameRenderer.getMainCamera().isInitialized();
  }

  /** 获取当前配置实例 */
  public static @NotNull Config getConfig() {
    return CONFIG_MANAGER.getConfig();
  }

  public static void resetFiniteCheckers() {
    ThirdPerson.FINITE_CHECKER.reset();
    ENTITY_AGENT.FINITE_CHECKER.reset();
    CAMERA_AGENT.FINITE_CHECKER.reset();
  }
}
