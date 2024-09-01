package net.leawind.mc.mixin;


import net.leawind.mc.thirdperson.ThirdPerson;
import net.minecraft.client.Minecraft;
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
		if (!Minecraft.getInstance().options.getCameraType().isFirstPerson() && !ThirdPerson.getConfig().allow_double_tap_sprint) {
			sprintTriggerTime = 0;
		}
	}
}
