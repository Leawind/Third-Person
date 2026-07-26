package io.github.leawind.thirdperson.internal.core.config;

/// User-facing smoothing choices. Internal half-lives remain implementation details.
public enum SmoothingPreset {
  OFF(0.0),
  FAST(0.035),
  BALANCED(0.10),
  CINEMATIC(0.25),
  ;

  private final double halfLifeSeconds;

  SmoothingPreset(double halfLifeSeconds) {
    this.halfLifeSeconds = halfLifeSeconds;
  }

  public double halfLifeSeconds() {
    return halfLifeSeconds;
  }
}
