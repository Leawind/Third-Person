package io.github.leawind.thirdperson.internal.integration.perspective;

import com.google.auto.service.AutoService;
import io.github.leawind.perspectiveapi.api.PerspectiveBehavior;
import io.github.leawind.perspectiveapi.api.PerspectiveState;
import io.github.leawind.perspectiveapi.api.context.PerspectiveContext;
import io.github.leawind.thirdperson.ThirdPerson;
import io.github.leawind.thirdperson.internal.application.ThirdPersonRuntime;
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
  private static final CameraParameters NORMAL_CAMERA =
      new CameraParameters(4.0, -0.18, 0.12);

  private final ThirdPersonRuntime runtime = ThirdPersonRuntime.getInstance();

  @Override
  public void onActivate() {
    runtime.onPerspectiveActivated();
  }

  @Override
  public void onDeactivate() {
    runtime.onPerspectiveDeactivated();
  }

  @Override
  public void applyCameraState(
      PerspectiveState.@NonNull Mutable state, @NonNull PerspectiveContext context) {
    if (!PerspectiveGuard.isThirdPersonCurrent() || !runtime.session().isPerspectiveActive()) {
      return;
    }

    Minecraft minecraft = Minecraft.getInstance();
    Entity entity = context.entity();
    if (entity == null || entity != minecraft.player) {
      return;
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

    CameraPose idealPose =
        CameraRig.calculate(
                pivot, rotation, NORMAL_CAMERA, state.getFovDeg(), aspectRatio)
            .orElse(null);
    if (idealPose == null) {
      return;
    }

    var idealPosition = idealPose.copyPosition(new Vector3d());
    var resolvedPosition =
        MinecraftCameraCollision.resolve(entity, pivot, idealPosition).orElse(null);
    if (resolvedPosition == null) {
      return;
    }

    CameraPose resolvedPose =
        CameraPose.tryCreate(resolvedPosition, rotation, state.getFovDeg()).orElse(null);
    if (resolvedPose == null) {
      return;
    }
    resolvedPose.copyPosition(state.position());
    resolvedPose.copyRotation(state.rotation());
  }
}
