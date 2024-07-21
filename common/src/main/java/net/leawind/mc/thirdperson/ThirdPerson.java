package net.leawind.mc.thirdperson;


import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import net.leawind.mc.thirdperson.mod.config.Config;
import net.leawind.mc.thirdperson.mod.config.ConfigManager;
import net.leawind.mc.thirdperson.mod.core.CameraAgent;
import net.leawind.mc.thirdperson.mod.core.EntityAgent;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ThirdPerson {
	public static final Minecraft     mc             = Minecraft.getInstance();
	public static final Logger        LOGGER         = LoggerFactory.getLogger(ThirdPersonConstants.MOD_NAME);
	public static final ConfigManager CONFIG_MANAGER = new ConfigManager();
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
	}

	/**
	 * 判断：模组功能已启用，且相机和玩家都已经初始化
	 */
	public static boolean isAvailable () {
		return mc.player != null    //
			   && mc.cameraEntity != null    //
			   && getConfig().is_mod_enable //
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
}
