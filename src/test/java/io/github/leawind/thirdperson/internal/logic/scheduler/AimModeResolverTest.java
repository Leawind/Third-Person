package io.github.leawind.thirdperson.internal.logic.scheduler;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AimModeResolverTest {
  @Test
  void manualAimingAlwaysWins() {
    assertTrue(AimModeResolver.shouldAim(true, false, false));
  }

  @Test
  void automaticAimingIsGatedBySmartAiming() {
    assertTrue(AimModeResolver.shouldAim(false, true, true));
    assertFalse(AimModeResolver.shouldAim(false, true, false));
    assertFalse(AimModeResolver.shouldAim(false, false, true));
  }
}
