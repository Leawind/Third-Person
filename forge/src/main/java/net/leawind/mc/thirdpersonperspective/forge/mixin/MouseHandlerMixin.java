package net.leawind.mc.thirdpersonperspective.forge.mixin;


import net.leawind.mc.thirdpersonperspective.core.CameraAgent;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(net.minecraft.client.MouseHandler.class)
public class MouseHandlerMixin {
	@Inject(method="turnPlayer()V", at=@At(value="INVOKE", target="Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"),
			locals=LocalCapture.CAPTURE_FAILHARD, cancellable=true)
	private void turnPlayerInject (CallbackInfo ci,
								   double d1,
								   double d2,
								   double d3,
								   double d4,
								   double d5,
								   double dx,
								   double dy,
								   int m) {
		if (CameraAgent.isAvailable() && CameraAgent.isThirdPerson) {
			if (Minecraft.getInstance().options.invertYMouse().get()) {
				dy = -dy;
			}
			CameraAgent.onCameraTurn(dx, dy);
			ci.cancel();
		}
	}
}
