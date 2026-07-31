package io.github.leawind.thirdperson.internal.bridge.events;

import java.util.Objects;
import org.jspecify.annotations.Nullable;

/// Holds the single listener installed for an internal bridge event.
final class SingleEventHandler<L> {
  private volatile @Nullable L listener;

  synchronized void install(L listener) {
    Objects.requireNonNull(listener, "listener");
    if (this.listener != null) {
      throw new IllegalStateException("Listener already installed");
    }
    this.listener = listener;
  }

  @Nullable L get() {
    return listener;
  }
}
