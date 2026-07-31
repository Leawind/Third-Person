package io.github.leawind.thirdperson.internal.bridge.events;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class SingleEventHandlerTest {
  @Test
  void installsOneListener() {
    SingleEventHandler<Runnable> handler = new SingleEventHandler<>();
    Runnable listener = () -> {};

    handler.install(listener);

    assertSame(listener, handler.get());
  }

  @Test
  void rejectsNullAndDuplicateListeners() {
    SingleEventHandler<Runnable> handler = new SingleEventHandler<>();
    handler.install(() -> {});

    assertThrows(NullPointerException.class, () -> new SingleEventHandler<>().install(null));
    assertThrows(IllegalStateException.class, () -> handler.install(() -> {}));
  }
}
