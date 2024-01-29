package net.leawind.mc.thirdperson.mixin;


import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.event.ThirdPersonEvents;
import net.leawind.mc.util.api.math.vector.Vector2d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value=net.minecraft.client.MouseHandler.class, priority=2000)
public class MouseHandlerMixin {
	@Shadow private double accumulatedDX;
	@Shadow private double accumulatedDY;

	/**
	 * 在 MouseHandler 尝试转动玩家前，阻止其行为。
	 * <p>
	 * 如果此时正在调整相机，那么不转动玩家，而是触发转动相机事件
	 * <p>
	 * 处理完后要重置累积变化量（accumulatedDX|Y）
	 */
	@Inject(method="turnPlayer()V", at=@At(value="HEAD"), cancellable=true)
	public void turnPlayer (CallbackInfo ci) {
		if (ThirdPerson.isAvailable() && ThirdPerson.isAdjustingCameraOffset()) {
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
	@Redirect(method="turnPlayer()V", at=@At(value="INVOKE", target="Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"))
	private void turnPlayerInject (LocalPlayer instance, double dy, double dx) {
		if (ThirdPerson.isAvailable() && ThirdPerson.isThirdPerson()) {
			ThirdPerson.CAMERA_AGENT.onCameraTurn(dy, dx);
		} else {
			assert Minecraft.getInstance().player != null;
			Minecraft.getInstance().player.turn(dy, dx);
		}
	}
}
