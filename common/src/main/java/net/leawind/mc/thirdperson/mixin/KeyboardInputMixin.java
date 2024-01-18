package net.leawind.mc.thirdperson.mixin;


import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.core.CameraAgent;
import net.leawind.mc.thirdperson.core.ModReferee;
import net.leawind.mc.thirdperson.core.PlayerAgent;
import net.leawind.mc.util.api.math.vector.Vector2d;
import net.leawind.mc.util.api.math.vector.Vector3d;
import net.leawind.mc.util.math.LMath;
import net.minecraft.client.Minecraft;
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
	public void tick_inject_tail (boolean isMoveSlowly, float sneakingSpeedBonus, CallbackInfo ci) {
		KeyboardInput that = ((KeyboardInput)(Object)this);
		if (CameraAgent.isAvailable() && ModReferee.isThirdPerson() && CameraAgent.isControlledCamera()) {
			Minecraft mc                = Minecraft.getInstance();
			double    cameraLookImpulse = (that.up ? 1: 0) - (that.down ? 1: 0);
			double    cameraLeftImpulse = (that.left ? 1: 0) - (that.right ? 1: 0);
			// 方向向量 != 0
			Vector3d lookImpulse        = LMath.toVector3d(CameraAgent.fakeCamera.getLookVector()).normalize();
			Vector3d leftImpulse        = LMath.toVector3d(CameraAgent.fakeCamera.getLeftVector()).normalize();
			Vector2d lookImpulseHorizon = Vector2d.of(lookImpulse.x(), lookImpulse.z()).normalize();
			Vector2d leftImpulseHorizon = Vector2d.of(leftImpulse.x(), leftImpulse.z()).normalize();
			// 乘上 impulse
			lookImpulse.mul(cameraLookImpulse);
			leftImpulse.mul(cameraLeftImpulse);
			lookImpulseHorizon.mul(cameraLookImpulse);
			leftImpulseHorizon.mul(cameraLeftImpulse);
			//求和
			lookImpulse.add(leftImpulse, PlayerAgent.impulse);
			lookImpulseHorizon.add(leftImpulseHorizon, PlayerAgent.impulseHorizon);
			if (PlayerAgent.impulseHorizon.length() > 1E-5 && mc.player != null) {
				PlayerAgent.impulseHorizon.normalize();
				float    playerYRot        = mc.player.getViewYRot(ThirdPerson.lastPartialTick);
				Vector2d playerLookHorizon = LMath.directionFromRotationDegree(playerYRot).normalize();
				Vector2d playerLeftHorizon = LMath.directionFromRotationDegree(playerYRot - 90).normalize();
				that.forwardImpulse = (float)(PlayerAgent.impulseHorizon.dot(playerLookHorizon));
				that.leftImpulse    = (float)(PlayerAgent.impulseHorizon.dot(playerLeftHorizon));
				if (isMoveSlowly) {
					that.forwardImpulse *= sneakingSpeedBonus;
					that.leftImpulse *= sneakingSpeedBonus;
				}
			}
		}
	}
}
