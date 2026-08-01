package io.github.leawind.thirdperson.internal.logic.scheduler.hud;

import java.util.Objects;

/// Owns HUD preferences.
public final class HudSettings {
  private CrosshairMode crosshairMode = CrosshairMode.AUTO;

  public CrosshairMode crosshairMode() {
    return crosshairMode;
  }

  public void setCrosshairMode(CrosshairMode crosshairMode) {
    this.crosshairMode = Objects.requireNonNull(crosshairMode, "crosshairMode");
  }
}
