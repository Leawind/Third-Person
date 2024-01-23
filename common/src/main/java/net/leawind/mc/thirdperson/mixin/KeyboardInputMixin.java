package net.leawind.mc.thirdperson.mixin;


import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.core.CameraAgent;
import net.leawind.mc.thirdperson.core.ModReferee;
import net.leawind.mc.util.api.math.LMath;
import net.leawind.mc.util.api.math.vector.Vector2d;
import net.leawind.mc.util.api.math.vector.Vector3d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.LocalPlayer;
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
	public void tick_inject_tail (boolean isMoveSlowly, float sneakingSpeedBonus, CallbackInfo ci) {
		KeyboardInput that = ((KeyboardInput)(Object)this);
		if (CameraAgent.isAvailable() && ModReferee.isThirdPerson() && CameraAgent.isControlledCamera()) {
			LocalPlayer player = Minecraft.getInstance().player;
			// 相机坐标系下的impulse
			double cameraLookImpulse = (that.up ? 1: 0) - (that.down ? 1: 0);
			double cameraLeftImpulse = (that.left ? 1: 0) - (that.right ? 1: 0);
			// 计算世界坐标系下的向前和向左 impulse
			Vector3d lookImpulse        = LMath.toVector3d(CameraAgent.fakeCamera.getLookVector()).normalize();    // 视线向量
			Vector3d leftImpulse        = LMath.toVector3d(CameraAgent.fakeCamera.getLeftVector()).normalize();
			Vector2d lookImpulseHorizon = Vector2d.of(lookImpulse.x(), lookImpulse.z()).normalize();    // 水平方向上的视线向量
			Vector2d leftImpulseHorizon = Vector2d.of(leftImpulse.x(), leftImpulse.z()).normalize();
			lookImpulse.mul(cameraLookImpulse);    // 这才是 impulse
			leftImpulse.mul(cameraLeftImpulse);
			lookImpulseHorizon.mul(cameraLookImpulse);    // 水平 impulse
			leftImpulseHorizon.mul(cameraLeftImpulse);
			// 世界坐标系下的 impulse
			lookImpulse.add(leftImpulse, ThirdPerson.impulse);
			lookImpulseHorizon.add(leftImpulseHorizon, ThirdPerson.impulseHorizon);
			// impulse 不为0，
			if (player != null && ThirdPerson.impulseHorizon.length() > 1E-5) {
				ThirdPerson.impulseHorizon.normalize();
				float    playerYRot        = player.getViewYRot(ThirdPerson.lastPartialTick);
				Vector2d playerLookHorizon = LMath.directionFromRotationDegree(playerYRot).normalize();
				Vector2d playerLeftHorizon = LMath.directionFromRotationDegree(playerYRot - 90).normalize();
				that.forwardImpulse = (float)(ThirdPerson.impulseHorizon.dot(playerLookHorizon));
				that.leftImpulse    = (float)(ThirdPerson.impulseHorizon.dot(playerLeftHorizon));
				if (isMoveSlowly) {
					that.forwardImpulse *= sneakingSpeedBonus;
					that.leftImpulse *= sneakingSpeedBonus;
				}
			}
		}
	}
}
