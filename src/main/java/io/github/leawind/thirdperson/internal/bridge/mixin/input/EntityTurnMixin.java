package io.github.leawind.thirdperson.internal.bridge.mixin.input;

import io.github.leawind.thirdperson.internal.bridge.events.LocalPlayerTurnEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
abstract class EntityTurnMixin {
  @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
  private void beforeTurn(double rawYaw, double rawPitch, CallbackInfo ci) {
    Entity entity = (Entity) (Object) this;
    LocalPlayer player = Minecraft.getInstance().player;
    if (entity == player && LocalPlayerTurnEvent.emit(player, rawYaw, rawPitch)) {
      ci.cancel();
    }
  }
}
