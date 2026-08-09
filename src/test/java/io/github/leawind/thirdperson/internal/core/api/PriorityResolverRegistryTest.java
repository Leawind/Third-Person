package io.github.leawind.thirdperson.internal.core.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PriorityResolverRegistryTest {
  @Test
  void firstHandledResolverWinsByPriority() {
    var registry =
        PriorityResolverRegistry.<String, String>builder()
            .register("vanilla", 0, ignored -> ExtensionResult.handled("vanilla"))
            .register(
                "compat",
                100,
                context ->
                    context.equals("compat")
                        ? ExtensionResult.handled("compat")
                        : ExtensionResult.pass())
            .freeze();

    assertEquals("compat", registry.resolve("compat").orElseThrow());
    assertEquals("vanilla", registry.resolve("other").orElseThrow());
  }

  @Test
  void registrationIsClosedAfterFreeze() {
    var builder = PriorityResolverRegistry.<String, String>builder();
    builder.register("one", 0, ignored -> ExtensionResult.pass());
    builder.freeze();

    assertThrows(
        IllegalStateException.class,
        () -> builder.register("two", 0, ignored -> ExtensionResult.pass()));
  }
}
