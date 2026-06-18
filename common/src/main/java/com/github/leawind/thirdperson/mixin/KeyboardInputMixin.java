package com.github.leawind.thirdperson.mixin;

import com.github.leawind.thirdperson.api.base.GameEvents;
import com.github.leawind.thirdperson.api.client.event.CalculateMoveImpulseEvent;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.player.KeyboardInput;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@SuppressWarnings("unused")
@Mixin(value = KeyboardInput.class, priority = 2000)
public class KeyboardInputMixin {
  /** 注入到tick的末尾，重新计算 leftImpulse 和 forwardImpulse 的值 */
  @ModifyArgs(
      method = "tick",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec2;<init>(FF)V"))
  @PerformanceSensitive
  private void postTick(
      Args args, @Local(ordinal = 0) float forwardImpulse, @Local(ordinal = 1) float leftImpulse) {
    var that = ((KeyboardInput) (Object) this);
    if (GameEvents.calculateMoveImpulse != null) {
      var event = new CalculateMoveImpulseEvent(that);

      event.forwardImpulse = forwardImpulse;
      event.leftImpulse = leftImpulse;

      GameEvents.calculateMoveImpulse.accept(event);

      args.setAll(event.leftImpulse, event.forwardImpulse);
    }
  }
}
