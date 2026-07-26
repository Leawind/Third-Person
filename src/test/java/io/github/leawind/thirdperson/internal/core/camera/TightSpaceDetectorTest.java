package io.github.leawind.thirdperson.internal.core.camera;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TightSpaceDetectorTest {
  @Test
  void usesSeparateConsecutiveEnterAndExitThresholds() {
    var detector = new TightSpaceDetector();

    assertFalse(detector.update(0.4, 4.0));
    assertTrue(detector.update(0.4, 4.0));
    assertTrue(detector.update(0.6, 4.0));
    assertTrue(detector.update(0.9, 4.0));
    assertTrue(detector.update(0.9, 4.0));
    assertFalse(detector.update(0.9, 4.0));
  }

  @Test
  void ignoresProfilesThatAreAlreadyNearFirstPerson() {
    var detector = new TightSpaceDetector();

    detector.update(0.1, 0.5);
    assertFalse(detector.update(0.1, 0.5));
    assertFalse(detector.isTight());
  }

  @Test
  void invalidMeasurementsFailOpen() {
    var detector = new TightSpaceDetector();
    detector.update(0.1, 4.0);
    detector.update(0.1, 4.0);
    assertTrue(detector.isTight());

    assertFalse(detector.update(Double.NaN, 4.0));
    assertFalse(detector.isTight());
  }
}
