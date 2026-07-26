package io.github.leawind.thirdperson.internal.core.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ConfigValidationTest {
  @Test
  void clampsFiniteValuesAndFallsBackForNonFiniteValues() {
    assertEquals(0.0, ConfigValidation.finiteClamped(-2.0, 0.0, 4.0, 1.0));
    assertEquals(4.0, ConfigValidation.finiteClamped(8.0, 0.0, 4.0, 1.0));
    assertEquals(2.0, ConfigValidation.finiteClamped(2.0, 0.0, 4.0, 1.0));
    assertEquals(1.0, ConfigValidation.finiteClamped(Double.NaN, 0.0, 4.0, 1.0));
    assertEquals(
        1.0, ConfigValidation.finiteClamped(Double.POSITIVE_INFINITY, 0.0, 4.0, 1.0));
  }
}
