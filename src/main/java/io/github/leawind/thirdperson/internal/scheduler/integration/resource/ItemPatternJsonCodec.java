package io.github.leawind.thirdperson.internal.scheduler.integration.resource;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import java.util.List;

/// Decodes one resource-pack item-pattern list through Mojang's serialization API.
final class ItemPatternJsonCodec {
  private static final Codec<List<String>> CODEC = Codec.STRING.listOf();

  private ItemPatternJsonCodec() {}

  static List<String> decode(JsonElement json) {
    var result = CODEC.parse(JsonOps.INSTANCE, json);
    return result
        .result()
        .orElseThrow(() -> new IllegalArgumentException("Expected a string array: " + result));
  }
}
