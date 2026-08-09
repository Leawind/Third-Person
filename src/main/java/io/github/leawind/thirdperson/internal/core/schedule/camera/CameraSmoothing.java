package io.github.leawind.thirdperson.internal.core.schedule.camera;

import java.util.Objects;

public record CameraSmoothing(
    double rotationHalfLife,
    double flyingPivotHalfLife,
    double adjustingOffsetHalfLife,
    double adjustingDistanceHalfLife,
    ModeSmoothing normal,
    ModeSmoothing aiming) {
  public CameraSmoothing {
    requireHalfLife(rotationHalfLife);
    requireHalfLife(flyingPivotHalfLife);
    requireHalfLife(adjustingOffsetHalfLife);
    requireHalfLife(adjustingDistanceHalfLife);
    Objects.requireNonNull(normal, "normal");
    Objects.requireNonNull(aiming, "aiming");
  }

  public CameraSmoothing withRotationHalfLife(double value) {
    return new CameraSmoothing(
        value,
        flyingPivotHalfLife,
        adjustingOffsetHalfLife,
        adjustingDistanceHalfLife,
        normal,
        aiming);
  }

  public CameraSmoothing withFlyingPivotHalfLife(double value) {
    return new CameraSmoothing(
        rotationHalfLife,
        value,
        adjustingOffsetHalfLife,
        adjustingDistanceHalfLife,
        normal,
        aiming);
  }

  public CameraSmoothing withAdjustingOffsetHalfLife(double value) {
    return new CameraSmoothing(
        rotationHalfLife, flyingPivotHalfLife, value, adjustingDistanceHalfLife, normal, aiming);
  }

  public CameraSmoothing withAdjustingDistanceHalfLife(double value) {
    return new CameraSmoothing(
        rotationHalfLife, flyingPivotHalfLife, adjustingOffsetHalfLife, value, normal, aiming);
  }

  private static void requireHalfLife(double value) {
    if (!Double.isFinite(value) || value < 0.0 || value > 0.2) {
      throw new IllegalArgumentException("Smoothing half-life must be within [0, 0.2]");
    }
  }
}
