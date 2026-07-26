package io.github.leawind.thirdperson.internal.integration.resource;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.leawind.thirdperson.internal.core.aiming.AimRule;
import io.github.leawind.thirdperson.internal.core.aiming.AimRuleAction;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.regex.Pattern;

final class AimRuleJsonCodec {
  private static final int MAX_ITEMS_PER_RULE = 1024;
  private static final Pattern NAMESPACE = Pattern.compile("[a-z0-9_.-]+");
  private static final Pattern PATH = Pattern.compile("[a-z0-9/._-]+");

  private AimRuleJsonCodec() {}

  static AimRule decode(String sourceId, JsonElement json) {
    if (json == null || !json.isJsonObject()) {
      throw new IllegalArgumentException("Rule root must be a JSON object");
    }
    JsonObject root = json.getAsJsonObject();
    JsonElement itemsElement = root.get("items");
    if (itemsElement == null || !itemsElement.isJsonArray()) {
      throw new IllegalArgumentException("items must be a JSON array");
    }
    if (itemsElement.getAsJsonArray().isEmpty()
        || itemsElement.getAsJsonArray().size() > MAX_ITEMS_PER_RULE) {
      throw new IllegalArgumentException("items must contain between 1 and 1024 IDs");
    }

    var itemIds = new LinkedHashSet<String>();
    for (JsonElement itemElement : itemsElement.getAsJsonArray()) {
      if (!itemElement.isJsonPrimitive()
          || !itemElement.getAsJsonPrimitive().isString()) {
        throw new IllegalArgumentException("Every item must be a string ID");
      }
      String itemId = normalizeItemId(itemElement.getAsString());
      if (itemId == null) {
        throw new IllegalArgumentException("Invalid item ID: " + itemElement);
      }
      itemIds.add(itemId);
    }

    JsonElement actionElement = root.get("action");
    if (actionElement == null
        || !actionElement.isJsonPrimitive()
        || !actionElement.getAsJsonPrimitive().isString()) {
      throw new IllegalArgumentException("action must be a string");
    }
    AimRuleAction action;
    try {
      action = AimRuleAction.valueOf(actionElement.getAsString().toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException exception) {
      throw new IllegalArgumentException("Unknown action: " + actionElement, exception);
    }

    int priority = 0;
    JsonElement priorityElement = root.get("priority");
    if (priorityElement != null) {
      if (!priorityElement.isJsonPrimitive()) {
        throw new IllegalArgumentException("priority must be an integer");
      }
      JsonPrimitive primitive = priorityElement.getAsJsonPrimitive();
      try {
        priority = primitive.getAsBigDecimal().intValueExact();
      } catch (RuntimeException exception) {
        throw new IllegalArgumentException("priority must be a 32-bit integer", exception);
      }
    }
    return new AimRule(sourceId, itemIds, action, priority);
  }

  private static String normalizeItemId(String value) {
    int separator = value.indexOf(':');
    String namespace = separator < 0 ? "minecraft" : value.substring(0, separator);
    String path = separator < 0 ? value : value.substring(separator + 1);
    if (value.indexOf(':', separator + 1) >= 0
        || !NAMESPACE.matcher(namespace).matches()
        || !PATH.matcher(path).matches()) {
      return null;
    }
    return namespace + ':' + path;
  }
}
