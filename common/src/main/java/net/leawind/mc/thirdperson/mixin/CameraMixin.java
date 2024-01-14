package net.leawind.mc.thirdperson.mixin;


import net.leawind.mc.thirdperson.core.CameraAgent;
import net.leawind.mc.thirdperson.event.ModEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value=net.minecraft.client.Camera.class, priority=2000)
public abstract class CameraMixin {
	@Unique private boolean l3p$wasFirstPerson = true;

	/**
	 * 如果刚 进入/退出 第三人称视角，则调用相应的事件处理函数
	 *
	 * @param blockGetter  用于获取维度中的方块状态
	 * @param entity       相机所属的实体
	 * @param detached     是否是第三人称视角
	 * @param reversedView 已经被我改成了false
	 */
	@Inject(method="setup", at=@At(value="HEAD"))
	public void setup_head (BlockGetter blockGetter, Entity entity, boolean detached, boolean reversedView, float partialTick, CallbackInfo ci) {
		if (CameraAgent.isAvailable()) {
			boolean isFirstPerson = Minecraft.getInstance().options.getCameraType().isFirstPerson();
			if (l3p$wasFirstPerson && !isFirstPerson) {
				ModEvents.onEnterThirdPerson();
			} else if (!l3p$wasFirstPerson && isFirstPerson) {
				ModEvents.onLeaveThirdPerson();
			}
			l3p$wasFirstPerson = isFirstPerson;
		}
	}

	/**
	 * 插入到 setup 方法中的第一个 move(DDD)V 调用之前
	 * <p>
	 * 调用咱相机代理的 tick 方法，更新相机的朝向、位置等信息
	 * <p>
	 * 该调用位于真正渲染画面之前。
	 * <p>
	 * GameRender#render -> GameRender#renderLevel -> Camera#setup
	 */
	@Inject(method="setup", at=@At(value="INVOKE", target="Lnet/minecraft/client/Camera;move(DDD)V", shift=At.Shift.BEFORE), cancellable=true)
	public void setup_invoke (BlockGetter level, Entity attachedEntity, boolean detached, boolean reversedView, float partialTick, CallbackInfo ci) {
		if (CameraAgent.isAvailable()) {
			if (reversedView) {
				// 咱给它转回去
				Camera camera = (Camera)(Object)this;
				((CameraInvoker)camera).invokeSetRotation(camera.getYRot() + 180.0f, -camera.getXRot());
			}
			ModEvents.onCameraSetup(level, partialTick);
			ci.cancel();
		}
	}
}
