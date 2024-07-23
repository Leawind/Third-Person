package net.leawind.mc.forge.thirdperson;


import net.leawind.mc.api.base.GameEvents;
import net.leawind.mc.api.client.events.ThirdPersonCameraSetupEvent;
import net.leawind.mc.mixin.CameraInvoker;
import net.minecraft.client.Camera;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ThirdPersonEventsForge {
	@SubscribeEvent(priority=EventPriority.LOW)
	public static void cameraSetupEvent (ViewportEvent.ComputeCameraAngles event) {
		if (GameEvents.thirdPersonCameraSetup != null) {
			Camera                      camera = event.getCamera();
			ThirdPersonCameraSetupEvent evt    = new ThirdPersonCameraSetupEvent(camera.getEntity(), (float)event.getPartialTick());
			GameEvents.thirdPersonCameraSetup.accept(evt);
			if (evt.set()) {
				event.setYaw(evt.yRot);
				event.setPitch(evt.xRot);
				((CameraInvoker)camera).invokeSetPosition(evt.pos);
			}
		}
	}
}
