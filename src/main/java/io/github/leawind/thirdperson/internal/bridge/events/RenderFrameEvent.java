package io.github.leawind.thirdperson.internal.bridge.events;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/// Neutral pre-world-render event used to apply interpolated client-side state.
public final class RenderFrameEvent {
  private static final CopyOnWriteArrayList<Listener> LISTENERS = new CopyOnWriteArrayList<>();

  private RenderFrameEvent() {}

  public static void register(Listener listener) {
    LISTENERS.add(Objects.requireNonNull(listener, "listener"));
  }

  public static void emit(float partialTick) {
    if (!Float.isFinite(partialTick)) {
      return;
    }
    for (Listener listener : LISTENERS) {
      listener.beforeRender(partialTick);
    }
  }

  @FunctionalInterface
  public interface Listener {
    void beforeRender(float partialTick);
  }
}
