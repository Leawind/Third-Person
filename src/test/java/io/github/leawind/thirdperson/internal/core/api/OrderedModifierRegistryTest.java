package io.github.leawind.thirdperson.internal.core.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class OrderedModifierRegistryTest {
  @Test
  void modifiersComposeByPriorityThenRegistrationOrder() {
    var registry =
        OrderedModifierRegistry.<String, String>builder()
            .register("last", 0, (context, value) -> value + context)
            .register("first", 100, (context, value) -> "[" + value + "]")
            .freeze();

    assertEquals("[value]!", registry.apply("!", "value"));
  }
}
