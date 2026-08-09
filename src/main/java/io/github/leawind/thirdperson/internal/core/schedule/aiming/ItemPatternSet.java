package io.github.leawind.thirdperson.internal.core.schedule.aiming;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/// Immutable item-predicate expressions collected from client resource packs.
public record ItemPatternSet(List<String> holdToAim, List<String> useToAim) {
  private static final ItemPatternSet EMPTY = new ItemPatternSet(List.of(), List.of());

  public ItemPatternSet {
    holdToAim = copy(holdToAim, "holdToAim");
    useToAim = copy(useToAim, "useToAim");
  }

  public static ItemPatternSet empty() {
    return EMPTY;
  }

  public int size() {
    return holdToAim.size() + useToAim.size();
  }

  private static List<String> copy(Collection<String> patterns, String name) {
    Objects.requireNonNull(patterns, name);
    if (patterns.stream().anyMatch(Objects::isNull)) {
      throw new IllegalArgumentException(name + " must not contain null values");
    }
    return List.copyOf(patterns);
  }
}
