package net.leawind.mc.mixin;


import net.leawind.mc.api.base.GameStatus;
import net.leawind.mc.util.annotations.VersionSensitive;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value=LocalPlayer.class, priority=2000)
public class LocalPlayerMixin {
	@VersionSensitive()
	@Inject(method="hasEnoughImpulseToStartSprinting()Z", at=@At("HEAD"), cancellable=true)
	private void lp (CallbackInfoReturnable<Boolean> ci) {
		if (GameStatus.sprintImpulseThreshold >= 0) {
			LocalPlayer that = (LocalPlayer)(Object)this;
			ci.setReturnValue(that.isUnderWater() ? that.input.hasForwardImpulse(): (double)that.input.forwardImpulse >= GameStatus.sprintImpulseThreshold);
			ci.cancel();
		}
	}
}
