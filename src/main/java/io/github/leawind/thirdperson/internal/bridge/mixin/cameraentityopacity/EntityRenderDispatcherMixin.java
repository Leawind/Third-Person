package io.github.leawind.thirdperson.internal.bridge.mixin.cameraentityopacity;

import io.github.leawind.thirdperson.internal.bridge.CameraEntityRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
/*? if >=1.21.11 {*/
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
/*? if >=26.1 {*/
import net.minecraft.client.renderer.state.level.CameraRenderState;
/*? } else {*/
/*import net.minecraft.client.renderer.state.CameraRenderState;
*//*? }*/
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/*? }*/
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityRenderDispatcher.class)
abstract class EntityRenderDispatcherMixin {
  /*? if >=1.21.11 {*/
  @Inject(method = "submit", at = @At("HEAD"), cancellable = true)
  private void beforeSubmitCameraEntity(
      EntityRenderState renderState,
      CameraRenderState camera,
      double x,
      double y,
      double z,
      PoseStack poseStack,
      SubmitNodeCollector nodeCollector,
      CallbackInfo ci) {
    if (!CameraEntityRenderState.isCameraEntityRenderState(renderState)) {
      return;
    }
    Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
    if (cameraEntity == null) {
      return;
    }
    if (CameraEntityRenderState.begin(partialTick(cameraEntity))) {
      CameraEntityRenderState.end();
      ci.cancel();
    }
  }

  @Inject(method = "submit", at = @At("TAIL"))
  private void afterSubmitCameraEntity(
      EntityRenderState renderState,
      CameraRenderState camera,
      double x,
      double y,
      double z,
      PoseStack poseStack,
      SubmitNodeCollector nodeCollector,
      CallbackInfo ci) {
    if (CameraEntityRenderState.isCameraEntityRenderState(renderState)) {
      CameraEntityRenderState.end();
    }
  }

  private static float partialTick(Entity entity) {
    Minecraft minecraft = Minecraft.getInstance();
    return minecraft
        .getDeltaTracker()
        .getGameTimeDeltaPartialTick(
            minecraft.level != null && !minecraft.level.tickRateManager().isEntityFrozen(entity));
  }
  /*? }*/
}
