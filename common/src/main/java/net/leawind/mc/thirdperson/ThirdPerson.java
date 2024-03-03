package net.leawind.mc.thirdperson;


import net.leawind.mc.thirdperson.api.config.Config;
import net.leawind.mc.thirdperson.api.config.ConfigManager;
import net.leawind.mc.thirdperson.api.core.CameraAgent;
import net.leawind.mc.thirdperson.api.core.EntityAgent;
import net.leawind.mc.thirdperson.impl.config.ConfigManagerImpl;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Debug:
 * <pre>
 * # 快速装填5的弩
 * /give @s crossbow{Enchantments:[{id:quick_charge,lvl:5}]}
 * # 靶子村民
 * /summon villager ~ ~ ~ {NoAI:1b}
 * </pre>
 */
public final class ThirdPerson {
	public static final Minecraft     mc             = Minecraft.getInstance();
	public static final Logger        LOGGER         = LoggerFactory.getLogger(ThirdPersonConstants.MOD_NAME);
	public static final ConfigManager CONFIG_MANAGER = new ConfigManagerImpl();
	public static       EntityAgent   ENTITY_AGENT;
	public static       CameraAgent   CAMERA_AGENT;

	public static void init () {
		ENTITY_AGENT = EntityAgent.create(mc);
		CAMERA_AGENT = CameraAgent.create(mc);
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
