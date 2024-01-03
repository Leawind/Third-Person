package net.leawind.mc.thirdperson.mixin;


import net.leawind.mc.thirdperson.core.CameraAgent;
import net.leawind.mc.thirdperson.core.PlayerAgent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 计算玩家移动方向和速度
 * <p>
 * 第三人称视角下，按下方向键时，玩家的移动方向应由相机朝向和按键决定
 */
@Mixin(net.minecraft.client.player.LocalPlayer.class)
public class LocalPlayerMixin {
	@Inject(method="serverAiStep", at=@At(value="TAIL"))
	public void serverAiStep_inject_tail (CallbackInfo ci) {
		if (CameraAgent.isAvailable() && CameraAgent.isThirdPerson() && CameraAgent.isControlledCamera()) {
			PlayerAgent.onServerAiStep();
		}
	}
}
