package net.leawind.mc.thirdperson;


import net.leawind.mc.thirdperson.api.config.Config;
import net.leawind.mc.util.math.vector.api.Vector2d;
import net.leawind.mc.util.math.vector.api.Vector3d;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public final class ThirdPersonStatus {
	public static final @NotNull Vector3d impulse                              = Vector3d.of(0);
	public static final @NotNull Vector2d impulseHorizon                       = Vector2d.of(0);
	/**
	 * @see ThirdPersonKeys#TOGGLE_AIMING
	 */
	public static                boolean  isToggleToAiming                     = false;
	public static                float    lastPartialTick                      = 1;
	public static                double   lastRenderTickTimeStamp              = 0;
	public static                boolean  wasRenderInThirdPersonLastRenderTick = false;
	public static                boolean  isTemporaryFirstPerson               = false;

	/**
	 * 是否正在调整摄像机偏移量
	 */
	public static boolean isAdjustingCameraOffset () {
		return isAdjustingCameraDistance();
	}

	/**
	 * 检查相机距离是否正在调整。
	 */
	public static boolean isAdjustingCameraDistance () {
		return ThirdPerson.isAvailable() && isThirdPerson() && ThirdPersonKeys.ADJUST_POSITION.isDown();
	}

	/**
	 * 根据 mc options 判断当前是否是第三人称
	 */
	public static boolean isThirdPerson () {
		return !ThirdPerson.mc.options.getCameraType().isFirstPerson();
	}

	/**
	 * 是否应该进入第三人称视角
	 */
	public static boolean shouldRenderInThirdPerson () {
		return ThirdPerson.getConfig().is_third_person_mode && !isTemporaryFirstPerson;
	}

	/**
	 * 当前是否显示准星
	 */
	public static boolean shouldRenderCrosshair () {
		Config config = ThirdPerson.getConfig();
		return ThirdPerson.isAvailable() && (ThirdPerson.ENTITY_AGENT.wasAiming() ? config.render_crosshair_when_aiming: config.render_crosshair_when_not_aiming);
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
		if (!ThirdPerson.ENTITY_AGENT.isCameraEntityExist()) {
			return false;
		} else if (!ThirdPerson.getConfig().use_camera_pick_in_creative) {
			return false;
		}
		return ThirdPerson.ENTITY_AGENT.getRawCameraEntity() instanceof Player player && player.isCreative();
	}

	/**
	 * 根据不透明度判断是否需要渲染相机实体
	 *
	 * @return 是否应当渲染相机实体
	 */
	public static boolean shouldRenderCameraEntity () {
		return ThirdPerson.ENTITY_AGENT.getSmoothOpacity() > ThirdPersonConstants.RENDERED_OPACITY_THRESHOLD;
	}
}
