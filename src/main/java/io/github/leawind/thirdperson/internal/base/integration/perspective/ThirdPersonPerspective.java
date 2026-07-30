package io.github.leawind.thirdperson.internal.base.integration.perspective;

import com.google.auto.service.AutoService;
import io.github.leawind.perspectiveapi.api.PerspectiveBehavior;
import io.github.leawind.perspectiveapi.api.PerspectiveState;
import io.github.leawind.perspectiveapi.api.context.PerspectiveContext;
import io.github.leawind.thirdperson.ThirdPerson;
import io.github.leawind.thirdperson.internal.base.application.BaseRuntime;
import io.github.leawind.thirdperson.internal.base.application.camera.CameraFrameInput;
import io.github.leawind.thirdperson.internal.base.core.camera.CameraPose;
import io.github.leawind.thirdperson.internal.base.integration.minecraft.MinecraftCameraCollision;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.jspecify.annotations.NonNull;

/// The single manually selectable perspective provided by this mod.
@AutoService(PerspectiveBehavior.class)
@PerspectiveBehavior.Info(
    id = ThirdPerson.PERSPECTIVE_ID,
    nameKey = "perspective.leawind_third_person.third_person.name",
    descriptionKey = "perspective.leawind_third_person.third_person.description",
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
  public void applyCameraState(
      PerspectiveState.@NonNull Mutable state, @NonNull PerspectiveContext context) {
    if (!PerspectiveGuard.isThirdPersonCurrent() || !runtime.isCameraControlEnabled()) {
      return;
    }

    Minecraft minecraft = Minecraft.getInstance();
    Entity entity = context.entity();
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

    var eyePosition = entity.getEyePosition(context.partialTicks());
    var pivot = new Vector3d(eyePosition.x, eyePosition.y, eyePosition.z);
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
  public void postApplyWhenActive(
      @NonNull PerspectiveState state, @NonNull PerspectiveContext context) {
    if (!PerspectiveGuard.isThirdPersonCurrent() || !runtime.isCameraControlEnabled()) {
      return;
    }
    Entity entity = context.entity();
    if (entity == null) {
      return;
    }
    CameraPose.tryCreate(state.position(), state.rotation(), state.getFovDeg())
        .ifPresent(runtime.session()::recordFinalCameraPose);
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
