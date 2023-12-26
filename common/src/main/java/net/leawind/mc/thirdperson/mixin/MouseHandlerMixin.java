package net.leawind.mc.thirdperson.mixin;


import net.leawind.mc.thirdperson.ThirdPersonMod;
import net.leawind.mc.thirdperson.core.CameraAgent;
import net.leawind.mc.thirdperson.core.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.MouseHandler.class)
public class MouseHandlerMixin {
	@Shadow
	private double accumulatedDX;
	@Shadow
	private double accumulatedDY;

	@Inject(method="turnPlayer()V", at=@At(value="HEAD"), cancellable=true)
	public void turnPlayer (CallbackInfo ci) {
		if (CameraAgent.isAvailable() && Options.isAdjustingCameraOffset()) {
			ThirdPersonMod.ModEvents.onAdjustingCamera(accumulatedDX, accumulatedDY);
			accumulatedDX = 0;
			accumulatedDY = 0;
			ci.cancel();
		}
	}
}
