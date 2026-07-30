package io.github.leawind.thirdperson.internal.base.core.camera;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CameraSubjectDimensionsTest {
  @Test
  void resolvesTheLegacyEntityAndVehicleAwareDistanceFormula() {
    var dimensions = new CameraSubjectDimensions(0.5, 2.0);

    assertEquals(3.5, dimensions.resolveDistance(1.5, 1.0));
  }

  @Test
  void largerVehiclesMoveTheCameraFartherAway() {
    var entityOnly = new CameraSubjectDimensions(0.5, 2.0);
    var largeVehicle = new CameraSubjectDimensions(0.5, 6.0);

    assertEquals(3.5, entityOnly.resolveDistance(1.5, 1.0));
    assertEquals(9.5, largeVehicle.resolveDistance(1.5, 1.0));
  }

  @Test
  void narrowerCompositionFovPreservesTheSubjectScale() {
    var dimensions = new CameraSubjectDimensions(0.5, 2.0);

    assertEquals(7.0, dimensions.resolveDistance(1.5, 0.5));
  }
}
