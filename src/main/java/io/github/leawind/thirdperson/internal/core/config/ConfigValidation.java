package io.github.leawind.thirdperson.internal.core.config;

/// Pure validation helpers shared by config decoders and future config screens.
public final class ConfigValidation {
  private ConfigValidation() {}

  public static double finiteClamped(double value, double minimum, double maximum, double fallback) {
    if (!Double.isFinite(value)) {
      return fallback;
    }
    return Math.max(minimum, Math.min(maximum, value));
  }
}
