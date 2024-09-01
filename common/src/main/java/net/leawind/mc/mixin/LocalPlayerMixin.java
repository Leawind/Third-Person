package net.leawind.mc.mixin;


import net.leawind.mc.thirdperson.ThirdPerson;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value=LocalPlayer.class, priority=2000)
public class LocalPlayerMixin {
	@Shadow protected int sprintTriggerTime;

	@Inject(method="aiStep()V", at=@At("HEAD"))
	private void preAiStep (CallbackInfo ci) {
		var config = ThirdPerson.getConfig();
		if (config.is_mod_enable && !ThirdPerson.mc.options.getCameraType().isFirstPerson() && !config.allow_double_tap_sprint) {
			sprintTriggerTime = 0;
		}
	}
}
