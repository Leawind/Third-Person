package io.github.leawind.thirdperson.internal.core.base.pivot;

/// Exponential half-lives for following a complete pivot pose.
public record CameraPivotSmoothing(double positionHalfLife, double rotationHalfLife) {
  public CameraPivotSmoothing {
    requireHalfLife(positionHalfLife);
    requireHalfLife(rotationHalfLife);
  }

  private static void requireHalfLife(double value) {
    if (!Double.isFinite(value) || value < 0.0) {
      throw new IllegalArgumentException("Pivot half-lives must be finite and non-negative");
    }
  }
}
