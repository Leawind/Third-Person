package net.leawind.mc.thirdperson.impl.core.rotation;


import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.util.math.LMath;
import net.leawind.mc.util.math.vector.api.Vector2d;
import net.leawind.mc.util.math.vector.api.Vector3d;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * 旋转目标，即玩家应该转向何处
 */
public enum RotateTarget {
	/**
	 * 保持当前朝向，不旋转
	 */
	NONE(() -> ThirdPerson.ENTITY_AGENT.getRawRotation(1)),
	/**
	 * 与相机朝向相同
	 */
	CAMERA_ROTATION(() -> ThirdPerson.CAMERA_AGENT.getRotation()),
	/**
	 * 转向相机的视线落点，即准星所指的位置
	 */
	CAMERA_HIT_RESULT(() -> {
		Optional<Vector3d> cameraHitPosition = ThirdPerson.CAMERA_AGENT.getPickPosition();
		if (cameraHitPosition.isEmpty()) {
			return CAMERA_ROTATION.getRotation();
		} else {
			Vector3d  eyePosition = ThirdPerson.ENTITY_AGENT.getRawEyePosition(1);
			Vector3d  viewVector  = cameraHitPosition.get().sub(eyePosition);
			return LMath.rotationDegreeFromDirection(viewVector);
		}
	}),
	/**
	 * 使用键盘控制的移动方向
	 * <p>
	 * 当没有使用键盘控制时保持当前朝向
	 */
	IMPULSE_DIRECTION(() -> ThirdPerson.impulseHorizon.length() < 1e-5    //
							? NONE.getRotation()    //
							: LMath.rotationDegreeFromDirection(ThirdPerson.impulse)),
	/**
	 * 使用键盘控制的移动方向（仅水平）
	 * <p>
	 * 当没有使用键盘控制时保持当前朝向
	 */
	HORIZONTAL_IMPULSE_DIRECTION(() -> {
		if (ThirdPerson.impulseHorizon.length() < 1e-5) {
			return NONE.getRotation();
		} else {
			double absoluteYRotDegree = LMath.rotationDegreeFromDirection(ThirdPerson.impulseHorizon);
			return Vector2d.of(0, absoluteYRotDegree);
		}
	});
	private final Supplier<Vector2d> rotationGetter;

	RotateTarget (@NotNull Supplier<Vector2d> rotationGetter) {
		this.rotationGetter = rotationGetter;
	}

	/**
	 * 获取玩家当前的目标朝向
	 */
	public @NotNull Vector2d getRotation () {
		return rotationGetter.get();
	}
}
