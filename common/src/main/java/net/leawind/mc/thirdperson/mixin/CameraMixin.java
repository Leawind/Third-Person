package net.leawind.mc.thirdperson.mixin;


import net.leawind.mc.api.client.ApiUtils;
import net.leawind.mc.api.client.events.CameraSetupEvent;
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
	 * 插入到 setup 方法中的第一个 move(DDD)V 调用之前
	 * <p>
	 * 该调用位于真正渲染画面之前。
	 * <p>
	 * GameRender#render -> GameRender#renderLevel -> Camera#setup
	 */
	@Inject(method="setup", at=@At(value="INVOKE", target="Lnet/minecraft/client/Camera;move(DDD)V", shift=At.Shift.BEFORE), cancellable=true)
	public void setup_invoke (BlockGetter level, Entity attachedEntity, boolean detached, boolean reversedView, float partialTick, CallbackInfo ci) {
		if (ApiUtils.setupCamera != null) {
			CameraSetupEvent event = new CameraSetupEvent(level, attachedEntity, detached, reversedView, partialTick);
			ApiUtils.setupCamera.accept(event);
			if (event.set) {
				Camera camera = (Camera)(Object)this;
				((CameraInvoker)camera).invokeSetPosition(event.pos);
				((CameraInvoker)camera).invokeSetRotation(event.yRot, event.xRot);
				ci.cancel();
			}
		}
		//		if (ThirdPerson.isAvailable() && ThirdPersonStatus.isRenderingInThirdPerson()) {
		//			ThirdPersonEvents.onCameraSetup(level, partialTick);
		//			ci.cancel();
		//		}
	}
}
