package net.leawind.mc.thirdpersonperspective.mixin;


import net.leawind.mc.thirdpersonperspective.core.CameraAgent;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.Camera.class)
public abstract class CameraMixin {
	@Unique
	private boolean ltpv$wasFirstPerson = true;

	/**
	 * 如果刚进入第三人称视角，则调用相应的事件处理函数
	 *
	 * @param blockGetter 用于获取维度中的方块状态
	 * @param entity      相机所属的实体
	 * @param detached    是否是第三人称视角
	 * @param isMirrored  已经被我改成了false
	 */
	@Inject(method="setup", at=@At(value="HEAD"))
	public void setup_head (BlockGetter blockGetter,
							Entity entity,
							boolean detached,
							boolean isMirrored,
							float lerpK,
							CallbackInfo ci) {
		if (CameraAgent.isAvailable()) {
			boolean isFirstPerson = Minecraft.getInstance().options.getCameraType().isFirstPerson();
			CameraAgent.isThirdPerson = false;
			if (ltpv$wasFirstPerson && !isFirstPerson) {
				CameraAgent.onEnterThirdPerson(lerpK);
			}
			ltpv$wasFirstPerson = isFirstPerson;
		}
	}

	/**
	 * 调用咱相机代理的 tick 方法，更新相机的朝向、位置等信息
	 */
	@Inject(method="setup", at=@At(value="INVOKE", target="Lnet/minecraft/client/Camera;move(DDD)V", shift=At.Shift.BEFORE),
			cancellable=true)
	public void setup_inject (BlockGetter level,
							  Entity entity,
							  boolean detached,
							  boolean isMirrored,
							  float lerpK,
							  CallbackInfo ci) {
		Camera camera = (Camera)(Object)this;
		if (isMirrored) {//TODO
			((CameraInvoker)this).invokeSetRotation(camera.getYRot() + 180.0f, -camera.getXRot());
		}
		if (CameraAgent.isAvailable()) {
			CameraAgent.isThirdPerson = true;
			CameraAgent.onRenderTick(level, entity, isMirrored, lerpK);
			ci.cancel();
		}
	}
}
