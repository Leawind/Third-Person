package io.github.leawind.thirdperson.internal.integration.resource;

import com.google.gson.JsonParser;
import io.github.leawind.thirdperson.ThirdPerson;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

/// Loads command-style item predicate expressions from every client resource namespace.
final class MinecraftItemPatternReloadListener
    extends SimplePreparableReloadListener<ItemPatternSet> {
  private static final String ROOT_DIRECTORY = "item_patterns";
  private static final String HOLD_TO_AIM = ROOT_DIRECTORY + "/hold_to_aim";
  private static final String USE_TO_AIM = ROOT_DIRECTORY + "/use_to_aim";

  private volatile ItemPatternSet snapshot = ItemPatternSet.empty();

  ItemPatternSet snapshot() {
    return snapshot;
  }

  void loadImmediately(ResourceManager resourceManager) {
    replace(load(resourceManager));
  }

  @Override
  protected ItemPatternSet prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
    return load(resourceManager);
  }

  @Override
  protected void apply(
      ItemPatternSet preparations, ResourceManager resourceManager, ProfilerFiller profiler) {
    replace(preparations);
  }

  private ItemPatternSet load(ResourceManager resourceManager) {
    try {
      return new ItemPatternSet(
          loadDirectory(resourceManager, HOLD_TO_AIM), loadDirectory(resourceManager, USE_TO_AIM));
    } catch (RuntimeException exception) {
      ThirdPerson.LOGGER.error(
          "Could not scan item-pattern resources; keeping previous rules", exception);
      return snapshot;
    }
  }

  private List<String> loadDirectory(ResourceManager resourceManager, String directory) {
    var patterns = new ArrayList<String>();
    resourceManager
        .listResources(directory, id -> id.getPath().endsWith(".json"))
        .entrySet()
        .stream()
        .sorted(Comparator.comparing(entry -> entry.getKey().toString()))
        .forEach(
            entry -> {
              var resourceId = entry.getKey();
              try (Reader reader = entry.getValue().openAsReader()) {
                patterns.addAll(ItemPatternJsonCodec.decode(JsonParser.parseReader(reader)));
              } catch (Exception exception) {
                ThirdPerson.LOGGER.error(
                    "Ignoring invalid item-pattern resource {}: {}",
                    resourceId,
                    exception.getMessage());
              }
            });
    return patterns;
  }

  private void replace(ItemPatternSet patterns) {
    snapshot = patterns;
    ThirdPerson.LOGGER.info("Loaded {} item-predicate expressions", patterns.size());
  }
}
