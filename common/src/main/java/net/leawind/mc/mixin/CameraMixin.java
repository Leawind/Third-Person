package net.leawind.mc.mixin;


import net.leawind.mc.api.base.GameEvents;
import net.leawind.mc.api.client.events.ThirdPersonCameraSetupEvent;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value=Camera.class, priority=2000)
public abstract class CameraMixin {
	/**
	 * setup 方法中第三人称下移动相机之前
	 * <p>
	 * setup 方法位于真正渲染画面之前。
	 * <p>
	 * GameRender#render -> GameRender#renderLevel -> Camera#setup
	 */
	@Inject(method="setup", at=@At(value="INVOKE", target="Lnet/minecraft/client/Camera;move(DDD)V", shift=At.Shift.BEFORE), cancellable=true)
	public void preMoveCamera (BlockGetter level, Entity attachedEntity, boolean detached, boolean reversedView, float partialTick, CallbackInfo ci) {
		if (GameEvents.thirdPersonCameraSetup != null) {
			ThirdPersonCameraSetupEvent event = new ThirdPersonCameraSetupEvent(level, attachedEntity, detached, reversedView, partialTick);
			GameEvents.thirdPersonCameraSetup.accept(event);
			if (event.set()) {
				Camera camera = (Camera)(Object)this;
				((CameraInvoker)camera).invokeSetPosition(event.pos);
				((CameraInvoker)camera).invokeSetRotation(event.yRot, event.xRot);
				ci.cancel();
			}
		}
	}
}
