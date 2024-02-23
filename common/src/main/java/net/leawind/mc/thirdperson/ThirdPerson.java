package net.leawind.mc.thirdperson;


import com.mojang.blaze3d.vertex.PoseStack;
import net.leawind.mc.thirdperson.api.config.Config;
import net.leawind.mc.thirdperson.api.config.ConfigManager;
import net.leawind.mc.thirdperson.api.core.CameraAgent;
import net.leawind.mc.thirdperson.api.core.EntityAgent;
import net.leawind.mc.thirdperson.impl.config.ConfigManagerImpl;
import net.leawind.mc.util.math.LMath;
import net.leawind.mc.util.math.vector.api.Vector2d;
import net.leawind.mc.util.math.vector.api.Vector3d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ThirdPerson {
	public static final          Minecraft     mc                      = Minecraft.getInstance();
	public static final          Logger        LOGGER                  = LoggerFactory.getLogger(ThirdPersonConstants.MOD_ID);
	public static final          ConfigManager CONFIG_MANAGER          = new ConfigManagerImpl();
	public static final @NotNull Vector3d      impulse                 = Vector3d.of(0);
	public static final @NotNull Vector2d      impulseHorizon          = Vector2d.of(0);
	public static                EntityAgent   ENTITY_AGENT;
	public static                CameraAgent   CAMERA_AGENT;
	public static                float         lastPartialTick         = 1;
	public static                double        lastRenderTickTimeStamp = 0;
	/**
	 * 是否通过按键切换到了瞄准模式
	 *
	 * @see ThirdPersonKeys#TOGGLE_AIMING
	 */
	public static                boolean       isToggleToAiming        = false;

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

	/**
	 * 是否正在调整摄像机偏移量
	 */
	public static boolean isAdjustingCameraOffset () {
		return isAdjustingCameraDistance() && !CAMERA_AGENT.wasCameraCloseToEntity();
	}

	/**
	 * 检查相机距离是否正在调整。
	 */
	public static boolean isAdjustingCameraDistance () {
		return isAvailable() && isThirdPerson() && ThirdPersonKeys.ADJUST_POSITION.isDown();
	}

	/**
	 * 根据 mc options 判断当前是否是第三人称
	 */
	public static boolean isThirdPerson () {
		return !mc.options.getCameraType().isFirstPerson();
	}

	/**
	 * 当前是否显示准星
	 */
	public static boolean shouldRenderCrosshair () {
		Config config = getConfig();
		return isAvailable() && (ENTITY_AGENT.wasAiming() ? config.render_crosshair_when_aiming: config.render_crosshair_when_not_aiming);
	}

	/**
	 * TODO 让玩家实体淡出淡入，而不是瞬间消失出现
	 * <p>
	 * 是否完全隐藏玩家实体
	 * <p>
	 * 当启用假的第一人称或相机距离玩家足够近时隐藏
	 * <p>
	 * 需要借助相机坐标和玩家眼睛坐标来判断
	 *
	 * @see LivingEntityRenderer#render(LivingEntity, float, float, PoseStack, MultiBufferSource, int)
	 */
	public static boolean wasCameraCloseToEntity () {
		Config config = getConfig();
		if (!config.player_fade_out_enabled) {
			return false;
		} else if (mc.cameraEntity == null) {
			return false;
		}
		Vector3d eyePosition    = ENTITY_AGENT.getPossiblySmoothEyePosition(lastPartialTick);
		Vector3d cameraPosition = LMath.toVector3d(CAMERA_AGENT.getRawCamera().getPosition());
		if (config.getCameraOffsetScheme().getMode().getMaxDistance() <= config.getDistanceMonoList().get(0)) {
			return true;
		} else {
			return eyePosition.distance(cameraPosition) <= config.getDistanceMonoList().get(0);
		}
	}

	/**
	 * 根据玩家的按键判断玩家是否想瞄准
	 */
	public static boolean doesPlayerWantToAim () {
		return isToggleToAiming || ThirdPersonKeys.FORCE_AIMING.isDown();
	}

	/**
	 * 探测射线是否应当起始于相机处，而非玩家眼睛处
	 */
	public static boolean shouldPickFromCamera () {
		if (!ENTITY_AGENT.isCameraEntityExist()) {
			return false;
		} else if (!getConfig().use_camera_pick_in_creative) {
			return false;
		}
		return ENTITY_AGENT.getRawCameraEntity() instanceof Player player && player.isCreative();
	}
}
