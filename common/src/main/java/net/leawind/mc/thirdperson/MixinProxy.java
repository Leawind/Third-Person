package net.leawind.mc.thirdperson;


import net.leawind.mc.thirdperson.core.CameraAgent;
import net.leawind.mc.thirdperson.core.PlayerAgent;
import net.leawind.mc.util.Vectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3f;
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
		if (CameraAgent.isAvailable() && CameraAgent.isThirdPerson()) {
			Vec3      eyePosition    = mc.player.getEyePosition(1);
			HitResult hr             = CameraAgent.pick();
			Vec3      eyeToHitResult = eyePosition.vectorTo(hr.getLocation());
			return eyeToHitResult.normalize();
		}
		return viewVectorFake;
	}

	public static void tick_KeyboardInputMixin (KeyboardInput that, boolean isMoveSlowly, float sneakingSpeedBonus, CallbackInfo ci) {
		if (CameraAgent.isAvailable() && CameraAgent.isThirdPerson() && CameraAgent.isControlledCamera()) {
			Minecraft mc                   = Minecraft.getInstance();
			float     cameraForward        = (that.up ? 1: 0) - (that.down ? 1: 0);
			float     cameraLeft           = (that.left ? 1: 0) - (that.right ? 1: 0);
			Vector3f  cameraForwardImpulse = CameraAgent.fakeCamera.getLookVector().mul(1, 0, 1).normalize(cameraForward);
			Vector3f  cameraLeftImpulse    = CameraAgent.fakeCamera.getLeftVector().mul(1, 0, 1).normalize(cameraLeft);
			PlayerAgent.horizonalAbsoluteImpulse.set(cameraForwardImpulse.x + cameraLeftImpulse.x, cameraForwardImpulse.z + cameraLeftImpulse.z);
			if (PlayerAgent.horizonalAbsoluteImpulse.length() > 1E-5 && mc.player != null) {
				float    playerRotation  = mc.player.getViewYRot(PlayerAgent.lastPartialTick);
				Vector3d playerForward3D = Vectors.directionFromRotationDegree(0, playerRotation);
				Vector3d playerLeft3D    = Vectors.directionFromRotationDegree(0, playerRotation - 90);
				Vector2d playerForward   = new Vector2d(playerForward3D.x, playerForward3D.z).normalize();
				Vector2d playerLeft      = new Vector2d(playerLeft3D.x, playerLeft3D.z).normalize();
				that.forwardImpulse = (float)(PlayerAgent.horizonalAbsoluteImpulse.dot(playerForward));
				that.leftImpulse    = (float)(PlayerAgent.horizonalAbsoluteImpulse.dot(playerLeft));
				if (isMoveSlowly) {
					that.forwardImpulse *= sneakingSpeedBonus;
					that.leftImpulse *= sneakingSpeedBonus;
				}
			}
		}
	}
}
