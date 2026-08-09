package io.github.leawind.thirdperson.internal.core.api;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/// Immutable highest-priority-first resolver chain.
public final class PriorityResolverRegistry<C, T> {
  private final List<Entry<C, T>> entries;

  private PriorityResolverRegistry(List<Entry<C, T>> entries) {
    this.entries = entries;
  }

  public static <C, T> Builder<C, T> builder() {
    return new Builder<>();
  }

  public Optional<T> resolve(C context) {
    Objects.requireNonNull(context, "context");
    for (Entry<C, T> entry : entries) {
      ExtensionResult<T> result =
          Objects.requireNonNull(entry.resolver().resolve(context), "extension result");
      if (result.isHandled()) {
        return result.value();
      }
    }
    return Optional.empty();
  }

  @FunctionalInterface
  public interface Resolver<C, T> {
    ExtensionResult<T> resolve(C context);
  }

  public static final class Builder<C, T> {
    private final List<Entry<C, T>> entries = new ArrayList<>();
    private final HashSet<String> ids = new HashSet<>();
    private boolean frozen;
    private long nextOrder;

    public Builder<C, T> register(
        String id, int priority, Resolver<? super C, ? extends T> resolver) {
      if (frozen) {
        throw new IllegalStateException("Extension registry builder is frozen");
      }
      Objects.requireNonNull(id, "id");
      Objects.requireNonNull(resolver, "resolver");
      if (id.isBlank() || !ids.add(id)) {
        throw new IllegalArgumentException("Extension id must be non-blank and unique: " + id);
      }
      @SuppressWarnings("unchecked")
      Resolver<C, T> typedResolver = (Resolver<C, T>) resolver;
      entries.add(new Entry<>(id, priority, nextOrder++, typedResolver));
      return this;
    }

    public PriorityResolverRegistry<C, T> freeze() {
      if (frozen) {
        throw new IllegalStateException("Extension registry builder is already frozen");
      }
      frozen = true;
      entries.sort(
          Comparator.<Entry<C, T>>comparingInt(Entry::priority)
              .reversed()
              .thenComparingLong(Entry::order));
      return new PriorityResolverRegistry<>(List.copyOf(entries));
    }
  }

  private record Entry<C, T>(
      String id, int priority, long order, Resolver<C, T> resolver) {}
}
