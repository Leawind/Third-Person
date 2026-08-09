package io.github.leawind.thirdperson.internal.core.schedule.sound;

/// Owns preferences that adapt camera-entity sounds to the active perspective.
public final class SoundSettings {
  private boolean centerCameraEntitySounds;

  public boolean centerCameraEntitySounds() {
    return centerCameraEntitySounds;
  }

  public void setCenterCameraEntitySounds(boolean centerCameraEntitySounds) {
    this.centerCameraEntitySounds = centerCameraEntitySounds;
  }
}
