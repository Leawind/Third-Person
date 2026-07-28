package io.github.leawind.thirdperson.internal.application.hud;

import io.github.leawind.thirdperson.internal.core.hud.ReticleMode;
import java.util.Objects;

/// Owns HUD preferences.
public final class HudSettings {
  private ReticleMode reticleMode = ReticleMode.AUTO;

  public ReticleMode reticleMode() {
    return reticleMode;
  }

  public void setReticleMode(ReticleMode reticleMode) {
    this.reticleMode = Objects.requireNonNull(reticleMode, "reticleMode");
  }
}
