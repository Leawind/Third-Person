package io.github.leawind.thirdperson.internal.bridge.events;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/// Neutral end-of-client-tick event used for lifecycle and logical state updates.
public final class ClientTickEvent {
  private static final CopyOnWriteArrayList<Runnable> LISTENERS = new CopyOnWriteArrayList<>();

  private ClientTickEvent() {}

  public static void register(Runnable listener) {
    LISTENERS.add(Objects.requireNonNull(listener, "listener"));
  }

  public static void emit() {
    for (Runnable listener : LISTENERS) {
      listener.run();
    }
  }
}
