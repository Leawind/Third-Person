package io.github.leawind.thirdperson.internal.core.base.pivot;

/// Exponential half-life for following the pivot position in world space.
public record CameraPivotSmoothing(double positionHalfLife) {
  public CameraPivotSmoothing {
    requireHalfLife(positionHalfLife);
  }

  private static void requireHalfLife(double value) {
    if (!Double.isFinite(value) || value < 0.0) {
      throw new IllegalArgumentException("Pivot half-lives must be finite and non-negative");
    }
  }
}
