package io.github.leawind.thirdperson.internal.integration.resource;

import io.github.leawind.thirdperson.internal.bridge.events.ClientTickEvent;
import io.github.leawind.thirdperson.internal.core.aiming.AimRuleAction;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/// Loads resource-pack aiming rules and publishes one immutable intent per client tick.
public final class MinecraftAimingRuleIntegration {
  private static final MinecraftAimingRuleReloadListener RELOAD_LISTENER =
      new MinecraftAimingRuleReloadListener();

  private static ReloadableResourceManager registeredResourceManager;
  private static volatile AimRuleAction currentAction;
  private static boolean registered;

  private MinecraftAimingRuleIntegration() {}

  public static void register() {
    if (registered) {
      return;
    }
    registered = true;
    ClientTickEvent.register(MinecraftAimingRuleIntegration::onClientTick);
  }

  public static AimRuleAction currentAction() {
    return currentAction;
  }

  private static void onClientTick() {
    Minecraft minecraft = Minecraft.getInstance();
    var resourceManager = minecraft.getResourceManager();
    if (resourceManager instanceof ReloadableResourceManager reloadable
        && reloadable != registeredResourceManager) {
      registeredResourceManager = reloadable;
      reloadable.registerReloadListener(RELOAD_LISTENER);
      RELOAD_LISTENER.loadImmediately(reloadable);
    }

    Player player = minecraft.player;
    currentAction = player == null || minecraft.level == null ? null : resolve(player);
  }

  private static AimRuleAction resolve(Player player) {
    var heldItemIds = new ArrayList<String>(2);
    addItemId(heldItemIds, player.getMainHandItem());
    addItemId(heldItemIds, player.getOffhandItem());
    String usedItemId =
        player.isUsingItem() && !player.getUseItem().isEmpty()
            ? itemId(player.getUseItem())
            : null;
    return RELOAD_LISTENER
        .snapshot()
        .resolve(heldItemIds, usedItemId, player.isUsingItem())
        .orElse(null);
  }

  private static void addItemId(ArrayList<String> itemIds, ItemStack stack) {
    if (!stack.isEmpty()) {
      itemIds.add(itemId(stack));
    }
  }

  private static String itemId(ItemStack stack) {
    return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
  }
}
