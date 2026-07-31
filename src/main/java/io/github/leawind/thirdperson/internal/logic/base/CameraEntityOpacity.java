package io.github.leawind.thirdperson.internal.logic.base;

import io.github.leawind.thirdperson.internal.logic.base.math.ExponentialSmoothing;

/// Tick-updated, frame-sampled opacity state for the active camera entity.
public final class CameraEntityOpacity {
  private double previous = 1.0;
  private double current = 1.0;
  private double target = 1.0;

  public void setTarget(double opacity) {
    if (!Double.isFinite(opacity)) {
      throw new IllegalArgumentException("Camera entity opacity must be finite");
    }
    target = clampUnit(opacity);
  }

  public double update(double deltaSeconds, double halfLifeSeconds) {
    previous = current;
    current = ExponentialSmoothing.interpolate(current, target, deltaSeconds, halfLifeSeconds);
    return current;
  }

  public float sample(float partialTick) {
    if (!Float.isFinite(partialTick)) {
      return (float) current;
    }
    double interpolation = clampUnit(partialTick);
    return (float) (previous + (current - previous) * interpolation);
  }

  public void reset() {
    previous = 1.0;
    current = 1.0;
    target = 1.0;
  }

  private static double clampUnit(double value) {
    return Math.max(0.0, Math.min(1.0, value));
  }
}
