package com.github.leawind.thirdperson.mixin;

import com.github.leawind.thirdperson.ThirdPerson;
import com.github.leawind.thirdperson.ThirdPersonStatus;
import com.github.leawind.thirdperson.api.base.GameEvents;
import com.github.leawind.thirdperson.api.client.event.RenderEntityEvent;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = LevelRenderer.class, priority = 2000)
public class LevelRendererMixin {
  @Shadow
  @Final
  private Minecraft minecraft;

  @Shadow
  @Final
  private RenderBuffers renderBuffers;

  @Unique
  private DeltaTracker deltaTracker;
  @Unique
  private final Map<EntityRenderState, Entity> entityMap = new HashMap<>();

  /** 允许取消渲染实体 */
  @Inject(method = "extractVisibleEntities", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"), cancellable = true)
  private void cancelRenderEntity(
          Camera camera, Frustum frustum, DeltaTracker deltaTracker, LevelRenderState renderState, CallbackInfo ci, @Local Entity entity, @Local EntityRenderState entityRenderState) {
    this.deltaTracker = deltaTracker;
    entityMap.put(entityRenderState, entity);
    if (GameEvents.renderEntity != null) {
      float partialTick = deltaTracker.getGameTimeDeltaPartialTick(!minecraft.level.tickRateManager().isEntityFrozen(entity));
      var event = new RenderEntityEvent(entity, partialTick);
      if (!GameEvents.renderEntity.apply(event)) {
        ci.cancel();
      }
    }
  }

  @Inject(method = "submitEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lnet/minecraft/client/renderer/state/CameraRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V", shift = At.Shift.AFTER))
  private void postRenderEntity(
          PoseStack poseStack, LevelRenderState renderState, SubmitNodeCollector nodeCollector, CallbackInfo ci, @Local EntityRenderState entityRenderState) {
    Entity entity = entityMap.remove(entityRenderState);
    if(entity == null) return;

    MultiBufferSource.BufferSource bufferSource = renderBuffers.bufferSource();
    float partialTick = deltaTracker.getGameTimeDeltaPartialTick(!minecraft.level.tickRateManager().isEntityFrozen(entity));

    if (ThirdPerson.isAvailable()
        && ThirdPersonStatus.isRenderingInThirdPerson()
        && entity == ThirdPerson.ENTITY_AGENT.getRawCameraEntity()) {
      if (ThirdPersonStatus.useCameraEntityOpacity(partialTick)
          && ThirdPersonStatus.shouldRenderCameraEntity(partialTick)) {
        bufferSource.endLastBatch();
      }
    }
  }
}
