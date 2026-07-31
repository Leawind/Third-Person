package io.github.leawind.thirdperson.internal.logic.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class InteractionRaycastGeometryTest {
  @Test
  void candidateRangeIncludesCameraToEyeDistance() {
    assertEquals(8.5, InteractionRaycastGeometry.candidateRange(4.5, 3.0, 4.0));
    assertEquals(10.0, InteractionRaycastGeometry.candidateRange(4.5, 6.0, 4.0));
  }

  @Test
  void candidateRangeRejectsInvalidInputs() {
    assertTrue(Double.isNaN(InteractionRaycastGeometry.candidateRange(-1.0, 3.0, 4.0)));
    assertTrue(
        Double.isNaN(
            InteractionRaycastGeometry.candidateRange(
                4.5, Double.POSITIVE_INFINITY, 4.0)));
  }

  @Test
  void rangeCheckMatchesVanillaStrictBoundary() {
    assertTrue(InteractionRaycastGeometry.isWithinRange(15.999, 4.0));
    assertFalse(InteractionRaycastGeometry.isWithinRange(16.0, 4.0));
    assertFalse(InteractionRaycastGeometry.isWithinRange(16.001, 4.0));
  }
}
