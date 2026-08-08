package io.github.leawind.thirdperson.internal.logic.base;

import com.google.auto.service.AutoService;
import io.github.leawind.perspectiveapi.api.PerspectiveBehavior;
import io.github.leawind.perspectiveapi.api.PerspectiveContext;
import io.github.leawind.perspectiveapi.api.PerspectiveInfo;
import io.github.leawind.perspectiveapi.api.PerspectiveState;
import io.github.leawind.thirdperson.ThirdPerson;
import io.github.leawind.thirdperson.internal.bridge.Bridge;
import io.github.leawind.thirdperson.internal.bridge.compat.sable.SableCompatibility;
import io.github.leawind.thirdperson.internal.logic.base.camera.CameraFrameInput;
import io.github.leawind.thirdperson.internal.logic.base.camera.CameraPose;
import io.github.leawind.thirdperson.internal.logic.base.camera.MinecraftCameraCollision;
import io.github.leawind.thirdperson.internal.logic.base.camera.MinecraftCameraSubjectDimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.jspecify.annotations.NonNull;

/// The single manually selectable perspective provided by this mod.
@AutoService(PerspectiveBehavior.class)
@PerspectiveInfo.Declaration(
    id = ThirdPerson.PERSPECTIVE_ID,
    icon = ThirdPerson.MOD_ID + ":textures/perspective/third_person.png",
    priority = 10,
    baseType = PerspectiveBehavior.BaseType.THIRD_PERSON_BACK)
@SuppressWarnings("unused")
public final class ThirdPersonPerspective implements PerspectiveBehavior {
  private long lastFrameNanos;
  private Entity previousCameraEntity;
  private final BaseRuntime runtime = BaseRuntime.getInstance();

  @Override
  public void onActivate() {
    lastFrameNanos = 0L;
    previousCameraEntity = null;
    runtime.onPerspectiveActivated();
  }

  @Override
  public void onDeactivate() {
    lastFrameNanos = 0L;
    previousCameraEntity = null;
    runtime.onPerspectiveDeactivated();
  }

  @Override
  public void computeCameraState(
      PerspectiveState.@NonNull Mutable state, @NonNull PerspectiveContext context) {
    if (!PerspectiveGuard.isThirdPersonCurrent() || !runtime.isCameraControlEnabled()) {
      return;
    }

    Minecraft minecraft = Minecraft.getInstance();
    Entity entity = context.cameraEntity();
    if (entity == null) {
      return;
    }

    if (entity != previousCameraEntity) {
      previousCameraEntity = entity;
      lastFrameNanos = 0L;
      runtime.session().resetCameraTracking();
    }

    Vec2 entityRotation = entity.getRotationVector();
    var lookController = runtime.session().lookController();
    if (!lookController.isInitialized()) {
      lookController.initialize(entityRotation.x, entityRotation.y);
    }

    var rotation = new Quaternionf();
    if (!lookController.copyRotation(rotation)) {
      return;
    }

    var eyePosition = SableCompatibility.getEyePositionInterpolated(entity, context.partialTicks());
    var interpolatedPivot = new Vector3d(eyePosition.x, eyePosition.y, eyePosition.z);
    var pivot =
        runtime
            .session()
            .cameraPivotSmoother()
            .sample(
                interpolatedPivot, context.partialTicks(), runtime.parameters().cameraSmoothing())
            .orElse(null);
    if (pivot == null) {
      return;
    }
    int windowHeight = minecraft.getWindow().getHeight();
    double aspectRatio =
        windowHeight > 0 ? (double) minecraft.getWindow().getWidth() / windowHeight : 1.0;
    var subjectDimensions = MinecraftCameraSubjectDimensions.resolve(entity).orElse(null);
    if (subjectDimensions == null) {
      return;
    }

    CameraFrameInput frame =
        CameraFrameInput.tryCreate(
                pivot,
                rotation,
                state.getFovDeg(),
                aspectRatio,
                subjectDimensions,
                frameDeltaSeconds())
            .orElse(null);
    if (frame == null) {
      return;
    }
    runtime
        .updateCamera(
            frame,
            (collisionPivot, desiredPosition) ->
                MinecraftCameraCollision.resolve(entity, collisionPivot, desiredPosition))
        .ifPresent(pose -> applyPose(state, pose));
  }

  @Override
  public void afterCameraStateResolved(
      @NonNull PerspectiveState state, @NonNull PerspectiveContext context) {
    if (!PerspectiveGuard.isThirdPersonCurrent() || !runtime.isCameraControlEnabled()) {
      return;
    }
    Entity entity = context.cameraEntity();
    if (entity == null) {
      return;
    }
    CameraPose.tryCreate(state.position(), state.rotation(), state.getFovDeg())
        .ifPresent(
            pose -> {
              runtime.session().recordFinalCameraPose(pose);
              if (!Bridge.vanillaPickFollowsCameraUpdate()) {
                MinecraftInteractionIntegration.refreshRaycast(context.partialTicks());
              }
            });
  }

  private static void applyPose(PerspectiveState.Mutable state, CameraPose pose) {
    pose.copyPosition(state.position());
    pose.copyRotation(state.rotation());
    state.setFovDeg(pose.fovDegrees());
  }

  private double frameDeltaSeconds() {
    long now = System.nanoTime();
    if (lastFrameNanos == 0L || now <= lastFrameNanos) {
      lastFrameNanos = now;
      return 0.0;
    }
    double deltaSeconds = (now - lastFrameNanos) * 1.0e-9;
    lastFrameNanos = now;
    return Math.min(deltaSeconds, 0.1);
  }
}
