package io.github.leawind.thirdperson.internal.integration.resource;

import com.google.gson.JsonParser;
import io.github.leawind.thirdperson.ThirdPerson;
import io.github.leawind.thirdperson.internal.core.aiming.AimRule;
import io.github.leawind.thirdperson.internal.core.aiming.AimRuleSet;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Comparator;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

final class MinecraftAimingRuleReloadListener
    extends SimplePreparableReloadListener<AimRuleSet> {
  private static final String DIRECTORY = ThirdPerson.MOD_ID + "/aiming_rules";

  private volatile AimRuleSet snapshot = AimRuleSet.empty();

  AimRuleSet snapshot() {
    return snapshot;
  }

  void loadImmediately(ResourceManager resourceManager) {
    replace(load(resourceManager));
  }

  @Override
  protected AimRuleSet prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
    return load(resourceManager);
  }

  @Override
  protected void apply(
      AimRuleSet preparations, ResourceManager resourceManager, ProfilerFiller profiler) {
    replace(preparations);
  }

  private AimRuleSet load(ResourceManager resourceManager) {
    var parsedRules = new ArrayList<AimRule>();
    try {
      resourceManager
          .listResources(DIRECTORY, id -> id.getPath().endsWith(".json"))
          .entrySet()
          .stream()
          .sorted(Comparator.comparing(entry -> entry.getKey().toString()))
          .forEach(
              entry -> {
                var resourceId = entry.getKey();
                try (Reader reader = entry.getValue().openAsReader()) {
                  parsedRules.add(
                      AimRuleJsonCodec.decode(
                          resourceId.toString(), JsonParser.parseReader(reader)));
                } catch (Exception exception) {
                  ThirdPerson.LOGGER.error(
                      "Ignoring invalid aiming rule {}: {}",
                      resourceId,
                      exception.getMessage());
                }
              });
      return new AimRuleSet(parsedRules);
    } catch (RuntimeException exception) {
      ThirdPerson.LOGGER.error("Could not scan aiming rules; keeping previous rules", exception);
      return snapshot;
    }
  }

  private void replace(AimRuleSet rules) {
    snapshot = rules;
    ThirdPerson.LOGGER.info("Loaded {} aiming resource rules", rules.size());
  }
}
