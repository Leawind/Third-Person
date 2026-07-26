package io.github.leawind.thirdperson.internal.core.camera;

/// Hysteresis for entering and leaving a collision-driven temporary first-person view.
public final class TightSpaceDetector {
  private static final double MIN_CONFIGURED_DISTANCE = 1.0;
  private static final double ENTER_DISTANCE = 0.45;
  private static final double EXIT_DISTANCE = 0.85;
  private static final int ENTER_TICKS = 2;
  private static final int EXIT_TICKS = 3;

  private boolean tight;
  private int consecutiveEnterTicks;
  private int consecutiveExitTicks;

  public boolean update(double availableDistance, double configuredDistance) {
    if (!Double.isFinite(availableDistance)
        || !Double.isFinite(configuredDistance)
        || availableDistance < 0.0
        || configuredDistance < MIN_CONFIGURED_DISTANCE) {
      reset();
      return false;
    }

    if (!tight) {
      consecutiveExitTicks = 0;
      consecutiveEnterTicks =
          availableDistance <= ENTER_DISTANCE ? consecutiveEnterTicks + 1 : 0;
      if (consecutiveEnterTicks >= ENTER_TICKS) {
        tight = true;
        consecutiveEnterTicks = 0;
      }
    } else {
      consecutiveEnterTicks = 0;
      consecutiveExitTicks =
          availableDistance >= EXIT_DISTANCE ? consecutiveExitTicks + 1 : 0;
      if (consecutiveExitTicks >= EXIT_TICKS) {
        tight = false;
        consecutiveExitTicks = 0;
      }
    }
    return tight;
  }

  public boolean isTight() {
    return tight;
  }

  public void reset() {
    tight = false;
    consecutiveEnterTicks = 0;
    consecutiveExitTicks = 0;
  }
}
