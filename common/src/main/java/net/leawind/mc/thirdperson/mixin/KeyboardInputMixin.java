package net.leawind.mc.thirdperson.mixin;


import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.ThirdPersonStatus;
import net.leawind.mc.util.math.LMath;
import net.leawind.mc.util.math.vector.api.Vector2d;
import net.leawind.mc.util.math.vector.api.Vector3d;
import net.minecraft.client.player.KeyboardInput;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value=KeyboardInput.class, priority=2000)
public class KeyboardInputMixin {
	/**
	 * 注入到tick的末尾，重新计算 leftImpulse 和 forwardImpulse 的值
	 */
	@Inject(method="tick", at=@At(value="TAIL"))
	@PerformanceSensitive
	public void tick_tail (boolean isMoveSlowly, float sneakingSpeedBonus, CallbackInfo ci) {
		KeyboardInput that = ((KeyboardInput)(Object)this);
		if (ThirdPerson.isAvailable() && ThirdPersonStatus.isRenderingInThirdPerson() && ThirdPerson.ENTITY_AGENT.isControlled()) {
			// 相机坐标系下的impulse
			double cameraLookImpulse = (that.up ? 1: 0) - (that.down ? 1: 0);
			double cameraLeftImpulse = (that.left ? 1: 0) - (that.right ? 1: 0);
			// 计算世界坐标系下的向前和向左 impulse
			Vector3d lookImpulse        = LMath.toVector3d(ThirdPerson.CAMERA_AGENT.getRawCamera().getLookVector()).normalize();    // 视线向量
			Vector3d leftImpulse        = LMath.toVector3d(ThirdPerson.CAMERA_AGENT.getRawCamera().getLeftVector()).normalize();
			Vector2d lookImpulseHorizon = Vector2d.of(lookImpulse.x(), lookImpulse.z()).normalize();    // 水平方向上的视线向量
			Vector2d leftImpulseHorizon = Vector2d.of(leftImpulse.x(), leftImpulse.z()).normalize();
			lookImpulse.mul(cameraLookImpulse);    // 这才是 impulse
			leftImpulse.mul(cameraLeftImpulse);
			lookImpulseHorizon.mul(cameraLookImpulse);    // 水平 impulse
			leftImpulseHorizon.mul(cameraLeftImpulse);
			// 世界坐标系下的 impulse
			lookImpulse.add(leftImpulse, ThirdPersonStatus.impulse);
			lookImpulseHorizon.add(leftImpulseHorizon, ThirdPersonStatus.impulseHorizon);
			// impulse 不为0，
			if (ThirdPersonStatus.impulseHorizon.length() > 1E-5) {
				ThirdPersonStatus.impulseHorizon.normalize();
				float    playerYRot        = ThirdPerson.ENTITY_AGENT.getRawPlayerEntity().getViewYRot(ThirdPersonStatus.lastPartialTick);
				Vector2d playerLookHorizon = LMath.directionFromRotationDegree(playerYRot).normalize();
				Vector2d playerLeftHorizon = LMath.directionFromRotationDegree(playerYRot - 90).normalize();
				that.forwardImpulse = (float)(ThirdPersonStatus.impulseHorizon.dot(playerLookHorizon));
				that.leftImpulse    = (float)(ThirdPersonStatus.impulseHorizon.dot(playerLeftHorizon));
				if (isMoveSlowly) {
					that.forwardImpulse *= sneakingSpeedBonus;
					that.leftImpulse *= sneakingSpeedBonus;
				}
			}
		}
	}
}
