package io.github.leawind.thirdperson.internal.logic.base.camera;

/// Independent exponential half-lives used before the camera rig is evaluated.
public record CameraSmoothingParameters(
    double horizontalPivotHalfLife,
    double verticalPivotHalfLife,
    double rotationHalfLife,
    double offsetHalfLife,
    double distanceHalfLife,
    double fovHalfLife) {
  public CameraSmoothingParameters {
    requireHalfLife(horizontalPivotHalfLife);
    requireHalfLife(verticalPivotHalfLife);
    requireHalfLife(rotationHalfLife);
    requireHalfLife(offsetHalfLife);
    requireHalfLife(distanceHalfLife);
    requireHalfLife(fovHalfLife);
  }

  private static void requireHalfLife(double value) {
    if (!Double.isFinite(value) || value < 0.0) {
      throw new IllegalArgumentException("Smoothing half-lives must be finite and non-negative");
    }
  }
}
