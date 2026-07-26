package io.github.leawind.thirdperson.internal.bridge.mixin;

import io.github.leawind.thirdperson.internal.bridge.events.LocalPlayerTurnEvent;
import io.github.leawind.thirdperson.internal.bridge.events.LocalPlayerMovementYawEvent;
import io.github.leawind.thirdperson.internal.integration.perspective.PerspectiveGuard;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
abstract class EntityMixin {
  @ModifyArg(
      method = "moveRelative",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/world/entity/Entity;getInputVector(Lnet/minecraft/world/phys/Vec3;FF)Lnet/minecraft/world/phys/Vec3;"),
      index = 2)
  private float modifyMovementYaw(float vanillaYaw) {
    Minecraft minecraft = Minecraft.getInstance();
    if ((Object) this != minecraft.player
        || ((Entity) (Object) this).isPassenger()
        || !PerspectiveGuard.isThirdPersonCurrent()) {
      return vanillaYaw;
    }
    return LocalPlayerMovementYawEvent.emit(vanillaYaw);
  }

  @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
  private void beforeTurn(double rawYaw, double rawPitch, CallbackInfo ci) {
    Minecraft minecraft = Minecraft.getInstance();
    if ((Object) this != minecraft.player
        || ((Entity) (Object) this).isPassenger()
        || !PerspectiveGuard.isThirdPersonCurrent()) {
      return;
    }
    if (LocalPlayerTurnEvent.emit(rawYaw, rawPitch)) {
      ci.cancel();
    }
  }
}
