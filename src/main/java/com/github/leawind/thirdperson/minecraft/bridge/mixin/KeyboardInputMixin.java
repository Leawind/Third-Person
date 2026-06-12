package com.github.leawind.thirdperson.minecraft.bridge.mixin;

import com.github.leawind.thirdperson.minecraft.bridge.events.GameClientEvents;
import com.github.leawind.thirdperson.minecraft.bridge.events.context.MoveImpulseContext;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.player.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value = KeyboardInput.class, priority = 2000)
public class KeyboardInputMixin {
  @ModifyArgs(
      method = "tick",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec2;<init>(FF)V"))
  private void postTick(
      Args args, @Local(ordinal = 0) float forwardImpulse, @Local(ordinal = 1) float leftImpulse) {
    var ctx = new MoveImpulseContext(forwardImpulse, leftImpulse);
    GameClientEvents.MODIFY_MOVE_IMPULSE.emit(ctx);
    args.setAll(ctx.leftImpulse, ctx.forwardImpulse);
  }
}
