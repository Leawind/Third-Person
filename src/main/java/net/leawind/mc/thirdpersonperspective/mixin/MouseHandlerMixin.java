package net.leawind.mc.thirdpersonperspective.mixin;

import net.leawind.mc.thirdpersonperspective.agent.CameraAgent;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 当玩家尝试转动视角时，
 * <p>
 * 如果是第三人称，则调用转动相机的事件处理函数
 * <p>
 * 如果是第一人称，则调用默认的方法，即直接转动玩家实体
 */
@Mixin(net.minecraft.client.MouseHandler.class)
public abstract class MouseHandlerMixin {
	@Redirect(method="turnPlayer", at=@At(value="INVOKE", target="Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"))
	public void inject_turnPlayer(LocalPlayer player, double y, double x){
		if(CameraAgent.isAvailable() && CameraAgent.getInstance().isFreeTpv){
			CameraAgent.getInstance().onCameraTurn(y, x);
		}else{
			player.turn(y, x);
		}
	}
}
