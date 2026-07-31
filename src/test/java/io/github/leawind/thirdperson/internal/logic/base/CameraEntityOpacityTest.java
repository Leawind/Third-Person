package io.github.leawind.thirdperson.internal.logic.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CameraEntityOpacityTest {
  @Test
  void smoothsByHalfLifeAndInterpolatesBetweenTicks() {
    var opacity = new CameraEntityOpacity();
    opacity.setTarget(0.0);

    assertEquals(0.5, opacity.update(0.0625, 0.0625), 1.0e-12);
    assertEquals(1.0f, opacity.sample(0.0f), 1.0e-6f);
    assertEquals(0.75f, opacity.sample(0.5f), 1.0e-6f);
    assertEquals(0.5f, opacity.sample(1.0f), 1.0e-6f);
  }

  @Test
  void clampsTargetsAndPartialTicks() {
    var opacity = new CameraEntityOpacity();
    opacity.setTarget(-2.0);
    opacity.update(0.0, 0.0);
    assertEquals(0.0f, opacity.sample(2.0f), 1.0e-6f);

    opacity.setTarget(3.0);
    opacity.update(0.0, 0.0);
    assertEquals(1.0f, opacity.sample(1.0f), 1.0e-6f);
  }

  @Test
  void rejectsNonFiniteTargetsAndResetsOpaque() {
    var opacity = new CameraEntityOpacity();
    assertThrows(IllegalArgumentException.class, () -> opacity.setTarget(Double.NaN));

    opacity.setTarget(0.0);
    opacity.update(0.0, 0.0);
    opacity.reset();
    assertEquals(1.0f, opacity.sample(1.0f), 1.0e-6f);
  }
}
