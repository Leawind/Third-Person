package net.leawind.mc.thirdpersonperspective.mixin;


import net.leawind.mc.thirdpersonperspective.ThirdPersonPerspectiveMod;
import net.leawind.mc.thirdpersonperspective.core.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.MouseHandler.class)
public class MouseHandlerMixin {
	//	@Inject(method="onMove", at=@At("HEAD"), cancellable=true)
	//	private void onMove (long windowHandler, double dx, double dy, CallbackInfo ci) {
	//		ThirdPersonPerspectiveMod.ModEvents.onMoseMove(dx, dy, ci);
	//	}
	@Shadow
	private double accumulatedDX;
	@Shadow
	private double accumulatedDY;

	@Inject(method="turnPlayer()V", at=@At(value="HEAD"), cancellable=true)
	public void turnPlayer (CallbackInfo ci) {
		if (Options.isAdjustingCameraOffset()) {
			ThirdPersonPerspectiveMod.ModEvents.onAdjustingCamera(accumulatedDX, accumulatedDY);
			accumulatedDX = 0;
			accumulatedDY = 0;
			ci.cancel();
		}
	}
}
