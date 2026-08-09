package io.github.leawind.thirdperson.internal.core.api;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/// Immutable highest-priority-first modifier pipeline.
public final class OrderedModifierRegistry<C, T> {
  private final List<Entry<C, T>> entries;

  private OrderedModifierRegistry(List<Entry<C, T>> entries) {
    this.entries = entries;
  }

  public static <C, T> Builder<C, T> builder() {
    return new Builder<>();
  }

  public T apply(C context, T initial) {
    Objects.requireNonNull(context, "context");
    T value = Objects.requireNonNull(initial, "initial");
    for (Entry<C, T> entry : entries) {
      value = Objects.requireNonNull(entry.modifier().modify(context, value), "modified value");
    }
    return value;
  }

  @FunctionalInterface
  public interface Modifier<C, T> {
    T modify(C context, T value);
  }

  public static final class Builder<C, T> {
    private final List<Entry<C, T>> entries = new ArrayList<>();
    private final HashSet<String> ids = new HashSet<>();
    private boolean frozen;
    private long nextOrder;

    public Builder<C, T> register(
        String id, int priority, Modifier<? super C, T> modifier) {
      if (frozen) {
        throw new IllegalStateException("Extension registry builder is frozen");
      }
      Objects.requireNonNull(id, "id");
      Objects.requireNonNull(modifier, "modifier");
      if (id.isBlank() || !ids.add(id)) {
        throw new IllegalArgumentException("Extension id must be non-blank and unique: " + id);
      }
      @SuppressWarnings("unchecked")
      Modifier<C, T> typedModifier = (Modifier<C, T>) modifier;
      entries.add(new Entry<>(id, priority, nextOrder++, typedModifier));
      return this;
    }

    public OrderedModifierRegistry<C, T> freeze() {
      if (frozen) {
        throw new IllegalStateException("Extension registry builder is already frozen");
      }
      frozen = true;
      entries.sort(
          Comparator.<Entry<C, T>>comparingInt(Entry::priority)
              .reversed()
              .thenComparingLong(Entry::order));
      return new OrderedModifierRegistry<>(List.copyOf(entries));
    }
  }

  private record Entry<C, T>(
      String id, int priority, long order, Modifier<C, T> modifier) {}
}
