package net.leawind.mc.mixin;


import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.leawind.mc.api.base.GameEvents;
import net.leawind.mc.api.client.events.PreMouseTurnPlayerEvent;
import net.leawind.mc.api.client.events.TurnPlayerEvent;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value=MouseHandler.class, priority=2000)
public class MouseHandlerMixin {
	@Shadow private double accumulatedDX;
	@Shadow private double accumulatedDY;

	/**
	 * 在根据鼠标位移转动玩家前触发
	 * <p>
	 * 如果在事件处理函数中调用了{@link PreMouseTurnPlayerEvent#cancelDefault()}，则后续处理将会取消，好像鼠标没有移动一样。
	 */
	@Inject(method="turnPlayer()V", at=@At(value="HEAD"), cancellable=true)
	public void preMouseTurnPlayer (CallbackInfo ci) {
		if (GameEvents.preMouseTurnPlayer != null) {
			PreMouseTurnPlayerEvent event = new PreMouseTurnPlayerEvent(accumulatedDX, accumulatedDY);
			GameEvents.preMouseTurnPlayer.accept(event);
			if (event.isDefaultCancelled()) {
				// 重置累积变化量
				accumulatedDX = 0;
				accumulatedDY = 0;
				ci.cancel();
			}
		}
	}

	/**
	 * 在根据鼠标位移计算完朝向变化量(dy,dx)后，会调用{@link LocalPlayer#turn}方法来旋转玩家
	 * <p>
	 * 这里可以阻止其旋转玩家。
	 *
	 * @param dy y轴角度（偏航角）变化量
	 * @param dx x轴角度（俯仰角）变化量
	 */
	@WrapWithCondition(method="turnPlayer()V", at=@At(value="INVOKE", target="Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"))
	public boolean turnPlayer (LocalPlayer player, double dy, double dx) {
		if (GameEvents.turnPlayer != null) {
			MouseHandler    that  = (MouseHandler)(Object)this;
			TurnPlayerEvent event = new TurnPlayerEvent(that, player, dx, dy);
			return GameEvents.turnPlayer.apply(event);
		}
		return true;
	}
}
