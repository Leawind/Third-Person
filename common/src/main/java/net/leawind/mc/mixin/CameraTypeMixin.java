package net.leawind.mc.mixin;


import net.leawind.mc.api.base.GameStatus;
import net.leawind.mc.thirdperson.ThirdPerson;
import net.minecraft.client.CameraType;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value=CameraType.class, priority=2000)
public class CameraTypeMixin {
	@Final @Shadow public boolean firstPerson;

	@Inject(method="isFirstPerson", at=@At("RETURN"), cancellable=true)
	private void isFirstPerson (@NotNull CallbackInfoReturnable<Boolean> ci) {
		if (ThirdPerson.isAvailable()) {
			ci.setReturnValue(firstPerson ^ GameStatus.isPerspectiveInverted);
			ci.cancel();
		}
	}

	@Inject(method="cycle", at=@At("RETURN"), cancellable=true)
	private void cycle (CallbackInfoReturnable<CameraType> ci) {
		if (ThirdPerson.getConfig().is_mod_enable) {
			CameraType that = (CameraType)(Object)this;
			if (that != CameraType.FIRST_PERSON) {
				ci.setReturnValue(CameraType.FIRST_PERSON);
				ci.cancel();
			}
		}
	}
}
