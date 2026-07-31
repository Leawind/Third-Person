package io.github.leawind.thirdperson.internal.logic.scheduler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.leawind.thirdperson.internal.logic.scheduler.SchedulerRuntime;
import io.github.leawind.thirdperson.internal.logic.scheduler.CameraProfileSlot;
import org.junit.jupiter.api.Test;

class ThirdPersonPersistentStateTest {
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
