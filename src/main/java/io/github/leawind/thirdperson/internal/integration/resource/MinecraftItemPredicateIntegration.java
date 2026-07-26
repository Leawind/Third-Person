package io.github.leawind.thirdperson.internal.integration.resource;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.leawind.thirdperson.ThirdPerson;
import io.github.leawind.thirdperson.internal.application.ThirdPersonRuntime;
import io.github.leawind.thirdperson.internal.bridge.events.ClientTickEvent;
import io.github.leawind.thirdperson.internal.core.config.ThirdPersonConfig;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.item.ItemPredicateArgument;
import net.minecraft.server.packs.resources.PreparableReloadListener;
/*? if !neoforge {*/
import net.minecraft.server.packs.resources.ReloadableResourceManager;
/*? }*/
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/// Compiles resource-pack and config item predicates, then evaluates them once per client tick.
public final class MinecraftItemPredicateIntegration {
  private static final MinecraftItemPatternReloadListener RELOAD_LISTENER =
      new MinecraftItemPatternReloadListener();
  private static final Match NONE = new Match(false, false);

  /*? if !neoforge {*/
  private static ReloadableResourceManager registeredResourceManager;
  /*? }*/
  private static ClientPacketListener compiledConnection;
  private static ItemPatternSet compiledResources;
  private static ThirdPersonConfig.AimingSettings compiledConfig;
  private static CompiledPredicates predicates = CompiledPredicates.empty();
  private static volatile Match currentMatch = NONE;
  private static boolean registered;

  private MinecraftItemPredicateIntegration() {}

  public static void register() {
    if (registered) {
      return;
    }
    registered = true;
    ClientTickEvent.register(MinecraftItemPredicateIntegration::onClientTick);
  }

  public static boolean isAutomaticallyAiming() {
    return currentMatch.aiming();
  }

  public static boolean isRequestingFirstPerson() {
    return currentMatch.firstPerson();
  }

  /// Exposes the shared listener to loader lifecycle adapters.
  public static PreparableReloadListener reloadListener() {
    return RELOAD_LISTENER;
  }

  private static void onClientTick() {
    Minecraft minecraft = Minecraft.getInstance();
    /*? if !neoforge {*/
    registerReloadListener(minecraft);
    /*? }*/

    ClientPacketListener connection = minecraft.getConnection();
    Player player = minecraft.player;
    if (connection == null || player == null || minecraft.level == null) {
      currentMatch = NONE;
      return;
    }

    ItemPatternSet resources = RELOAD_LISTENER.snapshot();
    ThirdPersonConfig.AimingSettings config =
        ThirdPersonRuntime.getInstance().config().aiming();
    if (connection != compiledConnection
        || resources != compiledResources
        || config != compiledConfig) {
      try {
        predicates = compile(connection, resources, config);
      } catch (RuntimeException exception) {
        predicates = CompiledPredicates.empty();
        ThirdPerson.LOGGER.error("Could not initialize the item-predicate parser", exception);
      }
      compiledConnection = connection;
      compiledResources = resources;
      compiledConfig = config;
    }
    currentMatch = predicates.match(player);
  }

  /*? if !neoforge {*/
  private static void registerReloadListener(Minecraft minecraft) {
    var resourceManager = minecraft.getResourceManager();
    if (resourceManager instanceof ReloadableResourceManager reloadable
        && reloadable != registeredResourceManager) {
      registeredResourceManager = reloadable;
      reloadable.registerReloadListener(RELOAD_LISTENER);
      RELOAD_LISTENER.loadImmediately(reloadable);
    }
  }
  /*? }*/

  private static CompiledPredicates compile(
      ClientPacketListener connection,
      ItemPatternSet resources,
      ThirdPersonConfig.AimingSettings config) {
    ItemPredicateArgument parser =
        ItemPredicateArgument.itemPredicate(
            CommandBuildContext.simple(connection.registryAccess(), connection.enabledFeatures()));
    return new CompiledPredicates(
        compilePatterns(parser, resources.holdToAim(), config.holdToAimItemPatterns()),
        compilePatterns(parser, resources.useToAim(), config.useToAimItemPatterns()),
        compilePatterns(
            parser, resources.useToFirstPerson(), config.useToFirstPersonItemPatterns()));
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

  private record Match(boolean aiming, boolean firstPerson) {}

  private record CompiledPredicates(
      List<Predicate<ItemStack>> holdToAim,
      List<Predicate<ItemStack>> useToAim,
      List<Predicate<ItemStack>> useToFirstPerson) {
    private static CompiledPredicates empty() {
      return new CompiledPredicates(List.of(), List.of(), List.of());
    }

    private Match match(Player player) {
      boolean aiming =
          anyMatches(player.getMainHandItem(), holdToAim)
              || anyMatches(player.getOffhandItem(), holdToAim);
      boolean firstPerson = false;
      if (player.isUsingItem()) {
        ItemStack used = player.getUseItem();
        aiming |= anyMatches(used, useToAim);
        firstPerson = anyMatches(used, useToFirstPerson);
      }
      return aiming || firstPerson ? new Match(aiming, firstPerson) : NONE;
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
