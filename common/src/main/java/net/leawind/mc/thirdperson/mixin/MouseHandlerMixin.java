package net.leawind.mc.thirdperson.mixin;


import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.ThirdPersonEvents;
import net.leawind.mc.thirdperson.ThirdPersonStatus;
import net.leawind.mc.util.math.vector.api.Vector2d;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value=MouseHandler.class, priority=2000)
public class MouseHandlerMixin {
	@Shadow private double accumulatedDX;
	@Shadow private double accumulatedDY;

	/**
	 * 在 MouseHandler 尝试转动玩家前，阻止其行为。
	 * <p>
	 * 如果此时相机跟随玩家旋转，那么不作更改
	 * <p>
	 * 如果此时正在调整相机，那么不转动玩家，而是触发转动相机事件
	 * <p>
	 * 处理完后要重置累积变化量（accumulatedDX|Y）
	 */
	@Inject(method="turnPlayer()V", at=@At(value="HEAD"), cancellable=true)
	public void turnPlayer_head (CallbackInfo ci) {
		if (ThirdPerson.isAvailable() && ThirdPersonStatus.isAdjustingCameraOffset() && !ThirdPersonStatus.shouldCameraTurnWithEntity()) {
			ThirdPersonEvents.onAdjustingCameraOffset(Vector2d.of(accumulatedDX, accumulatedDY));
			accumulatedDX = 0;
			accumulatedDY = 0;
			ci.cancel();
		}
	}

	/**
	 * 在计算完dx，dy之后，原本会调用LocalPlayer.turn方法来旋转玩家
	 * <p>
	 * 这里可以重定向该方法，改成旋转咱的相机
	 *
	 * @param dx x轴角度（俯仰角）变化量
	 * @param dy y轴角度（偏航角）变化量
	 */
	@WrapWithCondition(method="turnPlayer()V", at=@At(value="INVOKE", target="Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"))
	public boolean turnPlayer_invoke (LocalPlayer instance, double dy, double dx) {
		if (ThirdPerson.isAvailable() && ThirdPersonStatus.isRenderingInThirdPerson() && !ThirdPersonStatus.shouldCameraTurnWithEntity()) {
			ThirdPerson.CAMERA_AGENT.onCameraTurn(dy, dx);
		}
		return true;
	}
}
