package io.github.leawind.thirdperson.internal.core.schedule.hud;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CrosshairPolicyTest {
  @Test
  void neverChangesOtherPerspectives() {
    assertTrue(CrosshairPolicy.shouldRender(true, false, false, true, CrosshairMode.OFF, true));
    assertFalse(
        CrosshairPolicy.shouldRender(false, false, true, false, CrosshairMode.ALWAYS, false));
  }

  @Test
  void followsConfiguredModeWhileThisPerspectiveControlsTheCamera() {
    assertTrue(
        CrosshairPolicy.shouldRender(false, true, false, false, CrosshairMode.ALWAYS, false));
    assertTrue(CrosshairPolicy.shouldRender(false, true, true, false, CrosshairMode.ALWAYS, false));
    assertFalse(
        CrosshairPolicy.shouldRender(false, true, false, false, CrosshairMode.AIMING, false));
    assertTrue(CrosshairPolicy.shouldRender(false, true, true, false, CrosshairMode.AIMING, false));
    assertTrue(
        CrosshairPolicy.shouldRender(false, true, false, false, CrosshairMode.NOT_AIMING, false));
    assertFalse(
        CrosshairPolicy.shouldRender(false, true, true, false, CrosshairMode.NOT_AIMING, false));
    assertFalse(CrosshairPolicy.shouldRender(true, true, true, false, CrosshairMode.OFF, false));
  }

  @Test
  void optionallyHidesCrosshairWhenFallFlyingOutsideAimingMode() {
    assertFalse(CrosshairPolicy.shouldRender(false, true, false, true, CrosshairMode.ALWAYS, true));
    assertTrue(CrosshairPolicy.shouldRender(false, true, true, true, CrosshairMode.ALWAYS, true));
    assertTrue(CrosshairPolicy.shouldRender(false, true, false, true, CrosshairMode.ALWAYS, false));
  }
}
