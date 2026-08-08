package io.github.leawind.thirdperson.internal.logic.base.pivot;

/// Independent exponential half-lives for following an entity's eye position.
public record CameraPivotSmoothing(double horizontalHalfLife, double verticalHalfLife) {
  public CameraPivotSmoothing {
    requireHalfLife(horizontalHalfLife);
    requireHalfLife(verticalHalfLife);
  }

  private static void requireHalfLife(double value) {
    if (!Double.isFinite(value) || value < 0.0) {
      throw new IllegalArgumentException("Pivot half-lives must be finite and non-negative");
    }
  }
}
