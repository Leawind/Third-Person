package io.github.leawind.thirdperson.internal.core.aiming;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class AimRuleSetTest {
  @Test
  void resolvesTheHighestPriorityApplicableRule() {
    var rules =
        new AimRuleSet(
            List.of(
                new AimRule(
                    "example:low", Set.of("example:rifle"), AimRuleAction.AIM_WHILE_HOLDING, 10),
                new AimRule(
                    "example:high",
                    Set.of("example:rifle"),
                    AimRuleAction.FIRST_PERSON_WHILE_USING,
                    20)));

    assertEquals(
        AimRuleAction.AIM_WHILE_HOLDING,
        rules.resolve(Set.of("example:rifle"), null, false).orElseThrow());
    assertEquals(
        AimRuleAction.FIRST_PERSON_WHILE_USING,
        rules.resolve(Set.of("example:rifle"), "example:rifle", true).orElseThrow());
  }

  @Test
  void nonMatchingRulesDoNotProduceAnIntent() {
    var rules =
        new AimRuleSet(
            List.of(
                new AimRule(
                    "example:rule",
                    Set.of("example:rifle"),
                    AimRuleAction.AIM_WHILE_USING,
                    0)));

    assertTrue(rules.resolve(Set.of("minecraft:bow"), "minecraft:bow", true).isEmpty());
    assertTrue(rules.resolve(Set.of("example:rifle"), null, false).isEmpty());
  }

  @Test
  void equalPriorityUsesStableResourceIdOrdering() {
    var rules =
        new AimRuleSet(
            List.of(
                new AimRule(
                    "example:z", Set.of("example:item"), AimRuleAction.AIM_WHILE_HOLDING, 0),
                new AimRule(
                    "example:a",
                    Set.of("example:item"),
                    AimRuleAction.FIRST_PERSON_WHILE_USING,
                    0)));

    assertEquals("example:a", rules.rules().get(0).sourceId());
  }
}
