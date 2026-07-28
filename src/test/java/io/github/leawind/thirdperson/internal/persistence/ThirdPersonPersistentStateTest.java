package io.github.leawind.thirdperson.internal.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.leawind.thirdperson.internal.application.ThirdPersonRuntime;
import io.github.leawind.thirdperson.internal.core.camera.CameraProfileSlot;
import org.junit.jupiter.api.Test;

class ThirdPersonPersistentStateTest {
  @Test
  void extractsAndRestoresRuntimeOwnedValues() {
    ThirdPersonRuntime runtime = ThirdPersonRuntime.getInstance();
    ThirdPersonPersistentState defaults = ThirdPersonPersistentState.defaults();
    defaults.applyTo(runtime);
    try {
      runtime
          .cameraSettings()
          .updateProfile(CameraProfileSlot.NORMAL, profile -> profile.withOffsetX(0.42));
      runtime.aimingSettings().setSmartAiming(false);

      ThirdPersonPersistentState extracted = ThirdPersonPersistentState.extract(runtime);
      defaults.applyTo(runtime);
      extracted.applyTo(runtime);

      assertEquals(0.42, runtime.cameraSettings().normalProfile().offsetX());
      assertEquals(false, runtime.aimingSettings().smartAiming());
    } finally {
      defaults.applyTo(runtime);
    }
  }
}
