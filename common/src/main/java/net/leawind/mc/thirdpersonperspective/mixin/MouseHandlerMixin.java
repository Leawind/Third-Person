package net.leawind.mc.thirdpersonperspective.mixin;


import net.leawind.mc.thirdpersonperspective.core.CameraAgent;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * 当玩家尝试转动视角时，
 * <p>
 * 如果是第三人称，则调用转动相机的事件处理函数
 * <p>
 * 如果是第一人称，则调用默认的方法，即直接转动玩家实体
 */
@Mixin(net.minecraft.client.MouseHandler.class)
public abstract class MouseHandlerMixin {
	@Inject(method="turnPlayer()V", at=@At(value="INVOKE", target="Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"),
			locals=LocalCapture.CAPTURE_FAILHARD, cancellable=true)
	private void getLocalX (CallbackInfo ci,
							double d1,
							double d2,
							double d3,
							double d4,
							double d5,
							double d6,
							double d7,
							int m) {
		//		System.out.printf("\r TurnPlayer: %.4f, %.4f, %.4f, %.4f, %.4f, %.4f, %.4f", d1, d2, d3, d4, d5, d6, d7);
		if (CameraAgent.isAvailable() && CameraAgent.isThirdPersonEnabled) {
			double dx = d6, dy = d7;
			if (Minecraft.getInstance().options.invertYMouse().get()) {
				dy = -dy;
			}
			CameraAgent.onCameraTurn(dx, dy);
			ci.cancel();
		}
	}
}
