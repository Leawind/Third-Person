package io.github.leawind.thirdperson.internal.core.camera;

public record ModeSmoothing(
    double horizontalPivotHalfLife,
    double verticalPivotHalfLife,
    double offsetHalfLife,
    double distanceHalfLife,
    double fovHalfLife) {
  public ModeSmoothing {
    requireHalfLife(horizontalPivotHalfLife);
    requireHalfLife(verticalPivotHalfLife);
    requireHalfLife(offsetHalfLife);
    requireHalfLife(distanceHalfLife);
    requireHalfLife(fovHalfLife);
  }

  public ModeSmoothing withHorizontalPivotHalfLife(double value) {
    return new ModeSmoothing(
        value, verticalPivotHalfLife, offsetHalfLife, distanceHalfLife, fovHalfLife);
  }

  public ModeSmoothing withVerticalPivotHalfLife(double value) {
    return new ModeSmoothing(
        horizontalPivotHalfLife, value, offsetHalfLife, distanceHalfLife, fovHalfLife);
  }

  public ModeSmoothing withOffsetHalfLife(double value) {
    return new ModeSmoothing(
        horizontalPivotHalfLife, verticalPivotHalfLife, value, distanceHalfLife, fovHalfLife);
  }

  public ModeSmoothing withDistanceHalfLife(double value) {
    return new ModeSmoothing(
        horizontalPivotHalfLife, verticalPivotHalfLife, offsetHalfLife, value, fovHalfLife);
  }

  public ModeSmoothing withFovHalfLife(double value) {
    return new ModeSmoothing(
        horizontalPivotHalfLife, verticalPivotHalfLife, offsetHalfLife, distanceHalfLife, value);
  }

  private static void requireHalfLife(double value) {
    if (!Double.isFinite(value) || value < 0.0 || value > 0.2) {
      throw new IllegalArgumentException("Smoothing half-life must be within [0, 0.2]");
    }
  }
}
