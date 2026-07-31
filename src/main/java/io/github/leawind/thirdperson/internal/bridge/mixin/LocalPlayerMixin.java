package io.github.leawind.thirdperson.internal.bridge.mixin;

import io.github.leawind.thirdperson.internal.bridge.events.LocalPlayerMovementInputEvent;
import io.github.leawind.thirdperson.internal.bridge.events.LocalPlayerSprintImpulseEvent;
/*? if >1.21 {*/
import net.minecraft.client.player.ClientInput;
/*? } else {*/
/*import net.minecraft.client.player.Input;
*//*? }*/
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/// Adapts vanilla's forward-only sprint input check to camera-relative movement.
@Mixin(LocalPlayer.class)
abstract class LocalPlayerMixin {
  private static final double MOVING_THRESHOLD = 1.0e-5;

  /*? if >1.21 {*/
  @Shadow private ClientInput input;
  /*? } else {*/
  /*@Shadow public Input input;
  *//*? }*/

  /*? if >1.21 {*/
  @Inject(
      method = "aiStep",
      at =
          @At(
              value = "INVOKE",
              target = "Lnet/minecraft/client/player/ClientInput;tick()V",
              shift = Shift.AFTER))
  private void afterMovementInputUpdated(CallbackInfo ci) {
    Vec2 movement = input.getMoveVector();
    var modified =
        LocalPlayerMovementInputEvent.emit(
            (LocalPlayer) (Object) this, movement.x, movement.y);
    ((MovementInputAccessor) input)
        .setMoveVector(new Vec2(modified.leftImpulse(), modified.forwardImpulse()));
  }
  /*? } else {*/
  /*@Inject(
      method = "aiStep",
      at =
          @At(
              value = "INVOKE",
              target = "Lnet/minecraft/client/player/Input;tick(ZF)V",
              shift = Shift.AFTER))
  private void afterMovementInputUpdated(CallbackInfo ci) {
    var modified =
        LocalPlayerMovementInputEvent.emit(
            (LocalPlayer) (Object) this, input.leftImpulse, input.forwardImpulse);
    var accessor = (MovementInputAccessor) input;
    accessor.setLeftImpulse(modified.leftImpulse());
    accessor.setForwardImpulse(modified.forwardImpulse());
  }
  *//*? }*/

  /*? if >1.21 {*/
  @Redirect(
      method = {"canStartSprinting", "shouldStopRunSprinting", "shouldStopSwimSprinting"},
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/client/player/ClientInput;hasForwardImpulse()Z"))
  private boolean useDirectionalSprintInput(ClientInput input) {
    return modifyForwardImpulse(input, input.hasForwardImpulse(), MOVING_THRESHOLD);
  }

  /// Keeps vanilla's pre-tick trigger history on the same predicate as start eligibility.
  @Redirect(
      method = "aiStep",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/client/player/ClientInput;hasForwardImpulse()Z"))
  private boolean useDirectionalSprintTriggerHistory(ClientInput input) {
    return modifyForwardImpulse(input, input.hasForwardImpulse(), MOVING_THRESHOLD);
  }

  private boolean modifyForwardImpulse(
      ClientInput input, boolean vanillaResult, double minimumMagnitude) {
    LocalPlayer player = (LocalPlayer) (Object) this;
    Vec2 movement = input.getMoveVector();
    return LocalPlayerSprintImpulseEvent.emit(
        player, vanillaResult, movement.x, movement.y, minimumMagnitude);
  }
  /*? } else {*/
  /*@Inject(
      method = "hasEnoughImpulseToStartSprinting",
      at = @At("HEAD"),
      cancellable = true)
  private void useDirectionalStartingImpulse(CallbackInfoReturnable<Boolean> cir) {
    LocalPlayer player = (LocalPlayer) (Object) this;
    Input input = player.input;
    Vec2 movement = input.getMoveVector();
    double minimumMagnitude = player.isUnderWater() ? MOVING_THRESHOLD : 0.8;
    if (LocalPlayerSprintImpulseEvent.emit(
        player, false, movement.x, movement.y, minimumMagnitude)) {
      cir.setReturnValue(true);
    }
  }

  @Redirect(
      method = "aiStep",
      at =
          @At(
              value = "INVOKE",
              target = "Lnet/minecraft/client/player/Input;hasForwardImpulse()Z"))
  private boolean keepSprintingWithDirectionalInput(Input input) {
    LocalPlayer player = (LocalPlayer) (Object) this;
    boolean vanillaResult = input.hasForwardImpulse();
    Vec2 movement = input.getMoveVector();
    return LocalPlayerSprintImpulseEvent.emit(
        player, vanillaResult, movement.x, movement.y, MOVING_THRESHOLD);
  }
  *//*? }*/
}
