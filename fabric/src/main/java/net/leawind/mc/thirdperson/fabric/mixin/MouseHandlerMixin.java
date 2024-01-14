package net.leawind.mc.thirdperson.fabric.mixin;


import net.leawind.mc.thirdperson.core.CameraAgent;
import net.leawind.mc.thirdperson.core.ModReferee;
import net.leawind.mc.thirdperson.event.ModEvents;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * turnPlayer 方法会根据之前积累的鼠标移动来旋转玩家。
 * <p>
 * 当处于第三人称时，应当阻止其旋转玩家，并让它旋转相机的角度。
 */
@Mixin(net.minecraft.client.MouseHandler.class)
public class MouseHandlerMixin {
	/**
	 * 在计算完dx，dy之后，原本会调用LocalPlayer.turn方法来旋转玩家
	 * <p>
	 * 这里截胡该方法的调用，并旋转咱的相机，然后立即让该函数返回。
	 *
	 * @param dx x轴角度（俯仰角）变化量
	 * @param dy y轴角度（偏航角）变化量
	 */
	@Inject(method="turnPlayer()V", at=@At(value="INVOKE", target="Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"), locals=LocalCapture.CAPTURE_FAILHARD, cancellable=true)
	private void turnPlayerInject (CallbackInfo ci, double d1, double dx, double dy, int m) {
		if (CameraAgent.isAvailable() && ModReferee.isThirdPerson()) {
			if (Minecraft.getInstance().options.invertYMouse().get()) {
				dy = -dy;
			}
			ModEvents.onCameraTurn(dx, dy);
			ci.cancel();
		}
	}
}
