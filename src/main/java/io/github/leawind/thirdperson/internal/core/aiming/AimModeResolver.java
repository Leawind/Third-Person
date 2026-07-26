package io.github.leawind.thirdperson.internal.core.aiming;

/// Resolves manual input and an already evaluated automatic-aiming signal.
public final class AimModeResolver {
  private AimModeResolver() {}

  public static boolean shouldAim(
      boolean manualAiming, boolean smartAimingEnabled, boolean automaticAiming) {
    return manualAiming || (smartAimingEnabled && automaticAiming);
  }
}
