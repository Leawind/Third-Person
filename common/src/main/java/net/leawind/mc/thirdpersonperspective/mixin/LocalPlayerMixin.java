package net.leawind.mc.thirdpersonperspective.mixin;


import net.leawind.mc.thirdpersonperspective.core.CameraAgent;
import net.leawind.mc.thirdpersonperspective.core.PlayerAgent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 计算玩家移动方向和速度
 * <p>
 * 第三人称视角下，按下方向键时，玩家的移动方向可能需要和默认行为不一样。
 */
@Mixin(net.minecraft.client.player.LocalPlayer.class)
public class LocalPlayerMixin {
	@Inject(method="serverAiStep", at=@At(value="TAIL"))
	public void serverAiStep (CallbackInfo ci) {
		if (!CameraAgent.isAvailable() || !PlayerAgent.isAvailable()) {
			return;
		}
		if (!CameraAgent.isThirdPersonEnabled) {
			return;
		}
		PlayerAgent.onServerAiStep();
	}
}
