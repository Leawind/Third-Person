package io.github.leawind.thirdperson.internal.logic.scheduler.hud;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CrosshairPolicyTest {
  @Test
  void neverChangesOtherPerspectives() {
    assertTrue(CrosshairPolicy.shouldRender(true, false, true, CrosshairMode.OFF));
    assertFalse(CrosshairPolicy.shouldRender(false, false, true, CrosshairMode.ON));
  }

  @Test
  void followsModeOnlyWhileThisPerspectiveControlsTheCamera() {
    assertTrue(CrosshairPolicy.shouldRender(false, true, true, CrosshairMode.AUTO));
    assertTrue(CrosshairPolicy.shouldRender(false, true, true, CrosshairMode.ON));
    assertFalse(CrosshairPolicy.shouldRender(true, true, true, CrosshairMode.OFF));
    assertFalse(CrosshairPolicy.shouldRender(false, true, false, CrosshairMode.ON));
  }
}
