package com.github.leawind.thirdperson.minecraft.bridge.mixin;

import com.github.leawind.thirdperson.minecraft.bridge.events.GameClientEvents;
import com.github.leawind.thirdperson.minecraft.bridge.events.context.MouseTurnPlayerStartContext;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(value = MouseHandler.class, priority = 2000)
public class MouseHandlerMixin {
  @Shadow private double accumulatedDX;
  @Shadow private double accumulatedDY;

  /// 在根据鼠标位移转动玩家前触发
  @Inject(method = "turnPlayer(D)V", at = @At(value = "HEAD"), cancellable = true)
  private void preTurnPlayer(CallbackInfo ci) {
    var ctx = new MouseTurnPlayerStartContext(accumulatedDX, accumulatedDY);
    GameClientEvents.MOUSE_TURN_PLAYER_START.emit(ctx);
    if (ctx.cancelDefault) {
      // 重置累积变化量
      accumulatedDX = 0;
      accumulatedDY = 0;
      ci.cancel();
    }
  }
}
