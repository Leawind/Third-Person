package net.leawind.mc.thirdpersonperspective.mixin;


import net.leawind.mc.thirdpersonperspective.agent.CameraAgent;
import net.leawind.mc.thirdpersonperspective.agent.LocalPlayerAgent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
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
		LocalPlayer player = (LocalPlayer)(Object)this;
		if (Minecraft.getInstance().getCameraEntity() != player) {
			return;
		}
		if (!CameraAgent.isAvailable()) {
			return;
		}
		if (!LocalPlayerAgent.isAvailable()) {
			return;
		}
		if (!CameraAgent.getInstance().isFreeTpv) {
			return;
		}
		LocalPlayerAgent playerAgent = LocalPlayerAgent.getInstance();
		playerAgent.onServerAiStep();
	}
}
