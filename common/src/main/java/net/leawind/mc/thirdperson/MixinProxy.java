package net.leawind.mc.thirdperson;


import net.leawind.mc.thirdperson.core.CameraAgent;
import net.leawind.mc.thirdperson.core.PlayerAgent;
import net.leawind.mc.thirdperson.mixin.CameraInvoker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * TODO mixin proxy
 */
public class MixinProxy {
	public static void tick_KeyboardInputMixin (KeyboardInput that, boolean flag, float sneakingSpeedBonus, CallbackInfo ci) {
		if (CameraAgent.isAvailable() && CameraAgent.isThirdPerson() && CameraAgent.isControlledCamera()) {
			Minecraft mc            = Minecraft.getInstance();
			float     cameraForward = (that.up ? 1: 0) - (that.down ? 1: 0);
			float     cameraLeft    = (that.left ? 1: 0) - (that.right ? 1: 0);
			if (Float.isNaN(CameraAgent.camera.getLookVector().x)) {//TODO Why sometimes it is NaN ?
				((CameraInvoker)CameraAgent.camera).invokeSetRotation(CameraAgent.camera.getYRot(),
																	  CameraAgent.camera.getXRot());
			}
			Vector3f cameraForwardImpulse = CameraAgent.camera.getLookVector().mul(1, 0, 1).normalize(cameraForward);
			Vector3f cameraLeftImpulse    = CameraAgent.camera.getLeftVector().mul(1, 0, 1).normalize(cameraLeft);
			PlayerAgent.absoluteImpulse.set(cameraForwardImpulse.x + cameraLeftImpulse.x,
											cameraForwardImpulse.z + cameraLeftImpulse.z);
			if (PlayerAgent.absoluteImpulse.length() > 1E-5 && mc.player != null) {
				float    playerRotation  = mc.player.getViewYRot(Minecraft.getInstance().getFrameTime());
				Vec3     playerForward3D = Vec3.directionFromRotation(0, playerRotation);
				Vec3     playerLeft3D    = Vec3.directionFromRotation(0, playerRotation - 90);
				Vector2f playerForward   = new Vector2f((float)playerForward3D.x, (float)playerForward3D.z);
				Vector2f playerLeft      = new Vector2f((float)playerLeft3D.x, (float)playerLeft3D.z);
				that.forwardImpulse = PlayerAgent.absoluteImpulse.dot(playerForward) / playerForward.length();
				that.leftImpulse    = PlayerAgent.absoluteImpulse.dot(playerLeft) / playerLeft.length();
				if (flag) {
					that.forwardImpulse *= sneakingSpeedBonus;
					that.leftImpulse *= sneakingSpeedBonus;
				}
			}
		}
	}
}
