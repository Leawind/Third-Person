package com.github.leawind.thirdperson;


import com.github.leawind.thirdperson.config.Config;
import com.github.leawind.thirdperson.config.ConfigManager;
import com.github.leawind.thirdperson.core.CameraAgent;
import com.github.leawind.thirdperson.core.EntityAgent;
import com.github.leawind.util.FiniteChecker;
import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import dev.architectury.platform.Platform;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ThirdPerson {
	public static final Minecraft     mc             = Minecraft.getInstance();
	public static final Logger        LOGGER         = LoggerFactory.getLogger(ThirdPersonConstants.MOD_NAME);
	public static final ConfigManager CONFIG_MANAGER = new ConfigManager();
	public static final FiniteChecker FINITE_CHECKER = new FiniteChecker(err -> ThirdPerson.LOGGER.error(err.toString()));
	public static       EntityAgent   ENTITY_AGENT;
	public static       CameraAgent   CAMERA_AGENT;

	public static void init () {
		LOGGER.debug("Initializing mod {}", ThirdPersonConstants.MOD_NAME);
		MixinExtrasBootstrap.init();
		ENTITY_AGENT = EntityAgent.create(mc);
		ENTITY_AGENT = new EntityAgent(mc);
		CAMERA_AGENT = new CameraAgent(mc);
		CONFIG_MANAGER.tryLoad();
		ThirdPersonResources.register();
		ThirdPersonKeys.register();
		ThirdPersonEvents.register();
		Platform.getMod(ThirdPersonConstants.MOD_ID).registerConfigurationScreen(ThirdPerson.CONFIG_MANAGER::getConfigScreen);
	}

	/**
	 * 判断：模组功能已启用，且相机和玩家都已经初始化
	 */
	public static boolean isAvailable () {
		return mc.player != null    //
			   && mc.cameraEntity != null    //
			   && getConfig().is_mod_enabled //
			   && mc.gameRenderer.getMainCamera().isInitialized()    //
			;
	}

	/**
	 * 获取当前配置实例
	 *
	 * @return 配置实例
	 */
	public static @NotNull Config getConfig () {
		return CONFIG_MANAGER.getConfig();
	}

	public static void resetFiniteCheckers () {
		ThirdPerson.FINITE_CHECKER.reset();
		ENTITY_AGENT.FINITE_CHECKER.reset();
		CAMERA_AGENT.FINITE_CHECKER.reset();
	}
}
