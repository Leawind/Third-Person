package net.leawind.mc.thirdpersonperspective.mixin;


import net.leawind.mc.thirdpersonperspective.core.PlayerAgent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * handleKeybinds 方法中会处理各种按键事件， 其中包括鼠标使用、攻击、选取按键
 * <p>
 * 在这之前，我要立即将玩家转向准星所指的方向， 并通过调用 gameRender.pick 方法来更新玩家注视着的目标 (minecraft.hitResult)
 */
@Mixin(net.minecraft.client.Minecraft.class)
public class MinecraftMixin {
	@Inject(method="handleKeybinds", at=@At(value="HEAD"))
	private void handleKeybinds (CallbackInfo ci) {
		if (PlayerAgent.isAvailable()) {
			PlayerAgent.onBeforeHandleKeybinds();
		}
	}
}