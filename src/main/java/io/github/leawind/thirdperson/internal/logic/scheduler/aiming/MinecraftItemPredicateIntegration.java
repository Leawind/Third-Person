package io.github.leawind.thirdperson.internal.logic.scheduler.aiming;

import io.github.leawind.thirdperson.internal.core.schedule.aiming.AimingSettings;
import io.github.leawind.thirdperson.internal.core.schedule.aiming.ItemPatternSet;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.leawind.thirdperson.ThirdPerson;
import io.github.leawind.thirdperson.internal.bridge.Bridge;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.item.ItemPredicateArgument;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/// Compiles resource-pack and config item predicates, then evaluates them once per client tick.
public final class MinecraftItemPredicateIntegration {
  private static final MinecraftItemPatternReloadListener RELOAD_LISTENER =
      new MinecraftItemPatternReloadListener();

  private static ClientPacketListener compiledConnection;
  private static ItemPatternSet compiledResources;
  private static long compiledSettingsRevision = -1;
  private static CompiledPredicates predicates = CompiledPredicates.empty();
  private static volatile boolean automaticallyAiming;

  private MinecraftItemPredicateIntegration() {}

  public static boolean isAutomaticallyAiming() {
    return automaticallyAiming;
  }

  /// Exposes the shared listener to loader lifecycle adapters.
  public static PreparableReloadListener reloadListener() {
    return RELOAD_LISTENER;
  }

  public static void onClientTick(AimingSettings settings) {
    Objects.requireNonNull(settings, "settings");
    Minecraft minecraft = Minecraft.getInstance();
    Bridge.registerReloadListener(minecraft, RELOAD_LISTENER, RELOAD_LISTENER::loadImmediately);

    ClientPacketListener connection = minecraft.getConnection();
    Player player = minecraft.player;
    if (connection == null || player == null || minecraft.level == null) {
      automaticallyAiming = false;
      return;
    }

    ItemPatternSet resources = RELOAD_LISTENER.snapshot();
    if (connection != compiledConnection
        || resources != compiledResources
        || settings.revision() != compiledSettingsRevision) {
      try {
        predicates = compile(connection, resources, settings);
      } catch (RuntimeException exception) {
        predicates = CompiledPredicates.empty();
        ThirdPerson.LOGGER.error("Could not initialize the item-predicate parser", exception);
      }
      compiledConnection = connection;
      compiledResources = resources;
      compiledSettingsRevision = settings.revision();
    }
    automaticallyAiming = predicates.matches(player);
  }

  private static CompiledPredicates compile(
      ClientPacketListener connection, ItemPatternSet resources, AimingSettings settings) {
    ItemPredicateArgument parser =
        ItemPredicateArgument.itemPredicate(
            CommandBuildContext.simple(connection.registryAccess(), connection.enabledFeatures()));
    return new CompiledPredicates(
        compilePatterns(parser, resources.holdToAim(), settings.holdToAimItemPatterns()),
        compilePatterns(parser, resources.useToAim(), settings.useToAimItemPatterns()));
  }

  private static List<Predicate<ItemStack>> compilePatterns(
      ItemPredicateArgument parser, List<String> resourcePatterns, List<String> configPatterns) {
    var uniquePatterns = new LinkedHashSet<String>();
    uniquePatterns.addAll(resourcePatterns);
    uniquePatterns.addAll(configPatterns);
    var compiled = new ArrayList<Predicate<ItemStack>>(uniquePatterns.size());
    for (String pattern : uniquePatterns) {
      if (pattern.isBlank()) {
        continue;
      }
      try {
        var reader = new StringReader(pattern);
        Predicate<ItemStack> predicate = parser.parse(reader);
        reader.skipWhitespace();
        if (reader.canRead()) {
          throw new IllegalArgumentException("trailing input at position " + reader.getCursor());
        }
        compiled.add(predicate);
      } catch (CommandSyntaxException | IllegalArgumentException exception) {
        ThirdPerson.LOGGER.error(
            "Ignoring invalid item predicate '{}': {}", pattern, exception.getMessage());
      }
    }
    return List.copyOf(compiled);
  }

  private record CompiledPredicates(
      List<Predicate<ItemStack>> holdToAim, List<Predicate<ItemStack>> useToAim) {
    private static CompiledPredicates empty() {
      return new CompiledPredicates(List.of(), List.of());
    }

    private boolean matches(Player player) {
      boolean aiming =
          anyMatches(player.getMainHandItem(), holdToAim)
              || anyMatches(player.getOffhandItem(), holdToAim);
      if (player.isUsingItem()) {
        aiming |= anyMatches(player.getUseItem(), useToAim);
      }
      return aiming;
    }

    private static boolean anyMatches(ItemStack stack, List<Predicate<ItemStack>> predicates) {
      if (stack.isEmpty()) {
        return false;
      }
      for (Predicate<ItemStack> predicate : predicates) {
        if (predicate.test(stack)) {
          return true;
        }
      }
      return false;
    }
  }
}
