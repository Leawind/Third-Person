package io.github.leawind.thirdperson.internal.scheduler.aiming;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class AimingSettingsTest {
  @Test
  void defensivelyCopiesItemPatternLists() {
    var patterns = new ArrayList<>(List.of("minecraft:bow"));
    var settings = new AimingSettings();

    settings.setHoldToAimItemPatterns(patterns);
    patterns.add("minecraft:crossbow");

    assertEquals(List.of("minecraft:bow"), settings.holdToAimItemPatterns());
    assertThrows(
        UnsupportedOperationException.class,
        () -> settings.holdToAimItemPatterns().add("minecraft:trident"));
  }

  @Test
  void revisionChangesOnlyWhenPersistentValuesChange() {
    var settings = new AimingSettings();

    settings.setSmartAiming(true);
    assertEquals(0, settings.revision());
    settings.setSmartAiming(false);
    assertEquals(1, settings.revision());
  }
}
