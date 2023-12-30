package net.leawind.mc.thirdperson;


import net.leawind.mc.thirdperson.config.Config;
import net.leawind.mc.thirdperson.core.CameraAgent;
import net.leawind.mc.thirdperson.core.PlayerAgent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThirdPersonMod {
	public static final String MOD_ID = "leawind_third_person";
	public static final Logger LOGGER = LoggerFactory.getLogger(ThirdPersonMod.MOD_ID);

	public static void init () {
		Config.init();
		ModKeys.register();
		ModEvents.register();
	}

	public static void temp_KeyboardInputMixin_tick (KeyboardInput that, boolean flag, float amplifier) {
		if (CameraAgent.attachedEntity != CameraAgent.playerEntity) {
			return;
		}
		float    cameraForward        = (that.up ? 1: 0) - (that.down ? 1: 0);
		float    cameraLeft           = (that.left ? 1: 0) - (that.right ? 1: 0);
		Vector3f cameraForwardImpulse = CameraAgent.fakeCamera.getLookVector().mul(1, 0, 1).normalize(cameraForward);
		Vector3f cameraLeftImpulse    = CameraAgent.fakeCamera.getLeftVector().mul(1, 0, 1).normalize(cameraLeft);
		PlayerAgent.absoluteImpulse = new Vector2f(cameraForwardImpulse.x + cameraLeftImpulse.x,
												   cameraForwardImpulse.z + cameraLeftImpulse.z);
		if (PlayerAgent.absoluteImpulse.length() > 1E-5) {
			float    playerRotation  = CameraAgent.playerEntity.getViewYRot(Minecraft.getInstance().getFrameTime());
			Vec3     playerForward3D = Vec3.directionFromRotation(0, playerRotation);
			Vec3     playerLeft3D    = Vec3.directionFromRotation(0, playerRotation - 90);
			Vector2f playerLeft      = new Vector2f((float)playerLeft3D.x, (float)playerLeft3D.z);
			Vector2f playerForward   = new Vector2f((float)playerForward3D.x, (float)playerForward3D.z);
			that.forwardImpulse = PlayerAgent.absoluteImpulse.dot(playerForward);
			that.leftImpulse    = PlayerAgent.absoluteImpulse.dot(playerLeft);
			if (flag) {
				that.forwardImpulse *= amplifier;
				that.leftImpulse *= amplifier;
			}
		}
	}
}
