package io.github.leawind.thirdperson.internal.core.api;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/// Immutable highest-priority-first registry for stateful internal extension providers.
///
/// Every provider receives updates and resets. Resolution stops at the first handled result.
public final class PriorityProviderRegistry<U, C, T> {
  private final List<Entry<U, C, T>> entries;

  private PriorityProviderRegistry(List<Entry<U, C, T>> entries) {
    this.entries = entries;
  }

  public static <U, C, T> Builder<U, C, T> builder() {
    return new Builder<>();
  }

  public void update(U context) {
    Objects.requireNonNull(context, "context");
    entries.forEach(entry -> entry.provider().update(context));
  }

  public Optional<T> resolve(C context) {
    Objects.requireNonNull(context, "context");
    for (Entry<U, C, T> entry : entries) {
      ExtensionResult<T> result =
          Objects.requireNonNull(entry.provider().resolve(context), "extension result");
      if (result.isHandled()) {
        return result.value();
      }
    }
    return Optional.empty();
  }

  public void reset() {
    entries.forEach(entry -> entry.provider().reset());
  }

  public interface Provider<U, C, T> {
    default void update(U context) {}

    ExtensionResult<T> resolve(C context);

    default void reset() {}
  }

  public static final class Builder<U, C, T> {
    private final List<Entry<U, C, T>> entries = new ArrayList<>();
    private final HashSet<String> ids = new HashSet<>();
    private boolean frozen;
    private long nextOrder;

    public Builder<U, C, T> register(
        String id, int priority, Provider<? super U, ? super C, ? extends T> provider) {
      if (frozen) {
        throw new IllegalStateException("Extension registry builder is frozen");
      }
      Objects.requireNonNull(id, "id");
      Objects.requireNonNull(provider, "provider");
      if (id.isBlank() || !ids.add(id)) {
        throw new IllegalArgumentException("Extension id must be non-blank and unique: " + id);
      }
      @SuppressWarnings("unchecked")
      Provider<U, C, T> typedProvider = (Provider<U, C, T>) provider;
      entries.add(new Entry<>(id, priority, nextOrder++, typedProvider));
      return this;
    }

    public PriorityProviderRegistry<U, C, T> freeze() {
      if (frozen) {
        throw new IllegalStateException("Extension registry builder is already frozen");
      }
      frozen = true;
      entries.sort(
          Comparator.<Entry<U, C, T>>comparingInt(Entry::priority)
              .reversed()
              .thenComparingLong(Entry::order));
      return new PriorityProviderRegistry<>(List.copyOf(entries));
    }
  }

  private record Entry<U, C, T>(
      String id, int priority, long order, Provider<U, C, T> provider) {}
}
