package io.github.leawind.thirdperson.internal.core.api;

import java.util.Objects;

/// Thread-safe holder for a priority-based extension registry that freezes after first use.
///
/// Elements are registered before the registry is first used; afterwards the registry is frozen
/// and further registration is rejected. Static factories create holders for the supported
/// registry kinds:
///
/// <ul>
///   <li>{@link #resolver()} — highest-priority resolver chains.</li>
///   <li>{@link #provider()} — stateful providers with update/reset lifecycle.</li>
///   <li>{@link #modifier()} — ordered modifier pipelines.</li>
/// </ul>
///
/// @param <E> element type registered into the underlying registry builder
/// @param <R> frozen registry type
public final class ExtensionPoint<E, R> {
  private final Object lock = new Object();
  private final Registration<E> registration;
  private final Freezer<R> freezer;
  private volatile R registry;

  private ExtensionPoint(Registration<E> registration, Freezer<R> freezer) {
    this.registration = Objects.requireNonNull(registration, "registration");
    this.freezer = Objects.requireNonNull(freezer, "freezer");
  }

  /// Creates an extension point backed by a {@link PriorityResolverRegistry}.
  public static <C, T>
      ExtensionPoint<PriorityResolverRegistry.Resolver<C, T>, PriorityResolverRegistry<C, T>>
          resolver() {
    PriorityResolverRegistry.Builder<C, T> builder = PriorityResolverRegistry.builder();
    return new ExtensionPoint<>(builder::register, builder::freeze);
  }

  /// Creates an extension point backed by a {@link PriorityProviderRegistry}.
  public static <U, C, T>
      ExtensionPoint<
              PriorityProviderRegistry.Provider<U, C, T>,
              PriorityProviderRegistry<U, C, T>>
          provider() {
    PriorityProviderRegistry.Builder<U, C, T> builder = PriorityProviderRegistry.builder();
    return new ExtensionPoint<>(builder::register, builder::freeze);
  }

  /// Creates an extension point backed by an {@link OrderedModifierRegistry}.
  public static <C, T>
      ExtensionPoint<OrderedModifierRegistry.Modifier<C, T>, OrderedModifierRegistry<C, T>>
          modifier() {
    OrderedModifierRegistry.Builder<C, T> builder = OrderedModifierRegistry.builder();
    return new ExtensionPoint<>(builder::register, builder::freeze);
  }

  /// Registers an element with the given priority.
  ///
  /// Must be called before this extension point is first used, or an
  /// {@link IllegalStateException} is thrown.
  public void register(String id, int priority, E element) {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(element, "element");
    synchronized (lock) {
      if (registry != null) {
        throw new IllegalStateException(
            "Extension point is already frozen, cannot register new elements");
      }
      registration.register(id, priority, element);
    }
  }

  /// Returns the frozen registry, freezing it on the first call.
  public R get() {
    freeze();
    return registry;
  }

  /// Returns the frozen registry without freezing it, or null if it has never been used.
  public R getIfFrozen() {
    return registry;
  }

  private void freeze() {
    if (registry == null) {
      synchronized (lock) {
        if (registry == null) {
          registry = freezer.freeze();
        }
      }
    }
  }

  @FunctionalInterface
  private interface Registration<E> {
    void register(String id, int priority, E element);
  }

  @FunctionalInterface
  private interface Freezer<R> {
    R freeze();
  }
}
