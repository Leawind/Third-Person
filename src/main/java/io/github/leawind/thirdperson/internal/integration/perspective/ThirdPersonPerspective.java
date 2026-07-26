package io.github.leawind.thirdperson.internal.integration.perspective;

import com.google.auto.service.AutoService;
import io.github.leawind.perspectiveapi.api.PerspectiveBehavior;
import io.github.leawind.perspectiveapi.api.PerspectiveState;
import io.github.leawind.perspectiveapi.api.context.PerspectiveContext;
import io.github.leawind.thirdperson.ThirdPerson;
import io.github.leawind.thirdperson.internal.application.ThirdPersonRuntime;
import io.github.leawind.thirdperson.internal.core.camera.CameraInput;
import io.github.leawind.thirdperson.internal.core.camera.CameraParameters;
import io.github.leawind.thirdperson.internal.core.camera.CameraPose;
import io.github.leawind.thirdperson.internal.core.camera.CameraRig;
import io.github.leawind.thirdperson.internal.integration.minecraft.MinecraftCameraCollision;
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
    baseType = PerspectiveBehavior.BaseType.THIRD_PERSON_BACK,
    switchable = true)
@SuppressWarnings("unused")
public final class ThirdPersonPerspective implements PerspectiveBehavior {
  private long lastFrameNanos;
  private final ThirdPersonRuntime runtime = ThirdPersonRuntime.getInstance();

  @Override
  public void onActivate() {
    lastFrameNanos = 0L;
    runtime.onPerspectiveActivated();
  }

  @Override
  public void onDeactivate() {
    lastFrameNanos = 0L;
    runtime.onPerspectiveDeactivated();
  }

  @Override
  public void applyCameraState(
      PerspectiveState.@NonNull Mutable state, @NonNull PerspectiveContext context) {
    if (!PerspectiveGuard.isThirdPersonCurrentForLocalPlayer()
        || !runtime.isCameraControlEnabled()) {
      return;
    }

    Minecraft minecraft = Minecraft.getInstance();
    Entity entity = context.entity();
    var player = minecraft.player;
    if (entity == null || entity != player || player == null) {
      return;
    }

    Vec2 entityRotation = entity.getRotationVector();
    var lookController = runtime.session().lookController();
    if (!lookController.isInitialized()) {
      lookController.initialize(entityRotation.x, entityRotation.y);
    }

    var rotation = new Quaternionf();
    if (!lookController.copyRotation(rotation)) {
      applyLastSafePose(state);
      return;
    }

    var eyePosition = entity.getEyePosition(context.partialTicks());
    var pivot = new Vector3d(eyePosition.x, eyePosition.y, eyePosition.z);
    int windowHeight = minecraft.getWindow().getHeight();
    double aspectRatio =
        windowHeight > 0 ? (double) minecraft.getWindow().getWidth() / windowHeight : 1.0;

    boolean flyingOrSwimming = player.isFallFlying() || player.isSwimming();
    var profile = runtime.cameraProfile(flyingOrSwimming);
    CameraParameters cameraParameters = profile.cameraParameters();
    float targetFov = (float) (state.getFovDeg() * profile.fovMultiplier());
    CameraInput targetInput =
        CameraInput.tryCreate(pivot, rotation, cameraParameters, targetFov).orElse(null);
    if (targetInput == null) {
      applyLastSafePose(state);
      return;
    }

    double deltaSeconds = frameDeltaSeconds();
    CameraInput smoothedInput =
        runtime
            .session()
            .cameraSmoother()
            .update(targetInput, deltaSeconds, runtime.cameraSmoothing(flyingOrSwimming))
            .orElse(null);
    if (smoothedInput == null) {
      applyLastSafePose(state);
      return;
    }

    var smoothedPivot = smoothedInput.copyPivot(new Vector3d());
    var smoothedRotation = smoothedInput.copyRotation(new Quaternionf());
    CameraPose idealPose =
        CameraRig.calculate(
                smoothedPivot,
                smoothedRotation,
                smoothedInput.parameters(),
                smoothedInput.fovDegrees(),
                aspectRatio)
            .orElse(null);
    if (idealPose == null) {
      applyLastSafePose(state);
      return;
    }

    var idealPosition = idealPose.copyPosition(new Vector3d());
    var collisionResolvedPosition =
        MinecraftCameraCollision.resolve(entity, pivot, idealPosition).orElse(null);
    if (collisionResolvedPosition == null) {
      applyLastSafePose(state);
      return;
    }
    var resolvedPosition =
        runtime
            .session()
            .collisionRecovery()
            .resolve(pivot, collisionResolvedPosition, deltaSeconds)
            .orElse(null);
    if (resolvedPosition == null) {
      applyLastSafePose(state);
      return;
    }

    // Collision is position-only by construction: orientation and FOV remain those of idealPose.
    CameraPose resolvedPose = idealPose.withPosition(resolvedPosition).orElse(null);
    if (resolvedPose == null) {
      applyLastSafePose(state);
      return;
    }
    runtime.session().recordSafeCameraPose(resolvedPose);
    applyPose(state, resolvedPose);
  }

  @Override
  public void postApplyWhenActive(
      @NonNull PerspectiveState state, @NonNull PerspectiveContext context) {
    if (!PerspectiveGuard.isThirdPersonCurrentForLocalPlayer()
        || !runtime.isCameraControlEnabled()) {
      return;
    }
    Entity entity = context.entity();
    if (entity == null || entity != Minecraft.getInstance().player) {
      return;
    }
    CameraPose.tryCreate(state.position(), state.rotation(), state.getFovDeg())
        .ifPresent(runtime.session()::recordFinalCameraPose);
  }

  private void applyLastSafePose(PerspectiveState.Mutable state) {
    runtime.session().lastSafeCameraPose().ifPresent(pose -> applyPose(state, pose));
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
