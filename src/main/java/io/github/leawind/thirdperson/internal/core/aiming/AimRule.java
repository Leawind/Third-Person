package io.github.leawind.thirdperson.internal.core.aiming;

import java.util.Objects;
import java.util.Set;

/// One validated, Minecraft-independent resource rule.
public record AimRule(
    String sourceId, Set<String> itemIds, AimRuleAction action, int priority) {
  public AimRule {
    if (Objects.requireNonNull(sourceId, "sourceId").isBlank()) {
      throw new IllegalArgumentException("sourceId must not be blank");
    }
    itemIds = Set.copyOf(Objects.requireNonNull(itemIds, "itemIds"));
    if (itemIds.isEmpty() || itemIds.stream().anyMatch(String::isBlank)) {
      throw new IllegalArgumentException("itemIds must contain non-blank IDs");
    }
    Objects.requireNonNull(action, "action");
  }
}
