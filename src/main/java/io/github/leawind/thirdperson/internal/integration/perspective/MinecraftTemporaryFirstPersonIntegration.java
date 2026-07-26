package io.github.leawind.thirdperson.internal.integration.perspective;

import io.github.leawind.perspectiveapi.api.PerspectiveAPI;
import io.github.leawind.thirdperson.ThirdPerson;
import io.github.leawind.thirdperson.internal.application.ThirdPersonRuntime;
import io.github.leawind.thirdperson.internal.bridge.events.ClientTickEvent;
import io.github.leawind.thirdperson.internal.core.camera.CameraMode;
import io.github.leawind.thirdperson.internal.core.camera.CameraPose;
import io.github.leawind.thirdperson.internal.core.camera.CameraRig;
import io.github.leawind.thirdperson.internal.integration.minecraft.MinecraftCameraCollision;
import io.github.leawind.thirdperson.internal.core.aiming.AimRuleAction;
import io.github.leawind.thirdperson.internal.integration.resource.MinecraftAimingRuleIntegration;
import net.minecraft.client.Minecraft;
import org.joml.Quaternionf;
import org.joml.Vector3d;

/// Owns the Perspective API override used for tight spaces and spyglass use.
public final class MinecraftTemporaryFirstPersonIntegration {
  private static final String OVERRIDE_KEY = ThirdPerson.MOD_ID + ".temporary_first_person";
  private static final String FIRST_PERSON_ID = "perspective_api.first_person";
  private static final int OVERRIDE_PRIORITY = 100;
  private static boolean registered;

  private MinecraftTemporaryFirstPersonIntegration() {}

  public static void register() {
    if (registered) {
      return;
    }
    registered = true;
    PerspectiveAPI.runWhenReady(
        OVERRIDE_KEY,
        () ->
            PerspectiveAPI.getOverrideChain()
                .push(
                    OVERRIDE_KEY,
                    OVERRIDE_PRIORITY,
                    () ->
                        ThirdPersonRuntime.getInstance()
                                .session()
                                .isTemporaryFirstPersonRequested()
                            ? FIRST_PERSON_ID
                            : null));
    ClientTickEvent.register(MinecraftTemporaryFirstPersonIntegration::onClientTick);
  }

  private static void onClientTick() {
    Minecraft minecraft = Minecraft.getInstance();
    ThirdPersonRuntime runtime = ThirdPersonRuntime.getInstance();
    var session = runtime.session();
    var player = minecraft.player;
    boolean ownsPerspectiveSession =
        PerspectiveGuard.isThirdPersonCurrent() || session.isTemporaryFirstPersonRequested();
    if (!ownsPerspectiveSession
        || !runtime.config().enabled()
        || !session.isPerspectiveActive()
        || player == null
        || minecraft.level == null) {
      session.tightSpaceDetector().reset();
      runtime.requestTemporaryFirstPerson(false);
      return;
    }

    var lookController = session.lookController();
    if (session.mode() == CameraMode.TEMP_FIRST_PERSON) {
      lookController.initialize(player.getXRot(), player.getYRot());
    } else if (!lookController.isInitialized()) {
      lookController.initialize(player.getXRot(), player.getYRot());
    }

    boolean spyglass =
        player.isUsingItem()
            && "SPYGLASS".equals(player.getUseItem().getUseAnimation().name());
    boolean resourceFirstPerson =
        runtime.config().aiming().smartAiming()
            && MinecraftAimingRuleIntegration.currentAction()
                == AimRuleAction.FIRST_PERSON_WHILE_USING;
    boolean tight = false;
    if (runtime.config().camera().temporaryFirstPersonInTightSpace()) {
      tight = updateTightSpace(minecraft, runtime);
    } else {
      session.tightSpaceDetector().reset();
    }
    runtime.requestTemporaryFirstPerson(spyglass || resourceFirstPerson || tight);
  }

  private static boolean updateTightSpace(Minecraft minecraft, ThirdPersonRuntime runtime) {
    var player = minecraft.player;
    var session = runtime.session();
    var lookController = session.lookController();
    var rotation = new Quaternionf();
    if (!lookController.copyRotation(rotation)) {
      session.tightSpaceDetector().reset();
      return false;
    }

    var profile = runtime.cameraProfile(player.isFallFlying() || player.isSwimming());
    var eye = player.getEyePosition(1.0f);
    var pivot = new Vector3d(eye.x, eye.y, eye.z);
    int height = minecraft.getWindow().getHeight();
    double aspect = height > 0 ? (double) minecraft.getWindow().getWidth() / height : 1.0;
    float fov = minecraft.options.fov().get().floatValue();
    CameraPose ideal =
        CameraRig.calculate(pivot, rotation, profile.cameraParameters(), fov, aspect)
            .orElse(null);
    if (ideal == null) {
      session.tightSpaceDetector().reset();
      return false;
    }

    Vector3d resolved =
        MinecraftCameraCollision.resolve(
                player, pivot, ideal.copyPosition(new Vector3d()))
            .orElse(null);
    if (resolved == null) {
      session.tightSpaceDetector().reset();
      return false;
    }
    return session
        .tightSpaceDetector()
        .update(resolved.distance(pivot), profile.distance());
  }
}
