package net.leawind.mc.thirdperson.mixin;


import net.leawind.mc.thirdperson.core.CameraAgent;
import net.leawind.mc.thirdperson.core.ModOptions;
import net.leawind.mc.thirdperson.event.ModEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value=net.minecraft.client.MouseHandler.class, priority=2000)
public class MouseHandlerMixin {
	@Shadow
	private double accumulatedDX;
	@Shadow
	private double accumulatedDY;

	/**
	 * 在 MouseHandler 尝试转动玩家前，阻止其行为。
	 * <p>
	 * 如果此时正在调整相机，那么不转动玩家，而是触发转动相机事件
	 */
	@Inject(method="turnPlayer()V", at=@At(value="HEAD"), cancellable=true)
	public void turnPlayer (CallbackInfo ci) {
		if (CameraAgent.isAvailable() && ModOptions.isAdjustingCameraOffset()) {
			ModEvents.onAdjustingCameraOffset(accumulatedDX, accumulatedDY);
			accumulatedDX = 0;
			accumulatedDY = 0;
			ci.cancel();
		}
	}
}
