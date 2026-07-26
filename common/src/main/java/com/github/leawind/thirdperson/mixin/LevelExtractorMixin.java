package com.github.leawind.thirdperson.mixin;

import com.github.leawind.thirdperson.api.base.GameEvents;
import com.github.leawind.thirdperson.api.client.event.RenderEntityEvent;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(value = LevelExtractor.class, priority = 2000)
public class LevelExtractorMixin {
  @Shadow @Final private Minecraft minecraft;

  /** 允许取消渲染实体 */
  @Inject(
      method = "extractVisibleEntities",
      at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"),
      cancellable = true)
  private void cancelRenderEntity(
      Camera camera,
      Frustum frustum,
      DeltaTracker deltaTracker,
      LevelRenderState output,
      CallbackInfo ci,
      @Local Entity entity) {
    if (GameEvents.renderEntity != null) {
      float partialTick =
          deltaTracker.getGameTimeDeltaPartialTick(
              !minecraft.level.tickRateManager().isEntityFrozen(entity));
      var event = new RenderEntityEvent(entity, partialTick);
      if (!GameEvents.renderEntity.apply(event)) {
        ci.cancel();
      }
    }
  }
}
