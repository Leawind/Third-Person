package io.github.leawind.thirdperson.internal.core.schedule.camera;

public record ModeSmoothing(
    double pivotPositionHalfLife,
    double pivotRotationHalfLife,
    double offsetHalfLife,
    double distanceHalfLife,
    double fovHalfLife) {
  public ModeSmoothing {
    requireHalfLife(pivotPositionHalfLife);
    requireHalfLife(pivotRotationHalfLife);
    requireHalfLife(offsetHalfLife);
    requireHalfLife(distanceHalfLife);
    requireHalfLife(fovHalfLife);
  }

  public ModeSmoothing withPivotPositionHalfLife(double value) {
    return new ModeSmoothing(
        value, pivotRotationHalfLife, offsetHalfLife, distanceHalfLife, fovHalfLife);
  }

  public ModeSmoothing withPivotRotationHalfLife(double value) {
    return new ModeSmoothing(
        pivotPositionHalfLife, value, offsetHalfLife, distanceHalfLife, fovHalfLife);
  }

  public ModeSmoothing withOffsetHalfLife(double value) {
    return new ModeSmoothing(
        pivotPositionHalfLife, pivotRotationHalfLife, value, distanceHalfLife, fovHalfLife);
  }

  public ModeSmoothing withDistanceHalfLife(double value) {
    return new ModeSmoothing(
        pivotPositionHalfLife, pivotRotationHalfLife, offsetHalfLife, value, fovHalfLife);
  }

  public ModeSmoothing withFovHalfLife(double value) {
    return new ModeSmoothing(
        pivotPositionHalfLife, pivotRotationHalfLife, offsetHalfLife, distanceHalfLife, value);
  }

  private static void requireHalfLife(double value) {
    if (!Double.isFinite(value) || value < 0.0 || value > 0.2) {
      throw new IllegalArgumentException("Smoothing half-life must be within [0, 0.2]");
    }
  }
}
