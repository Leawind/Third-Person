package io.github.leawind.thirdperson.internal.core.schedule.hud;

import java.util.Objects;

/// Owns HUD preferences.
public final class HudSettings {
  private CrosshairMode crosshairMode = CrosshairMode.ALWAYS;
  private boolean hideCrosshairWhenFallFlyingAndNotAiming = true;

  public CrosshairMode crosshairMode() {
    return crosshairMode;
  }

  public void setCrosshairMode(CrosshairMode crosshairMode) {
    this.crosshairMode = Objects.requireNonNull(crosshairMode, "crosshairMode");
  }

  public boolean hideCrosshairWhenFallFlyingAndNotAiming() {
    return hideCrosshairWhenFallFlyingAndNotAiming;
  }

  public void setHideCrosshairWhenFallFlyingAndNotAiming(boolean hide) {
    hideCrosshairWhenFallFlyingAndNotAiming = hide;
  }

  public void restore(
      CrosshairMode crosshairMode, boolean hideCrosshairWhenFallFlyingAndNotAiming) {
    this.crosshairMode = Objects.requireNonNull(crosshairMode, "crosshairMode");
    this.hideCrosshairWhenFallFlyingAndNotAiming = hideCrosshairWhenFallFlyingAndNotAiming;
  }
}
