package io.github.leawind.thirdperson.internal.base.core.math;

/// Frame-rate-independent exponential smoothing helpers.
public final class ExponentialSmoothing {
  private ExponentialSmoothing() {}

  /// Returns the interpolation factor for the given elapsed time and half-life.
  ///
  /// A zero half-life means that the target should be applied immediately.
  public static double alpha(double deltaSeconds, double halfLifeSeconds) {
    if (!Double.isFinite(deltaSeconds)
        || !Double.isFinite(halfLifeSeconds)
        || deltaSeconds < 0.0
        || halfLifeSeconds < 0.0) {
      throw new IllegalArgumentException("Smoothing inputs must be finite and non-negative");
    }
    if (halfLifeSeconds == 0.0) {
      return 1.0;
    }
    if (deltaSeconds == 0.0) {
      return 0.0;
    }
    return Math.min(1.0, 1.0 - Math.pow(2.0, -deltaSeconds / halfLifeSeconds));
  }

  public static double interpolate(
      double current, double target, double deltaSeconds, double halfLifeSeconds) {
    if (!Double.isFinite(current) || !Double.isFinite(target)) {
      throw new IllegalArgumentException("Values to interpolate must be finite");
    }
    return current + (target - current) * alpha(deltaSeconds, halfLifeSeconds);
  }
}
