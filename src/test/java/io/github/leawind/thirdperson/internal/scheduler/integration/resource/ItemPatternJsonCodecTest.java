package io.github.leawind.thirdperson.internal.scheduler.integration.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.gson.JsonParser;
import java.util.List;
import org.junit.jupiter.api.Test;

class ItemPatternJsonCodecTest {
  @Test
  void decodesCommandStyleItemPredicatesWithoutInterpretingThem() {
    assertEquals(
        List.of("minecraft:bow", "#example:ranged[example:mode=aim]"),
        ItemPatternJsonCodec.decode(
            JsonParser.parseString(
                "[\"minecraft:bow\",\"#example:ranged[example:mode=aim]\"]")));
  }

  @Test
  void rejectsObjectsAndNonStringEntries() {
    assertThrows(
        IllegalArgumentException.class,
        () -> ItemPatternJsonCodec.decode(JsonParser.parseString("{}")));
    assertThrows(
        IllegalArgumentException.class,
        () -> ItemPatternJsonCodec.decode(JsonParser.parseString("[\"minecraft:bow\",1]")));
  }
}
