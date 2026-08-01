package io.github.leawind.thirdperson.internal.logic.scheduler.sound;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SoundSettingsTest {
  @Test
  void ownsTheDefaultSoundState() {
    var settings = new SoundSettings();

    assertFalse(settings.centerCameraEntitySounds());
    settings.setCenterCameraEntitySounds(true);
    assertTrue(settings.centerCameraEntitySounds());
  }
}
