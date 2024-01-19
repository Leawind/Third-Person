package net.leawind.mc.thirdperson.mixin;


import net.leawind.mc.thirdperson.core.CameraAgent;
import net.leawind.mc.thirdperson.core.ModReferee;
import net.leawind.mc.thirdperson.event.ThirdPersonEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 计算玩家移动方向和速度
 * <p>
 * 第三人称视角下，按下方向键时，玩家的移动方向应由相机朝向和按键决定
 */
@Mixin(value=net.minecraft.client.player.LocalPlayer.class, priority=2000)
public class LocalPlayerMixin {
	@Inject(method="serverAiStep", at=@At(value="TAIL"))
	public void serverAiStep_inject_tail (CallbackInfo ci) {
		if (CameraAgent.isAvailable() && ModReferee.isThirdPerson() && CameraAgent.isControlledCamera()) {
			ThirdPersonEvents.onServerAiStep();
		}
	}
}
