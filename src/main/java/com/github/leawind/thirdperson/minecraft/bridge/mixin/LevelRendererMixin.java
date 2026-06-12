package com.github.leawind.thirdperson.minecraft.bridge.mixin;

import com.github.leawind.thirdperson.minecraft.bridge.EntityOpacityAccessor;
import com.github.leawind.thirdperson.minecraft.bridge.events.GameClientEvents;
import com.github.leawind.thirdperson.minecraft.bridge.events.context.ExtractVisibleEntitiesContext;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
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

@SuppressWarnings("unused")
@Mixin(value = LevelRenderer.class, priority = 2000)
public class LevelRendererMixin {
  @Shadow @Final private Minecraft minecraft;

  @Shadow @Final private RenderBuffers renderBuffers;

  @Unique private DeltaTracker deltaTracker;
  @Unique private final Map<EntityRenderState, Entity> entityMap = new HashMap<>();

  /** 允许取消渲染实体 */
  @Inject(
      method = "extractVisibleEntities",
      at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"),
      cancellable = true)
  private void cancelRenderEntity(
      Camera camera,
      Frustum frustum,
      DeltaTracker deltaTracker,
      LevelRenderState levelRenderState,
      CallbackInfo ci,
      @Local Entity entity,
      @Local EntityRenderState entityRenderState) {
    this.deltaTracker = deltaTracker;
    entityMap.put(entityRenderState, entity);

    ClientLevel level = minecraft.level;

    float partialTicks =
        deltaTracker.getGameTimeDeltaPartialTick(
            level == null || !level.tickRateManager().isEntityFrozen(entity));

    var ctx = new ExtractVisibleEntitiesContext(entity, partialTicks);
    GameClientEvents.EXTRACT_VISIBLE_ENTITIES.emit(ctx);

    if (ctx.cancelRendering) {
      ci.cancel();
    }
  }

  @Inject(
      method = "submitEntities",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lnet/minecraft/client/renderer/state/CameraRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V",
              shift = At.Shift.AFTER))
  private void postRenderEntity(
      PoseStack poseStack,
      LevelRenderState levelRenderState,
      SubmitNodeCollector submitNodeCollector,
      CallbackInfo ci,
      @Local EntityRenderState entityRenderState) {
    Entity entity = entityMap.remove(entityRenderState);
    if (entity == null) return;

    MultiBufferSource.BufferSource bufferSource = renderBuffers.bufferSource();
    ClientLevel level = minecraft.level;
    float partialTicks =
        deltaTracker.getGameTimeDeltaPartialTick(
            level == null || !minecraft.level.tickRateManager().isEntityFrozen(entity));
    float opacity = ((EntityOpacityAccessor) entity).leawind_third_person$getOpacity();
    if (opacity < 1.0f) {
      bufferSource.endLastBatch();
    }
  }
}
