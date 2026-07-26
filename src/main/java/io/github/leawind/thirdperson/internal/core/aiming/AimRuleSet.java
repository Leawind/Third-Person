package io.github.leawind.thirdperson.internal.core.aiming;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/// Immutable, deterministically ordered snapshot of all valid aiming resource rules.
public final class AimRuleSet {
  private static final Comparator<AimRule> RULE_ORDER =
      Comparator.comparingInt(AimRule::priority)
          .reversed()
          .thenComparing(AimRule::sourceId)
          .thenComparing(rule -> rule.action().name());
  private static final AimRuleSet EMPTY = new AimRuleSet(List.of());

  private final List<AimRule> rules;

  public AimRuleSet(Collection<AimRule> rules) {
    Objects.requireNonNull(rules, "rules");
    this.rules =
        rules.stream()
            .map(rule -> Objects.requireNonNull(rule, "rule"))
            .sorted(RULE_ORDER)
            .toList();
  }

  public static AimRuleSet empty() {
    return EMPTY;
  }

  public int size() {
    return rules.size();
  }

  public List<AimRule> rules() {
    return rules;
  }

  public Optional<AimRuleAction> resolve(
      Collection<String> heldItemIds, String usedItemId, boolean usingItem) {
    Set<String> held = Set.copyOf(Objects.requireNonNull(heldItemIds, "heldItemIds"));
    for (AimRule rule : rules) {
      boolean matches =
          switch (rule.action()) {
            case AIM_WHILE_HOLDING -> rule.itemIds().stream().anyMatch(held::contains);
            case AIM_WHILE_USING, FIRST_PERSON_WHILE_USING ->
                usingItem && usedItemId != null && rule.itemIds().contains(usedItemId);
          };
      if (matches) {
        return Optional.of(rule.action());
      }
    }
    return Optional.empty();
  }
}
