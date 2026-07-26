package io.github.leawind.thirdperson.internal.integration.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.gson.JsonParser;
import io.github.leawind.thirdperson.internal.core.aiming.AimRuleAction;
import org.junit.jupiter.api.Test;

class AimRuleJsonCodecTest {
  @Test
  void decodesAndNormalizesAValidRule() {
    var rule =
        AimRuleJsonCodec.decode(
            "example:rules/rifle.json",
            JsonParser.parseString(
                """
                {
                  "items": ["example:rifle", "minecraft:bow"],
                  "action": "aim_while_using",
                  "priority": 100
                }
                """));

    assertEquals(AimRuleAction.AIM_WHILE_USING, rule.action());
    assertEquals(100, rule.priority());
    assertEquals(2, rule.itemIds().size());
  }

  @Test
  void rejectsUnknownActionsAndInvalidItemIds() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            AimRuleJsonCodec.decode(
                "example:bad_action.json",
                JsonParser.parseString(
                    "{\"items\":[\"minecraft:bow\"],\"action\":\"lock_target\"}")));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            AimRuleJsonCodec.decode(
                "example:bad_item.json",
                JsonParser.parseString(
                    "{\"items\":[\"Bad ID\"],\"action\":\"aim_while_holding\"}")));
  }

  @Test
  void priorityMustBeAnExactInteger() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            AimRuleJsonCodec.decode(
                "example:fractional.json",
                JsonParser.parseString(
                    "{\"items\":[\"minecraft:bow\"],\"action\":\"aim_while_using\",\"priority\":1.5}")));
  }
}
