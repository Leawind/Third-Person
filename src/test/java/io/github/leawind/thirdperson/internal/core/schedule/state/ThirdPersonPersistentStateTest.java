package io.github.leawind.thirdperson.internal.core.schedule.state;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.leawind.thirdperson.internal.core.schedule.SchedulerRuntime;
import io.github.leawind.thirdperson.internal.core.schedule.camera.CameraProfileSlot;
import io.github.leawind.thirdperson.internal.core.schedule.hud.CrosshairMode;
import org.junit.jupiter.api.Test;

class ThirdPersonPersistentStateTest {
  @Test
  void ownsDefaultHudState() {
    ThirdPersonPersistentState defaults = ThirdPersonPersistentState.defaults();

    assertEquals(CrosshairMode.ALWAYS, defaults.hud().crosshair());
    assertEquals(true, defaults.hud().hideCrosshairWhenFallFlyingAndNotAiming());
  }

  @Test
  void extractsAndRestoresRuntimeOwnedValues() {
    SchedulerRuntime runtime = SchedulerRuntime.getInstance();
    ThirdPersonPersistentState defaults = ThirdPersonPersistentState.defaults();
    defaults.applyTo(runtime);
    try {
      runtime
          .cameraSettings()
          .updateProfile(CameraProfileSlot.NORMAL, profile -> profile.withOffsetX(0.42));
      runtime.aimingSettings().setSmartAiming(false);
      runtime.soundSettings().setCenterCameraEntitySounds(true);
      runtime.hudSettings().setCrosshairMode(CrosshairMode.NOT_AIMING);
      runtime.hudSettings().setHideCrosshairWhenFallFlyingAndNotAiming(false);

      ThirdPersonPersistentState extracted = ThirdPersonPersistentState.extract(runtime);
      defaults.applyTo(runtime);
      extracted.applyTo(runtime);

      assertEquals(0.42, runtime.cameraSettings().normalProfile().offsetX());
      assertEquals(false, runtime.aimingSettings().smartAiming());
      assertEquals(true, runtime.soundSettings().centerCameraEntitySounds());
      assertEquals(CrosshairMode.NOT_AIMING, runtime.hudSettings().crosshairMode());
      assertEquals(false, runtime.hudSettings().hideCrosshairWhenFallFlyingAndNotAiming());
    } finally {
      defaults.applyTo(runtime);
    }
  }
}
