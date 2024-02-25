package net.leawind.mc.thirdperson;


import com.mojang.blaze3d.vertex.PoseStack;
import net.leawind.mc.thirdperson.api.config.Config;
import net.leawind.mc.util.math.LMath;
import net.leawind.mc.util.math.vector.api.Vector2d;
import net.leawind.mc.util.math.vector.api.Vector3d;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public final class ThirdPersonStatus {
	public static final @NotNull Vector3d impulse                 = Vector3d.of(0);
	public static final @NotNull Vector2d impulseHorizon          = Vector2d.of(0);
	/**
	 * @see ThirdPersonKeys#TOGGLE_AIMING
	 */
	public static                boolean  isToggleToAiming        = false;
	public static                float    lastPartialTick         = 1;
	public static                double   lastRenderTickTimeStamp = 0;

	/**
	 * 是否正在调整摄像机偏移量
	 */
	public static boolean isAdjustingCameraOffset () {
		return isAdjustingCameraDistance() && !ThirdPerson.CAMERA_AGENT.wasCameraCloseToEntity();
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
	 * 当前是否显示准星
	 */
	public static boolean shouldRenderCrosshair () {
		Config config = ThirdPerson.getConfig();
		return ThirdPerson.isAvailable() && (ThirdPerson.ENTITY_AGENT.wasAiming() ? config.render_crosshair_when_aiming: config.render_crosshair_when_not_aiming);
	}

	/**
	 * TODO 让玩家实体淡出淡入，而不是瞬间消失出现 参考 https://github.com/Exopandora/ShoulderSurfing/commit/d488ec52e24897e47551cf2767f9372f7707c94b
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
		Config config = ThirdPerson.getConfig();
		if (!config.player_fade_out_enabled) {
			return false;
		} else if (ThirdPerson.mc.cameraEntity == null) {
			return false;
		}
		Vector3d eyePosition    = ThirdPerson.ENTITY_AGENT.getPossiblySmoothEyePosition(lastPartialTick);
		Vector3d cameraPosition = LMath.toVector3d(ThirdPerson.CAMERA_AGENT.getRawCamera().getPosition());
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
		if (!ThirdPerson.ENTITY_AGENT.isCameraEntityExist()) {
			return false;
		} else if (!ThirdPerson.getConfig().use_camera_pick_in_creative) {
			return false;
		}
		return ThirdPerson.ENTITY_AGENT.getRawCameraEntity() instanceof Player player && player.isCreative();
	}
}
