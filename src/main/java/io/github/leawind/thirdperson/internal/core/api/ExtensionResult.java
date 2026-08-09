package io.github.leawind.thirdperson.internal.core.api;

import java.util.Objects;
import java.util.Optional;

/// Explicit result of one prioritized extension resolver.
///
/// A pass means that the resolver does not apply to the current context. A handled result always
/// contains a non-null value, so an empty domain result must be represented by a domain value rather
/// than being confused with a pass.
public final class ExtensionResult<T> {
  private static final ExtensionResult<?> PASS = new ExtensionResult<>(false, null);

  private final boolean handled;
  private final T value;

  private ExtensionResult(boolean handled, T value) {
    this.handled = handled;
    this.value = value;
  }

  @SuppressWarnings("unchecked")
  public static <T> ExtensionResult<T> pass() {
    return (ExtensionResult<T>) PASS;
  }

  public static <T> ExtensionResult<T> handled(T value) {
    return new ExtensionResult<>(true, Objects.requireNonNull(value, "value"));
  }

  public boolean isHandled() {
    return handled;
  }

  public Optional<T> value() {
    return handled ? Optional.of(value) : Optional.empty();
  }
}
