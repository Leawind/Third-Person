package io.github.leawind.thirdperson.internal.core.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ReticlePolicyTest {
  @Test
  void neverChangesOtherPerspectives() {
    assertTrue(ReticlePolicy.shouldRender(true, false, true, ReticleMode.OFF));
    assertFalse(ReticlePolicy.shouldRender(false, false, true, ReticleMode.ON));
  }

  @Test
  void followsModeOnlyWhileThisPerspectiveControlsTheCamera() {
    assertTrue(ReticlePolicy.shouldRender(false, true, true, ReticleMode.AUTO));
    assertTrue(ReticlePolicy.shouldRender(false, true, true, ReticleMode.ON));
    assertFalse(ReticlePolicy.shouldRender(true, true, true, ReticleMode.OFF));
    assertFalse(ReticlePolicy.shouldRender(false, true, false, ReticleMode.ON));
  }
}
