package net.leawind.mc.thirdperson;


import net.leawind.mc.thirdperson.core.CameraAgent;
import net.leawind.mc.thirdperson.core.ModReferee;
import net.leawind.mc.thirdperson.core.PlayerAgent;
import net.leawind.mc.util.vector.Vector2d;
import net.leawind.mc.util.vector.Vector3d;
import net.leawind.mc.util.vector.Vectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * TODO Mixin
 */
public class MixinProxy {
	public static Vec3 storeViewVector (Vec3 viewVectorFake) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null) {
			return viewVectorFake;
		}
		if (CameraAgent.isAvailable() && ModReferee.isThirdPerson()) {
			Vec3      eyePosition    = mc.player.getEyePosition(1);
			HitResult hr             = CameraAgent.pick();
			Vec3      eyeToHitResult = eyePosition.vectorTo(hr.getLocation());
			return eyeToHitResult.normalize();
		}
		return viewVectorFake;
	}

	public static void recalculateImpulse (KeyboardInput that, boolean isMoveSlowly, float sneakingSpeedBonus, CallbackInfo ci) {
		//				//				if (mc.player.isSwimming()) {
		//				//					PlayerAgent.turnToDirection(PlayerAgent.absoluteImpulse, true);
		//				//					that.forwardImpulse = (float)PlayerAgent.absoluteImpulse.length();
		//				//				} else {
		if (CameraAgent.isAvailable() && ModReferee.isThirdPerson() && CameraAgent.isControlledCamera()) {
			Minecraft mc                = Minecraft.getInstance();
			double    cameraLookImpulse = (that.up ? 1: 0) - (that.down ? 1: 0);
			double    cameraLeftImpulse = (that.left ? 1: 0) - (that.right ? 1: 0);
			// 方向向量 != 0
			Vector3d lookImpulse        = Vectors.toVector3d(CameraAgent.fakeCamera.getLookVector()).normalize();
			Vector3d leftImpulse        = Vectors.toVector3d(CameraAgent.fakeCamera.getLeftVector()).normalize();
			Vector2d lookImpulseHorizon = new Vector2d(lookImpulse.x, lookImpulse.z).normalize();
			Vector2d leftImpulseHorizon = new Vector2d(leftImpulse.x, leftImpulse.z).normalize();
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
				float    playerRotation    = mc.player.getViewYRot(PlayerAgent.lastPartialTick);
				Vector2d playerLookHorizon = Vectors.directionFromRotationDegree(playerRotation).normalize();
				Vector2d playerLeftHorizon = Vectors.directionFromRotationDegree(playerRotation - 90).normalize();
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
