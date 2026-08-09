package io.github.leawind.thirdperson.internal.core.base.math;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ExponentialSmoothingTest {
  @Test
  void reachesHalfwayAfterOneHalfLife() {
    assertEquals(0.5, ExponentialSmoothing.alpha(0.25, 0.25), 1.0e-12);
    assertEquals(5.0, ExponentialSmoothing.interpolate(0.0, 10.0, 0.25, 0.25), 1.0e-12);
  }

  @Test
  void supportsImmediateAndZeroElapsedUpdates() {
    assertEquals(1.0, ExponentialSmoothing.alpha(0.0, 0.0));
    assertEquals(0.0, ExponentialSmoothing.alpha(0.0, 0.25));
  }

  @Test
  void rejectsInvalidInputs() {
    assertThrows(IllegalArgumentException.class, () -> ExponentialSmoothing.alpha(-1.0, 1.0));
    assertThrows(
        IllegalArgumentException.class,
        () -> ExponentialSmoothing.interpolate(Double.NaN, 1.0, 1.0, 1.0));
  }
}
