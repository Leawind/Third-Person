package io.github.leawind.thirdperson.internal.core.aiming;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AimModeResolverTest {
  @Test
  void manualAimingAlwaysWins() {
    assertTrue(AimModeResolver.shouldAim(true, false, false, AimUseAnimation.NONE));
  }

  @Test
  void smartAimingRequiresAnActiveSupportedAnimation() {
    assertTrue(AimModeResolver.shouldAim(false, true, true, AimUseAnimation.BOW));
    assertTrue(AimModeResolver.shouldAim(false, true, true, AimUseAnimation.CROSSBOW));
    assertTrue(AimModeResolver.shouldAim(false, true, true, AimUseAnimation.SPEAR));
    assertFalse(AimModeResolver.shouldAim(false, true, false, AimUseAnimation.BOW));
    assertFalse(AimModeResolver.shouldAim(false, false, true, AimUseAnimation.BOW));
    assertFalse(AimModeResolver.shouldAim(false, true, true, AimUseAnimation.OTHER));
  }
}
