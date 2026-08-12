package io.github.leawind.thirdperson.internal.core.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class PriorityProviderRegistryTest {
  @Test
  void updatesAndResetsEveryProviderInPriorityOrder() {
    var events = new ArrayList<String>();
    var registry =
        PriorityProviderRegistry.<String, String, String>builder()
            .register("low", 0, provider("low", false, events))
            .register("high", 100, provider("high", false, events))
            .freeze();

    registry.update("tick");
    registry.reset();

    assertEquals(List.of("high:tick", "low:tick", "high:reset", "low:reset"), events);
  }

  @Test
  void firstHandledProviderWinsByPriority() {
    var events = new ArrayList<String>();
    var registry =
        PriorityProviderRegistry.<String, String, String>builder()
            .register("fallback", 0, provider("fallback", true, events))
            .register("pass", 200, provider("pass", false, events))
            .register("winner", 100, provider("winner", true, events))
            .freeze();

    assertEquals("winner:frame", registry.resolve("frame").orElseThrow());
    assertEquals(List.of("pass:frame", "winner:frame"), events);
  }

  @Test
  void registrationIsClosedAfterFreeze() {
    var builder = PriorityProviderRegistry.<String, String, String>builder();
    builder.register("one", 0, provider("one", false, new ArrayList<>()));
    builder.freeze();

    assertThrows(
        IllegalStateException.class,
        () -> builder.register("two", 0, provider("two", false, new ArrayList<>())));
  }

  private static PriorityProviderRegistry.Provider<String, String, String> provider(
      String id, boolean handles, List<String> events) {
    return new PriorityProviderRegistry.Provider<>() {
      @Override
      public void update(String context) {
        events.add(id + ":" + context);
      }

      @Override
      public ExtensionResult<String> resolve(String context) {
        events.add(id + ":" + context);
        return handles ? ExtensionResult.handled(id + ":" + context) : ExtensionResult.pass();
      }

      @Override
      public void reset() {
        events.add(id + ":reset");
      }
    };
  }
}
