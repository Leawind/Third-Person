package net.leawind.mc.thirdperson.core.rotation;


import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.ThirdPersonConstants;
import net.leawind.mc.thirdperson.ThirdPersonStatus;
import net.leawind.mc.thirdperson.core.CameraAgent;
import net.leawind.mc.util.math.LMath;
import net.leawind.mc.util.math.vector.Vector2d;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * 旋转目标，即玩家应该转向何处
 */
public enum RotateTargetEnum {
	/**
	 * 保持当前朝向，不旋转
	 */
	NONE(() -> ThirdPerson.ENTITY_AGENT.getRawRotation(1)),
	INTEREST_POINT(() -> {
		var optionalPoint = ThirdPerson.ENTITY_AGENT.getInterestPoint();
		if (optionalPoint.isEmpty()) {
			return NONE.getRotation();
		}
		var player            = ThirdPerson.ENTITY_AGENT.getRawPlayerEntity();
		var point             = optionalPoint.get();
		var toInterestedPoint = point.subtract(player.getEyePosition(ThirdPersonStatus.lastPartialTick));
		if (toInterestedPoint.length() < 1e-5) {
			return NONE.getRotation();
		}
		var    playerRot  = ThirdPerson.ENTITY_AGENT.getRawRotation(1);
		var    rot        = LMath.rotationDegreeFromDirection(LMath.toVector3d(toInterestedPoint));
		double leftBound  = player.yBodyRot - ThirdPersonConstants.VANILLA_PLAYER_HEAD_ROTATE_LIMIT_DEGREES;
		double rightBound = player.yBodyRot + ThirdPersonConstants.VANILLA_PLAYER_HEAD_ROTATE_LIMIT_DEGREES;
		assert playerRot.isFinite();
		assert rot.isFinite();
		if (LMath.isWithinDegrees(rot.y(), leftBound, rightBound)) {
			playerRot.y(rot.y());
		} else {
			playerRot.y(LMath.subtractDegrees(rot.y(), leftBound) < LMath.subtractDegrees(rot.y(), rightBound) ? leftBound: rightBound);
		}
		playerRot.x(rot.x());
		return playerRot;
	}),
	DEFAULT(() -> {
		var entity = ThirdPerson.ENTITY_AGENT.getRawCameraEntity();
		if (!ThirdPerson.CONFIG_MANAGER.getConfig().player_rotate_to_interest_point || entity.getControlledVehicle() instanceof LivingEntity) {
			return NONE.getRotation();
		}
		return INTEREST_POINT.getRotation();
	}),
	/**
	 * 与相机朝向相同
	 */
	CAMERA_ROTATION(() -> ThirdPerson.CAMERA_AGENT.getRotation()),
	/**
	 * 转向相机的视线落点，即准星所指的位置
	 */
	CAMERA_HIT_RESULT(() -> {
		var cameraHitPosition = ThirdPerson.CAMERA_AGENT.getPickPosition();
		if (cameraHitPosition.isEmpty()) {
			return CAMERA_ROTATION.getRotation();
		}
		var eyePosition = ThirdPerson.ENTITY_AGENT.getRawEyePosition(ThirdPersonStatus.lastPartialTick);
		var viewVector  = cameraHitPosition.get().sub(eyePosition);
		return LMath.rotationDegreeFromDirection(viewVector);
	}),
	/**
	 * 预测玩家想射击的目标实体
	 * <p>
	 * 玩家将朝向的目标点为 相机位置 + 相机射线单位向量*目标实体距离
	 * <p>
	 * 这样在射击远处的实体时，就不需要考虑玩家视线与相机视线间的偏移量了。
	 * <p>
	 * 但是问题在于，当周围有许多实体时，对目标实体的预测可能不准确。
	 *
	 * @see CameraAgent#predictTargetEntity()
	 */
	PREDICTED_TARGET_ENTITY(() -> {
		var rotation = CAMERA_HIT_RESULT.getRotation();
		if (!ThirdPerson.getConfig().enable_target_entity_predict || !ThirdPerson.ENTITY_AGENT.isControlled()) {
			return rotation;
		}
		var predicted = ThirdPerson.CAMERA_AGENT.predictTargetEntity();
		if (predicted.isEmpty()) {
			return rotation;
		}
		var camera       = ThirdPerson.CAMERA_AGENT.getRawCamera();
		var target       = predicted.get();
		var playerEyePos = ThirdPerson.ENTITY_AGENT.getRawEyePosition(ThirdPersonStatus.lastPartialTick);
		var cameraPos    = LMath.toVector3d(camera.getPosition());
		var targetPos    = LMath.toVector3d(target.getPosition(ThirdPersonStatus.lastPartialTick));
		var end          = LMath.toVector3d(camera.getLookVector()).normalize(cameraPos.distance(targetPos)).add(cameraPos);
		var eyeToEnd     = end.sub(playerEyePos);
		if (eyeToEnd.length() < 1e-5) {
			return rotation;
		}
		return LMath.rotationDegreeFromDirection(eyeToEnd);
	}),
	/**
	 * 使用键盘控制的移动方向
	 * <p>
	 * 当没有使用键盘控制时保持当前朝向
	 */
	IMPULSE_DIRECTION(() -> ThirdPersonStatus.impulseHorizon.length() < 1e-5    //
							? DEFAULT.getRotation()    //
							: LMath.rotationDegreeFromDirection(ThirdPersonStatus.impulse)),
	/**
	 * 使用键盘控制的移动方向（仅水平）
	 * <p>
	 * 当没有使用键盘控制时保持当前朝向
	 */
	HORIZONTAL_IMPULSE_DIRECTION(() -> {
		if (ThirdPersonStatus.impulseHorizon.length() < 1e-5) {
			return DEFAULT.getRotation();
		}
		double absoluteYRotDegree = LMath.rotationDegreeFromDirection(ThirdPersonStatus.impulseHorizon);
		return Vector2d.of(0.1, absoluteYRotDegree);
	});
	private final Supplier<Vector2d> rotationGetter;

	RotateTargetEnum (@NotNull Supplier<Vector2d> rotationGetter) {
		this.rotationGetter = rotationGetter;
	}

	/**
	 * 获取玩家当前的目标朝向
	 */
	public @NotNull Vector2d getRotation () {
		var rotation = rotationGetter.get();
		assert rotation.isFinite();
		return rotation;
	}
}